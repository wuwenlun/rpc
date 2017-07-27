package com.haida.wu.rpc_server.mediator;

import java.lang.reflect.InvocationTargetException;

import com.haida.wu.rpc.model.request.ServerRequest;
import com.haida.wu.rpc.model.response.Response;
import com.haida.wu.rpc_server.process.BeanMethod;
import com.haida.wu.rpc_server.process.DistributeProcess;

public class Mediator {

	public Response communicate(ServerRequest request) {
		String command = request.getCommand();
		BeanMethod beanMethod = DistributeProcess.beanMethodMap.get(command);
		try {
			Object result = beanMethod.getMethod().invoke(beanMethod.getBean(), request.getContent());
			Response response = new Response();
			response.setContent(result);
			response.setId(request.getId());
			return response;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
