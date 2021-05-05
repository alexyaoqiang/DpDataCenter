package com.wiscom.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();

    public static void main(String[] args) {

        String str="{↵    \"type\": \"GET\",↵    \"function\": \"/RestFulServer/StringAndIntTest?str=AAA&num=222\"↵}";


    }
}
