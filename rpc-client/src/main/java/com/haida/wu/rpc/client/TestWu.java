package com.haida.wu.rpc.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.haida.wu.rpc.client.config.AppConfig;
import com.haida.wu.rpc.facade.annotation.RemoteInvoke;
import com.haida.wu.rpc.facade.remote.api.UserServiceIFacade;
import com.haida.wu.rpc.model.business.User;
import com.haida.wu.rpc.utils.CommonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class TestWu {
	
	@RemoteInvoke
	private UserServiceIFacade userServiceIFacade;
	
	@Test
	public void testee() {
		User user = userServiceIFacade.getUserById("1");
		System.out.println(user.getAge());
	}
	
	@Test
	public void testURI() throws URISyntaxException {
		URL url = TestWu.class.getClassLoader().getResource("rpc.properties");
		System.out.println(url);
	}

}
