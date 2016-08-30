package com.nd.hano.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.IBridge;
import com.nd.hano.web.webinterface.IJsAccessControl;
import com.nd.hano.web.webinterface.IWebConfigManager;
import com.nd.hano.web.webinterface.IWebView;
import com.nd.hano.web.webinterface.IWebViewContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 系统默认WebView实现
 * Created by caimk on 15-10-22 下午7:59.
 */
public final class DefWebView extends WebView implements IWebView {
    public static final String ANDROID_INTERFACE_NAME = "AndroidInterface";
    private static final String TAG = "DefWebView";
    private WebSettings mWebSettings;
    private AbsActivity mActivity;
    private DefJsBridge mDefJsBridge;
    private IWebClient mWebClient;
    private String mCurrentTitle;
    private String mCachedUrl;
    private IWebConfigManager mWebConfigManager;
    private boolean mHandleTouchFromHtml;
    /**
     * 用于标记当前url是否注入过Bridge js，因为外部链接再加载过程中不会触发Bridge的注入
     */
    private boolean mHasBridgeInjected;

    /**
     * 存储url和title(<url, title>)
     */
    private Map<String, String> mDataMap = new HashMap<>();

    /**
     * @param container        webview的容器
     * @param activity         当前Activity代理
     * @param control          权限控制
     * @param webConfigManager 外部传入当前app的一些状态给webview
     */
    public DefWebView(IWebViewContainer container, AbsActivity activity, IJsAccessControl control, IWebConfigManager webConfigManager) {
        super(activity.getContext());
        mActivity = activity;
        mWebConfigManager = webConfigManager;
        mDefJsBridge = new DefJsBridge(container, activity, control);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.addJavascriptInterface(mDefJsBridge, ANDROID_INTERFACE_NAME);
        } else {
            //fix android 4.1 security issue
            super.removeJavascriptInterface("searchBoxJavaBridge_");
        }
        initWebSetting();
    }

    @Override
    public void addJavascriptInterface(Object object, String name) {
        Log.i(TAG, "Can't inject Javasrcipt interface from outside! ");
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void evaluateJavascript(String js) {
        evaluateJavascript(js, null);
    }

    @Override
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.evaluateJavascript(script, resultCallback);
        } else {
            script = "javascript:" + script;
            super.loadUrl(script);
        }
    }

    @Override
    public void setWebClient(IWebClient webClient) {
        mWebClient = webClient;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public String getCachedUrl() {
        return mCachedUrl;
    }

    @Override
    public IBridge getJsBridge() {
        return mDefJsBridge;
    }

    @Override
    public boolean hasInjectBridge() {
        return mHasBridgeInjected;
    }

    @Override
    public void handleTouchEventFromHtml() {
        Log.i(TAG, " handleTouchEventFromHtml ==== ");
        mHandleTouchFromHtml = true;
    }

    @Override
    public boolean isHandleTouchEventFromHtml() {
        return mHandleTouchFromHtml;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initWebSetting() {
        if (mWebSettings == null) {
            mWebSettings = getSettings();
        }
        updateUAString();
        setInitialScale(0);
        setVerticalScrollBarEnabled(false);
        //缓存的路径，设置为私有，只有本身可以使用
        String cacheDir = mActivity.getContext().getDir("database",
                Context.MODE_PRIVATE).getPath();
        // TODO: 2015/12/24 cordova 不设置的我也不设置 TCL个破手机

        // 可任意比例缩放
        mWebSettings.setUseWideViewPort(true);
        // 自适应屏幕大小
        mWebSettings.setLoadWithOverviewMode(true);
        // 两个手指缩放
        mWebSettings.setSupportZoom(true);
        // 可缩放
        mWebSettings.setBuiltInZoomControls(true);
        // 是否显示焦距进度条
        mWebSettings.setDisplayZoomControls(false);
        // 是否启用JavaScript
        mWebSettings.setJavaScriptEnabled(true);
        // 支持通过JavaScript打开新窗口
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        // 数据缓存路径
        mWebSettings.setAppCachePath(cacheDir);
        // 是否进行缓存
        mWebSettings.setAppCacheEnabled(false);
        // 数据库缓存路径
        mWebSettings.setDatabasePath(cacheDir);
        // 是否对数据库缓存
        mWebSettings.setDatabaseEnabled(true);
        // 是否开启本地存储
        mWebSettings.setDomStorageEnabled(true);
        // 文字编码格式
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //save the html favicon
        WebIconDatabase.getInstance().open(mActivity.getContext().getDir("htmlicon", Context.MODE_PRIVATE).getPath());
        // 设置缓存模式
        // 1、如果有网络，则根据cache-control决定是否去网络取数据，
        // 2、如果没有网络，则读取本地缓存
//        if (mIsNeedCache && judgeNetWorkStatus(mActivity)) {
//            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        } else {
//            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }

        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mWebClient != null) {
                    mWebClient.isDoLoading(newProgress);
                }
                if (newProgress == 100) {
                    String title = mDataMap.get(view.getUrl());
                    if (!TextUtils.isEmpty(title) && !title.equals(mCurrentTitle)) {
                        mCurrentTitle = title;
                        if (mWebClient != null) {
                            mWebClient.onReceivedTitle(title);
                        }
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                String url = view.getUrl();
                if (!TextUtils.isEmpty(title) && !title.equals(mCurrentTitle)) {
                    mCurrentTitle = title;
                    mDataMap.put(url, title);
                    if (mWebClient != null) {
                        mWebClient.onReceivedTitle(title);
                    }
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                if (mWebClient != null) {
                    mWebClient.onReceivedFavicon(icon);
                }
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.i(TAG, "js prompt: " + message);
                try {
                    JSONObject requestJson = new JSONObject(message);
                    String type = requestJson.getString("type");
                    switch (type) {
                        case "exec":
                            String entry = requestJson.getString("entry");
                            String method = requestJson.getString("method");
                            String args = null;
                            if (requestJson.has("args")) {
                                args = requestJson.getString("args");
                            }
                            if (!requestJson.isNull("callback")) {
                                String callbackId = requestJson.getString("callback");
                                mDefJsBridge.invokeMethodAsync(entry, method, args, callbackId);
                                result.confirm();
                            } else {
                                result.confirm(mDefJsBridge.invokeMethod(entry, method, args));
                            }
                            break;
                        case "log":
                            String msg = requestJson.getString("message");
                            Log.i(TAG, "webview print log : " + msg);
                            result.confirm();
                            break;
                        case "require":
                            String entryName = requestJson.getString("entry");
                            result.confirm(mDefJsBridge.require(entryName));
                            break;
                        case "trigger_event":
                            String code = requestJson.getString("code");
                            String param = requestJson.getString("param");
                            mDefJsBridge.triggerEvent(code, param);
                            result.confirm();
                            break;
                        case "register_event":
                            String eventName = requestJson.getString("eventName");
                            String callback = requestJson.getString("callback");
                            mDefJsBridge.registerListener(eventName, callback);
                            result.confirm();
                            break;
                        case "unregister_event":
                            String unregistetEventName = requestJson.getString("eventName");
                            String unregisterCallback = requestJson.getString("callback");
                            mDefJsBridge.unRegisterListener(unregistetEventName, unregisterCallback);
                            result.confirm();
                            break;
                        case "injectSuccess":
                            mDefJsBridge.onInjectSuccess();
                            result.confirm();
                            break;
                        default:
                            Log.i(TAG, "js prompt: can't find type in json");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        this.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mWebClient != null) {
                    boolean result = mWebClient.shouldOverrideUrlLoading(url);
                    if (result) {
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    if (WebViewUtil.filterBridgeUrl(url)) {
                        try {
                            Log.i(TAG, "***** shouldInterceptRequest url : " + url);
                            InputStream inputStream = mActivity.getContext().getAssets().open("mafwebview/NativeInterface.js");
                            mHasBridgeInjected = true;
                            return new WebResourceResponse("test/javascript", "UTF-8", inputStream);
                        } catch (IOException e) {
                            mHasBridgeInjected = false;
                            e.printStackTrace();
                        }
                    } else if (mWebClient != null) {
                        mWebClient.onLoadResource(url);
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (WebViewUtil.filterBridgeUrl(request.getUrl().toString())) {
                        Log.i(TAG, "***** shouldInterceptRequest : " + request.getUrl());
                        try {
                            InputStream inputStream = mActivity.getContext().getAssets().open("mafwebview/NativeInterface.js");
                            mHasBridgeInjected = true;
                            return new WebResourceResponse("test/javascript", "UTF-8", inputStream);
                        } catch (IOException e) {
                            mHasBridgeInjected = false;
                            e.printStackTrace();
                        }
                    } else if (mWebClient != null) {
                        mWebClient.onLoadResource(request.getUrl().toString());
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "on page start : " + url);
                mHasBridgeInjected = false;
                if (mWebClient != null) {
                    mWebClient.onLoadStared(url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mCachedUrl = url;
                if (mWebClient != null) {
                    mWebClient.onLoadSuccess();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mWebClient != null) {
                    mWebClient.onLoadFail(failingUrl, errorCode);
                }
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });

        this.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (mWebClient != null) {
                    mWebClient.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        });
    }

    private void updateUAString() {
        String uaString = mWebSettings.getUserAgentString();
        Log.i(TAG, "default ua : " + uaString);
        StringBuilder appendUa = new StringBuilder();
        appendUa.append(uaString).append("; SmartCanWebView/1.2.3 ");
        if (mWebConfigManager != null) {
            String appName = mWebConfigManager.getAppName() == null ? "" : mWebConfigManager.getAppName();
            String pkgName = mWebConfigManager.getPackageName() == null ? "" : mWebConfigManager.getPackageName();
            String language = mWebConfigManager.getLanguage() == null ? "" : mWebConfigManager.getLanguage();
            IWebConfigManager.NET_TYPE netType = mWebConfigManager.getNetState() == null ? IWebConfigManager.NET_TYPE.NONE : mWebConfigManager.getNetState();
            appendUa.append("NetType/").append(netType.getValue()).append(" Language/").append(language).append(" PackageName/").append(pkgName).append(" AppName/").append(appName);
        }
        mWebSettings.setUserAgentString(appendUa.toString());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, " webview dispatchTouchEvent " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean result = super.dispatchTouchEvent(ev);
                return mHandleTouchFromHtml || result;
            case MotionEvent.ACTION_UP:
                mHandleTouchFromHtml = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public void destroy() {
//        //验证内存泄漏问题
////        try {
////            super.onDetachedFromWindow();
////            super.destroy();
////        } catch (Exception e) {
////            e.printStackTrace();
//        }
//    }
}
