package com.nd.hano.web.webinterface;

import android.webkit.JavascriptInterface;

/**
 * 注入到webview中js方法的接口
 * Created by lyy on 16/2/23.
 */
public interface IJsBridge {

    /**
     * 同步调用原生方法
     * @param component
     * @param method
     * @param param
     */
    @JavascriptInterface
    String invokeMethod(String component, String method, String param);

    /**
     * 异步调用原生方法
     * @param entry
     * @param method
     * @param param
     * @param callback
     */
    @JavascriptInterface
    String invokeMethodAsync(String entry, String method, String param, String callback) ;

    /**
     * 向原生触发广播事件
     * @param eventCode
     * @param jsonStr
     */
    @JavascriptInterface
    void triggerEvent(String eventCode, String jsonStr);

    /**
     * js 注册事件
     * @param eventCode
     * @param callback
     */
    @JavascriptInterface
    void registerListener(String eventCode, String callback);

    /**
     * js 取消注册事件
     * @param eventCode
     * @param callBack
     */
    @JavascriptInterface
    void unRegisterListener(String eventCode, String callBack);

    /**
     * 打印日志
     * @param paramString
     */
    @JavascriptInterface
    void printLog(String paramString);

    /**
     * NativeInterface注入成功
     */
    @JavascriptInterface
    void onInjectSuccess();

}
