package org.screamingsandals.lib.nms.utils;

import java.lang.reflect.Method;

public class ClassMethod {
	private Method method;
	
	public ClassMethod(Method method) {
		this.method = method;
	}
	
	public Object invokeStatic(Object...params) {
		return invokeInstance(null, params);
	}
	
	public Object invokeInstance(Object instance, Object...params) {
		try {
			return method.invoke(instance, params);
		} catch (Throwable t) {
			return null;
		}
	}
	
	public Method getReflectedMethod() {
		return method;
	}
}
