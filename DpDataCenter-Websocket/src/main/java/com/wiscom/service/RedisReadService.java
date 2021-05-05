package com.wiscom.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wiscom.fallback.RedisReadServiceFallback;

import java.util.List;
import java.util.Map;

/**
* @author gavin
* @version 创建日期:2020年6月12日
*/
@FeignClient(name = "DpDataCenter-RedisService",
             fallback = RedisReadServiceFallback.class)
public interface RedisReadService {

	@GetMapping("/readTableData")
	public List<Map<String, Object>> getTableInfo(@RequestParam("key") String key);

	@GetMapping("/readCacheTableData")
	public List<Map<String, Object>> getCacheTableInfo(@RequestParam("key") String key);
}
