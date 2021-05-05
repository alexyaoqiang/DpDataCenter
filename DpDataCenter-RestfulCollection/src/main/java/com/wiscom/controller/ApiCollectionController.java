package com.wiscom.controller;

import com.wiscom.exception.AjaxResponse;
import com.wiscom.model.dppz.DataExecute;
import com.wiscom.service.ApiCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.ResultSet;

/**
* @author gavin
* @version 创建日期:2020年6月12日
*/

@RestController
public class ApiCollectionController {

	@Resource
	private ApiCollectionService apiCollectionService;

	private static Logger log = LoggerFactory.getLogger(ApiCollectionController.class);

	/**
	 * 【数据采集】此接口由manage调用，传过来数据源ID和对应的指标项，组成完整的url，调取接口获取数据后存入redis
	 */
	@PostMapping("/handleDataBindDetail")
	public void handleDataBindDetail(@RequestBody DataExecute dataExecute) {
		log.info("当前数据的dsId:"+dataExecute.getDsId()+ "----对应的执行指标:" + dataExecute.getExcuteDetail()
				+ "----componentFixColumnMap:" + dataExecute.getComponentFixColumnMap());
		apiCollectionService.handleDataBindDetail(dataExecute);

	}

	/**
	 * 【数据预览】此接口由manage调用，传过来数据源ID和对应的指标项，组成完整的url，调取接口获取数据后返回给manage
	 */
	@PostMapping("/executeSql")
	public String executeSql(@RequestBody DataExecute dataExecute) {
		log.info("请求预览数据：id:"+dataExecute.getDsId()+",executeDetail:"+dataExecute.getExcuteDetail()+",fixColumn:"+dataExecute.getFixColumn());
		String result=apiCollectionService.executeSql(dataExecute.getDsId(), dataExecute.getExcuteDetail(),dataExecute.getFixColumn());
		log.info("请求预览返回给manager的数据："+result);
		return result;
	}
}
