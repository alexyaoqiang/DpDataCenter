package com.wiscom.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
* @author gavin
* @version 创建日期:2020年6月19日
*/
@Configuration
@ConfigurationProperties(prefix = "thread-num")
public class CollectionProperties {
	private int item;
	private int thread;
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public int getThread() {
		return thread;
	}
	public void setThread(int thread) {
		this.thread = thread;
	}
	
}
