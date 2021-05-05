package com.wiscom.model.dppz;
/**
* @author gavin
* @version 创建日期:2020年6月11日
*/

public class DataBindConf {
	protected String sceneId;
	protected String componentId;
	protected String groupIndex;
	protected String intfcId;
	protected String intfcParam;
	protected boolean isUsed;
	protected String createdBy;
	protected String createdTime;
	protected String updatedBy;
	protected String updateTime;
	protected String componentCode;


	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getGroupIndex() {
		return groupIndex;
	}
	public void setGroupIndex(String groupIndex) {
		this.groupIndex = groupIndex;
	}
	public String getIntfcId() {
		return intfcId;
	}
	public void setIntfcId(String intfcId) {
		this.intfcId = intfcId;
	}
	public String getIntfcParam() {
		return intfcParam;
	}
	public void setIntfcParam(String intfcParam) {
		this.intfcParam = intfcParam;
	}
	public boolean isUsed() {
		return isUsed;
	}
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getComponentCode() {
		return componentCode;
	}

	public void setComponentCode(String componentCode) {
		this.componentCode = componentCode;
	}

	@Override
	public String toString() {
		return "DataBindConf{" +
				"sceneId='" + sceneId + '\'' +
				", componentId='" + componentId + '\'' +
				", groupIndex='" + groupIndex + '\'' +
				", intfcId='" + intfcId + '\'' +
				", intfcParam='" + intfcParam + '\'' +
				", isUsed=" + isUsed +
				", createdBy='" + createdBy + '\'' +
				", createdTime='" + createdTime + '\'' +
				", updatedBy='" + updatedBy + '\'' +
				", updateTime='" + updateTime + '\'' +
				", componentCode='" + componentCode + '\'' +
				'}';
	}
}
