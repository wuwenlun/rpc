package com.haida.wu.rpc_server.service.api;

import com.haida.wu.rpc.model.business.User;
import com.haida.wu.rpc_server.annotation.Remote;

@Remote
public interface UserServiceI {
	
	public User getUserById(String id);
	
}
