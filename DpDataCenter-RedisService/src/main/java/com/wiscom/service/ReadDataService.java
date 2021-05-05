package com.wiscom.service;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public interface ReadDataService {

    Map<Object, Map<Object,Object>> selectByKey(String key);

    public List<Map<Object, Object>> readTableData(String key);

    public List<Map<Object, Object>> readCacheTableData(String key);

    public void addCaCheTableData(Map<String, Object> map);

    public void addTableData(String key, Map<Object, Object> value);

    public List<Map<Object, Object>> readTableData1(String key);

    public List<Map<Object, Object>> readCacheTable1(String key);
}
