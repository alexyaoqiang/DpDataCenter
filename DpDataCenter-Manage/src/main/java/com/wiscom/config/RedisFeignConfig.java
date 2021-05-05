package com.wiscom.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 从Nacos发现服务的Feign配置接口(dpDataCenter_redisService)
 * Created by YL on 2020/7/13.
 */
@FeignClient(name = "DpDataCenter-RedisService",
        fallback = RedisRemoteHystrix.class)
public interface RedisFeignConfig {

    @GetMapping("/selectByKey")
    @ResponseBody
    Map<Object, Map<Object,Object>> selectByKey(@RequestParam("key") String key);


}
