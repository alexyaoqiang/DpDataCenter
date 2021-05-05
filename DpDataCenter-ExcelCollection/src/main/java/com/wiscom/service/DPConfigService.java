package com.wiscom.service;
/**
* @author gavin
* @version 创建日期:2020年6月12日
*/

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wiscom.fallback.DPConfigServiceFallback;

@FeignClient(name = "dpDataCenter-manage",
             fallback = DPConfigServiceFallback.class)
public interface DPConfigService {
	
	@GetMapping("/getConfigByType")
	public String getConfigByType(@RequestParam("type") String type);

}
