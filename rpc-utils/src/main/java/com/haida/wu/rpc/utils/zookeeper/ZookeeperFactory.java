package com.haida.wu.rpc.utils.zookeeper;

import java.util.Properties;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.haida.wu.rpc.utils.CommonUtils;
import com.haida.wu.rpc.utils.PropertiesUtil;

public class ZookeeperFactory {
	
	private static CuratorFramework client;
	
	public static CuratorFramework createClient(String configFilePath) {
		if(null!=client) {
			return client;
		}
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(500, 3);
		
		Properties properties = new PropertiesUtil(configFilePath).getProperties();
		String address = properties.getProperty(CommonUtils.ZOOKEEPER_SERVER_ADDRESS);
		
		client = CuratorFrameworkFactory.newClient(address, retryPolicy);
		client.start();
		return client;
	}
	
}
