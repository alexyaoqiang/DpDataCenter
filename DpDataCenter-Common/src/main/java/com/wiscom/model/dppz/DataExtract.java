package com.wiscom.model.dppz;
/**
* @author gavin
* @version 创建日期:2020年6月12日
*/

public class DataExtract {
	
	private String id;
	private String name;
	private String description;
	private String dsId;
	private String intfcDetail;
	private String intfcParam;
	private String model;
	private Integer frequence;
	private String isUsed;
	private String createdBy;;
	private String createdTime;;
	private String updatedBy;;
	private String updatedTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIntfcDetail() {
		return intfcDetail;
	}
	public void setIntfcDetail(String intfcDetail) {
		this.intfcDetail = intfcDetail;
	}
	public String getIntfcParam() {
		return intfcParam;
	}
	public void setIntfcParam(String intfcParam) {
		this.intfcParam = intfcParam;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

	public Integer getFrequence() {
		return frequence;
	}

	public void setFrequence(Integer frequence) {
		this.frequence = frequence;
	}

	public String getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(String isUsed) {
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
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	@Override
	public String toString() {
		return "DataExtract{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", dsId='" + dsId + '\'' +
				", intfcDetail='" + intfcDetail + '\'' +
				", intfcParam='" + intfcParam + '\'' +
				", model='" + model + '\'' +
				", frequence='" + frequence + '\'' +
				", isUsed='" + isUsed + '\'' +
				", createdBy='" + createdBy + '\'' +
				", createdTime='" + createdTime + '\'' +
				", updatedBy='" + updatedBy + '\'' +
				", updatedTime='" + updatedTime + '\'' +
				'}';
	}
}
