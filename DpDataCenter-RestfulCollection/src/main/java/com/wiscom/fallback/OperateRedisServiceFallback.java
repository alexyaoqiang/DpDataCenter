package com.wiscom.fallback;

import com.wiscom.service.ApiCollectionService;
import com.wiscom.service.OperateRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 使用Feign调用redisService中的接口,调用失败时的处理类
 */
@Component
public class OperateRedisServiceFallback implements OperateRedisService {

    private static Logger log = LoggerFactory.getLogger(OperateRedisServiceFallback.class);


    @Override
    public void addTableData(String key, Map<Object, Object> map) {
        log.error("调用redis入库的微服务失败");
    }

}
