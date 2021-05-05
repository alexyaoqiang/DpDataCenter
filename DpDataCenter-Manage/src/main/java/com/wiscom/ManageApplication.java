package com.wiscom;


import com.wiscom.constant.MappingConstant;
import com.wiscom.thread.CollectionDispatcherRunnable;
import com.wiscom.thread.LogExportRunnable;
import com.wiscom.thread.MonitorRunnable;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.wiscom.mapper")
@SpringBootApplication
public class ManageApplication {

    private static Logger log = LoggerFactory.getLogger(ManageApplication.class);

    public static Logger logger = LoggerFactory.getLogger("customLog");


    public static void main(String[] args) {

        ApplicationContext context =  SpringApplication.run(ManageApplication.class,args);

        log.info("ManageApplication started-teat!");

        /* 初始化 */
        init();

        /* 指标、数据源、绑定关系采集线程 */
        MonitorRunnable monitor = (MonitorRunnable) context.getBean("monitorRunnable");
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();

        /* 运行状态分析与日志输出线程 */
        LogExportRunnable logExportRunnable = (LogExportRunnable) context.getBean("logExportRunnable");
        Thread logExportThread = new Thread(logExportRunnable);
        logExportThread.start();

         /* 采集项调度线程线程 */
        CollectionDispatcherRunnable collectionDispatcherRunnable = (CollectionDispatcherRunnable) context.getBean("collectionDispatcherRunnable");
        Thread CollectionDispatcherThread = new Thread(collectionDispatcherRunnable);
        CollectionDispatcherThread.start();

        //监控DBConfigThread,若中断,则重新启动
        while (true) {

            if (!monitorThread.isAlive()) {
                monitorThread.start();
                log.info("Monitor线程重启");
            }

            if (!logExportThread.isAlive()){
                logExportThread.start();
                log.info("LogExport线程重启");
            }

            if (!CollectionDispatcherThread.isAlive()){
                CollectionDispatcherThread.start();
                log.info("CollectionDispatcher线程重启");
            }
            try {
                //每隔5s监测一次DBConfigThread
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 初始化：
     * 1、设置数据源类型与调用采集服务的映射关系
     */
    private static void init(){
        MappingConstant.typeToServiceMapping.put("mysql","dpDataCenter-dbCollection");
        MappingConstant.typeToServiceMapping.put("oracle","dpDataCenter-dbCollection");
        MappingConstant.typeToServiceMapping.put("postgresql","dpDataCenter-dbCollection");
        MappingConstant.typeToServiceMapping.put("sqlserver","dpDataCenter-dbCollection");
        MappingConstant.typeToServiceMapping.put("restapi","dpDataCenter-restfulCollection");
    }


}
