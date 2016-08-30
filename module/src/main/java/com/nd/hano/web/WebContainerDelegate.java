package com.nd.hano.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;


import com.nd.hano.web.js.ActivityResultCallback;
import com.nd.hano.web.js.IMenuRegisterListener;
import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.IWebViewContainer;

import java.util.Map;
import java.util.Random;

/**
 * 管理webview及相关内容和生命周期，以及ActivityResult事件的分发
 * Created by hano on 16/3/1.
 */
public class WebContainerDelegate {

    private AbsActivity mActivity;
    private IWebViewContainer mWebContainer;
    private ActivityResultCallback mCallback;
    /**
     * fixme
     * 这个标示位用来暂时作为一个workaround，用于解决当app factory中 webViewActivity被放置于
     * containerActivity中作为tab时真正的activity是containerActivity导致startActivityForResult
     * 无法返回结果的问题，后面要通过修改整个startActivityForResult传递机制来解决这个问题。
     */
    private boolean mActivityInOtherContainer;

    public WebContainerDelegate(Activity activity) {
        mActivity = createActivityProxy(activity);
        mWebContainer = createWebViewContainer();
    }

    public IWebViewContainer getWebContainer() {
        return mWebContainer;
    }

    public void onActivityResume() {
        mWebContainer.getWebView().evaluateJavascript(JsMethodUtil.onActivityResume());
    }

    public void onActivityPause() {
        mWebContainer.getWebView().evaluateJavascript(JsMethodUtil.onActivityPause());
    }

    public void onActivityDestory() {

    }

    public void onActivityResult(int resultCode, Intent data) {
        if (mCallback != null) {
            mCallback.onActivityResult(resultCode, data);
        }
    }

    private IWebViewContainer createWebViewContainer() {
        return new WebViewContainer(mActivity);
    }

    private AbsActivity createActivityProxy(final Activity activity) {

        return new AbsActivity() {
            @Override
            public void finish() {
                activity.finish();
            }

            @Override
            public void openHardwareAccelerate() {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }

            @Override
            public Context getContext() {
                // TODO: 16/3/1 这里发现一个bug，在webview构造函数中如果传入Context而不是Activity，会导致网
                // 页中的alert方法失效
                return activity;
            }

            @Override
            public void startActivityForResult(Intent intent, ActivityResultCallback callback) {
                mCallback = callback;
                if (mActivityInOtherContainer && activity.getParent() != null) {
                    activity.getParent().startActivityForResult(intent, generateRandomRequestCode());
                } else {
                    activity.startActivityForResult(intent, generateRandomRequestCode());
                }
            }

            @Override
            public boolean registerMenu(Map<String, String> map) {
                return activity instanceof IMenuRegisterListener && ((IMenuRegisterListener) activity).registerMenu(map);
            }

            @Override
            public boolean unRegisterMenu(String s) {
                return activity instanceof IMenuRegisterListener && ((IMenuRegisterListener) activity).unRegisterMenu(s);
            }
        };
    }

    private int generateRandomRequestCode() {
        return new Random().nextInt(60000);
    }

    public boolean isActivityInOtherContainer() {
        return mActivityInOtherContainer;
    }

    public void setActivityInOtherContainer(boolean mActivityInOtherContainer) {
        this.mActivityInOtherContainer = mActivityInOtherContainer;
    }
}
