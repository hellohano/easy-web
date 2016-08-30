package com.nd.hano.web;

/**
 * 工具类类类
 * Created by lyy on 16/6/13.
 */
public class WebViewUtil {

    private WebViewUtil() {

    }

    /**
     * 为注入Bridge.js拦截特定url
     * 目前拦截的url为(第二个url为x5适配所需，x5内核中shouldInterceptRequest无法拦截到http://localhost/JsBridge.js请求)：
     * http://localhost/JsBridge.js
     * http://101.com
     *
     * @param url 要检测的url
     * @return 是否为注入Bridge.js的特定url
     */
    public static boolean filterBridgeUrl(String url) {
        return url.equals("http://localhost/JsBridge.js") || url.equals("http://101.com/JsBridge.js")
                || url.equals("https://101.com/JsBridge.js") || url.equals("https://localhost/JsBridge.js");
    }
}
