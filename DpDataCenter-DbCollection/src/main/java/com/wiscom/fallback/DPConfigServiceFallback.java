package com.wiscom.fallback;

import java.util.HashMap;
import java.util.Map;

import com.wiscom.model.dppz.DataServiceNotify;
import org.springframework.stereotype.Component;
import com.wiscom.service.DPConfigService;

/**
 * 使用Feign调用manage中的接口,调用失败时的处理类
 */
@Component
public class DPConfigServiceFallback implements DPConfigService {

    @Override
    public Map<String, Object> getConfigByType(String[] type, Map<String, String> notifyMap) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("status", "无法获取数据采集配置");
        return retMap;
    }

}
