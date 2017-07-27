package com.haida.wu.rpc.facade.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.haida.wu.rpc.facade.annotation.InvokeName;
import com.haida.wu.rpc.facade.annotation.RemoteInvoke;
import com.haida.wu.rpc.utils.CommonUtils;
import com.haida.wu.rpc.utils.netty.Request;
import com.haida.wu.rpc.utils.netty.Response;
import com.haida.wu.rpc.utils.netty.RpcClient;

@Component
public class InvokeProxy implements BeanPostProcessor{

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> beanClazz = bean.getClass();
		Field[] fields = beanClazz.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RemoteInvoke.class)) {
				field.setAccessible(true);
				Enhancer enhancer = new Enhancer();
				final Class<?> type = field.getType();
				enhancer.setInterfaces(new Class[]{field.getType()});
				enhancer.setCallback(new MethodInterceptor() {
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
						Request request = new Request();
						String command = "";
						if(type.isAnnotationPresent(InvokeName.class)) {
							InvokeName invokeName = type.getAnnotation(InvokeName.class);
							command = invokeName.name();
						} else {
							command = defaultRemotePath(type.getName());
						}
						command += CommonUtils.SEPARATOR+method.getName();
						request.setCommand(command);
						request.setContent(args);
						Response response = RpcClient.send(request);
						return JSON.parseObject(response.getContent().toString(), method.getReturnType());
					}
				});
				try {
					field.set(bean, enhancer.create());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
	
	private String defaultRemotePath(String className) {
		String last = StringUtils.substringAfterLast(className, ".");
		last = StringUtils.substringBefore(last, "Facade");
		char beginChar = last.charAt(0);
		String begin = String.valueOf(beginChar);
		last = StringUtils.substring(last, 1, last.length());
		return begin.toLowerCase()+last+"mpl";
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
