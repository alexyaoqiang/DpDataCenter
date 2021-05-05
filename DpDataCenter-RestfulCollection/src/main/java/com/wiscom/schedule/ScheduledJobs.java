package com.wiscom.schedule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.model.dppz.DataServiceNotify;
import com.wiscom.model.dppz.DataSource;
import com.wiscom.service.DPConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScheduledJobs {

    private static Logger log = LoggerFactory.getLogger(ScheduledJobs.class);

    @Resource
    private DPConfigService dpConfigService;

    //数据源类型数组
    private static final String[] TYPE = {"restapi"};
    //此对象用于记录manager微服务的数据源信息是否发生变化
    private static DataServiceNotify dataServiceNotify;

    //设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
    private static ObjectMapper objectMapper=new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    //key:数据源Id value:整个数据源对象
    public static Map<String,DataSource> dataSourceMap=new ConcurrentHashMap<>();
    //当前线程池创建的线程数量
    private static int threadNum;

    public static ExecutorService threadPool;

    /**
     * 此定时任务每隔一段时间向manager微服务获取最新的数据源信息
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void getNewestDataSource() {
        log.info("开始执行getNewestDataSource向manager获取最新的数据源信息");
        Map notifyMap = new HashMap<>();
        if (dataServiceNotify==null) {  //第一次获取数据源信息时，此对象是空的
            notifyMap=new HashMap<>();
        }else { //获取本地保存的DataServiceNotify对象，传给manager，用于比较核对数据源信息是否发生变化
            notifyMap.put("dsUpdate", dataServiceNotify.getDsUpdate());
            notifyMap.put("intfcUpdate", dataServiceNotify.getIntfcUpdate());
            notifyMap.put("bindUpdate", dataServiceNotify.getBindUpdate());
        }
        Map map=dpConfigService.getConfigByType(TYPE,notifyMap);    //向manager发送请求
        if(map==null) {
            log.error("向manager请求返回的数据源信息为null，原因是微服务还未向nacos注册成功");
            return;
        }
        log.info("向manager请求返回的数据源信息,{}",map);
        //解析map中的 code
        Integer code= (Integer) map.get("code");
        //解析map中的 dataServiceNotify
        Map dataServiceNotifyMap=(Map) map.get("dataServiceNotify");
        dataServiceNotify=objectMapper.convertValue(dataServiceNotifyMap,DataServiceNotify.class);
        //解析map中的 dataSource
        //key:数据源ID value:数据源组成的Map结构
        Map<String,Map<String,String>> dataSourceFromManager= (Map<String, Map<String, String>>) map.get("dataSource");
        if (dataSourceFromManager!=null) {     //如果manager发现这边传过去的DataServiceNotify对象和服务端一致，则认为数据源没有更新，就不会传dataSource回来
            dataSourceMap.clear();    //清空原来保存的数据源信息
            for(Map.Entry<String,Map<String,String>> entry:dataSourceFromManager.entrySet()) {
                DataSource dataSource=objectMapper.convertValue(entry.getValue(),DataSource.class);
                dataSourceMap.put(dataSource.getId(),dataSource);
            }
        }
        //解析map中的 threadNum
        Map<String,Integer> threadNumMap=(Map) map.get("threadNum");
        if(threadNumMap!=null) {    //如果manager发现这边传过去的DataServiceNotify对象和服务端一致，则认为数据源没有更新，就不会传threadNum回来
            int threadCount=0;  //计数当前接口上要求的线程总数
            Iterator<Map.Entry<String,Integer>> it=threadNumMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,Integer> entry=it.next();
                log.info("key,{},value,{}",entry.getKey(),entry.getValue());
                threadCount+=entry.getValue();
            }
            if(threadCount==0) {
                threadCount=1;
            }
            log.info("接口认为需要的线程数量为："+threadCount);
//            threadCount=5;

            if (threadPool==null) { //只有第一次查询数据源信息的时候，线程池才为null，表示线程池还没有被创建
                threadPool= Executors.newFixedThreadPool(threadCount);
                threadNum=threadCount;
                log.info("初始化线程池");
            }else {
                if (threadCount>threadNum) {
                    threadPool.shutdown();
                    threadPool= Executors.newFixedThreadPool(threadCount);
                    threadNum=threadCount;
                    log.info("所需要的线程数量大于当前线程的数量，关闭线程池，重新创建");
                }
            }
        }
        log.info("最新的datasource个数为："+dataSourceMap.size()+",具体的ip:port如下：");
        for(Map.Entry<String,DataSource> entry:dataSourceMap.entrySet()) {
            log.info(entry.getValue().getInter());
        }

    }
}
