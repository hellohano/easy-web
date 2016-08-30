package com.nd.hano.web.webinterface;

/**
 * webvview工厂
 * Created by carl on 15/11/3.
 */
public interface IWebViewFactory {

    /**
     * 创建IWebview
     * @param webViewContainer webview容器
     * @param activity 当前activity的代理
     * @return IWebview
     */
    IWebView createWebView(IWebViewContainer webViewContainer, AbsActivity activity);
    IJsAccessControl getAccessControl();
    IWebConfigManager getWebConfigManager();
}
