package com.wiscom.service;

import com.wiscom.fallback.OperateRedisServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 使用Feign调用redisService中的接口
 */
@FeignClient(name = "DpDataCenter-RedisService",
        fallback = OperateRedisServiceFallback.class)
public interface OperateRedisService {

    @GetMapping("/addTableData")
    void addTableData(@RequestParam("key") String key, @RequestBody Map<Object, Object> map);

}
