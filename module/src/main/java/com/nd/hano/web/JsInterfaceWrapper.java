package com.nd.hano.web;

import com.nd.hano.web.js.INativeContext;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 对于向js提供原生能力的对象，会在{@link DefJsBridge}中封装成该对象以方便Bridge进行调用
 * Created by hano on 2015/11/6.
 */
public class JsInterfaceWrapper {

    private Map<String, Method> methodMap = new HashMap<>();

    private Object mInstance;

    /**
     * 添加instance拥有的方法
     *
     * @param methodName method name，作为key
     * @param method     value
     */
    public void addMethod(String methodName, Method method) {
        Class<?>[] types = method.getParameterTypes();
        methodMap.put(getMethodKey(methodName, types), method);
    }

    /**
     * 执行instance中的方法，执行前要确保调用过{@link #createClassIntance(Object)}来创建instance和
     * 通过{@link #addMethod(String, Method)}添加要执行的方法
     *
     * @param methodName method name
     * @param param      method parameters
     * @return native方法要确保返回结果是json格式字符串
     */
    public String invokeMethod(String methodName, Object... param) {
        if (methodName == null || methodName.length() == 0 || mInstance == null) {
            return null;
        }
        Class[] types = new Class[2];
        types[0] = INativeContext.class;
        types[1] = JSONObject.class;

        Method method = getMethod(methodName, types);
        if (method == null) {
            return null;
        }
        Object ret = null;
        try {
            ret = method.invoke(mInstance, param);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (ret != null) {
            return ret.toString();
        }
        return "";
    }

    public void createClassIntance(Object instance) {
        mInstance = instance;
    }

    /**
     * 获取method
     *
     * @param methodName method name
     * @param types      method 参数
     * @return method
     */
    private Method getMethod(String methodName, Class<?>[] types) {
        return methodMap.get(getMethodKey(methodName, types));
    }

    private String getMethodKey(String methodName, Class<?>[] types) {
        String key = methodName;
        for (Class<?> cls : types) {
            key += ",";
            key += cls.getSimpleName();
        }
        return key;
    }
}

