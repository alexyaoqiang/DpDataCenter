package com.wiscom.thread;

import com.wiscom.service.WebSocketService;
import com.wiscom.utils.ApplicationContextProvider;

import java.util.concurrent.TimeUnit;

public class SubscribeManageThread implements Runnable {

    private WebSocketService webSocketService;

    @Override
    public void run() {
        //获取RedisDataService对象
        this.webSocketService = ApplicationContextProvider.getBean(WebSocketService.class);

        while(true) {
            //获取Table中的数据并广播
            webSocketService.broadData();
            try {
                //每1s推送一次数据
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
