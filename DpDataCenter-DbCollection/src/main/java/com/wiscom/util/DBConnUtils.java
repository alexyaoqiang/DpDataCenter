package com.wiscom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.bean.DruidDataSourceProperties;
import com.wiscom.model.dppz.DataSource;
import com.wiscom.service.DBCollectionService;
import com.wiscom.thread.DBConfigThread;
import com.wiscom.thread.DataCollectionThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@Component
public class DBConnUtils {

    //执行sql超时时间
    private int statementTimeOut = 3;

    @Autowired
    private DBCollectionService dbCollectionService;

    // 根据conn和sql执行sql语句
    public ResultSet executeSql(Connection conn, String sql) {

        Statement st;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.setQueryTimeout(statementTimeOut);
            rs = st.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    //根据数据源获取数据库连接对象
    public Connection getConn(String id) {

        //数据库连接地址
        String jdbcUrl = "";
        //连接池配置
        DruidDataSourceProperties druidDataSourceProperties;
        //获取本地缓存的对象
        Map<String, DataSource> dsMap;
        //获取本地缓存的对象
        Map<String, DataSource> dsMap_new;
        //数据库对象
        DataSource dataSource;
        //线程对象
        DataCollectionThread dataCollectionThread;
        //连接池
        DruidDataSourceUtil druidDataSource;
        //数据库连接对象
        Connection conn = null;

        //获取缓存的数据库对象
        dsMap = DBConfigThread.dsMap;
        dsMap_new = DBConfigThread.dsMap_new;
        //获取缓存的数据库对象
        ObjectMapper mapper = new ObjectMapper();
        dataSource = mapper.convertValue(dsMap.get(id), DataSource.class);
        //若获取不到,则获取最新的缓存对象
        if (dataSource == null) {
            ObjectMapper mapper1 = new ObjectMapper();
            dataSource = mapper1.convertValue(dsMap_new.get(id), DataSource.class);
        }
        //获取数据库连接地址
        jdbcUrl = dbCollectionService.getUrl(dataSource);
        //根据数据源配置创建连接池
        druidDataSourceProperties = new DruidDataSourceProperties();
        druidDataSourceProperties.setJdbcUrl(jdbcUrl);
        druidDataSourceProperties.setUsername(dataSource.getUserName());
        druidDataSourceProperties.setPassword(dataSource.getPwd());
        try {
            druidDataSource = new DruidDataSourceUtil(druidDataSourceProperties);
            conn = druidDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
