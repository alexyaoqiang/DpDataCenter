package com.wiscom.service;

import java.util.Map;

public interface DataSourceService {

    Map getTableData(Long id);

    Map connectionTest(String type,String host, Integer port,String userName,String pwd,String service);
}
