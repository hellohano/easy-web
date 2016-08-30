package com.nd.hano.web;


import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.IJsAccessControl;
import com.nd.hano.web.webinterface.IWebConfigManager;
import com.nd.hano.web.webinterface.IWebView;
import com.nd.hano.web.webinterface.IWebViewContainer;
import com.nd.hano.web.webinterface.IWebViewFactory;

/**
 * 默认提供的Webview factory
 * Created by carl on 15/11/4.
 */
public class DefWebViewFactory implements IWebViewFactory {

    @Override
    public IWebView createWebView(IWebViewContainer container, AbsActivity activity) {
        return new DefWebView(container, activity, getAccessControl(), getWebConfigManager());
    }

    @Override
    public IJsAccessControl getAccessControl() {
        return null;
    }

    @Override
    public IWebConfigManager getWebConfigManager() {
        return null;
    }
}
