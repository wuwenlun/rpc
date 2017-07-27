package com.haida.wu.rpc.utils.netty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import io.netty.channel.ChannelFuture;
import junit.framework.Assert;

public class ChannelManager {
	
	public static TreeMap<Integer,ChannelFuture> channels = new TreeMap<>();
	private static ReentrantReadWriteLock lock = null;
	private static ReadLock readLock = null;
	private static WriteLock writeLock = null;
	private static Map<ChannelFuture,Integer> position = new HashMap<>();
	static {
		lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}
	
	public static ChannelFuture get() {
		ChannelFuture channelFuture = null;
		try {
			readLock.lock();
			int size = channels.size();
			double random = Math.random()*size;
			BigDecimal bigDecimal = new BigDecimal(random);
			int index = bigDecimal.intValue();
			Integer number = channels.ceilingKey(index);
			if(number != null) {
				channelFuture = channels.get(number);
			}
		} finally {
			readLock.unlock();
		}
		return channelFuture;
	}
	
	public static void add(Integer weight,ChannelFuture channelFuture) {
		Assert.assertNotNull(weight);
		Assert.assertNotNull(channelFuture);
		try {
			writeLock.lock();
			Integer total = channels.lastKey();
			total += weight;
			channels.put(total, channelFuture);
			position.put(channelFuture, total);
		} finally {
			writeLock.unlock();
		}
	}
	
	public static void clear() {
		try {
			writeLock.lock();
			channels.clear();
		} finally {
			writeLock.unlock();
		}
	}
	
	private static Integer getPositionByChannel(ChannelFuture future) {
		int index;
		try {
			readLock.lock();
			index = position.get(future);
		} finally {
			readLock.unlock();
		}
		return index;
	}
	
	public static void replace(ChannelFuture old, ChannelFuture fresh) {
		try {
			writeLock.lock();
			Integer index = getPositionByChannel(old);
			channels.put(index, fresh);
		} finally {
			writeLock.unlock();
		}
	}

}
