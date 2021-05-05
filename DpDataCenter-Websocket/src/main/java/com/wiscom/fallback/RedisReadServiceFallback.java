package com.wiscom.fallback;

import org.springframework.stereotype.Component;

import com.wiscom.service.RedisReadService;

import java.util.List;
import java.util.Map;

/**
* @author gavin
* @version 创建日期:2020年6月12日
*/

@Component
public class RedisReadServiceFallback implements RedisReadService{

	@Override
	public List<Map<String, Object>> getTableInfo(String key) {
		return null;
	}

	@Override
	public List<Map<String, Object>> getCacheTableInfo(String key) {
		return null;
	}
}
