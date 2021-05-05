package com.wiscom.config;

import com.wiscom.model.dppz.DataExecute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 从Nacos发现服务的Feign配置接口(dpDataCenter-restfulCollection)
 * Created by YL on 2020/11/03.
 */
@FeignClient(name = "DpDataCenter-RestfulCollection",
        fallback = RestfullRemoteHystrix.class)
public interface RestfullFeignConfig {

    /**
     * 预览的SQL语句执行接口
     * @param dataExecute
     * @return
     */
    @PostMapping("/executeSql")
    @ResponseBody
    String executeSql(@RequestBody DataExecute dataExecute);

    /**
     * 调用根据数据源不同调用不同服务加入执行队列的接口
     * @param dataExecute
     */
    @PostMapping("/handleDataBindDetail")
    @ResponseBody
    void handleDataBindDetail(@RequestBody DataExecute dataExecute);

}
