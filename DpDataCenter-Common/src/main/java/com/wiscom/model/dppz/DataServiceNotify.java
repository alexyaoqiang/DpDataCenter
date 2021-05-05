package com.wiscom.model.dppz;
/**
 * @author cyang
 * @version 创建日期:2020年6月17日
 */
public class DataServiceNotify {

    private String code;//ID

    private String dsUpdate;//数计源更新标识

    private String intfcUpdate;//指标更新标识

    private String bindUpdate;//数据绑定更新标识

    public DataServiceNotify(){
        code = "";
        dsUpdate = "";
        intfcUpdate = "";
        bindUpdate = "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDsUpdate() {
        return dsUpdate;
    }

    public void setDsUpdate(String dsUpdate) {
        this.dsUpdate = dsUpdate;
    }

    public String getIntfcUpdate() {
        return intfcUpdate;
    }

    public void setIntfcUpdate(String intfcUpdate) {
        this.intfcUpdate = intfcUpdate;
    }

    public String getBindUpdate() {
        return bindUpdate;
    }

    public void setBindUpdate(String bindUpdate) {
        this.bindUpdate = bindUpdate;
    }

    @Override
    public String toString() {
        return "DataServiceNotify{" +
                "code='" + code + '\'' +
                ", dsUpdate='" + dsUpdate + '\'' +
                ", intfcUpdate='" + intfcUpdate + '\'' +
                ", bindUpdate='" + bindUpdate + '\'' +
                '}';
    }
}
