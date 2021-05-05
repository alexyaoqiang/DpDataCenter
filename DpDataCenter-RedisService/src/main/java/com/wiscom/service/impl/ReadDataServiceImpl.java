package com.wiscom.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.wiscom.RedisServiceApplication;
import com.wiscom.Utils.RedisOperateUtils;
import com.wiscom.service.ReadDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;

@Service
public class ReadDataServiceImpl implements ReadDataService {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(ReadDataServiceImpl.class);

    @Resource
    private RedisOperateUtils redisOperateUtils;

    /**
     * 通过key从Redis中查询数据
     * @param key
     * @return
     */
    @Override
    public Map<Object, Map<Object,Object>> selectByKey(String key) {

        Map<Object, Map<Object,Object>> map = new HashMap<>();

        Set<String> keySet = redisOperateUtils.matchlKey(key);

        for(String redisKey : keySet){
            map.put(redisKey,redisOperateUtils.getData(redisKey));

        }

        RedisServiceApplication.logger.info("selectByKey 接口被调用，返回值："+map.toString());

        return map;
    }

    /**
     * 读取table数据
     *
     * @param key

     * @return 传入的sid与cid组成的key下面的内容
     */
    @Override
    public List<Map<Object, Object>> readTableData(String key) {

        //匹配TABLE中的key
        Set<String> tableKeys = redisOperateUtils.matchTabelKey(key);

        //接收传入的sid和cid组成的key和其下面的内容
        Map<String, Object> map = new HashMap<>();

        //接收传入的sid与cid组成的key下面的内容
        List<Map<Object, Object>> list = new ArrayList();

        tableKeys.forEach(tableKey -> {
            //查询table中的数据，没有则返回空
            if (redisOperateUtils.getData(tableKey) == null
                    || redisOperateUtils.getData(tableKey).size() < 1) {
                return;
            } else {
                Map<Object, Object> tableDataMap = redisOperateUtils.getData(tableKey);
                map.put(tableKey, tableDataMap);
                list.add(tableDataMap);
            }
        });

        //把查询到的table数据添加到cacheTABLE中
        addCaCheTableData(map);

        //删除table中的key
        redisOperateUtils.deleteKey(tableKeys);

        if(list!=null && list.size()>0){
            RedisServiceApplication.logger.info("readTableData 接口被调用，返回值：");
            for(Map<Object,Object> mapLog:list){
                RedisServiceApplication.logger.info(mapLog.toString());
            }
        }

        return list;
    }


    /**
     * 读取cacheTable数据，若没有，则读取table中的数据
     *
     * @param key
     * @return 传入的sid与cid组成的key下面的内容
     */
    @Override
    public List<Map<Object, Object>> readCacheTableData(String key) {

        //匹配cacheTABLE中的key
        Set<String> cacheTableKeys = redisOperateUtils.matchCacheTabelKey(key);

        //接收传入的sid和cid组成的key和其下面的内容
        Map<String, Object> map = new HashMap<>();

        //接收传入的sid与cid组成的key下面的内容
        List<Map<Object, Object>> list = new ArrayList();

        //若cacheTABLE中有keys，则去cacheTABLE中读取，没有则去TABLE中读取，都没有则返回
        if (cacheTableKeys != null && cacheTableKeys.size() > 0) {

            for (String cacheTableKey : cacheTableKeys) {

                Map<Object, Object> cacheTableDataMap = redisOperateUtils.getData(cacheTableKey);

                map.put(cacheTableKey, cacheTableDataMap);

                list.add(cacheTableDataMap);

            }

        }else {

            list = readTableData(key);

        }

        if(list!=null){
            RedisServiceApplication.logger.info("readCacheTableData 接口被调用，返回值：");
            for(Map<Object,Object> mapLog:list){
                RedisServiceApplication.logger.info(mapLog.toString());
            }
        }

        return list;
    }


    /**
     * 专用于把从table中读取到的数据写入cacheTable
     *
     * @param map map<>中，key为String类型，value为Map<Object,Object>类型
     */
    @Override
    public void addCaCheTableData(Map<String, Object> map) {


        for(String key : map.keySet()){
            Map<Object, Object> map1 = (Map<Object, Object>) map.get(key);

            //把TABLE转换为cacheTABLE
            key = key.replace("TABLE", "cacheTABLE");

            //若redis中存在相同的key和value，直接返回
            if (redisOperateUtils.judgeKey(key) && redisOperateUtils.judgeContent(key, map1)) {
                return;
            } else {
                redisOperateUtils.addTableData(key, map1);
            }
            RedisServiceApplication.logger.info("写入数据到CacheTable：key = "+key+"    data = "+JSONArray.parseArray((String) map1.get("data")).size());

        }


    }


    /**
     * 添加数据进去table
     *
     * @param key   包含sid，cid和falg组成的key
     * @param value
     */
    @Override
    public void addTableData(String key, Map<Object, Object> value) {

        String retirekey = "TABLE:" + key;
        String s = "cacheTABLE:" + key;

        //读取cacheTable中的data是否存在，不存在直接写入，存在则与传入的data比较，不同则写入，相同则返回。
        if (redisOperateUtils.judgeContent(s, value)) {
            return;
        } else {
            redisOperateUtils.addTableData(retirekey, value);
            RedisServiceApplication.logger.info("写入数据到Table：key = "+retirekey+"    data = "+JSONArray.parseArray((String) value.get("data")).size());
        }


    }


    /**
     * 只读取table中的数据
     *
     * @param key
     * @return 返回key对应下面的内容
     */
    @Override
    public List<Map<Object, Object>> readTableData1(String key) {

        //匹配table中的key
        Set<String> tableKeys = redisOperateUtils.matchTableKey1(key);

        //接收传入的key和其下面的内容
        Map<String, Object> map = new HashMap<>();

        //接收传入的key下面的内容
        List<Map<Object, Object>> list = new ArrayList();

        tableKeys.forEach(tableKey -> {
            //查询table中的数据，没有则返回空
            if (redisOperateUtils.getData(tableKey) == null
                    || redisOperateUtils.getData(tableKey).size() < 1) {
                return;
            } else {
                Map<Object, Object> tableDataMap = redisOperateUtils.getData(tableKey);
                map.put(tableKey, tableDataMap);
                list.add(tableDataMap);
            }
        });

        if(list!=null){
            RedisServiceApplication.logger.info("readTableData1 接口被调用，返回值：");
            for(Map<Object,Object> mapLog:list){
                RedisServiceApplication.logger.info(mapLog.toString());
            }
        }

        return list;
    }


    /**
     * 只进行读cacheTable操作
     *
     * @param key
     * @return 返回传入key对应的内容
     */
    @Override
    public List<Map<Object, Object>> readCacheTable1(String key) {

        //匹配cacheTable中的key
        Set<String> cacheTableKeys = redisOperateUtils.matchTableKey1(key);

        //接收传入的key和其下面的内容
        Map<String, Object> map = new HashMap<>();

        //接收传入的key下面的内容
        List<Map<Object, Object>> list = new ArrayList();

        cacheTableKeys.forEach(cacheTableKey -> {
            //查询table中的数据，没有则返回空
            if (redisOperateUtils.getData(cacheTableKey) == null
                    || redisOperateUtils.getData(cacheTableKey).size() < 1) {
                return;
            } else {
                Map<Object, Object> tableDataMap = redisOperateUtils.getData(cacheTableKey);
                map.put(cacheTableKey, tableDataMap);
                list.add(tableDataMap);
            }
        });

        return list;
    }
}
