package com.wiscom.controller;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.wiscom.model.dppz.DataExecute;
import com.wiscom.service.DBCollectionService;
import com.wiscom.thread.DBConfigThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DBCollectionController {

    private static Logger log = LoggerFactory.getLogger(DBCollectionController.class);

    @Autowired
    private DBCollectionService dbCollectionService;

    /**
     * 根据数据源id和sql语句执行sql,并返回执行结果
     */
    @RequestMapping("/executeSql")
    public List<Map<String,Object>> executeSql(@RequestParam("id") String id, @RequestParam("sql") String sql) {
        return dbCollectionService.executeSql(id, sql);
    }

    /**
     * 将采集项加入采集队列,manage中的线程调用
     */
    @PostMapping("/handleDataBindDetail")
    public synchronized void handleDataBindDetail(@RequestBody DataExecute dataExecute) {
        dbCollectionService.handleDataBindDetail(dataExecute);
        log.info("当前加入队列的IntfcId:"+dataExecute.getIntfcId()+ "----对应的sql:" + dataExecute.getExcuteDetail());
    }
}
