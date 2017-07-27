package com.haida.wu.rpc_server.process;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.haida.wu.rpc.utils.CommonUtils;
import com.haida.wu.rpc_server.annotation.Remote;
import com.haida.wu.rpc_server.annotation.RemoteMethod;

@Component
public class DistributeProcess implements BeanPostProcessor{
	
	public static Map<String,BeanMethod> beanMethodMap = new HashMap<>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		Class<?> beanClazz = bean.getClass();
		if(beanClazz.isAnnotationPresent(Remote.class)) {
			Method[] methods = beanClazz.getMethods();
			for(Method method : methods) {
				RemoteMethod remoteMethod = method.getAnnotation(RemoteMethod.class);
				if(null!=remoteMethod) {
					String methodName = method.getName();
					String key = beanName+CommonUtils.SEPARATOR+methodName;
					BeanMethod beanMethod = new BeanMethod();
					beanMethod.setBean(bean);
					beanMethod.setMethod(method);
					beanMethodMap.put(key, beanMethod);
				}
			}
		}
		
		return bean;
	}

}
