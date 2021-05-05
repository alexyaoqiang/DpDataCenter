package com.wiscom.util;
/**
* @author gavin
* @version 创建日期:2020年6月16日
*/

import com.alibaba.druid.pool.DruidDataSource;
import com.wiscom.bean.DruidDataSourceProperties;

import javax.sql.DataSource;
import java.sql.*;

//连接池的工具类
public class DruidDataSourceUtil {
    private DataSource dataSource;
    
    public DruidDataSourceUtil(DruidDataSourceProperties druidDataSourceProperties) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(druidDataSourceProperties.getUsername());
        druidDataSource.setPassword(druidDataSourceProperties.getPassword());
        druidDataSource.setUrl(druidDataSourceProperties.getJdbcUrl());
        System.out.println("=--------------------------------------");
        System.out.println(druidDataSourceProperties.getJdbcUrl());
        System.out.println("=--------------------------------------");
        druidDataSource.setDriverClassName(druidDataSourceProperties.getDriverClassName());
        if (druidDataSourceProperties.getInitialSize() != null) {
            druidDataSource.setInitialSize(druidDataSourceProperties.getInitialSize());
        }
        if (druidDataSourceProperties.getMinIdle() != null) {
            druidDataSource.setMinIdle(druidDataSourceProperties.getMinIdle());
        }
        druidDataSource.setMaxActive(druidDataSourceProperties.getMaxActive());
        druidDataSource.setMaxWait(druidDataSourceProperties.getMaxWait());
        
        dataSource = druidDataSource;
    }

    // 提供获取连接池的方法
    public DataSource getDataSource() {
        return dataSource;
    }

    // 提供获取连接的方法
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 提供关闭资源的方法【connection是归还到连接池】
    // 提供关闭资源的方法 【方法重载】3 dql
    public void closeResource(ResultSet resultSet, Statement statement, Connection connection) {
        // 关闭结果集
        // ctrl+alt+m 将java语句抽取成方法
        closeResultSet(resultSet);
        // 关闭语句执行者
        closeStatement(statement);
        // 关闭连接
        closeConnection(connection);
    }

    // 提供关闭资源的方法 【方法重载】 2 dml
    public void closeResource(Statement statement, Connection connection) {
        // 关闭语句执行者
        closeStatement(statement);
        // 关闭连接
        closeConnection(connection);
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
