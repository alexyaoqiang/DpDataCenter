package com.wiscom.thread;

import com.alibaba.fastjson.JSONObject;
import com.wiscom.ManageApplication;
import com.wiscom.config.CollectionFeignConfig;
import com.wiscom.config.RedisFeignConfig;
import com.wiscom.config.RestfullFeignConfig;
import com.wiscom.constant.MappingConstant;
import com.wiscom.model.dppz.DataExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 采集项调度线程
 * Created by YL on 2020/7/10.
 */
@Component(value = "collectionDispatcherRunnable")
public class CollectionDispatcherRunnable implements Runnable {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(CollectionDispatcherRunnable.class);

    public static Map<String,String> sysVariableMap = new HashMap<>();  //<key,value>  $key$

    public static Map<String,Object> sysVariableTimeMap = new HashMap<>();  //<name,alias>

    public  Map<String, String> getSysVariableMap() {
        return sysVariableMap;
    }

    public Map<String, Object> getSysVariableTimeMap() {
        return sysVariableTimeMap;
    }

    @Autowired
    private MonitorRunnable monitorRunnable;

    @Resource
    private CollectionFeignConfig collectionFeignConfig;

    @Resource
    private RestfullFeignConfig restfullFeignConfig;

    @Resource
    private RedisFeignConfig redisFeignConfig;

    @Override
    public void run() {

        log.info("CollectionDispatcherThread start！");

        int count = 0;  //计数器，结合指标执行频率判断在否执行该指标

        int frequence;  //指标采集频率

        String serviceName;  //服务名称

        Map<Object, Map<Object,Object>> redisMap = new HashMap<>(); //selectGlobleVariable方法的返回值

        String key; //$key$

        String sql;

        JSONObject json;  //vqlue从字符串转的json对象

        Map<String,String> sysVariableCacheMap;  //临时存储：<key,value>  $key$

        Map<String,Object> sysVariableTimeCacheMap;  //<name,alias>

        while (true) {

            //遍历Map集合，获取DataExecute对象
            for (DataExecute dataExecute : monitorRunnable.getDel()) {

                    frequence = dataExecute.getFrequence(); //SQL语句执行频率

                    //判断当前sql语句是否到达执行时间（启动时执行一次，后面根据指标频率执行sql语句）
                    if (count % frequence == 0) {

                        /* 替换sys变量 */

                        sysVariableCacheMap = new HashMap<>(); //初始化临时缓存集合
                        sysVariableTimeCacheMap = new HashMap<>();


                        redisMap = redisFeignConfig.selectByKey("WIS:SYS:*");

                        //selectByKey方法只有在调用失败时才会返回null，没有数据返回空Map
                        if(redisMap==null){

                            log.error("从Redis数据库获取 WIS:SYS:* 失败");
                            sysVariableTimeCacheMap=null;

                        }else {
                            if (redisMap.size() != 0) {
                                for (Object redisKey : redisMap.keySet()) {

                                    key = String.valueOf(redisKey).split(":")[2];

                                    sysVariableCacheMap.put(key, String.valueOf(redisMap.get(redisKey).get("data")));

                                    sysVariableTimeCacheMap.put(key, String.valueOf(redisMap.get(redisKey).get("alias")));
                                }
                            }
                        }

                        sysVariableMap=sysVariableCacheMap;
                        sysVariableTimeMap = sysVariableTimeCacheMap;

                        sql = dataExecute.getExcuteDetail();

                        //替换全局变量
                        if(sql.contains("$") && sysVariableMap.size()!=0){
                            Pattern pattern = Pattern.compile("\\$(.*?)\\$");
                            Matcher m = pattern.matcher(sql);
                            String sysValue;
                            while(m.find()){
                                sysValue = sysVariableMap.get(m.group(1));
                                if(sysValue == null){
                                    log.error("Redis数据库中不存在sys变量：WIN:SYS:"+m.group(1));
                                    continue;
                                }
                                sql = sql.replaceAll(Matcher.quoteReplacement(m.group(0)), sysValue );
                            }
                            dataExecute.setExcuteDetail(sql);
                        }

                        serviceName =MappingConstant.typeToServiceMapping.get(monitorRunnable.getDsTypeMap().get(dataExecute.getDsId()).toLowerCase());

                        //调用dpCollection服务接口
                        if("dpDataCenter-dbCollection".equals(serviceName)){

                            ManageApplication.logger.info("时间："+count+"-----"+dataExecute.toString());

                            collectionFeignConfig.handleDataBindDetail(dataExecute);
                        }
                        //调用dpCollection服务接口
                        if("dpDataCenter-restfulCollection".equals(serviceName)){

                            ManageApplication.logger.info("时间："+count+"-----"+dataExecute.toString());

                            restfullFeignConfig.handleDataBindDetail(dataExecute);

                        }

                    }

            }

            count++;

            //线程休眠1秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        }

    }


}
