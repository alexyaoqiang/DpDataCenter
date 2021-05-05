package com.wiscom.controller;

import com.wiscom.service.DataConfigService;
import com.wiscom.util.ConstructRespEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author gavin
 * @version 创建日期:2020年6月12日
 */

@RestController
public class DPConfigController {

    private static Logger log = LoggerFactory.getLogger(DPConfigController.class);

    @Autowired
    private DataConfigService dataConfigService;


    /**
     * 根据数据类型获取数据源的列表以及每个数据源的线程数
     * @param typeList  数据源类型
     * @param dataServiceNotifyMap  返回三张表最新更新时间、数据源对应线程数（已绑定）、数据源对应信息（所有）
     * @return
     */
    @PostMapping("/getConfigByType")
    public Map<String, Object> getConfigByType(@RequestParam("type") List<String> typeList,@RequestBody Map<String,String> dataServiceNotifyMap) {
        return dataConfigService.getConfigByType(typeList,dataServiceNotifyMap);
    }

    /**
     * 获取sys变量名称与别名
     * @return
     */
    @GetMapping("/getSysVariableTime")
    public ResponseEntity<String> getSysVariableTime() {
        return ConstructRespEntity.constructResponseEntity(dataConfigService.getSysVariableTime());
    }


}



