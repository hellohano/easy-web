package com.nd.hano.web;

import android.text.TextUtils;
import android.util.Log;

import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.IWebView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Carl on 15/11/4.
 */
public class JsEventCenter {
    private final static String TAG = "EventCenter";
    /**
     * 事件监听的存储容器；一个事件上面可以注册多个监听
     */
    private Map<String, List<String>> mEventMap;

    private AbsActivity mActivity;
    private IWebView mWebView;

    private static JsEventCenter mInstance;

    public JsEventCenter(AbsActivity ctx, IWebView webView) {
        mEventMap = new HashMap<String, List<String>>();
        mActivity = ctx;
        mWebView = webView;
    }

    /**
     * 注册一个事件监听
     *
     * @param eventName 事件名称
     * @param callback  事件回调
     */
    public synchronized void registerListener(String eventName, String callback) {
        List<String> list = mEventMap.get(eventName);
        if (list == null) {
            list = new LinkedList<String>();
            mEventMap.put(eventName, list);
        }

        if (!list.contains(callback)) {
            list.add(callback);
        }
    }

    /**
     * 解除监听
     *
     * @param eventName 事件名称
     * @param callback
     */
    public synchronized void unRegisterListener(String eventName, String callback) {
        List<String> list = mEventMap.get(eventName);
        if (list != null) {
            list.remove(callback);
        }
    }

    /**
     * 触发事件
     *
     * @param eventName the event to trigger
     * @param data      send to register
     */
    public synchronized void triggerEvent(String eventName, final String data) {
        if (TextUtils.isEmpty(eventName)) {
            Log.i(TAG, "event name is empty, just return");
            return;
        }

        List<String> list = mEventMap.get(eventName);
        if (list == null) {
            Log.i(TAG, "no listener found for event: " + eventName);
            return;
        }

        final List<String> cbs = new LinkedList<String>();
        cbs.addAll(list);
        mActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (String callback : cbs) {
                    String cb = String.format("javascript:%s", callback);
                    cb = cb.replace("==param==", data);
                    mWebView.evaluateJavascript(cb);
                }
            }
        });
    }

}
