package com.wiscom.mapper;


import com.wiscom.model.dppz.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface DataSourceInfoMapper {

    DataServiceNotify getDataServiceNotifyInfo();

    List<DataSource> getAllDataSourceInfo();

    List<DataExtract> getAllDataExtractInfo();

    Map<String, List<DataBindConf>> getDataBindConfInfoByIntfcId(@Param("intfcId")long intfcId);

    //查询data_bind_conf表中处于used状态的记录（其关联的数据源和坐标也需要处于used状态）
    List<DataBindConf> getAllDataBindConfInfo();

    //通过数据源id获取数据源详细信息
    DataSource getDataSourceInfoById(Long id);


}
