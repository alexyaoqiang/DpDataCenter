package com.wiscom.controller;

import com.wiscom.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.wiscom.service.RedisReadService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author gavin
 * @version 创建日期:2020年6月12日
 */

@RestController
public class WebsocketController {

    private static Logger log = LoggerFactory.getLogger(WebsocketController.class);

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private RedisReadService redisReadService;

    @Resource
    private SimpMessagingTemplate template;

    //订阅
    @SubscribeMapping("/TableData/{sid}/{componentCode}")
    public void manageSubscribedInfo(@DestinationVariable("sid") String sid,
                                     @DestinationVariable("componentCode") String componentCode,
                                     StompHeaderAccessor accessor) {
        log.info(sid + ":" + componentCode + "订阅成功");
        //组件收到订阅时,获取cacheTable中的数据,并推送给客户端
        List<Map<String, Object>> cacheTableInfo = redisReadService.getCacheTableInfo(sid + ":" + componentCode);
        String des = "/topic/TableData/" + sid + "/" + componentCode;
        template.convertAndSend(des, "OK");
        if (cacheTableInfo != null) {
            for (Map<String, Object> map : cacheTableInfo) {
                template.convertAndSend(des, map);
            }
        }
        //维护订阅时key与session的关系
        webSocketService.manageSubscribedInfo(sid, componentCode, accessor);
    }

    //取消订阅
    @MessageMapping("/DisTableData/{sid}/{componentCode}")
    public void manageCancelSubscribe(@DestinationVariable("sid") String sid,
                                      @DestinationVariable("componentCode") String componentCode,
                                      StompHeaderAccessor accessor) {
        log.info(sid + ":" + componentCode + "订阅已取消");
        //维护取消订阅时key与session的关系
        webSocketService.manageCancelSubscribeInfo(sid, componentCode, accessor);
    }

}
