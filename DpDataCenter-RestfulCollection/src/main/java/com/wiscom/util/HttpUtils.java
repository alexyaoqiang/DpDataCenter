package com.wiscom.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.controller.ApiCollectionController;
import com.wiscom.exception.CustomException;
import com.wiscom.exception.CustomExceptionType;
import com.wiscom.model.dppz.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static ObjectMapper objectMapper=new ObjectMapper();

    @Resource(name = "httpClient")
    private RestTemplate restTemplate;

    public Map getTemplate(String url) throws CustomException {
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
        int statusCodeValue = responseEntity.getStatusCodeValue(); // 获取响应码值
        if(statusCodeValue==404) {
            String message="该url:"+url+"报404";
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,message);
        }
        log.info("get请求返回数据：",responseEntity.getBody());
        return responseEntity.getBody();
    }

    public Map postTemplate(String url, Map body) throws CustomException{
        // 请求头设置,x-www-form-urlencoded格式的数据
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 组装请求体
        HttpEntity<Map<String, String>> request =
                new HttpEntity<Map<String, String>>(body, headers);
        // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
        ResponseEntity<Map> responseEntity =restTemplate.postForEntity(url, request, Map.class);
        int statusCodeValue = responseEntity.getStatusCodeValue(); // 获取响应码值
        if(statusCodeValue==404) {
            String message="该url:"+url+"报404";
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,message);
        }
        return responseEntity.getBody();
    }

}
