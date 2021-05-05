package com.wiscom.controller;

import com.wiscom.service.DataPreViewService;
import com.wiscom.util.ConstructRespEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 数据预览接口
 * Created by YL on 2020/7/10.
 */
@RestController
@RequestMapping(value="/dataPreviewController")
public class DataPreviewController {

    @Resource
    private DataPreViewService dataPreViewService;

    /**
     * 替换坐标的参数，调用collection服务获取预览结果
     * @param param
     * @return { "code": "0000", "data": true }
     */
    @RequestMapping(value="/viewData",method = RequestMethod.POST,produces="text/html;charset=UTF-8")
    public ResponseEntity<String> viewData(@RequestBody Map<String,Object> param){

        Map<String,Object> resultMap =dataPreViewService.viewData(param);

        return ConstructRespEntity.constructResponseEntity(resultMap);
    }




}
