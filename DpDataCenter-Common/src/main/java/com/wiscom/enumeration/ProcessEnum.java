package com.wiscom.enumeration;
/**
* @author gavin
* @version 创建日期:2020年6月19日
*/

public enum ProcessEnum {
    EXECUTE_SUCCESS(1001,"处理成功"),
    
    EXECUTE_FAIL(2001,"处理执行失败");

    private int code;

    private String message;

    ProcessEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
