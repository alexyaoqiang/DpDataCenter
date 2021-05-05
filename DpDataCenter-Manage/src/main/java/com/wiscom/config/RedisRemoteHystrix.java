package com.wiscom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Nacos与Feign远程调用服务接口失败后的操作
 * Created by YL on 2020/7/13.
 */
@Component
public class RedisRemoteHystrix implements RedisFeignConfig {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(RedisRemoteHystrix.class);

    @Override
    public Map<Object, Map<Object,Object>> selectByKey(@RequestParam("key") String key) {
        log.error("Hystrix：DpDataCenter-RedisSeveice服务的selectByKey接口调用失败");
        return null;
    }
}
