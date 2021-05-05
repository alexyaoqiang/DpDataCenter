package com.wiscom.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单消息模板,用来推送消息
 */
@Service
public class WebSocketService {

    //用于存放已经订阅的Key
    public static Set<String> subscribedKeySet = new HashSet<>();

    //用于存放已经订阅的SessionId
    public static Set<String> subscribedSessionSet = new HashSet<>();

    //组件订阅时根据key加入List中进行维护(key,list<session>)
    public static Map<String, List<String>> keyMap = new ConcurrentHashMap<>();

    //组件订阅时记录session对象以及session下的订阅列表(session,list<key>)
    public static Map<String, List<String>> sessionMap = new ConcurrentHashMap<>();

    //ip下的sessionList
    public static Map<String, Set<String>> ipMap = new ConcurrentHashMap<>();

    //管理每个session下面的key
    List<String> keyList = null;

    //管理每个key下面的sessionId
    List<String> sessionList = null;

    @Resource
    private RedisReadService redisReadService;

    @Resource
    private SimpMessagingTemplate template;

    //订阅
    public void manageSubscribedInfo(String sid, String componentCode, StompHeaderAccessor accessor) {

        //获取订阅的key
        String subscribeKey = sid + ":" + componentCode;
        //获取相关参数
        Map<String, Object> map = (Map<String, Object>) accessor.getSessionAttributes().get("attributes");
        //得到sessionId
        String sessionId = map.get("sessionId").toString();
        //得到ip
        String ip = map.get("ip").toString();
        //ip下的session集合
        Set<String> set;

        //管理ip下的session
        if (ipMap.containsKey(ip)) {
            set = ipMap.get(ip);
            if (!set.contains(sessionId)) {
                set.add(sessionId);
            }
        } else {
            set = new HashSet<>();
            set.add(sessionId);
            ipMap.put(ip, set);
        }

        //订阅时,维护key下的session (key, list<session>)
        if (subscribedKeySet.contains(subscribeKey) && keyMap.containsKey(subscribeKey)) {
            //若key已经存在,则获取key下的list,并将sessionId加到list中
            List<String> list = keyMap.get(subscribeKey);
            list.add(sessionId);
        } else {
            //若key不存在,则创建新的list,并将sessionId加到list中
            sessionList = new ArrayList<>();
            sessionList.add(sessionId);
            keyMap.put(subscribeKey, sessionList);
            //将key加入集合中
            subscribedKeySet.add(subscribeKey);
        }

        //订阅时,维护session下的key (session, list<key>)
        if (subscribedSessionSet.contains(sessionId) && sessionMap.containsKey(sessionId)) {
            //若session已经存在,则获取session下的list,并将key加到session中
            List<String> list = sessionMap.get(sessionId);
                list.add(subscribeKey);
        } else {
            //若session不存在,则创建新的list,并将key加到list中
            keyList = new ArrayList<>();
            keyList.add(subscribeKey);
            sessionMap.put(sessionId, keyList);
            //将session加入集合中
            subscribedSessionSet.add(sessionId);
        }
    }

    //取消订阅
    public void manageCancelSubscribeInfo(String sid, String componentCode, StompHeaderAccessor accessor) {
        Map<String, Object> map = (Map<String, Object>) accessor.getSessionAttributes().get("attributes");
        String sessionId = map.get("sessionId").toString();
        //维护session下的key,将对应的key移除
        String str = sid + ":" + componentCode;
        //维护key下的session,将对应的sessionId移除
        keyMap.forEach((key, list) -> {
            if (str.equals(key)) {
                if (list.contains(sessionId) && list.size() > 1) {
                    list.remove(sessionId);
                } else if (list.contains(sessionId) && list.size() == 1) {
                    keyMap.remove(key);
                    //移除集合中的key
                    subscribedKeySet.remove(key);
                }
            }
        });

        sessionMap.forEach((session, list) -> {
            if (sessionId.equals(session)) {
                if (list.contains(str) && list.size() > 1) {
                    list.remove(str);
                } else if (list.contains(str) && list.size() == 1) {
                    sessionMap.remove(session);
                    //移除集合中的session
                    subscribedSessionSet.remove(session);
                }
            }
        });
    }

    //关闭session
    public void manageCloseSessionInfo(String sessionId) {
        //维护key下的session,将对应的sessionId移除
        keyMap.forEach((key, list) -> {
            if (list.contains(sessionId) && list.size() > 1) {
                list.remove(sessionId);
            } else if (list.contains(sessionId) && list.size() == 1) {
                keyMap.remove(key);
                //移除集合中的key
                subscribedKeySet.remove(key);
            }
        });
        //维护session下的key,直接移除session
        sessionMap.remove(sessionId);
    }

    //获取Table中的数据,存入list并定时推送
    public void broadData() {
        //获取已经订阅的key
        Map<String, List<String>> keyMap = WebSocketService.keyMap;
        //遍历已经订阅的key取出数据
        keyMap.forEach((key, list) -> {
            String[] str = key.split(":");
            String des = "/topic/TableData/" + str[0] + "/" + str[1];
            List<Map<String, Object>> tableInfo = redisReadService.getTableInfo(key);
            for (Map<String, Object> map : tableInfo) {
                //所有已订阅的key定时推送table中的数据
                template.convertAndSend(des, map);
            }
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        });
    }
}

