package com.haida.wu.rpc.model.request;

public class ServerRequest {
	
	private int id;
	
	private Object[] content;
	
	private String command;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object[] getContent() {
		return content;
	}

	public void setContent(Object[] content) {
		this.content = content;
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String toString() {
		return "serverRequest--->id:"+id+" content:"+content;
	}
	
}
