package com.wiscom.restTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Configuration
public class ContextConfig {

    //使用HttpComponentsClientHttpRequestFactory初始化RestTemplate bean对象
    @Bean("httpClient")
    public RestTemplate httpClientRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        /**
         * 将MyRestErrorHandler 在RestTemplate实例化的时候进行注册
         */
        restTemplate.setErrorHandler(new MyRestErrorHandler());
        //添加拦截器(自定义的拦截器)
//        restTemplate.getInterceptors().add(getCustomInterceptor());
        return restTemplate;
    }

    //实现一个拦截器：使用拦截器为每一个HTTP请求添加Basic Auth认证用户名密码信息（可以在这个基础上进一步简化）
    private ClientHttpRequestInterceptor getCustomInterceptor(){
        ClientHttpRequestInterceptor interceptor = (httpRequest, bytes, execution) -> {
//            httpRequest.getHeaders().set("authorization",
//                    "Basic " +
//                            Base64.getEncoder()
//                                    .encodeToString("admin:adminpwd".getBytes()));
            return execution.execute(httpRequest, bytes);
        };
        return interceptor;
    }

}