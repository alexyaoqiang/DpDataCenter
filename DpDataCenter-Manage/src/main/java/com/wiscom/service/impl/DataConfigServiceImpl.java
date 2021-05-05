package com.wiscom.service.impl;

import com.wiscom.configuration.CollectionProperties;
import com.wiscom.model.dppz.*;
import com.wiscom.service.DataConfigService;
import com.wiscom.thread.CollectionDispatcherRunnable;
import com.wiscom.thread.MonitorRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class DataConfigServiceImpl implements DataConfigService {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(DataConfigServiceImpl.class);

    @Resource
    private MonitorRunnable monitorRunnable;

    //获取程序配置文件
    @Autowired
    private CollectionProperties collectionProperties;

    @Autowired
    private CollectionDispatcherRunnable collectionDispatcherRunnable;


    /**
     * 根据数据源类型，返回相应的数据源信息
     * @param typeList  数据源类型
     * @param dataServiceNotifyMap  返回三张表最新更新时间、数据源对应线程数（已绑定）、数据源对应信息（所有）
     * @return
     */
    @Override
    public Map<String, Object> getConfigByType(List<String> typeList, Map<String, String> dataServiceNotifyMap) {
        //返回值
        Map<String, Object> returnMap = new HashMap<>();

        //最新的表更改时间
        DataServiceNotify lastestDataServiceNotify = new DataServiceNotify();

        //保存数据类型对应的数据源
        Set<String> dsIdSet = new HashSet<>();

        //保存数据类型对应的数据源线程数
        Map<String, Integer> threadNumMap = new HashMap<>();

        //判断dbCollection服务中缓存的时间是否过期（即：与data_server_notify表中的数据在否相同）
        boolean isChange = getOtherchangeData(dataServiceNotifyMap.get("dsUpdate"),dataServiceNotifyMap.get("intfcUpdate"),dataServiceNotifyMap.get("bindUpdate"));

        lastestDataServiceNotify.setDsUpdate(monitorRunnable.getDataServiceNotify().getDsUpdate());

        lastestDataServiceNotify.setIntfcUpdate(monitorRunnable.getDataServiceNotify().getIntfcUpdate());

        lastestDataServiceNotify.setBindUpdate(monitorRunnable.getDataServiceNotify().getBindUpdate());

        returnMap.put("code",1001);
        returnMap.put("dataServiceNotify",lastestDataServiceNotify);

        //数据库表未被修改，仅仅返回“code”、“dataServiceNotify”两个键值对
        if(!isChange){
            return returnMap;
        }

        //获取dataSourceList集合数据（全部）
        List<DataSource> dataSourceList = monitorRunnable.getDataSourceList();

        //Map<dsId, DataSource>
        Map<String, DataSource> dataSourceMap=new HashMap<>();

        //移获取属于typeList中数据库类型的数据源记录，保存到threadNumMap集合，并将属于typeList中数据库类型的数据源id保存到dsIdSet集合
        for(DataSource dataSource : dataSourceList){
            if(typeList.contains(dataSource.getType().toLowerCase())){
                dataSourceMap.put(dataSource.getId(),dataSource);
                dsIdSet.add(dataSource.getId());
            }
        }

        //获取List<DataExecute>集合(绑定关系表数据)
        List<DataExecute> del = monitorRunnable.getDel();

        //获取数据源与需要的线程数的对应关系
        Map<String, Integer> threadNum = calculateRatio(del);

        //获取属于dsIdSet中数据库类型的数据源记录，保存到threadNumMap集合
        for(String dataSourceId : threadNum.keySet()){
            if(dsIdSet.contains(dataSourceId)){
                threadNumMap.put(dataSourceId,threadNum.get(dataSourceId));
            }
        }

        //将前面时间校对的结果以及获取到的DataSource集合等数据作为输入参数获取返回的map
        returnMap.put("dataSource", dataSourceMap);
        returnMap.put("threadNum", threadNumMap);

        return returnMap;

    }

    /**
     * 获取sys变量的名称和别名
     * @return
     */
    @Override
    public Map<String, Object> getSysVariableTime() {

        //返回值
        Map resultMap  = new HashMap<>();

        resultMap.put("code","0000");

        Map<String,Object> sysVariableTimeMap = collectionDispatcherRunnable.getSysVariableTimeMap();
        if(sysVariableTimeMap!=null){
            resultMap.put("code","0000");
            resultMap.put("data",collectionDispatcherRunnable.getSysVariableTimeMap());
        }else{
            resultMap.put("code","0132");
            resultMap.put("data","系统变量获取失败！");
        }

        return resultMap;

    }


    /**
     * 判断传递过来的时间与三张表的最新更新时间是相同
     * 相同返回false；不同返回true
     * @param dsUpdate  data_source表最新更新时间
     * @param intfcUpdate data_extract表最新更新时间
     * @param bindUpdate data_bind_conf表最新更新时间
     * @return
     */
    public boolean getOtherchangeData(String dsUpdate,String intfcUpdate, String bindUpdate) {

        String lastIntfcUpdate = monitorRunnable.getDataServiceNotify().getIntfcUpdate();

        String lastBindUpdate = monitorRunnable.getDataServiceNotify().getBindUpdate();

        String lastDsUpdate = monitorRunnable.getDataServiceNotify().getDsUpdate();

        if(dsUpdate==null || intfcUpdate==null || bindUpdate==null){
            return true;
        }

        if((dsUpdate.equals(lastDsUpdate))&&(intfcUpdate.equals(lastIntfcUpdate))&&(bindUpdate.equals(lastBindUpdate))){
            return false ;
        }

        return true;
    }


    /**
     * 根据数据源绑定的data_bind_conf数量、线程数，计算在dpCollection服务中该数据源应创建的线程数
     * @param del
     * @return
     */
    public Map<String, Integer> calculateRatio(List<DataExecute> del) {

        int threadCount,itemCount;  //每个数据源的采集项数,每个数据源需要创建的线程数

        int itemPerThread = collectionProperties.getItem();  //每个线程的执行的采集数
        int maxThreadNum = collectionProperties.getThread();  //每个数据源启动的最大线程数

        //Map<dsId,threadNum>
        Map<String, Integer> dstcMap = new HashMap<>();

        Map<String,Integer> dsItemNum = new HashMap<>();  //Map<dsId,itemNum>
        String dsId;
        int componentSetSize;

        if ( del == null || del.size()==0 ) {
            return dstcMap;
        }

        //获取每个数据源对应的采集项数
        for(DataExecute dataExecute : del){
            dsId  =dataExecute.getDsId();
            componentSetSize= dataExecute.getComponentFixColumnMap().size();
            if(dsItemNum.containsKey(dataExecute.getDsId())){
                dsItemNum.put(dsId,dsItemNum.get(dsId)+componentSetSize);
            }else{
                dsItemNum.put(dsId,componentSetSize);
            }
        }

        if ( dsItemNum == null || dsItemNum.size()==0 ) {
            return dstcMap;
        }

        if(itemPerThread<=0 ||  maxThreadNum<=0){
            log.error("外部配置的线程参数设置不合理：itemPerThread="+itemPerThread+" ; maxThreadNum="+maxThreadNum);
            return dstcMap;
        }

        //计算每个数据源在dpCollection服务中需创建的线程数
        for (String dataSourceId : dsItemNum.keySet()) {

            //每个数据源的采集项数
            itemCount  = dsItemNum.get(dataSourceId);

            //未超最大采集项数（最大线程数*每个线程采集项数）
            if (itemCount <= (itemPerThread * maxThreadNum)) {

                //计算线程数
                threadCount = (int)Math.ceil((double) itemCount / (double)itemPerThread);

            }
            else { //超过最大采集项数（最大线程数*每个线程采集项数）

                threadCount =  maxThreadNum;

            }
            if (threadCount > 0) {
                dstcMap.put(dataSourceId, threadCount);
            }

        }

        return  dstcMap;

    }
}
