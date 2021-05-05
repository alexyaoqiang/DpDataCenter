package com.wiscom.service.impl;

import com.wiscom.Utils.RedisOperateUtils;
import com.wiscom.service.GlobalVarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GlobalVarServiceImpl implements GlobalVarService {

    @Resource
    private RedisOperateUtils redisOperateUtils;

    /**
     * 添加全局变量数据
     *
     * @param sid
     * @param key
     * @param value
     */
    @Override
    public void addParamChangeData(String sid, String key, String value) {

        //拼接全局变量的key
        String mainKey = "ParamChange:" + sid + ":#" + key + "#";

        //拼接key的值
        String changeKey = "#" + key + "#";

        //添加数据进入redis
        redisOperateUtils.addData(mainKey, "value", value);
        redisOperateUtils.addData(mainKey, "key", changeKey);
        redisOperateUtils.addData(mainKey, "sid", sid);
    }

    /**
     * 删除全局变量数据
     *
     * @param sid
     * @param key
     */
    @Override
    public void deleteParamChangeData(String sid, String key) {

        //拼接全局变量的key
        String mainKey = "ParamChange:" + sid + ":#" + key + "#";

        //删除单个key
        redisOperateUtils.deleteSingleKey(mainKey);
    }

    /**
     * 查询全局变量数据
     *
     * @param sid
     * @param key
     * @return
     */
    @Override
    public String selectParamChangeData(String sid, String key) {

        //拼接全局变量的key
        String paramChangeKey = "ParamChange:" + sid + ":#" + key + "#";

        //接收查询到的数据
        String object;

        //判断redis中是否存在key，存在，返回对应value；不存在，返回null
        if (redisOperateUtils.judgeKey(paramChangeKey)) {
            object = (String) redisOperateUtils.getData(paramChangeKey).get("value");
        } else {
            object = null;
        }
        return object;

    }

}
