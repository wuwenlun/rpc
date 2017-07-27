package com.haida.wu.rpc_server.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
	
	private static CuratorFramework client;
	
	public static CuratorFramework createClient() {
		if(null!=client) {
			return client;
		}
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(500, 3);
		client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
		client.start();
		return client;
	}
	
}
