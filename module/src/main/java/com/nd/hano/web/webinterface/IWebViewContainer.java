package com.nd.hano.web.webinterface;

import android.view.View;
import android.view.ViewGroup;

import com.nd.hano.web.JsEventCenter;
import com.nd.hano.web.event.IEventDispatcher;
import com.nd.hano.web.js.IWebViewContainerListener;

import java.util.Map;

/**
 *
 * Created by hano on 2015/11/30.
 */
public interface IWebViewContainer {

    /**
     * 取得消息派发器
     *
     * @return
     */
    IEventDispatcher getDispatcher();

    /**
     * get IWebView
     *
     * @return
     */
    IWebView getWebView();

    /**
     * get root view, this view contains webview and native view;
     *
     * @return root view
     */
    View getView();

    /**
     * get js eventCenter
     *
     * @return
     */
    JsEventCenter getJsEventCenter();

    /**
     * 在webview上层显示native view
     *
     * @param view
     * @param params
     */
    void openFrame(View view, ViewGroup.LayoutParams params);

    /**
     * todo appfactory menu专供 回头要重构（＝（；￣ェ￣）＝）
     * 获取当前网页的title，desc，icon等信息
     */
    void getCurrentPageInfo(Map param);

    void setWebViewContainerListener(IWebViewContainerListener listener);

    void stopRefresh();

    void setWebViewContainerRefreshEnable(boolean enable);
}
