//package com.wiscom.thread;
//
//import com.wiscom.model.dppz.DataExecute;
//import com.wiscom.service.DBCollectionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * 采集项调度线程
// * Created by YL on 2020/8/21.
// */
//public class ExecuteSQLThread implements Runnable  {
//
//    private static Logger logger = LoggerFactory.getLogger(ExecuteSQLThread.class);
//
//    @Autowired
//    private DBCollectionService dbCollectionService;
//
//    @Override
//    public void run() {
//
//        logger.info("ExecuteSQLThread Start !");
//
//        //休眠十秒
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            logger.error(e.getMessage());
//        }
//
//        //消息消费者者对象
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("YL-TEST");
//        //设置NameServer地址
//        consumer.setNamesrvAddr("172.19.139.247:9876");
//        //订阅Topic
//        try {
//            consumer.subscribe("TopicA","TagA");
//        } catch (MQClientException e) {
//            logger.error("RocketMQ消费者订阅失败，请检查Topic或Tag是否正确");
//        }
//
//        //回调方法处理数据
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//                for(MessageExt messageExt:list){
//                    DataExecute dataExecute = (DataExecute)objectToByteArray(messageExt.getBody());
//                    dbCollectionService.handleDataBindDetail(dataExecute);
////                    System.out.println(dataExecute.toString());
//                    }
//                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                }
//        });
//
//        //开启消费者
//        try {
//            consumer.start();
//        }
//        catch (MQClientException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 二进制数组转对象
//     * @param bytes
//     * @return
//     */
//    private Object objectToByteArray(byte[] bytes){
//
//        ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
//        ObjectInputStream ons= null;
//        try {
//            ons = new ObjectInputStream(byteInt);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Object obj= null;
//        try {
//            obj = ons.readObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return obj;
//    }
//
//
//}
