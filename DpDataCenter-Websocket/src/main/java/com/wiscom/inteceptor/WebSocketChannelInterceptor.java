package com.wiscom.inteceptor;

import com.wiscom.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <websocket消息监听，用于监听websocket用户连接情况>
 * <功能详细描述>
 **/
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private WebSocketService webSocketService;

    //log日志打印
    Logger logger = (Logger) LoggerFactory.getLogger(WebSocketChannelInterceptor.class);

    //用于存放已经订阅的session列表
    public static Set<String> subscribedSessionSet = WebSocketService.subscribedSessionSet;

    //在消息发送之前调用
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
        return message;
    }

    // 在消息发送后立刻调用，boolean值参数表示该调用的返回值
    @Override
    public void postSend(Message<?> message, MessageChannel messageChannel, boolean b) {

        //获得浏览器发送的心跳
        long[] heartbeat = null;
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        //获取相关参数
        Map<String, Object> map = (Map<String, Object>) accessor.getSessionAttributes().get("attributes");
        //sessionId
        String sessionId = map.get("sessionId").toString();
        //IP地址
        String ip = map.get("ip").toString();
        //端口
        String port = map.get("port").toString();
        // 忽略心跳消息等非STOMP消息
        if(accessor.getCommand() == null) {
            return;
        }
        // 根据连接状态做处理,可以根据实际场景,对上线,下线,首次成功连接做处理
        switch (accessor.getCommand()) {
            case CONNECT:
                //System.out.println("httpSession key:" + sessionId + "---" + ip + "---" + port + "连接成功");
                break;
            case CONNECTED:
                //System.out.println("httpSession key:" + sessionId + "---" + ip + "---" + port + "连接中");
                break;
            case DISCONNECT:
                //session关闭时,将sessionId从两个全局map中移除
                webSocketService.manageCloseSessionInfo(sessionId);
                //session关闭时,若session下没有订阅的key了,则将sessionId从subscribedSessionList中移除
                if (WebSocketService.sessionMap.get(sessionId) == null || WebSocketService.sessionMap.get(sessionId).size() == 0) {
                    subscribedSessionSet.remove(sessionId);
                }
                break;
            default:
                break;
        }
    }

    /*
     * 1. 在消息发送完成后调用，而不管消息发送是否产生异常，在次方法中，我们可以做一些资源释放清理的工作
     * 2. 此方法的触发必须是preSend方法执行成功，且返回值不为null,发生了实际的消息推送，才会触发
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel messageChannel, boolean b, Exception e) {
    }

    /* 1. 在消息被实际检索之前调用，如果返回false,则不会对检索任何消息，只适用于(PollableChannels)，
     * 2. 在websocket的场景中用不到
     */
    @Override
    public boolean preReceive(MessageChannel messageChannel) {
        return true;
    }

    /*
     * 1. 在检索到消息之后，返回调用方之前调用，可以进行信息修改，如果返回null,就不会进行下一步操作
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel messageChannel) {
        return message;
    }

    /*
     * 1. 在消息接收完成之后调用，不管发生什么异常，可以用于消息发送后的资源清理
     * 2. 只有当preReceive 执行成功，并返回true才会调用此方法
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel messageChannel, Exception e) {
    }

}
