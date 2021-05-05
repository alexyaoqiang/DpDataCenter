package com.wiscom.config;

import com.wiscom.inteceptor.HttpHandShakeIntecepter;
import com.wiscom.inteceptor.WebSocketChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker //开启消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册端点,发布或者订阅消息的时候需要连接此端点
     * setAllowedOrigins非必须,*表示允许其他域进行连接
     * withSockJS表示开启socketjs支持
     */
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/v1.0")
                .addInterceptors(new HttpHandShakeIntecepter())
                .setAllowedOrigins("*").withSockJS();
    }


//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//
//        // 自定义心跳线程调度器，用于控制心跳线程
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        // 线程池线程数，心跳连接开线程
//        taskScheduler.setPoolSize(1);
//        // 线程名前缀
//        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
//        // 初始化
//        taskScheduler.initialize();
//
//        //enableSimpleBroker 服务端推送给客户端的路径前缀
//        registry.enableSimpleBroker("/app", "/topic")
//                .setHeartbeatValue(new long[]{10000,10000})
//                .setTaskScheduler(taskScheduler);
//        //setApplicationDestinationPrefixes 客户端发送给服务端的路径前缀
//        registry.setApplicationDestinationPrefixes("/app", "/topic");
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {//配置消息代理(message broker)

        registry.enableSimpleBroker("/topic", "/queue");

        registry.setApplicationDestinationPrefixes("/topic");


    }

    /**
     * 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        /*
         * 1. setMessageSizeLimit 设置消息缓存的字节数大小 字节
         * 2. setSendBufferSizeLimit 设置websocket会话时，缓存的大小 字节
         * 3. setSendTimeLimit 设置消息发送会话超时时间，毫秒
         */
        registration.setMessageSizeLimit(10240)
                .setSendBufferSizeLimit(10240)
                .setSendTimeLimit(10000);
    }

    /**
     * 设置输入消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        /*
         * 配置消息线程池
         * 1. corePoolSize 配置核心线程池，当线程数小于此配置时，不管线程中有无空闲的线程，都会产生新线程处理任务
         * 2. maxPoolSize 配置线程池最大数，当线程池数等于此配置时，不会产生新线程
         * 3. keepAliveSeconds 线程池维护线程所允许的空闲时间，单位秒
         */
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(60);
        /*
         * 添加stomp自定义拦截器，可以根据业务做一些处理
         * springframework 4.3.12 之后版本此方法废弃，代替方法 interceptors(ChannelInterceptor... interceptors)
         * 消息拦截器，实现ChannelInterceptor接口
         */
        //registration.setInterceptors(new WebSocketChannelInterceptor());
        registration.interceptors(webSocketChannelInterceptor());
    }

    /**
     *设置输出消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
     * @param registration
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(60);
        //registration.setInterceptors(new WebSocketChannelInterceptor());
        //registration.interceptors(webSocketChannelInterceptor());
    }

    /**
     * 拦截器加入spring ioc容器
     * @return
     */
    @Bean
    public WebSocketChannelInterceptor webSocketChannelInterceptor() {
        return new WebSocketChannelInterceptor();
    }

}
