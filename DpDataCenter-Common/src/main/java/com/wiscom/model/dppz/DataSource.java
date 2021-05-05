package com.wiscom.model.dppz;
/**
* @author gavin
* @version 创建日期:2020年6月11日
*/

public class DataSource {
	
	private String id;
	private String name;
	private String type;
	private String host;
	private String port;
	private String userName;
	private String pwd;
	private String service;
	private String inter;
	private boolean isUsed ;
	private String createdBy;
	private String createdTime;
	private String updatedBy;
	private String updatedTime;
	private String subType;
	private String supplementParam;

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getSupplementParam() {
		return supplementParam;
	}

	public void setSupplementParam(String supplementParam) {
		this.supplementParam = supplementParam;
	}

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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getInter() {
		return inter;
	}
	public void setInter(String inter) {
		this.inter = inter;
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
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "DataSource{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", host='" + host + '\'' +
				", port='" + port + '\'' +
				", userName='" + userName + '\'' +
				", pwd='" + pwd + '\'' +
				", service='" + service + '\'' +
				", inter='" + inter + '\'' +
				", isUsed=" + isUsed +
				", createdBy='" + createdBy + '\'' +
				", createdTime='" + createdTime + '\'' +
				", updatedBy='" + updatedBy + '\'' +
				", updatedTime='" + updatedTime + '\'' +
				", subType='" + subType + '\'' +
				", supplementParam='" + supplementParam + '\'' +
				'}';
	}

	public String identify(){
		return "DataSource{" +
				"type='" + type + '\'' +
				", host='" + host + '\'' +
				", port='" + port + '\'' +
				", userName='" + userName + '\'' +
				", pwd='" + pwd + '\'' +
				", service='" + service + '\'' +
				'}';
	}
}
