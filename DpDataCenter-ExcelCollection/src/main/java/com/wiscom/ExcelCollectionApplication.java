package com.wiscom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients 
@SpringCloudApplication
public class ExcelCollectionApplication 
{
	private static Logger log = LoggerFactory.getLogger(ExcelCollectionApplication.class);
	
    public static void main( String[] args )
    {
    	SpringApplication.run(ExcelCollectionApplication.class);
    	log.info("ExcelCollectionApplication started!");
    }
}
