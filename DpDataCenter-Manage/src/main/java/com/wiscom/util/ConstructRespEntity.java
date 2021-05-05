package com.wiscom.util;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class ConstructRespEntity {

	public static ResponseEntity<String> constructResponseEntity(Map<String, Object> result){
		
        HttpHeaders headers = new HttpHeaders();
        
        headers.set("Access-Control-Allow-Origin", "*");
        
        JsonConfig cfg = new JsonConfig();
		
        cfg.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));
        
        JSONObject jsonObj = JSONObject.fromObject(result,cfg);
		
        return new ResponseEntity<String>(jsonObj.toString(),headers,HttpStatus.OK);
		
	}
	
}
