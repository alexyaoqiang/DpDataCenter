package com.wiscom.service;
/**
* @author gavin
* @version 创建日期:2020年6月12日
*/

import com.wiscom.fallback.DPConfigServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "DpDataCenter-Manage",
             fallback = DPConfigServiceFallback.class)
public interface DPConfigService {

	@PostMapping("/getConfigByType")
	@ResponseBody
	Map<String, Object> getConfigByType(@RequestParam("type") String[] type, @RequestBody Map<String, String> notifyMap);
}
