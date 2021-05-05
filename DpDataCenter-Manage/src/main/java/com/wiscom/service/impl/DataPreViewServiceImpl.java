package com.wiscom.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.wiscom.ManageApplication;
import com.wiscom.config.CollectionFeignConfig;
import com.wiscom.config.RestfullFeignConfig;
import com.wiscom.constant.MappingConstant;
import com.wiscom.model.dppz.DataExecute;
import com.wiscom.service.DataConfigService;
import com.wiscom.service.DataPreViewService;
import com.wiscom.thread.CollectionDispatcherRunnable;
import com.wiscom.thread.MonitorRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by YL on 2020/7/13.
 */
@Service
public class DataPreViewServiceImpl implements DataPreViewService {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(DataPreViewServiceImpl.class);

    @Resource
    private CollectionFeignConfig collectionFeignConfig;
    @Resource
    private RestfullFeignConfig restfullFeignConfig;

    @Autowired
    private CollectionDispatcherRunnable collectionDispatcherRunnable;

    @Autowired
    private MonitorRunnable monitorRunnable;

    @Override
    public Map<String,Object> viewData(Map<String,Object> param) {

        ManageApplication.logger.info("数据预览接口调用！");

        //返回值
        Map resultMap  = new HashMap<>();
        //固定参数fixColumn的值
        String fixColumn="";
        //获取dsId
        String dsId = String.valueOf(param.get("id"));
        //获取sql，数据库和API类型都是字符串
        String sql =  String.valueOf(param.get("sql"));
        //参数，类型为JSON
        Map<String,Object> paramMap = (Map<String, Object>) param.get("param");

        //替换sql中的参数
        for(String key:paramMap.keySet()){

            //固定参数fixColumn
            if("fixColumn".equals(key)){
                fixColumn=((Map<String, String>)paramMap.get(key)).get("value");
                continue;
            }

            //替换sql语句中的参数
            sql = sql.replace("@"+key+"@",((Map<String, String>)paramMap.get(key)).get("value"));

        }

        Map<String,String> globalVariableMap = collectionDispatcherRunnable.getSysVariableMap();


        if(sql.contains("$") && globalVariableMap.size()!=0){
            //替换全局变量（如果存在）
            Pattern pattern = Pattern.compile("\\$(.*?)\\$");
            Matcher m = pattern.matcher(sql);
            while(m.find()){
                sql = sql.replaceAll(Matcher.quoteReplacement(m.group(0)), globalVariableMap.get(m.group(1)));
            }
        }


        //根据数据源类型判断调用那个服务的接口
        String serviceName = MappingConstant.typeToServiceMapping.get(monitorRunnable.getDsTypeMap().get(dsId).toLowerCase());

        //调用Collection服务接口，获取预览结果
        List<Map<String,Object>> list= new ArrayList<>();

        ManageApplication.logger.info("服务名称： "+serviceName);

        //1、调用dbCollection服务接口，获取并处理预览结果
        if("dpDataCenter-dbCollection".equals(serviceName)){

            ManageApplication.logger.info("调用dbCollection服务的预览接口");
            ManageApplication.logger.info("dsId= "+dsId);
            ManageApplication.logger.info("sql= "+sql);

            try{

                list = collectionFeignConfig.executeSql(dsId, sql);

            } catch (Exception e) {

                log.error(serviceName+"服务的executeSql接口调用失败！");
                e.printStackTrace();

                resultMap.put("code","0131");
                resultMap.put("data","Execute SQL failed ("+serviceName+")");

                return resultMap;
            }

            if(list==null){

                log.error(serviceName+"服务的executeSql接口调用失败！");

                resultMap.put("code","0131");
                resultMap.put("data","Execute SQL failed ("+serviceName+")");

                return resultMap;

            }else{
                ManageApplication.logger.info("返回值 = "+list.toString());
            }

        }


        //2、调用restfulCollection服务接口，获取并处理预览结果
        if("dpDataCenter-restfulCollection".equals(serviceName)){

            ManageApplication.logger.info("调用restfulCollection服务的预览接口");
            ManageApplication.logger.info("dsId = "+dsId);

            ManageApplication.logger.info("sql = "+sql);
            ManageApplication.logger.info("fixColumn = "+fixColumn);

            String result;
            Map<String,Object> map;

            //String通过Nacos+Feign传递时“&”后内容被切割
            DataExecute dataExecute = new DataExecute();
            dataExecute.setDsId(dsId);
            dataExecute.setExcuteDetail(sql);
            dataExecute.setFixColumn(fixColumn);

            try{
                result = restfullFeignConfig.executeSql(dataExecute);
            }catch (Exception e) {

                e.printStackTrace();
                log.error(serviceName+"服务的executeSql接口调用失败！");

                resultMap.put("code","0131");
                resultMap.put("data","Execute SQL failed ("+serviceName+")");

                return resultMap;
            }

            if("Failed".equals(result)){

                resultMap.put("code","0131");
                resultMap.put("data","Execute SQL failed ("+serviceName+")");

                return resultMap;
            }

            ManageApplication.logger.info("返回值 = "+result);

            if(result!=null){
                JSONArray jsonArray = JSONArray.parseArray(result);
                for(int i=0;i<jsonArray.size();i++){
                    Map<String, String> object = (Map<String, String>) jsonArray.get(i);
                    map =new HashMap<>();
                    for(String key:object.keySet()){
                        map.put(key,object.get(key));
                    }
                    list.add(map);
                }
            }

        }

        resultMap.put("code","0000");
        resultMap.put("data",list);

        return resultMap;
    }


}
