package com.haida.wu.rpc_server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.haida.wu.rpc_server.config.AppConfig;

public class SpringServer {
	
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	}	
}
