package com.nd.hano.web.webinterface;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.ValueCallback;

/**
 * 默认webview能力接口
 * Created by caimk on 15-10-29 下午2:17.
 */
public interface IWebView {

    /**
     * 向webview注入的js桥接类的名称
     */
    public static final String ANDROID_INTERFACE_NAME = "AndroidInterface";

    /**
     * 加载url，注意不要通过这个方法执行js，执行js调用{@link #evaluateJavascript}
     *
     * @param url 被加载的url
     */
    void loadUrl(String url);

    /**
     * 获取webview
     *
     * @return webview view对象
     */
    View getView();

    /**
     * 获取JS桥接接口
     *
     * @return JsMethod
     */
    IBridge getJsBridge();

    /**
     * 执行js方法
     *
     * @param js 被执行的js
     */
    void evaluateJavascript(String js);

    void evaluateJavascript(String js, ValueCallback<String> resultCallback);

    void onPause();

    void onResume();

    boolean requestFocus();

    void stopLoading();

    String getCachedUrl();

    String getUrl();

    boolean canGoBack();

    void reload();

    void goBack();

    void setVisibility(int visibility);

    void setWebClient(IWebClient webClient);

    void destroy();

    boolean hasInjectBridge();

    void handleTouchEventFromHtml();

    boolean isHandleTouchEventFromHtml();

    interface IWebClient {
        void onLoadStared(String newUrl);

        /**
         * 正在执行加载。
         * ！！注意：这个方法在加载进度改变的时候都会调用。！！
         *
         * @param progress 当前加载进度，范围[0~100]
         */
        void isDoLoading(int progress);

        /**
         * 完成加载
         */
        boolean onLoadSuccess();

        void onLoadFail(String url, int errorCode);

        /**
         * 接收到标题
         * @param title 标题文字
         */
        void onReceivedTitle(String title);

        void onReceivedFavicon(Bitmap icon);

        boolean shouldOverrideUrlLoading(String url);

        void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);

        /**
         *
         * @param resourceUrl js or css or ...
         */
        void onLoadResource(String resourceUrl);
    }

}
