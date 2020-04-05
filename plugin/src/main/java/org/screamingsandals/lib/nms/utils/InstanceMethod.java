package org.screamingsandals.lib.nms.utils;

import java.lang.reflect.Method;

public class InstanceMethod extends ClassMethod {
	private Object instance;
	
	public InstanceMethod(Object instance, Method method) {
		super(method);
		this.instance = instance;
	}
	
	public Object invoke(Object...params) {
		return invokeInstance(instance, params);
	}
	
	public Object getInstance() {
		return this.instance;
	}
}
