package com.wiscom;

import com.wiscom.inteceptor.WebSocketChannelInterceptor;
import com.wiscom.service.WebSocketService;
import com.wiscom.thread.SubscribeManageThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients 
@SpringCloudApplication
@EnableDiscoveryClient
public class WebSocketApplication {
    public static Logger logger = LoggerFactory.getLogger("customLog");

	private static Logger log = LoggerFactory.getLogger(WebSocketApplication.class);
	
    public static void main( String[] args ) {
    	SpringApplication.run(WebSocketApplication.class,args);
        //获取数据线程启动
        SubscribeManageThread subscribeManageThread = new SubscribeManageThread();
        Thread t = new Thread(subscribeManageThread);
        t.start();
    	log.info("WebSocketApplication started!");

    	while (true) {
    	    //打印ip地址对应的session集合
            WebSocketService.ipMap.forEach((key, value) -> {log.info("ip:" + key + "---session集合:" + value.toString());});
            //打印sessionId下对应的组件个数
            WebSocketService.sessionMap.forEach((key, value) -> {log.info("sessionId:" + key + "下的组件个数为" + value.size());});
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
