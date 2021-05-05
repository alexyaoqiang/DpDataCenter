package com.wiscom.exception;

import lombok.Data;

/**
 * 统一响应数据结构
 *
 * 对于不同的场景，提供了四种构建AjaxResponse 的方法。
 * 1、当请求成功的情况下，可以使用AjaxResponse.success()构建返回结果给前端。
 * 2、当查询请求等需要返回业务数据，请求成功的情况下，可以使用AjaxResponse.success(data)构建返回结果给前端。携带结果数据。
 * 3、当请求处理过程中发生异常，需要将异常转换为CustomException ，然后在控制层使用AjaxResponse error(CustomException)构建返回结果给前端。
 * 4、在某些情况下，没有任何异常产生，我们判断某些条件也认为请求失败。这种使用AjaxResponse error(customExceptionType,errorMessage)构建响应结果。
 */
@Data
public class AjaxResponse {


    private boolean isok;   //isok表示该请求是否处理成功（即是否发生异常）。true表示请求处理成功，false表示处理失败。
    private int code;   //code对响应结果进一步细化，200表示请求成功，400表示用户操作导致的异常，500表示系统异常，999表示其他异常。与CustomExceptionType枚举一致。
    private String message; //message：友好的提示信息，或者请求结果提示信息。如果请求成功这个信息通常没什么用，如果请求失败，该信息需要展示给用户。
    private Object data;    //data：通常用于查询数据请求，成功之后将查询数据响应给前端。

    private AjaxResponse() {

    }

    //请求出现异常时的响应数据封装
    //当请求处理过程中发生异常，需要将异常转换为CustomException ，然后在控制层使用AjaxResponse error(CustomException)构建返回结果给前端
    public static AjaxResponse error(CustomException e) {

        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setIsok(false);
        resultBean.setCode(e.getCode());
        if(e.getCode() == CustomExceptionType.USER_INPUT_ERROR.getCode()){
            resultBean.setMessage(e.getMessage());
        }else if(e.getCode() == CustomExceptionType.SYSTEM_ERROR.getCode()){
            resultBean.setMessage(e.getMessage() + ",服务端出现异常");
        }else{
            resultBean.setMessage("系统出现未知异常");
        }
        //TODO 这里最好将异常信息持久化
        return resultBean;
    }

    //请求出现异常时的响应数据封装
    //在某些情况下，没有任何异常产生，我们判断某些条件也认为请求失败。这种使用
    public static AjaxResponse error(CustomExceptionType customExceptionType,
                                     String errorMessage) {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setIsok(false);
        resultBean.setCode(customExceptionType.getCode());
        resultBean.setMessage(errorMessage);
        //TODO 这里最好将异常信息持久化
        return resultBean;
    }

    //请求成功的响应，不带查询数据（用于删除、修改、新增接口）
    public static AjaxResponse success() {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setIsok(true);
        resultBean.setCode(200);
        resultBean.setMessage("请求响应成功!");
        return resultBean;
    }

    //请求成功的响应，带有查询数据（用于数据查询接口）
    public static AjaxResponse success(Object data) {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setIsok(true);
        resultBean.setCode(200);
        resultBean.setMessage("请求响应成功!");
        resultBean.setData(data);
        return resultBean;
    }

    //请求成功的响应，带有查询数据（用于数据查询接口）
    public static AjaxResponse success(Object obj,String message){
        AjaxResponse ajaxResponse = new AjaxResponse();
        ajaxResponse.setIsok(true);
        ajaxResponse.setCode(200);
        ajaxResponse.setMessage(message);
        ajaxResponse.setData(obj);
        return ajaxResponse;
    }

    @Override
    public String toString() {
        return "AjaxResponse{" +
                "isok=" + isok +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}