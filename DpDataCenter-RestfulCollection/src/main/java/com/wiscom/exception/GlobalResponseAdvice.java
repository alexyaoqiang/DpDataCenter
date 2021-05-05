package com.wiscom.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscom.controller.ApiCollectionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发RESTful服务时，要求HTTP状态码能够体现业务的最终执行状态，所以说：我们有必要让业务状态(AjaxResponse的code)与HTTP协议Response状态码一致
 *
 * 实现ResponseBodyAdvice 接口的作用是：在将数据返回给用户之前，做最后一步的处理。
 * 也就是说，ResponseBodyAdvice 的处理过程在全局异常处理的后面
 */
@Component
@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice {

    private static ObjectMapper objectMapper=new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(GlobalResponseAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        //return returnType.hasMethodAnnotation(ResponseBody.class);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        //如果响应结果是JSON数据类型
        if (selectedContentType.isCompatibleWith(
                MediaType.APPLICATION_JSON)) {
            //如果Controller或全局异常处理响应的结果body是AjaxResponse，就直接return给前端
            if (body instanceof AjaxResponse) { //如果是AjaxResponse，说明不是200
                AjaxResponse ajaxResponse = (AjaxResponse)body;
                if(ajaxResponse.getCode() != 999) { //999 不是标准的HTTP状态码，特殊处理
                    //为HTTP响应结果设置状态码，状态码就是AjaxResponse的code，二者达到统一
                    response.setStatusCode(
                            HttpStatus.valueOf(((AjaxResponse) body).getCode()));
                }
                List list=new ArrayList<>();
                list.add(body);
                return list;
            }
        }
        List list=new ArrayList<>();
        list.add(body);
        return body;
    }
}
