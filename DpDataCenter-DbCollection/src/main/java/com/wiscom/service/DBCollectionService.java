package com.wiscom.service;

import com.wiscom.model.dppz.DataExecute;
import com.wiscom.model.dppz.DataSource;
import com.wiscom.thread.DBConfigThread;
import com.wiscom.thread.DataCollectionThread;
import com.wiscom.util.DBConnUtils;
import com.wiscom.util.DruidDataSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Service
public class DBCollectionService {

    private static Logger log = LoggerFactory.getLogger(DBCollectionService.class);

    @Autowired
    private DBConnUtils dbConnUtils;

    /**
     * 数据预览,根据数据源id以及sql语句执行sql
     */
    public List<Map<String,Object>> executeSql(String id, String sql) {

        //数据库连接对象
        Connection conn = null;

        //初始化结果集
        ResultSet rs;

        //获取Runnable集合,根据数据源id对应的Runnable实例获取数据库连接对象conn
        try {
            //若获取不到连接,创建临时连接对象
            if (DBConfigThread.runnableMap.get(id) == null) {
                //根据工具类获取数据库连接对象
                conn = dbConnUtils.getConn(id);
            } else {
                //根据缓存获取数据库连接对象
                DataCollectionThread dataCollectionThread = DBConfigThread.runnableMap.get(id);
                conn = dataCollectionThread.conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //执行sql语句
        rs = dbConnUtils.executeSql(conn, sql);
        List<Map<String,Object>> list= new ArrayList<>();
        int count = 0;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            count = rsmd.getColumnCount();

            while (rs.next()) {
                Map map = new HashMap<String, Object>();

                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i+1), rs.getObject(i+1));
                }

                list.add(map);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 采集项加入采集队列
     */
    public void handleDataBindDetail(DataExecute dataExecute) {

        //System.out.println(dataExecute.getIntfcId());

        //初始化数据源Id
        String dsId = "";

        //初始化采集队列
        Queue<DataExecute> sqlQueen;

        //存放Runnable对象的Map,key为数据源Id,value为数据源对应的Runnable对象
        Map<String, DataCollectionThread> runnableMap;

        //获取数据源Id
        dsId = dataExecute.getDsId();

        //判断Runnable集合中是否包含该数据源,若包含该数据源,则进行采集项入队操作
        if (DBConfigThread.runnableMap.containsKey(dsId)) {
            //获取数据源对应的采集队列
            runnableMap = DBConfigThread.runnableMap;
            //根据数据源Id获取线程对应的采集队列
            sqlQueen = runnableMap.get(dsId).sqlQueen;
            //将传入的数据封装成采集项实体类并加入采集队列
            sqlQueen.add(dataExecute);
        }

    }

    /**
     * 根据数据源获取url
     */
    public String getUrl(DataSource ds) {

        //初始化数据库连接地址
        String url = "";

        switch (ds.getType().toLowerCase()) {
            case "sqlserver":
                url = "jdbc:" + ds.getType().toLowerCase() + "://" + ds.getHost() + ":" + ds.getPort() + ";" + "DatabaseName="
                        + ds.getService();
                break;
            case "postgresql":
                url = "jdbc:" + "postgresql" + "://" + ds.getHost() + ":" + ds.getPort() + "/" + ds.getService();
                break;
            case "oracle":
                url = "jdbc:" + "oracle" + ":thin:@//" + ds.getHost() + ":" + ds.getPort() + "/" + ds.getService();
                break;
            case "dm":
            case "mysql":
            case "kingbase":
                url = "jdbc:" + ds.getType().toLowerCase() + "://" + ds.getHost() + ":" + ds.getPort() + "/" + ds.getService() + "?serviceTimezone=UTC&useOldAliasMetadataBehavior=true";
                break;
        }
        return url;
    }

}
