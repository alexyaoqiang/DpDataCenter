package com.wiscom.thread;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.service.DBCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wiscom.bean.DruidDataSourceProperties;
import com.wiscom.enumeration.ProcessEnum;
import com.wiscom.model.dppz.DataServiceNotify;
import com.wiscom.model.dppz.DataSource;
import com.wiscom.service.DPConfigService;
import com.wiscom.util.ApplicationContextProvider;

/**
 * 线程处理类
 */
public class DBConfigThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(DBConfigThread.class);

    //数据源对应的Runnable实例,key为数据源id,value为数据源对应的Runnable实例
    public static Map<String, DataCollectionThread> runnableMap = new HashMap<>();

    //数据源采集线程,key为数据源id,value为数据源对应线程集合
    public static Map<String, List<Thread>> dataCollectionThread = new HashMap<>();

    //所有的数据源的List集合
    //public static List<DataSource> dataSourceList = new ArrayList<>();

    //本地缓存的所有数据源的Map集合
    public static Map<String, Integer> dataSourceMap = new HashMap<>();

    //数据源类型数组
    private String[] type = {"mysql", "sqlserver", "postgresql"};

    //单位时间
    private final int UNIT_INTERVAL = 5;

    //当前执行周期
    private int count = 0;

    //notifyMap
    Map<String, String> notifyMap = new HashMap<>();

    //notify对象
    DataServiceNotify notify = new DataServiceNotify();

    //获取采集配置服务
    private DPConfigService dpConfigService;

    //提供接口的业务类
    private DBCollectionService dbCollectionService;

    //根据manage接口获取到的数据源id对应的线程数,key为数据源id,value为数据源对应线程数
    Map<String, Integer> dstcMap = new HashMap<>();

    //本地缓存的所有数据源对象
    public static Map<String, DataSource> dsMap = new HashMap<>();

    //传入的新数据源对象
    public static Map<String, DataSource> dsMap_new = new HashMap<>();

    @Override
    public void run() {

        //线程中获得dpConfigService对象
        dpConfigService = ApplicationContextProvider.getBean(DPConfigService.class);
        //线程中获得dbCollectionService对象
        dbCollectionService = ApplicationContextProvider.getBean(DBCollectionService.class);

        while (true) {
            try {
                //获取最新数据采集配置
                getDBConfig();
                //DataCollection数据采集线程状态监测
                //monitorThreadState();
                //打印Runnable实例下的线程集合
                dataCollectionThread.forEach((key, value) -> {
                    log.info("Runnable实例:" + key + "对应的线程集合" + value.toString());
                });
                //执行周期从0开始
                count++;
                count = count % Integer.MAX_VALUE;
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }

            try {
                //每隔5s创建采集线程执行sql
                TimeUnit.SECONDS.sleep(UNIT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取最新数据采集配置
     */
    private void getDBConfig() {
        //判断数据源是否变化的标识
        boolean flag = false;

        //请求状态码
        int code = 0;

        //调用manage接口的getConfigType
        //Map<String, Object> dbMap = dpConfigService.getConfigByType(type, notify.getDsUpdate(), notify.getIntfcUpdate(), notify.getBindUpdate());
        Map<String, Object> dbMap = dpConfigService.getConfigByType(type, notifyMap);

        if (dbMap != null && dbMap.size() >= 2) {
            //rpc远程调用,获得的类型需强制转换
            ObjectMapper mapper1 = new ObjectMapper();
            //根据manage接口获取DataServiceNotify对象,并更新原有的DataServiceNotify对象
            notify = mapper1.convertValue(dbMap.get("dataServiceNotify"), DataServiceNotify.class);
            notifyMap = new HashMap<>();
            notifyMap.put("dsUpdate", notify.getDsUpdate());
            notifyMap.put("intfcUpdate", notify.getIntfcUpdate());
            notifyMap.put("bindUpdate", notify.getBindUpdate());
            //根据manage接口获取code状态码
            code = (int) dbMap.get("code");
        }

        //根据code判断请求是否成功,成功时为1001
        if (code == ProcessEnum.EXECUTE_SUCCESS.getCode()) {
            //获取数据源id对应的线程数
            dstcMap = (Map<String, Integer>) dbMap.get("threadNum");
            //只传入code和notify对象时,直接返回
            if (dstcMap == null) {
                return;
            }

            //传入dataSourceMap和threadNumList不为空
            if (dbMap.get("dataSource") != null && dstcMap != null) {
                //第一次请求时
                if (dsMap.size() == 0) {
                    //获取到的所有的数据源信息Map<String, DataSource>
                    dsMap = (Map<String, DataSource>) dbMap.get("dataSource");
                    dsMap_new = (Map<String, DataSource>) dbMap.get("dataSource");
                    //新增线程
                    updateDataSource(dstcMap);
                    //传入的新增数据源集合不为空时,更新本地缓存
                    if (dstcMap != null && dstcMap.size() > 0) {
                        //更新本地存储的数据源id集合
                        dataSourceMap = (Map<String, Integer>) dbMap.get("threadNum");
                    }
                }
                dsMap_new = (Map<String, DataSource>) dbMap.get("dataSource");
                //判断datasource是否发生变化,old>=new
                if (dataSourceMap.size() >= dstcMap.size()) {
                    for (String id : dataSourceMap.keySet()) {
                        DataSource oldDatasource = null;
                        DataSource newDatasource = null;
                        //判断本地缓存的老数据源
                        ObjectMapper mapper = new ObjectMapper();
                        oldDatasource = mapper.convertValue(dsMap.get(id), DataSource.class);
                        //判断传入的新数据源
                        if (dstcMap.containsKey(id)) {
                            ObjectMapper mapper1 = new ObjectMapper();
                            newDatasource = mapper1.convertValue(dsMap_new.get(id), DataSource.class);
                        }
                        //新数据源中不存在或者老数据源发生了变化
                        if (newDatasource == null || !(oldDatasource.identify().equals(newDatasource.identify()))) {
                            flag = true;
                            break;
                        }
                    }
                } else {
                    //old<new
                    for (String id : dstcMap.keySet()) {
                        DataSource oldDatasource = null;
                        DataSource newDatasource = null;
                        //遍历本地缓存的老数据源
                        if (dataSourceMap.containsKey(id)) {
                            ObjectMapper mapper1 = new ObjectMapper();
                            oldDatasource = mapper1.convertValue(dsMap.get(id), DataSource.class);
                        }
                        //遍历传入的新数据源dsList
                        if (dstcMap.containsKey(id)) {
                            ObjectMapper mapper1 = new ObjectMapper();
                            newDatasource = mapper1.convertValue(dsMap_new.get(id), DataSource.class);
                        }
                        //旧数据源已不存在或者数据源发生了变化
                        if (oldDatasource == null || !(oldDatasource.identify().equals(newDatasource.identify()))) {
                            flag = true;
                            break;
                        }
                    }
                }
                //数据源发生变化
                if (flag) {
                    //更新数据源
                    updateDataSource(dstcMap);
                    //更新本地缓存的数据源
                    dataSourceMap = (Map<String, Integer>) dbMap.get("threadNum");
                    //更新本地缓存的数据源id对应的DataSource集合
                    dsMap = (Map<String, DataSource>) dbMap.get("dataSource");
                }
            } else {
                //数据源不发生变化,则管理数据源下的线程数,新增、减少或不变
                manageThreadCount(dstcMap);
            }

        } else if (code == ProcessEnum.EXECUTE_FAIL.getCode()) {
            log.error("request getConfigByType error:" + ProcessEnum.EXECUTE_FAIL.getMessage());
        } else {
            log.error("request getConfigByType unknown code:" + code);
        }
    }


    /**
     * 更新数据源
     */
    private void updateDataSource(Map<String, Integer> dstcMap) {
        //本地存储的旧数据源对象
        DataSource oldDs;
        //更新传入的新的数据源对象
        DataSource newDs;
        //数据源下的线程集合
        List<Thread> threadList;
        //线程实例
        Thread tempThread;
        //Runnable实例
        DataCollectionThread runnable = null;
        //线程实例
        Thread thread;
        //数据库连接地址
        String jdbcUrl;
        //连接池配置
        DruidDataSourceProperties druidDataSourceProperties;

        //遍历正在运行的采集线程对象
        for (String id : runnableMap.keySet()) {
            oldDs = new DataSource();
            newDs = new DataSource();
            //旧数据源dataSourceMap
            ObjectMapper mapper = new ObjectMapper();
            oldDs = mapper.convertValue(dsMap.get(id), DataSource.class);
            //新数据源dsMap
            if (dstcMap.containsKey(id)) {
                ObjectMapper mapper1 = new ObjectMapper();
                newDs = mapper1.convertValue(dsMap_new.get(id), DataSource.class);
            }
            //已无该数据源,或数据源发生变化identity
            if (newDs == null || !(oldDs.identify().equals(newDs.identify()))) {
                //获得该数据源id下的所有采集线程集合
                threadList = dataCollectionThread.get(id);
                //中断该数据源id下的所有采集线程
                if (threadList != null) {
                    //中断线程
                    for (int i = 0; i < threadList.size(); i++) {
                        tempThread = threadList.get(i);
                        if (tempThread != null) {
                            try {
                                tempThread.interrupt();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                //从线程缓存中踢出该数据源引用对象
                runnableMap.remove(id);
                //数据源id对应的采集线程集合中移除该数据源
                this.dataCollectionThread.remove(id);
            }
            //无变化
            else {
                //将数据源从dstcMap中移除,无需重新创建线程
                Iterator it = dstcMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if (id.equals(key)) {
                        it.remove();
                    }
                }
            }
        }

        //新增数据源线程,遍历需要创建线程的dsList
        for (String id : dstcMap.keySet()) {
            if (dstcMap.containsKey(id)) {
                //根据数据源id获取dataSource对象
                ObjectMapper mapper1 = new ObjectMapper();
                DataSource dataSource = mapper1.convertValue(dsMap_new.get(id), DataSource.class);
                //打印日志
                log.info(dataSource.toString());
                jdbcUrl = dbCollectionService.getUrl(dataSource);
                if (jdbcUrl.equals("")) {
                    continue;
                }
                log.info(dataSource.toString() + "上线");
                //根据数据源配置创建连接池
                druidDataSourceProperties = new DruidDataSourceProperties();
                druidDataSourceProperties.setJdbcUrl(jdbcUrl);
                druidDataSourceProperties.setUsername(dataSource.getUserName());
                druidDataSourceProperties.setPassword(dataSource.getPwd());
                //获取Runnable实例
                runnable = new DataCollectionThread(druidDataSourceProperties);
                runnableMap.put(id, runnable);

                //根据计算的线程数创建线程
                if (dstcMap != null && dstcMap.size() != 0) {
                    for (int j = 0; j < dstcMap.get(id); j++) {
                        thread = new Thread(runnable, "线程" + id + "_" + j);
                        //启动线程
                        thread.start();
                        //获取该数据源Id对应的线程集合
                        threadList = dataCollectionThread.get(id);
                        //若该数据源下没有线程,则新创建一个集合
                        if (threadList == null) {
                            threadList = new ArrayList<>();
                            //更新数据源对应的线程集合
                            dataCollectionThread.put(id, threadList);
                        }
                        //将创建的线程加入集合中
                        threadList.add(thread);
                    }
                }
            }
        }
    }

    /**
     * 管理数据源id对应的线程数,新增、减少或者不变
     *
     * @param dstcMap
     */
    public void manageThreadCount(Map<String, Integer> dstcMap) {
        //新传进来的数据源id对应线程数
        Integer newCount;
        //本地缓存的数据源id对应线程数
        Integer oldCount;
        //已创建的数据源id对应的线程集合
        List<Thread> threadList;

        for (String id : dstcMap.keySet()) {
            newCount = dstcMap.get(id);
            threadList = dataCollectionThread.get(id);
            oldCount = threadList.size();
            //获取Runnable实例
            DataCollectionThread runnable = runnableMap.get(id);
            if (newCount == oldCount) {
                //线程数不变
                continue;
            } else if (newCount > oldCount) {
                //新增线程
                int count = newCount - oldCount;
                Thread thread;
                for (int i = 0; i < count; i++) {
                    thread = new Thread(runnable, "线程" + id + "_" + (oldCount + i + 1));
                    //启动线程
                    thread.start();
                    //加入线程列表中
                    threadList.add(thread);
                }
            } else {
                //减少线程,先中断该数据源id下的所有采集线程,再创建线程
                if (threadList != null) {
                    for (int i = 0; i < threadList.size(); i++) {
                        Thread tempThread = threadList.get(i);
                        if (tempThread != null) {
                            tempThread.interrupt();
                        }
                    }
                }
                //重新创建线程
                for (int i = 0; i < newCount; i++) {
                    Thread thread = new Thread(runnable, "线程" + id + "_" + (oldCount + i));
                    //启动线程
                    thread.start();
                    //加入线程列表中
                    threadList.add(thread);
                }
            }
        }
    }

    /**
     * 线程状态监测
     */
    public void monitorThreadState() {
        dataCollectionThread.forEach((key, value) -> {
            List<Thread> listThread = dataCollectionThread.get(key);
            //线程中断时,重新创建线程并启动
            for (Thread thread : listThread) {
                if (!thread.isAlive()) {
                    thread.start();
                }
            }
        });
    }

}