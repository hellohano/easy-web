package com.nd.hano.web;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nd.hano.web.js.IActivityProxy;
import com.nd.hano.web.js.IContainerProxy;
import com.nd.hano.web.js.IWebViewContainerListener;
import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.AbsNativeContext;
import com.nd.hano.web.webinterface.IWebView;
import com.nd.hano.web.webinterface.IWebViewContainer;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * 保存Context， callback id， webview
 * Created by hano on 2015/11/17.
 */
public class NativeContext extends AbsNativeContext {

    private static final String TAG = "NC";
    private WeakReference<AbsActivity> mActivity;
    private String mCallbackId;
    private IWebViewContainer mWebViewContainer;
    private HashMap<String, Object> contextObj;

    private IContainerProxy mContainerProxy;

    /**
     * 构造函数
     *  @param activity   当前activity
     * @param callbackId 如果当前要执行异步方法，则需要传入callbackId，native才能回调回js，
     *                   如果当前要执行同步方法，则不需要传入
     * @param webView    当前webview
     */
    public NativeContext(AbsActivity activity, String callbackId, IWebViewContainer webView) {
        mActivity = new WeakReference<>(activity);
        mCallbackId = callbackId;
        mWebViewContainer = webView;
        contextObj = new HashMap<>();
    }

    /**
     * 获取当前context,可能为空
     *
     * @return context
     */
    @Override
    public Context getContext() {
        if(mActivity.get()!=null){
            return mActivity.get().getContext();
        }else{
            return null;
        }
    }

    @Override
    public IContainerProxy getContainer() {
        if (mContainerProxy == null) {
            initContainerProxy();
        }
        return mContainerProxy;
    }

    @Override
    public Object getValue(String key) {
        return contextObj.get(key);
    }

    @Override
    public IActivityProxy getActivity() {
        if(mActivity.get()!=null){
            return mActivity.get();
        }else{
            return null;
        }
    }

    private void initContainerProxy() {
        mContainerProxy = new IContainerProxy() {

            @Override
            public void openFrame(View view, ViewGroup.LayoutParams layoutParams) {
                mWebViewContainer.openFrame(view, layoutParams);
            }

            @Override
            public void setContainerListener(IWebViewContainerListener listener) {
                mWebViewContainer.setWebViewContainerListener(listener);
            }

            @Override
            public void stopContainerRefresh() {
                if(mActivity.get() == null){
                    Log.i(TAG, "the activity impl is released");
                    return;
                }
                mActivity.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mWebViewContainer.stopRefresh();
                    }
                });
            }

            @Override
            public void handleTouchEventFromHtml() {
                mWebViewContainer.getWebView().handleTouchEventFromHtml();
            }

            @Override
            public void setContainerRefreshEnable(boolean b) {
                mWebViewContainer.setWebViewContainerRefreshEnable(b);
            }
        };
    }

    /**
     * 注入上下文对象，方便外部拓展
     *
     * @param key    key
     * @param object value
     */
    @Override
    public void putContextObject(String key, Object object) {
        contextObj.put(key, object);
    }

    /**
     * 注入上下文对象，方便外部拓展
     *
     * @param map {@link #putContextObject(String, Object)}
     */
    @Override
    public void putContextObjectMap(Map<String, Object> map) {
        contextObj.putAll(map);
    }

    /**
     * 执行callback success，注意只有在当前执行异步方法的时候才能调用该方法
     *
     * @param message 回调参数
     */
    @Override
    public void success(final String message) throws IllegalStateException {
        if(mActivity.get() == null){
            Log.i(TAG, "the activity impl is released");
            throw new IllegalStateException("the activity instance is released");
        }
        mActivity.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                String js = JsMethodUtil.toSuccessCallbackString(mCallbackId, message);
                IWebView webview = mWebViewContainer.getWebView();
                if (webview != null) {
                    webview.evaluateJavascript(js);
                }
            }
        });
    }

    /**
     * 执行callback success，注意只有在当前执行异步方法的时候才能调用该方法
     *
     * @param json 回调参数
     */
    @Override
    public void success(final JSONObject json) throws IllegalStateException {
        success(json.toString());
    }

    /**
     * 执行callback fail，注意只有在当前执行异步方法的时候才能调用该方法
     *
     * @param message 回调参数
     */
    @Override
    public void fail(final String message) throws IllegalStateException {
        if(mActivity.get() == null){
            Log.i(TAG, "the activity impl is released");
            throw new IllegalStateException("the activity instance is released");
        }
        mActivity.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                String js = JsMethodUtil.toErrorCallbackString(mCallbackId, message);
                IWebView webview = mWebViewContainer.getWebView();
                if (webview != null) {
                    webview.evaluateJavascript(js);
                }
            }
        });
    }

    /**
     * 执行callback fail，注意只有在当前执行异步方法的时候才能调用该方法
     *
     * @param json 回调参数
     */
    @Override
    public void fail(final JSONObject json) throws IllegalStateException {
        fail(json.toString());
    }

    @Override
    public void callListener(final String message) throws IllegalStateException {
        if(mActivity.get() == null){
            Log.i(TAG, "the activity impl is released");
            throw new IllegalStateException("the activity instance is released");
        }
        mActivity.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                String js = JsMethodUtil.toListenCallbackString(mCallbackId, message);
                IWebView webview = mWebViewContainer.getWebView();
                if (webview != null) {
                    webview.evaluateJavascript(js);
                }
            }
        });
    }

    @Override
    public void callListener(final JSONObject json) throws IllegalStateException {
        callListener(json.toString());
    }

    @Override
    public void notify(final String message) throws IllegalStateException {
        if(mActivity.get() == null){
            Log.i(TAG, "the activity impl is released");
            throw new IllegalStateException("the activity instance is released");
        }
        mActivity.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                String js = JsMethodUtil.toListenCallbackString(mCallbackId, message);
                IWebView webview = mWebViewContainer.getWebView();
                if (webview != null) {
                    webview.evaluateJavascript(js);
                }
            }
        });
    }

    @Override
    public void notify(JSONObject json) throws IllegalStateException {
        callListener(json.toString());
    }

}
