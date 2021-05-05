package com.wiscom.service;

import java.util.Map;

public interface DataPreViewService {

    //替换坐标的参数，调用collection服务获取预览结果
    Map<String,Object> viewData(Map<String,Object> param);

}
