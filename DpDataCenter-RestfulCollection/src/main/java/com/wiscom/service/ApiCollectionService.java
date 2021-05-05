package com.wiscom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.DataExecuteSon;
import com.wiscom.exception.AjaxResponse;
import com.wiscom.exception.CustomException;
import com.wiscom.exception.CustomExceptionType;
import com.wiscom.model.dppz.DataExecute;
import com.wiscom.schedule.ScheduledJobs;
import com.wiscom.util.HttpUtils;
import com.wiscom.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiCollectionService {

    @Resource
    private ScheduledJobs scheduledJobs;
    @Resource
    private HttpUtils httpUtils;
    @Resource
    private OperateRedisService operateRedisService;

    private static Logger log = LoggerFactory.getLogger(ApiCollectionService.class);

    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    //key: componentKeySet
    public static Map<String, DataExecuteSon> dataExecuteSonMap = new ConcurrentHashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    //将字符串封装到List，并序列化为字符串
    public String packageErrorResult(String message) {
        List list=new ArrayList();
        Map map=new HashMap();
        map.put("errorMessage",message);
        list.add(map);
        String result = null;
        try {
            result=objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
        return result;
    }

    /**
     * 执行api采集
     */
    public void handleDataBindDetail(DataExecute dataExecute) {

        if (ScheduledJobs.dataSourceMap.size() == 0) {
            log.info("此时数据源还没有初始赋值，进行第一次获取");
            scheduledJobs.getNewestDataSource();
        }

        //拼接完整的url地址
        String dsId = dataExecute.getDsId();
        Map excuteDetailMap = null;
        try {
            excuteDetailMap = objectMapper.readValue(dataExecute.getExcuteDetail(), Map.class);
        } catch (JsonProcessingException e) {
            log.error("handleDataBindDetail方法中excuteDetail反序列化出错");
            return;
        }
        String type = (String) excuteDetailMap.get("type");
        String function = (String) excuteDetailMap.get("function");
        Map<String, String> body = (Map<String, String>) excuteDetailMap.get("body");
        String totalUrl = ScheduledJobs.dataSourceMap.get(dsId).getInter() + function;
        log.info("handleDataBindDetail方法中组成的totalUrl,{}", totalUrl);

        ScheduledJobs.threadPool.execute(() -> {
            Map result = null;
            if ("GET".equals(type.toUpperCase())) {
                try {
                    result = httpUtils.getTemplate(totalUrl);
                } catch (CustomException e) {
                    log.error("handleDataBindDetail方法中get请求出错："+e.getMessage());
                    throw e;
                }
            }
            if ("POST".equals(type.toUpperCase())) {
                try {
                    result = httpUtils.postTemplate(totalUrl, body);
                } catch (CustomException e) {
                    log.error("handleDataBindDetail方法中post请求出错："+e.getMessage());
                    throw e;
                }
            }
            //模拟服务器响应时间延迟20s
//            try {
//                Thread.sleep(20000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Object dataObject = result.get("data");
            log.info("API采集到的数据中的data,{}", dataObject);
            Boolean success= (Boolean) result.get("success");
            if (success==null || !success) {    //代表响应数据为错误提示
                return;
            }
            Map<String, String> componentFixColumnMap = dataExecute.getComponentFixColumnMap();
            Iterator<Map.Entry<String, String>> it = componentFixColumnMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                log.info("下面开始处理（存redis）key,{},fixColumn,{}", entry.getKey(), entry.getValue());
                String[] strs = entry.getKey().split(":");
                String SCENE_ID = strs[0];
                String componentCode = strs[1];
                String group = strs[2];
                Map param = new HashMap();
                param.put("SCENE_ID", SCENE_ID);
                param.put("componentCode", componentCode);
                param.put("group", group);
                param.put("cTime",TimeUtils.dateToString1(new Date()));
                String data= null;
                try {
                    log.info("开始组装data");
                    data = packageData(entry.getValue(),dataObject,totalUrl);
                } catch (Exception e) {
                    log.error("handleDataBindDetail中二次组装data出错：",e.getMessage());
                    continue;
                }
                param.put("data",data);
                operateRedisService.addTableData(entry.getKey(), param);
                log.info("执行线程：" + Thread.currentThread().getName() + ",存储的key:" + entry.getKey()+",存储的value:"+param);
            }

        });

    }

    /**
     * 根据接口返回的data里面的值以及fixColumn，对data【存入redis或者数据预览】进行二次封装
     * @param fixColumn
     * @param dataObject
     * @return
     * @throws Exception
     */
    private String packageData(String fixColumn, Object dataObject,String totalUrl) {
        /*
            下面组装data
         */
        //判断fixColumn是否为空
        if (StringUtils.isEmpty(fixColumn)) {
            //直接把data下（接口上返回的data）的数据存入redis
            List result;
            //判断dataObject是否为List，因为manager需要的是List格式的数据
            if(dataObject instanceof List) {
                log.info("fixColumn为空，且响应数据中data下的value类型为List");
                result= (List) dataObject;
            }else { //dataObject不是List说明返回的应该是错误提示
                log.info("fixColumn为空，但是响应数据中data下的value类型不是List");
                String errorResult= packageErrorResult("fixColumn为空，但是响应数据中data下的value类型不是List");
                return errorResult;
            }
            String data = null;
            try {
                data = objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return data;
        }else {
            //判断data下（接口上返回的data）的数据类型是否为List且长度是否为1
            if(dataObject instanceof List && ((List) dataObject).size()==1) {
                //result指的就是data后面的value数据
                Map<String,Object> result=objectMapper.convertValue(((List) dataObject).get(0),Map.class);
                Object fixColumnValue=result.get(fixColumn);
                if(fixColumnValue instanceof List) {
                    log.info("fixColumn非空，且响应数据中data下的value类型为List，长度为1，且根据fixColumn取到的结果为List");
                    String data = null;
                    try {
                        data = objectMapper.writeValueAsString(fixColumnValue);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return data;
                }else {
                    log.info("fixColumn非空，且响应数据中data下的value类型为List，长度为1，但是根据fixColumn取到的结果不是List");
                    List list=new ArrayList<>();
                    Map fixColumnMap=new HashMap();
                    fixColumnMap.put(fixColumn,fixColumnValue);
                    list.add(fixColumnMap);
                    String data = null;
                    try {
                        data = objectMapper.writeValueAsString(list);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return data;
                }
            }else{
                String message="根据url:"+totalUrl+"得到的响应数据格式有问题";
                log.error(message);
                String errorResult=packageErrorResult(message);
                return errorResult;
            }
        }

    }

    /**
     * 数据预览,根据数据源id以及对应的指标项获取数据返回
     */
    public String executeSql(String dsId, String executeDetail,String fixColumn) throws CustomException{
        if (ScheduledJobs.dataSourceMap.size() == 0) {
            log.info("此时数据源还没有初始赋值，进行第一次获取");
            scheduledJobs.getNewestDataSource();
        }

        //拼接完整的url地址
        Map excuteDetailMap = null;
        try {
            excuteDetailMap = objectMapper.readValue(executeDetail, Map.class);
        } catch (JsonProcessingException e) {
            log.error("executeSql中传过来的executeDetail不是标准的json字符串");
//            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"executeSql中传过来的executeDetail不是标准的json字符串");
            String errorResult=packageErrorResult("executeSql中传过来的executeDetail不是标准的json字符串");
            return errorResult;
        }
        String type = (String) excuteDetailMap.get("type");
        String function = (String) excuteDetailMap.get("function");
        Map<String, String> body = (Map<String, String>) excuteDetailMap.get("body");
        String totalUrl = ScheduledJobs.dataSourceMap.get(dsId).getInter() + function;
        log.info("executeSql中的totalUrl,{}", totalUrl);

        Map result = null;
        if ("GET".equals(type.toUpperCase())) {
            try {
                result = httpUtils.getTemplate(totalUrl);
            } catch (CustomException e) {
                log.error("executeSql方法中get请求出错："+e.getMessage());
                String errorResult=packageErrorResult("executeSql方法中get请求出错："+e.getMessage());
                return errorResult;
            }
        }
        if ("POST".equals(type.toUpperCase())) {
            try {
                result = httpUtils.postTemplate(totalUrl, body);
            } catch (CustomException e) {
                log.error("executeSql方法中post请求出错："+e.getMessage());
                String errorResult=packageErrorResult("executeSql方法中post请求出错："+e.getMessage());
                return errorResult;
            }
        }
        if (!"GET".equals(type.toUpperCase()) && !"POST".equals(type.toUpperCase())) {
            String errorResult=packageErrorResult("请求方法既不是get方法也不是post方法");
            return errorResult;
        }
        Object dataObject = result.get("data");
        Boolean success= (Boolean) result.get("success");
        if (success==null || !success) {    //代表响应数据为错误提示
            String errorResult= null;
            try {
                errorResult = packageErrorResult(objectMapper.writeValueAsString(result));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return errorResult;
        }
        String data = null;
        try {
            log.info("开始组装data");
            data = packageData(fixColumn,dataObject,totalUrl);
        } catch (Exception e) {
            log.error("executeSql中二次组装data出错：",e);
            String errorResult=packageErrorResult("executeSql中二次组装data出错"+e);
            return errorResult;
        }
        return data;
    }
}
