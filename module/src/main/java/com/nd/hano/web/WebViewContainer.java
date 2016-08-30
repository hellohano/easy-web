package com.nd.hano.web;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nd.hano.web.event.IEventDispatcher;
import com.nd.hano.web.js.IWebViewContainerListener;
import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.IBridge;
import com.nd.hano.web.webinterface.IWebView;
import com.nd.hano.web.webinterface.IWebViewContainer;
import com.nd.hano.web.webinterface.IWebViewFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认Web容器实现
 * Created by carl on 15/11/30.
 */
public class WebViewContainer implements IWebViewContainer, CustomSwipeRefreshLayout.CanChildScrollUpCallback {

    private static final String CONTEXT_KEY = "sd_cache_dir";
    private static final String KEY_GET_PAGE_INFO_PARAM_EXT_MSG = "key_menu_extend_message";
    private static final String KEY_GET_PAGE_INFO_PARAM_EVENT = "event";

    private IEventDispatcher mDispatcher;
    private IWebViewContainerListener mWebContainerListener;
    private IWebViewFactory mWebViewFactory;
    private AbsActivity mActivity;
    private IWebView mWebView;
    private JsEventCenter mEventCenter;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mRootView;
    private Map<String, Object> mInjectedBridges = new HashMap<>();
    private String mSdCacheDir;

    public WebViewContainer(AbsActivity context) {
        mWebViewFactory = new DefWebViewFactory();
        mActivity = context;
    }

    public WebViewContainer(AbsActivity context, IWebViewFactory factory) {
        mWebViewFactory = factory;
        mActivity = context;
    }

    public WebViewContainer(AbsActivity context, String sdCacheDir) {
        mWebViewFactory = new DefWebViewFactory();
        mActivity = context;
        mSdCacheDir = sdCacheDir;
    }

    public WebViewContainer(AbsActivity context, String sdCacheDir, IWebViewFactory factory) {
        mWebViewFactory = factory;
        mActivity = context;
        mSdCacheDir = sdCacheDir;
    }

    /**
     * 注入Dispatcher
     *
     * @param dispatcher 注入的对象
     */
    public void setDispatcher(IEventDispatcher dispatcher) {
        mDispatcher = dispatcher;
    }

    @Override
    public IEventDispatcher getDispatcher() {
        return mDispatcher;
    }

    @Override
    public IWebView getWebView() {
        if (mWebView == null) {
            initWebView();
            initEventCenter();
        }
        return mWebView;
    }

    @Override
    public View getView() {
        if (mRootView == null) {
            mRootView = new FrameLayout(mActivity.getContext());
            mRootView.addView(getWebView().getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (mSwipeRefreshLayout == null) {
            mSwipeRefreshLayout = new CustomSwipeRefreshLayout(mActivity.getContext());
            mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
            mSwipeRefreshLayout.addView(mRootView);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mWebContainerListener != null) {
                        mWebContainerListener.onRefresh();
                    }
                }
            });
            //默认不开启下拉刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
        return mSwipeRefreshLayout;
    }

    @Override
    public void stopRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public JsEventCenter getJsEventCenter() {
        return mEventCenter;
    }

    @Override
    public void setWebViewContainerListener(IWebViewContainerListener listener) {
        mWebContainerListener = listener;
    }

    @Override
    public void getCurrentPageInfo(final Map param) {
        String eventName = param.get(KEY_GET_PAGE_INFO_PARAM_EVENT) + "";
        String extMsg = param.get(KEY_GET_PAGE_INFO_PARAM_EXT_MSG) + "";
        final String getPageInfoJs = JsMethodUtil.getPageInfo(eventName, extMsg);
        //如果没有注入过Bridge 要先去注入
        if (!mWebView.hasInjectBridge()) {
            mWebView.getJsBridge().listenBridgeJS(new IBridge.BridgeListener() {
                @Override
                public void onBridgeInjectSuccess() {
                    mActivity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.evaluateJavascript(getPageInfoJs);
                        }
                    });
                }
            });
            mWebView.evaluateJavascript(getFromAssets("mafwebview/NativeInterface.js"));
        } else {
            mWebView.evaluateJavascript(getPageInfoJs);
        }
    }

    private String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mActivity.getContext().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                Result.append(line).append("\n");
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void initWebView() {
        if (mWebViewFactory == null) {
            mWebViewFactory = new DefWebViewFactory();
        }
        mWebView = mWebViewFactory.createWebView(this, mActivity);
        if (mInjectedBridges.size() > 0) {
            mWebView.getJsBridge().injectToJs(mInjectedBridges);
        }
        if (mSdCacheDir != null) {
            mWebView.getJsBridge().injectContextObject(CONTEXT_KEY, mSdCacheDir);
        }
    }

    /**
     * 注入提供给js的native能力对象
     *
     * @param name         key
     * @param bridgeObject value
     */
    public void injectToJs(String name, Object bridgeObject) {
        mInjectedBridges.put(name, bridgeObject);
        if (mWebView != null) {
            mWebView.getJsBridge().injectToJs(name, bridgeObject);
        }
    }

    @Override
    public void openFrame(final View view, final ViewGroup.LayoutParams params) {
        mActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mRootView.addView(view, params);
            }
        });
    }

    private void initEventCenter() {
        mEventCenter = new JsEventCenter(mActivity, mWebView);
    }

    /**
     * @return 根据child状态决定是否触发刷新 true则不触发
     */
    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mWebView.getView().getScrollY() > 0 || mWebView.isHandleTouchEventFromHtml();
    }

    @Override
    public void setWebViewContainerRefreshEnable(final boolean enable) {
        mActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setEnabled(enable);
            }
        });
    }
}
