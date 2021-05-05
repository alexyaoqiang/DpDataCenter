package com.wiscom.service;

import java.util.Map;

import com.wiscom.model.dppz.DataServiceNotify;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.wiscom.fallback.DPConfigServiceFallback;

/**
 * 使用Feign调用manage中的接口
 */
@FeignClient(name = "DpDataCenter-Manage",
        fallback = DPConfigServiceFallback.class)
public interface DPConfigService {

    @PostMapping("/getConfigByType")
    @ResponseBody
    Map<String, Object> getConfigByType(@RequestParam("type") String[] type, @RequestBody Map<String, String> notifyMap);

}
