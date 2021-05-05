package com.wiscom.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface DataConfigService {

    //获取数据源的配置信息（连接信息、线程数）
    Map<String, Object> getConfigByType(List<String> typeList, Map<String,String> dataServiceNotifyMap);

    Map<String, Object> getSysVariableTime();

}
