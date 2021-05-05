package com.wiscom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.wiscom.thread.DBConfigThread;

import java.util.concurrent.TimeUnit;

@EnableFeignClients
@SpringCloudApplication
public class DBCollectionApplication {

    public static Logger logger = LoggerFactory.getLogger("customLog");

    private static Logger log = LoggerFactory.getLogger(DBCollectionApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DBCollectionApplication.class,args);
        log.info("DBCollectionApplication started!");

        //启动DBConfigThread
        DBConfigThread runnable = new DBConfigThread();
        Thread thread = new Thread(runnable);
        thread.start();

        //启动ExecuteSQLThread
//        ExecuteSQLThread executeSQLRunnable = new ExecuteSQLThread();
//        Thread executeSQLThread = new Thread(executeSQLRunnable);
//        executeSQLThread.start();

        //监控DBConfigThread,若中断,则重新启动
        while (true) {
            //监控线程状态
            if (thread.getState().equals(Thread.State.TERMINATED)) {
                thread.start();
                log.info("DBConfigThread线程重启!");
            }
            try {
                //每隔5s监测一次DBConfigThread
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //log.info("DBConfigThread线程存活着!");
        }

    }
}
