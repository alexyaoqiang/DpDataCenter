package com.wiscom.fallback;

import com.wiscom.service.OperateRedisService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 使用Feign调用redisService中的接口,调用失败时的处理类
 */
@Component
public class OperateRedisServiceFallback implements OperateRedisService {

    @Override
    public void addTableData(String key, Map<Object, Object> map) {
        System.out.println("数据添加失败!");
    }

}
