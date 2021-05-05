package com.wiscom.config;

import com.wiscom.model.dppz.DataExecute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 从Nacos发现服务的Feign配置接口(dpDataCenter_dbCollection)
 * Created by YL on 2020/7/13.
 */
@FeignClient(name = "DpDataCenter-DbCollection",
        fallback = CollectionRemoteHystrix.class)
public interface CollectionFeignConfig {

    /**
     * 预览的SQL语句执行接口
     * @param id
     * @param sql
     * @return
     */
    @GetMapping("/executeSql")
    @ResponseBody
    List executeSql(@RequestParam("id") String id, @RequestParam("sql") String sql);

    /**
     * 调用根据数据源不同调用不同服务加入执行队列的接口
     * @param dataExecute
     */
    @PostMapping("/handleDataBindDetail")
    @ResponseBody
    void handleDataBindDetail(@RequestBody DataExecute dataExecute);

}
