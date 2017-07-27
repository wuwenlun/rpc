package com.haida.wu.rpc.model.response;

public class Response {
	private int id;
	private Object content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	
	public String toString() {
		return "response--->id:"+id+" content:"+content;
	}
		
}
