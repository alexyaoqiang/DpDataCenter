package com.wiscom.service.impl;

import com.wiscom.service.DynamicDataService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicDataServiceImpl implements DynamicDataService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 写入paramChange
     *
     * @param sid   场景id
     * @param param
     * @param val
     */
    @Override
    public void dynamicDataChange(String sid, String param, String val) {
        //拼接key
        String str = "paramChange:" + sid + ":" + param;
        //存放需要写入的数据
        Map<Object, Object> map = new HashMap<>();

        map.put(param, val);
        stringRedisTemplate.opsForHash().put(str, param, val);
    }

    /**
     * 删除数据
     *
     * @param sid
     */
    @Override
    public void dynamicDataReset(String sid) {

        //匹配paramChange中的key
        Set<String> tableKeys = stringRedisTemplate.keys("paramChange:" + sid + "*");

        tableKeys.forEach(key -> {
            stringRedisTemplate.delete(key);
        });
    }
}
