package com.wiscom.config;

import com.wiscom.model.dppz.DataExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Nacos与Feign远程调用服务接口失败后的操作
 * Created by YL on 2020/7/13.
 */
@Component
public class CollectionRemoteHystrix implements CollectionFeignConfig {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(CollectionRemoteHystrix.class);

    @Override
    public List executeSql(@RequestParam("id") String dsId, @RequestParam("sql") String sql) {
        log.error("Hystrix：DpDataCenter-DbCollection服务的excuteSQL接口调用失败");
        return null;
    }

    @Override
    public void handleDataBindDetail(@RequestBody DataExecute dataExecute) {
        log.error("Hystrix：DpDataCenter-DbCollection服务的handleDataBindDetail接口调用失败");
    }
}
