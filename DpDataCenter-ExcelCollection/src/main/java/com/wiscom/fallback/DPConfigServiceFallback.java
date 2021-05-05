package com.wiscom.fallback;

import org.springframework.stereotype.Component;

import com.wiscom.service.DPConfigService;

/**
* @author gavin
* @version 创建日期:2020年6月12日
*/


@Component
public class DPConfigServiceFallback implements DPConfigService {
	
	@Override
	public String getConfigByType(String type) {
		return "服务消费端::降级";
	}

}
