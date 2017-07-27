package com.haida.wu.rpc.model.request;

import java.util.concurrent.atomic.AtomicInteger;

public class Request {
	
	private int id;
	private Object content;
	private static AtomicInteger aid = new AtomicInteger(0);
	private String command;
	
	public Request() {
		id = aid.incrementAndGet();
	}

	public int getId() {
		return id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
	
	public String toString() {
		return "request--->id:"+id+" content:"+content;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
}
