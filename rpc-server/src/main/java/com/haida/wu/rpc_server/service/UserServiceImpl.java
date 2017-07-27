package com.haida.wu.rpc_server.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.haida.wu.rpc.model.business.User;
import com.haida.wu.rpc_server.annotation.Remote;
import com.haida.wu.rpc_server.annotation.RemoteMethod;
import com.haida.wu.rpc_server.service.api.UserServiceI;

@Remote
public class UserServiceImpl implements UserServiceI {

	@RemoteMethod
	public User getUserById(String id) {
		if(StringUtils.isEmpty(id)) return null;
		if("1".equals(id)) {
			User user = new User();
			user.setAge(26);
			user.setId("1");
			user.setName("吴文伦");
			user.setBirthday(new Date());
			user.setRemark("我是一只小小小小鸟，想要飞，却怎么样也飞不高！！");
			return user;
		}
		if("2".equals(id)) {
			User user = new User();
			user.setAge(26);
			user.setId("2");
			user.setName("道恩强森");
			user.setBirthday(new Date());
			user.setRemark("wwe世界金腰带冠军。");
			return user;
		}
		return null;
	}

}
