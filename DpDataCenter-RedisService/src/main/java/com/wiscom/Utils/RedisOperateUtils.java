package com.wiscom.Utils;

import com.wiscom.RedisServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RedisOperateUtils {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(RedisOperateUtils.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取所有符合条件的key
     *
     * @param key
     * @return 返回匹配到的keys集合
     */
    public Set matchlKey(String key) {
        Set<String> tableKeys = stringRedisTemplate.keys(key);
        return tableKeys;
    }

    /**
     * 写入Table
     *
     * @param key
     * @param value
     */
    public void addTableData(String key, Map<Object, Object> value) {

        stringRedisTemplate.opsForHash().putAll(key, value);

    }



    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean judgeKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 判断传入的内容是否与table中的内容一致
     *
     * @param key
     * @param value
     * @return 一致返回true，不一致返回false
     */
    public boolean judgeContent(String key, Map<Object, Object> value) {
        //若获取的data不存在数据，则返回false，存在则进行比较
        if (stringRedisTemplate.opsForHash().entries(key).get("data") == null ||
                stringRedisTemplate.opsForHash().entries(key).get("data").equals("")){
            return false;
        }else {
            if (stringRedisTemplate.opsForHash().entries(key).get("data").equals(value.get("data"))) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * 匹配cacheTable中的key
     *
     * @param key
     * @return 返回匹配到的keys集合
     */
    public Set matchCacheTabelKey(String key) {
        String retirekey = "cacheTABLE:" + key + "*";
        Set<String> cacheTableKeys = stringRedisTemplate.keys(retirekey);
        return cacheTableKeys;
    }


    /**
     * 匹配table中的key
     *
     * @param key
     * @return 返回匹配到的keys集合
     */
    public Set matchTabelKey(String key) {
        String retirekey = "TABLE:" + key + "*";
        Set<String> tableKeys = stringRedisTemplate.keys(retirekey);
        return tableKeys;
    }


    /**
     * 匹配ParamChange中的key
     *
     * @param sId
     * @param key
     * @return返回匹配到的keys集合
     */
    public Set matchParamChangeKey(String sId, String key) {
        String changeKey = "#" + key + "#";
        Set<String> ParamChangeKeys = stringRedisTemplate.keys("ParamChange:" + sId + ":" + changeKey);
        return ParamChangeKeys;
    }


    /**
     * 获取传入的cacheTablekey对应的内容
     *
     * @param key
     * @return map类型的内容
     */
    public Map getData(String key) {
//        log.info("获取值：key = "+key);
        Map<Object, Object> tableDataMap = stringRedisTemplate.opsForHash().entries(key);
        return tableDataMap;
    }


    /**
     * 删除table中所有的key
     *
     * @param key
     */
    public void deleteKey(Set key) {
        if (key.size()>0 && key!=null){
            RedisServiceApplication.logger.info("删除Table中数据：key = "+key.toString());
        }
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除table中单独的key
     *
     * @param key
     */
    public void deleteSingleKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据主key，副key，value写入
     *
     * @param mainKey
     * @param key
     * @param value
     */
    public void addData(String mainKey, String key, String value) {
        stringRedisTemplate.opsForHash().put(mainKey, key, value);
    }

    /**
     * 匹配table中的key
     *
     * @param key
     * @return 匹配到的key集合
     */
    public Set matchTableKey1(String key) {
        Set<String> TableKeys = stringRedisTemplate.keys(key);
        return TableKeys;
    }
}
