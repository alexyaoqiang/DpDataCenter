package com.wiscom.model.dppz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YL
 * @version 创建日期:2020年8月18日
 */
public class DataExecute implements Serializable {
	
	private String dsId;
	private String intfcId;
	private String excuteDetail;
	private Integer frequence;
	private Map<String,String> componentFixColumnMap = new HashMap<>();  //<SceneId:ComponentId:ComponentCode:GroupIndex，fixColumn>
	private String fixColumn;

	@Override
	public String toString() {
		return "DataExecute{" +
				"dsId='" + dsId + '\'' +
				", intfcId='" + intfcId + '\'' +
				", excuteDetail='" + excuteDetail + '\'' +
				", frequence=" + frequence +
				", componentFixColumnMap=" + componentFixColumnMap +
				", fixColumn='" + fixColumn + '\'' +
				'}';
	}

	public String getFixColumn() {
		return fixColumn;
	}

	public void setFixColumn(String fixColumn) {
		this.fixColumn = fixColumn;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getIntfcId() {
		return intfcId;
	}

	public void setIntfcId(String intfcId) {
		this.intfcId = intfcId;
	}

	public String getExcuteDetail() {
		return excuteDetail;
	}

	public void setExcuteDetail(String excuteDetail) {
		this.excuteDetail = excuteDetail;
	}

	public Integer getFrequence() {
		return frequence;
	}

	public void setFrequence(Integer frequence) {
		this.frequence = frequence;
	}

	public Map<String, String> getComponentFixColumnMap() {
		return componentFixColumnMap;
	}

	public void setComponentFixColumnMap(Map<String, String> componentFixColumnMap) {
		this.componentFixColumnMap = componentFixColumnMap;
	}
}
