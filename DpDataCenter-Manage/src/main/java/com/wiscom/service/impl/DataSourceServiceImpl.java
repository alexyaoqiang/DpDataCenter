package com.wiscom.service.impl;

import com.wiscom.ManageApplication;
import com.wiscom.mapper.DataSourceInfoMapper;
import com.wiscom.model.dppz.DataSource;
import com.wiscom.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    //日志输出
    private static Logger log = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private DataSourceInfoMapper dataSourceInfoMapper;

    /**
     * 测试数据源是否正确
     * @param type
     * @param host
     * @param port
     * @param userName
     * @param pwd
     * @param service
     * @return
     */
    @Override
    public Map connectionTest(String type,String host, Integer port,String userName,String pwd,String service){

        ManageApplication.logger.info("数据库连接测试接口调用！");

        ManageApplication.logger.info("type="+type);
        ManageApplication.logger.info("host="+host);
        ManageApplication.logger.info("port="+port);
        ManageApplication.logger.info("userName="+userName);
        ManageApplication.logger.info("pwd="+pwd);
        ManageApplication.logger.info("service="+service);

        //返回内容
        Map resultMap  = new HashMap<>();

        Map<String,String> map = getClassNameAndUrl(type, host,port.toString(), service);

        String url=map.get("url");
        String className=map.get("className");

        if(url==null || className==null){

            log.error("该接口暂不支持"+type+"数据库连接测试！");

            resultMap.put("code","0130");
            resultMap.put("data","The database type is not support");

            return resultMap;

        }

        Connection connection = null;
        try {

            Class.forName(className);
            connection = DriverManager.getConnection(url, userName,pwd);

        } catch (Exception e) {

            log.error(e.getMessage());
            e.printStackTrace();

            resultMap.put("code","0130");
            resultMap.put("data",e.getMessage());

            return resultMap;

        }finally {
            try {
                if(connection!=null){
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        ManageApplication.logger.info("返回值=true");

        resultMap.put("code","0000");
        resultMap.put("data",true);

        return resultMap;

    }


    /**
     * 获取数据中所有表的字段信息
     * @param id
     * @return
     */
    @Override
    public Map getTableData(Long id){

        ManageApplication.logger.info("数据库表字段信息获取接口调用! ");

        ManageApplication.logger.info("数据源id="+id);

        //返回内容
        Map resultMap  = new HashMap<>();

        DataSource dataSource =  dataSourceInfoMapper.getDataSourceInfoById(id);

        if(dataSource == null){

            log.error("data_source表中获取不到该数据源id对应的数据源连接信息");

            resultMap.put("code","0130");
            resultMap.put("data","Cannot get info of the datasource id");

            return resultMap;

        }

        Map<String,String> map = getClassNameAndUrl(dataSource.getType(), dataSource.getHost(),dataSource.getPort(),dataSource.getService());
        String url=map.get("url");
        String className=map.get("className");
        String sql = map.get("sql");

        if(url==null || className==null || sql==null){

            log.error("该接口暂不支持  "+dataSource.getType()+"  "+dataSource.getHost()+":"+dataSource.getPort()+"/"+dataSource.getService()+"  此数据库的表字段信息获取！");

            resultMap.put("code","0130");
            resultMap.put("data","The database type is not support");

            return resultMap;

        }

        Map<String,List<String>> tableInfoMap = new HashMap<>();

        Connection connection=null;
        Statement statement = null;
        ResultSet resultSet =null;

        try {
            Class.forName(className);
            connection = DriverManager.getConnection(url, dataSource.getUserName(),dataSource.getPwd());
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);


            while (resultSet.next()) {

                String tableName = resultSet.getString("table_name");
                String columnName = resultSet.getString("column_name");

                if(!tableInfoMap.containsKey(tableName)){
                    tableInfoMap.put(tableName,new ArrayList<>());
                }

                tableInfoMap.get(tableName).add(columnName);

            }

        } catch (Exception e) {

            e.printStackTrace();
            log.error(e.getMessage());

            resultMap.put("code","0130");
            resultMap.put("data",e.getMessage());

            return resultMap;

        }finally {
            if(resultSet!=null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement!=null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        ManageApplication.logger.info("返回值="+tableInfoMap.toString());

        resultMap.put("code","0000");
        resultMap.put("data",tableInfoMap);

        return resultMap;
    }

    /**
     * 通过数据库类型组装获取数据库驱动名称、数据库连接url
     * @param type
     * @param host
     * @param port
     * @param service
     * @return
     */
    private Map<String,String> getClassNameAndUrl(String type, String host,String port, String service){

        Map<String,String> map =new HashMap<>();

        //数据库连接
        String url=null;
        String className=null;
        String sql = null;

        switch (type.toLowerCase()) {
            case "mysql":
                className = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:" + type.toLowerCase() + "://" + host + ":" + port + "/" + service + "?serviceTimezone=UTC&useOldAliasMetadataBehavior=true";
                sql = "SELECT table_name,column_name FROM information_schema.columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            case "postgresql":
                className = "org.postgresql.Driver";
                url = "jdbc:" + type.toLowerCase() + "://" + host + ":" + port + "/" + service;
                sql = "SELECT table_name,column_name FROM information_schema.columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            case "sqlserver":
                className = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                url = "jdbc:" + type.toLowerCase() + "://" + host + ":" + port + ";" + "DatabaseName=" + service;
               sql = "SELECT table_name,column_name FROM information_schema.columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            case "oracle":
                className = "oracle.jdbc.driver.OracleDriver";
                url = "jdbc:" + type.toLowerCase() + ":thin:@" + host + ":" + port + ":" + service;
               sql = "SELECT table_name,column_name FROM user_table_columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            case "kingbase":
                className = "com.kingbase.Driver";
                url = "jdbc:" + type.toLowerCase() + "://" + host + ":" + port + "/" + service;
                sql = "SELECT table_name,column_name FROM information_schema.columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            case "dm":
                className = "dm.jdbc.driver.DmDriver";
                url = "jdbc:" + type.toLowerCase() + "://" + host + ":" + port + "/" + service;
                sql = "SELECT table_name,column_name FROM information_schema.columns where table_schema='"+service+"' order by table_name asc,column_name asc ";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type.toLowerCase());
        }

        map.put("className",className);
        map.put("url",url);
        map.put("sql",sql);

        return map;
    }


}
