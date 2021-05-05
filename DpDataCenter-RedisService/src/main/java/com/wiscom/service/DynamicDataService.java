package com.wiscom.service;

import org.springframework.stereotype.Component;

@Component
public interface DynamicDataService {

    public void dynamicDataChange(String sid, String param, String val);

    public void dynamicDataReset(String sid);
}
