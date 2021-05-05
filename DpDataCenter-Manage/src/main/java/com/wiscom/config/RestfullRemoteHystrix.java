package com.wiscom.config;

import com.wiscom.model.dppz.DataExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Nacos与Feign远程调用服务接口失败后的操作
 * Created by YL on 2020/7/13.
 */
@Component
public class RestfullRemoteHystrix implements RestfullFeignConfig {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(RestfullRemoteHystrix.class);

    @Override
    public String executeSql(@RequestBody DataExecute dataExecute) {
        log.error("Hystrix：DpDataCenter-RestfulCollection服务的excuteSQL接口调用失败");
        return "Failed";
    }

    @Override
    public void handleDataBindDetail(@RequestBody DataExecute dataExecute) {
        log.error("Hystrix：DpDataCenter-RestfulCollection服务的handleDataBindDetail接口调用失败");
    }
}
