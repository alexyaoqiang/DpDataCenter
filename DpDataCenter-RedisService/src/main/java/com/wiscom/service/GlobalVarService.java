package com.wiscom.service;

import org.springframework.stereotype.Component;

@Component
public interface GlobalVarService {
    public void addParamChangeData(String sid, String key, String value);

    public String selectParamChangeData(String sid, String key);

    public void deleteParamChangeData(String sid, String key);
}
