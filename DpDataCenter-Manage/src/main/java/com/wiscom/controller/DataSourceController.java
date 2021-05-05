package com.wiscom.controller;

import com.wiscom.service.DataSourceService;
import com.wiscom.util.ConstructRespEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据源测试与内容获取
 * 1、测试数据源连接参数是否正确
 * 2、获取数据中石油表的字段信息
 * Created by YL on 2020/10/27.
 */
@RestController
@RequestMapping(value="/dataSourceController")
public class DataSourceController {


    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 测试数据库连接是否有效
     * @param type 数据库类型
     * @param host 主机ip
     * @param port 端口号
     * @param userName 用户名
     * @param pwd 密码
     * @param service 数据库名称2
     * @return { "code": "0000", "data": true }
     */
    @GetMapping(value="/connectionTest")
    public ResponseEntity<String> connectionTest(@RequestParam("type") String type, @RequestParam("host") String host, @RequestParam("port")Integer port, @RequestParam("userName")String userName, @RequestParam("pwd")String pwd, @RequestParam("service")String service){

        Map resultMap = dataSourceService.connectionTest( type, host,  port, userName, pwd, service);

        return ConstructRespEntity.constructResponseEntity(resultMap);

    }

    /**
     * 通过数据源id获取该数据源中所有表的字段信息
     * @param id  数据源id
     * @return {"code": "0000","data": {"tableName": ["columnName1","columnName2"],}}
     */
    @GetMapping(value="/getTableData")
    public ResponseEntity<String> getTableData(Long id){

        Map resultMap = dataSourceService.getTableData(id);

        return ConstructRespEntity.constructResponseEntity(resultMap);
    }






}
