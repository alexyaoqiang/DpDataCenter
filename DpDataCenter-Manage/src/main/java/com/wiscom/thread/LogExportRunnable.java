package com.wiscom.thread;

import com.wiscom.ManageApplication;
import com.wiscom.model.dppz.DataExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 运行状态分析与日志输出线程
 * Created by YL on 2020/8/18.
 */
@Component(value = "logExportRunnable")
public class LogExportRunnable implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(LogExportRunnable.class); //日志输出

    @Autowired
    private MonitorRunnable monitorRunnable;

    private Map<Integer,Integer> frequenceMap = new TreeMap<>(); //Map<采集频率，指标数>

    private Map<String,Integer> datasourceMap = new TreeMap<>(); //Map<采数据源ID，指标数>

    private Map<Integer,Integer> timeMap = new TreeMap<>(); //Map<时间点，指标数>

    @Override
    public void run() {

        logger.info("LogExportRunnable start！" );

        int frequence; //采集频率

        String dsId; //数据源Id

        String time; //五分钟内的时间点

        String logInfo; //拼接日志输出信息

        while (true) {

            //线程休眠1分钟
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            try {
                //遍历Map集合，获取DataExecute对象
                for (DataExecute dataExecute : monitorRunnable.getDel()) {

                    try {

                        frequence = dataExecute.getFrequence();
                        dsId  = dataExecute.getDsId();

                        //frequenceMap集合中存放采集频率与对应指标数
                        if(frequenceMap.containsKey(frequence)){
                            frequenceMap.put(frequence,frequenceMap.get(frequence)+1);
                        }else{
                            frequenceMap.put(frequence,1);
                        }

                        //datasourceMap集合中存放数据源id与对应指标数
                        if(datasourceMap.containsKey(dsId)){
                            datasourceMap.put(dsId,datasourceMap.get(dsId)+1);
                        }else{
                            datasourceMap.put(dsId,1);
                        }

                        //timeMap集合中存放数据源id与对应指标数
                        for(int i=0;i<300;i++){

                            //不存在，添加，执行指标数为0
                            if(!timeMap.containsKey(i)){
                                timeMap.put(i,0);
                            }

                            //当前时间点坐标执行，执行数加一
                            if(i % frequence == 0){
                                timeMap.put(i,timeMap.get(i)+1);
                            }

                        }

                    } catch (Exception e) {
                        logger.error("dsId=" + dataExecute.getIntfcId());
                    }

                }

                //日志输出
                logInfo="";

                for(Integer fre : frequenceMap.keySet()){
                    logInfo+=" {"+fre+": "+frequenceMap.get(fre)+"}，";
                }
                ManageApplication.logger.info("采集频率/指标数： "+logInfo);

                logInfo="";
                for(String ds : datasourceMap.keySet()){
                    logInfo+=" {"+ds+": "+datasourceMap.get(ds)+"}，";
                }
                ManageApplication.logger.info("数据源ID/指标数： "+logInfo);

                logInfo="";
                for(Integer tim : timeMap.keySet()){
                    logInfo+=" {"+secondToMMSS(tim)+": "+timeMap.get(tim)+"}，";
                }
                ManageApplication.logger.info("五分钟内时间点/指标数： "+logInfo);

                ManageApplication.logger.info("执行指标数："+ monitorRunnable.getDel().size());

                //清空集合
                frequenceMap = new TreeMap<>();
                datasourceMap = new TreeMap<>();
                timeMap = new TreeMap<>();

                //线程休眠5分钟
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }

            } catch (Exception e) {
                logger.error("LogExportRunnable线程报错");
            }

        }

    }

    /**
     * 秒数转换为XX分XX秒的格式
     * @param time
     * @return
     */
    private String secondToMMSS(int time){

        String ms;

        if(time/60==0){
            ms="0分";
        }else{
            ms=time/60+"分";
        }

        if(time%60>=10){
            ms=ms+time%60+"秒";
        }else{
            ms=ms+"0"+time%60+"秒";
        }

        return ms;
    }

}
