package com.wiscom.thread;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.wiscom.model.dppz.DataExecute;
import com.wiscom.service.OperateRedisService;
import com.wiscom.util.ApplicationContextProvider;
import com.wiscom.util.DBConnUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wiscom.bean.DruidDataSourceProperties;
import com.wiscom.util.DruidDataSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 采集线程对应的Runnable实例
 */
public class DataCollectionThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(DataCollectionThread.class);

    //采集项队列
    public Queue<DataExecute> sqlQueen = new ConcurrentLinkedQueue<>();

    //Redis写数据服务
    private OperateRedisService operateRedisService;

    //sql执行的工具类
    private DBConnUtils dbConnUtils;

    //连接池
    public DruidDataSourceUtil druidDataSource;

    //连接池属性
    public DruidDataSourceProperties druidDataSourceProperties;

    //数据库连接对象
    public Connection conn = null;

    //重连次数限制
    private static int i = 0;

    /**
     * 根据配置初始化连接池
     */
    public DataCollectionThread(DruidDataSourceProperties druidDataSourceProperties) {
        this.druidDataSourceProperties = druidDataSourceProperties;
        try {
            druidDataSource = new DruidDataSourceUtil(druidDataSourceProperties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ResultSet rs;

        //线程中获得Redis服务对象
        operateRedisService = ApplicationContextProvider.getBean(OperateRedisService.class);
        //线程中获得sql工具类对象
        dbConnUtils = ApplicationContextProvider.getBean(DBConnUtils.class);
        //获取ResultSet对象中列的类型和属性信息的对象
        ResultSetMetaData rsMetaData;
        //定义采集项
        DataExecute dbd;
        //采集项中获得的sql语句
        String sql;
        //json数组
        JSONArray jsonArray;
        //json对象
        JSONObject jsonObject;
        //写入redis的数据类型,key为字符串"data",value是用sql执行结果包装的json数组
        Map<Object, Object> dataMap;
        //场景id,组件id,key中的groupIndex,写入redis的key
        Set<String> keys;
        //sql查询后的结果的总列数
        int count;

        //获取数据库连接对象,每个Runnable对象只获取一次
        try {
            if (conn == null) {
                conn = druidDataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                while (sqlQueen.size() > 0) {
                    //数据库中断时,每隔20s尝试重新获取一次数据库连接
                    while (conn == null && i < 10) {
                        log.error("数据库连接失败,正在尝试重新连接...");
                        try {
                            //正常出队列
                            sqlQueen.poll();
                            //每隔20s重新获取一次数据库连接
                            conn = druidDataSource.getConnection();
                            if (conn == null) {
                                i++;
                            } else {
                                //达到连接次数10次或者重新连上时,重置连接次数
                                i = 0;
                            }
                            Thread.sleep(20000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //打印当前正在运行的线程
                    log.info(Thread.currentThread().getName() + "正在运行---队列长度:" + sqlQueen.size());
                    //获得队列头部的采集项
                    dbd = sqlQueen.poll();
                    if (dbd != null) {
                        //从采集项中获得sql语句
                        sql = dbd.getExcuteDetail();
                        if (sql != null) {
                            try {
                                //开始时间
                                long begin = System.currentTimeMillis();
                                //调用工具类执行sql语句
                                rs = dbConnUtils.executeSql(conn, sql);
                                if (rs == null) {
                                    //执行结果为空时,直接跳出当次循环,进行下次循环
                                    log.error("IntfcId:" + dbd.getIntfcId() + ",sql语句:" + sql + "执行异常");
                                    continue;
                                }
                                //结束时间
                                long end = System.currentTimeMillis();
                                //sql语句执行时间超过3s时,打印出来
                                if ((end - begin) > 3000) {
                                    log.info("执行时间超过3s的sql---:" + sql);
                                }
                                //处理结果集
                                rsMetaData = rs.getMetaData();
                                //获取数据库总列数
                                count = rsMetaData.getColumnCount();
                                //初始化json数据
                                jsonArray = new JSONArray();
                                //初始化存入redis的集合
                                dataMap = new HashMap<>();
                                //取出Map<String, String>中的key
                                keys = dbd.getComponentFixColumnMap().keySet();
                                //遍历sql执行的结果集
                                while (rs.next()) {
                                    //遍历结果中的每一行数据,初始化json对象
                                    jsonObject = new JSONObject();
                                    for (int i = 1; i <= count; i++) {
                                        //每个json对象存数据的一行
                                        jsonObject.put(rsMetaData.getColumnLabel(i), rs.getObject(i));
                                    }
                                    //json数组存所有的行数据
                                    jsonArray.put(jsonObject);
                                }
                                //打印sql执行结果
                                log.info("IntfcId:" + dbd.getIntfcId() + ":" + sql + "执行结果长度为:" + jsonArray.length());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                for (String key : keys) {
                                    String[] str = key.split(":");
                                    //将最终的json数组存入集合中
                                    dataMap.put("data", jsonArray.toString());
                                    dataMap.put("SCENE_ID", str[0]);
                                    //dataMap.put("CID", str[1]);
                                    dataMap.put("componentCode", str[1]);
                                    dataMap.put("group", str[2]);
                                    dataMap.put("ctime", sdf.format(new Date()));
                                    //调用redis服务进行写入
                                    operateRedisService.addTableData(key, dataMap);
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                                log.error(e.getMessage());
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                //每隔20ms执行一次
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
