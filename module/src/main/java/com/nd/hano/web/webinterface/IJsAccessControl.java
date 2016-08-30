package com.nd.hano.web.webinterface;

/**
 * Created by carl on 16/1/4.
 */
public interface IJsAccessControl {
    /**
     * 当前h5页面是否允许访问指定的方法
     * @param entry 注入的入口名
     * @param method 注入的方法名
     * @param srcUrl 当前页面url
     * @return true if allow, false if else
     */
    boolean allowAccessMethod(String entry, String method, String srcUrl);

}
