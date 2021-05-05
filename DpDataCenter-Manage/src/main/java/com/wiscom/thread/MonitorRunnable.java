package com.wiscom.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wiscom.mapper.DataSourceInfoMapper;
import com.wiscom.model.dppz.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * @author cyang
 * @version 创建日期:2020年6月22日
 */
@Component(value = "monitorRunnable")
public class MonitorRunnable implements Runnable
{
    //日志
    private static Logger log = LoggerFactory.getLogger(MonitorRunnable.class);
    //data_source、data_extract、data_bind_info表的最新更新时间，数据来源于data_service_notify表
    private DataServiceNotify dataServiceNotify = new DataServiceNotify();
    //List<DataSource>，数据来源于data_source表
    private List<DataSource> dataSourceList = new ArrayList<>();
    //Map<dsId,dsType>，数据来源于data_source表
    private Map<String, String> dsTypeMap = new HashMap<>();
    //Map<dsType, Map<dsId, DataSource>>，数据来源于data_source表
    private Map<String, Map<String, DataSource>> dsm = new HashMap<>();
    //List<DataExtract>，数据来源于data_extract表
    private List<DataExtract> dataExtractList = new ArrayList<>();
    //Map<intfcId, DataExtract>，数据来源于data_extract表
    private Map<String, DataExtract> dem = new HashMap<>();
    //List<DataBindConf>，数据来源于data_bind_info表
    private List<DataBindConf> dataBindConfList = new ArrayList<>();
    //List<DataExecute>，替换参数后的sql语句，数据来源于data_extract、data_bind_info表
    private List<DataExecute> del = new ArrayList<>();

    public DataServiceNotify getDataServiceNotify()
    {
        return dataServiceNotify;
    }

    public List<DataExecute> getDel()
    {
        return del;
    }

    public List<DataSource> getDataSourceList()
    {
        return dataSourceList;
    }

    public Map<String, String> getDsTypeMap()
    {
        return dsTypeMap;
    }

    @Autowired
    private DataSourceInfoMapper dataSourceInfoMapper;

    @Override
    public void run()
    {
        log.info("Monitor start！");
        DataServiceNotify dataServiceNotifyNew;
        while (true)
        {
            try
            {
                //从数据库中获取最新的更新时间
                dataServiceNotifyNew = dataSourceInfoMapper.getDataServiceNotifyInfo();
                //判定从配置更新时间表中获取的数据是否为null
                if (dataServiceNotifyNew != null)
                {
                    //若数据源表的更新时间发生了变更
                    if (!dataServiceNotify.getDsUpdate().equals(dataServiceNotifyNew.getDsUpdate()))
                    {
                        //重置缓存中的ds表更新时间
                        dataServiceNotify.setDsUpdate(dataServiceNotifyNew.getDsUpdate());
                        log.info("数据源表发生变化！");
                        log.info("变化前：" + dataSourceList.size());
                        dataSourceList = dataSourceInfoMapper.getAllDataSourceInfo();
                        for (DataSource dataSource : dataSourceList)
                        {
                            dsTypeMap.put(dataSource.getId(), dataSource.getType());
                            if (!dsm.containsKey(dataSource.getType()))
                            {
                                dsm.put(dataSource.getType(), new HashMap<>());
                            }
                            dsm.get(dataSource.getType()).put(dataSource.getId(), dataSource);
                        }
                        log.info("变化后：" + dataSourceList.size());
                    }

                    //若数据配置表的更新时间发生了变更
                    if (!(dataServiceNotify.getIntfcUpdate()).equals(dataServiceNotifyNew.getIntfcUpdate()))
                    {
                        //重置缓存中的de表更新时间
                        dataServiceNotify.setIntfcUpdate(dataServiceNotifyNew.getIntfcUpdate());
                        log.info("指标表发生变化！");
                        log.info("变化前：" + dataExtractList.size());
                        dataExtractList = dataSourceInfoMapper.getAllDataExtractInfo();
                        for (DataExtract dataExtract : dataExtractList)
                        {
                            dem.put(dataExtract.getId(), dataExtract);
                        }
                        del = getDataExecuteList();
                        log.info("变化后：" + dataExtractList.size());
                    }
                    //场景组件表的更新时间发生了变更
                    if (!(dataServiceNotify.getBindUpdate()).equals(dataServiceNotifyNew.getBindUpdate()))
                    {
                        //重置缓存中的db表更新时间
                        dataServiceNotify.setBindUpdate(dataServiceNotifyNew.getBindUpdate());
                        log.info("绑定表发生变化");
                        log.info("变化前：" + dataBindConfList.size());
                        //获取data_bind_conf表中所有绑定记录，缓存到dataBindConfList集合
                        dataBindConfList = dataSourceInfoMapper.getAllDataBindConfInfo();
                        //将data_extract与data_bind_conf表的信息整合后返回List<DataExecute>集合
                        del = getDataExecuteList();
                        log.info("变化前：" + dataBindConfList.size());
                    }
                }
            } catch (Exception e)
            {
                log.error("Monitor线程获取数据库数据更新缓存失败！");
                e.printStackTrace();
            }

            //休眠5秒
            try
            {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将data_extract与data_bind_conf表的信息整合后返回List<DataExecute>集合
     * 以替换后的sql语句为主
     * 不同sql语句可能具有相同intfcId
     * 相同sql语句可能具有不同sceneId、componenetId、groupIndex、componentCode
     *
     * @return
     */
    public List<DataExecute> getDataExecuteList()
    {
        String sql;
        //固定参数fixColumn的值
        String fixColumn = "";
        Map<String, DataExecute> dataExecuteMap;
        DataExecute dataExecute = null;
        List<DataExecute> dataExecuteList = new ArrayList<>();
        String componentKey;
        List<DataBindConf> dataBindConfById;
        //Map<intfcId,List<DataBindConf>>
        Map<String, List<DataBindConf>> intfcMap = new HashMap<>();
        //将data_bind_info表的信息转换为Map<intfcId,List<DataBindConf>
        for (DataBindConf dataBindConf : dataBindConfList)
        {
            String intfcId = dataBindConf.getIntfcId();
            if (!intfcMap.containsKey(intfcId))
            {
                intfcMap.put(intfcId, new ArrayList<>());
            }
            intfcMap.get(intfcId).add(dataBindConf);
        }

        //遍历指标数据
        for (DataExtract dataExtract : dataExtractList)
        {
            //获取指标对应的绑定信息
            dataBindConfById = intfcMap.get(dataExtract.getId());
            if (dataBindConfById != null && dataBindConfById.size() > 0)
            {
                dataExecuteMap = new HashMap<>();
                //遍历该指标的绑定信息
                for (DataBindConf dataBindConf : dataBindConfById)
                {
                    fixColumn = "";
                    //参数替换占位符，组成完整sql语句
                    sql = dataExtract.getIntfcDetail();
                    String intfcParam = dataBindConf.getIntfcParam();
                    JSONArray array = JSON.parseArray(intfcParam);
                    JSONObject jso;
                    //替换组件配置变量
                    if (array.size() > 0)
                    {
                        //替换sql中动态参数
                        for (int i = 0; i < array.size(); i++)
                        {
                            jso = array.getJSONObject(i);
                            //fixColumn为固定参数，不需要替换
                            if ("fixColumn".equals(jso.getString("key")))
                            {
                                fixColumn = jso.getString("value");
                                continue;
                            }
                            sql = sql.replaceAll("@" + jso.getString("key") + "@", Matcher.quoteReplacement(jso.getString("value")));
                        }
                    }

                    //新增componentKey组成部分componentCode
                    componentKey = dataBindConf.getSceneId() + ":" + dataBindConf.getComponentCode() + ":" + dataBindConf.getGroupIndex();
                    //注意：同一指标id的intfcDetail使用initfcParam替换后具有不同sql，initfcParam来自绑定信息，可以不同，若sql相同但场景、主键等有所不同，则将这一部分信息存储在getComponentKeySet中（唯一）
                    if (!dataExecuteMap.containsKey(sql + ":" + dataExtract.getId()))
                    {
                        dataExecute = new DataExecute();
                        dataExecuteMap.put(sql + ":" + dataExtract.getId(), dataExecute);
                    }
                    dataExecute.setDsId(dataExtract.getDsId());
                    dataExecute.setIntfcId(dataExtract.getId());
                    dataExecute.setExcuteDetail(sql);
                    dataExecute.setFrequence(dataExtract.getFrequence());
                    dataExecute.getComponentFixColumnMap().put(componentKey, fixColumn);
                }
                //遍历获取dataExecute中的value
                for (DataExecute value : dataExecuteMap.values())
                {
                    dataExecuteList.add(value);
                }
            }
        }
        return dataExecuteList;
    }
}


