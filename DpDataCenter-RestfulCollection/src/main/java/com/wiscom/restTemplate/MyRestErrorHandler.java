package com.wiscom.restTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * RestTemplate请求结果异常是可以自定义处理的。
 * 在开始进行自定义的异常处理逻辑之前，我们有必要看一下异常处理的默认实现。
 *
 * ResponseErrorHandler是RestTemplate请求结果的异常处理器接口
 *      接口的第一个方法hasError用于判断HttpResponse是否是异常响应（通过状态码）
 *      接口的第二个方法handleError用于处理异常响应结果（非200状态码段）
 *
 * DefaultResponseErrorHandler是ResponseErrorHandler的默认实现
 *
 */
public class MyRestErrorHandler implements ResponseErrorHandler {

    /**
     * 判断返回结果response是否是异常结果
     * 主要是去检查response 的HTTP Status
     * 仿造DefaultResponseErrorHandler实现即可
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
        return (statusCode != null ? statusCode.isError(): hasError(rawStatusCode));
    }

    protected boolean hasError(int unknownStatusCode) {
        HttpStatus.Series series = HttpStatus.Series.valueOf(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // 里面可以实现你自己遇到了Error进行合理的处理
        //TODO 将接口请求的异常信息持久化
    }
}
