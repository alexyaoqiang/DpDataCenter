package com.wiscom.controller;

import com.wiscom.service.DynamicDataService;
import com.wiscom.service.GlobalVarService;
import com.wiscom.service.ReadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author gavin
 * @version 创建日期:2020年6月12日
 */

@RestController
public class RedisController {

    @Autowired
    private ReadDataService readDataService;

    @Autowired
    private DynamicDataService dynamicDataService;

    @Autowired
    private GlobalVarService globalVarService;

    @GetMapping("/getValuesByKey")
    public String getValuesByKey(@RequestParam("key") String key) {
        return "redis::返回值 => " + key;
    }

    /**
     * 通过key从Redis中查询数据
     * @param key
     * @return map类型的数据
     */
    @GetMapping("/selectByKey")
    public Map<Object, Map<Object,Object>> selectByKey(@RequestParam("key") String key) {
        return readDataService.selectByKey(key);
    }

    /**
     * 读取Table中数据
     *
     * @param key
     * @return map类型的数据
     */
    @GetMapping("/readTableData")
    public List<Map<Object, Object>> readTableData(@RequestParam("key") String key) {
        return readDataService.readTableData(key);
    }

    /**
     * 读取cacheTable数据
     *
     * @param key
     * @return map类型的数据
     */
    @GetMapping("/readCacheTableData")
    public List<Map<Object, Object>> readCacheTableData(@RequestParam("key") String key) {
        return readDataService.readCacheTableData(key);
    }

    /**
     * 添加数据进入table
     *
     * @param key
     * @param value
     */
    @PostMapping("/addTableData")
    public void addTableData(@RequestParam("key") String key, @RequestBody Map<Object, Object> value) {
        readDataService.addTableData(key, value);
    }

    /**
     * 添加全局变量(尝试)
     *
     * @param sid
     * @param param
     * @param val
     */
    @PostMapping("/dynamicDataChange")
    public void dynamicDataChange(@RequestParam("sid") String sid, @RequestParam("param") String param,
                                  @RequestParam("val") String val) {
        dynamicDataService.dynamicDataChange(sid, param, val);
    }

    /**
     * 删除全局变量(尝试)
     *
     * @param sid
     */
    @DeleteMapping("/dynamicDataReset")
    public void dynamicDataReset(@RequestParam("sid") String sid) {
        dynamicDataService.dynamicDataReset(sid);
    }

    /**
     * 添加全局变量数据
     *
     * @param sid
     * @param key
     * @param value
     */
    @PostMapping("/addParamChangeData")
    public void addParamChangeData(@RequestParam("sid") String sid, @RequestParam("key") String key,
                                   @RequestParam("value") String value) {
        globalVarService.addParamChangeData(sid, key, value);
    }

    /**
     * 删除全局变量数据
     *
     * @param sid
     * @param key
     */
    @DeleteMapping("/deleteParamChangeData")
    public void deleteParamChangeData(@RequestParam("sid") String sid, @RequestParam("key") String key) {
        globalVarService.deleteParamChangeData(sid, key);
    }

    /**
     * 查询全局变量数据
     *
     * @param sid
     * @param key
     * @return 查询到的map类型数据
     */
    @GetMapping("/selectParamChangeData")
    public String selectParamChangeData(@RequestParam("sid") String sid, @RequestParam("key") String key) {
        return globalVarService.selectParamChangeData(sid, key);
    }

    /**
     * 用key读取table中数据
     *
     * @param key
     * @return map类型的数据
     */
    @GetMapping("/ReadTableDataByKey")
    public List<Map<Object, Object>> readTableData1(@RequestParam("key") String key) {
        return readDataService.readTableData1(key);
    }

    /**
     * 用key读取cacheTable中数据
     *
     * @param key
     * @return map类型的数据
     */
    @GetMapping("/ReadCacheTableDataByKey")
    public List<Map<Object, Object>> readCacheTable1(@RequestParam("key") String key) {
        return readDataService.readCacheTable1(key);
    }
}
