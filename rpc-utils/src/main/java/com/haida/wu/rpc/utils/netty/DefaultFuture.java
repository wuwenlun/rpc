package com.haida.wu.rpc.utils.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
	
	private Response response;
	
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public static ConcurrentHashMap<Integer, DefaultFuture> futures = new ConcurrentHashMap<>();
	
	public DefaultFuture(Request request) {
		futures.put(request.getId(), this);
	}
	
	public Response get(long time) {
		lock.lock();
		try {
			while(!isDone()) {
				condition.await(time, TimeUnit.MILLISECONDS);
				return response;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return response;
	}
	
	public boolean isDone() {
		if(null != response) {
			return true;
		}
		return false;
	}
	
	public static void receive(Response response) {
		if(null==response) return;
		DefaultFuture defaultFuture = DefaultFuture.futures.get(response.getId());
		if(null!=defaultFuture) {
			defaultFuture.lock.lock();
			try {
				defaultFuture.setResponse(response);
				defaultFuture.condition.signalAll();
			} finally {
				defaultFuture.lock.unlock();
			}
		}
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
}
