package com.wiscom.fallback;

import com.wiscom.service.DPConfigService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
* @author gavin
* @version 创建日期:2020年6月12日
*/


@Component
public class DPConfigServiceFallback implements DPConfigService {
	@Override
	public Map<String, Object> getConfigByType(String[] type, Map<String, String> notifyMap) {
		return null;
	}

//	@Override
//	public String getConfigByType(String type) {
//		return "服务异常";
//	}


}
