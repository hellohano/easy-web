package com.nd.hano.web;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.nd.hano.web.js.INativeContext;
import com.nd.hano.web.js.InvokeDelegate;
import com.nd.hano.web.js.annotation.JsMethod;
import com.nd.hano.web.module.WebContainerModule;
import com.nd.hano.web.util.StringUtils;
import com.nd.hano.web.webinterface.AbsActivity;
import com.nd.hano.web.webinterface.AbsNativeContext;
import com.nd.hano.web.webinterface.IBridge;
import com.nd.hano.web.webinterface.IJsAccessControl;
import com.nd.hano.web.webinterface.IJsBridge;
import com.nd.hano.web.webinterface.IWebViewContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * JsBridge默认实现
 * Created by Carl on 15/11/04
 */
public final class DefJsBridge implements IBridge, IJsBridge {

    private static final String TAG = "DefJsBridge";
    private static final String PRESENTER_MANAGER_PREFIX = "_pm";
    protected final static String JSONOBJECT_NULL = "{}";
    protected final static String JSONOBJECT_ACCESSDENY = "{\"access_deny\":1}";
    protected final static String JSONOBJECT_ACCESSALLOW = "{\"access_deny\":0}";
    private IWebViewContainer mWebContainer;
    private Map<String, Object> mJsBridgeMap;
    private Map<String, JsInterfaceWrapper> mJsInterfaceWrapperMap;
    private Map<String, Object> mContextObjects;
    private AbsActivity mActivity;
    private IJsAccessControl mAccessControl;
    private List<BridgeListener> mListenerList;

    /**
     * 构造函数
     *
     * @param webViewContainer webview容器 {@link IWebViewContainer}
     * @param activity         当前activity
     */
    public DefJsBridge(IWebViewContainer webViewContainer, AbsActivity activity, IJsAccessControl control) {
        mWebContainer = webViewContainer;
        mJsBridgeMap = new HashMap<>();
        mJsInterfaceWrapperMap = new HashMap<>();
        mContextObjects = new HashMap<>();
        mActivity = activity;
        mAccessControl = control;
        mListenerList = new ArrayList<>();
        injectToJs(WebContainerModule.MODULE_NAME, new WebContainerModule());
    }

    /**
     * 注入提供给js的原生能力
     *
     * @param entry 注入js的原生对象名称
     * @param o     注入js的原生对象
     */
    @Override
    public void injectToJs(String entry, Object o) {
        if (entry.contains(",")) {
            String[] nameList = entry.split(",");
            for (String name : nameList) {
                mJsBridgeMap.put(name, o);
            }
        } else {
            mJsBridgeMap.put(entry, o);
        }
    }

    /**
     * 注入js能力
     *
     * @param bridgeMap 对象集合，{@link #injectToJs(String, Object)}
     */
    @Override
    public void injectToJs(Map<String, Object> bridgeMap) {
        mJsBridgeMap.putAll(bridgeMap);
    }

    /**
     * 注入当前上下文的自定义环境变量
     *
     * @param key key
     * @param obj value
     */
    @Override
    public void injectContextObject(String key, Object obj) {
        mContextObjects.put(key, obj);
    }

    /**
     * 同步执行向webview注入的native通道方法
     *
     * @param entry  实际执行的native class
     * @param method 实际执行的native method
     * @param param  参数
     * @return 方法返回的结果，必须是标准json格式
     */
    @Override
    @JavascriptInterface
    public String invokeMethod(String entry, String method, String param) {
        if (!mJsBridgeMap.containsKey(entry)) {
            return JSONOBJECT_NULL;
        }
        JSONObject obj = null;
        if (param != null && !param.equals("undefined")) {
            try {
                obj = new JSONObject(param);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            obj = new JSONObject();
        }
        if (mAccessControl != null && !mAccessControl.allowAccessMethod(entry, method, mWebContainer.getWebView().getCachedUrl())) {
            return JSONOBJECT_ACCESSDENY;
        }
        JsInterfaceWrapper wrapper = null;
        wrapper = mJsInterfaceWrapperMap.containsKey(entry) ? mJsInterfaceWrapperMap.get(entry) : addJsInterfaceWrapper(entry);
        final AbsNativeContext nativeContext = new NativeContext(mActivity, null, mWebContainer);
        nativeContext.putContextObjectMap(mContextObjects);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            mActivity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    InvokeDelegate.getInstance().handleInvoke(nativeContext);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (wrapper != null) {
                return wrapper.invokeMethod(method, nativeContext, obj);
            }
        } else {
            // TODO: 16/7/8 4.1下通过jspromot方法调用时，此时的thread为UIThread,可以不需要做线程处理 
            InvokeDelegate.getInstance().handleInvoke(nativeContext);
            if (wrapper != null) {
                return wrapper.invokeMethod(method, nativeContext, obj);
            }
        }

        return JSONOBJECT_NULL;
    }

    /**
     * 异步执行向webview注入的native通道方法
     *
     * @param entry    实际执行的native class
     * @param method   实际执行的native method
     * @param param    参数
     * @param callback 提供给原生的回调函数，会被封装在{@link NativeContext}
     */
    @Override
    @JavascriptInterface
    public String invokeMethodAsync(final String entry, final String method, final String param, final String callback) {
        if (mAccessControl != null && !mAccessControl.allowAccessMethod(entry, method, mWebContainer.getWebView().getCachedUrl())) {
            return JSONOBJECT_ACCESSDENY;
        }
        mActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                new JsAsyncTask(entry, method, callback).execute(param);
            }
        });
        return JSONOBJECT_NULL;
    }

    /**
     * 触发event，event通过{@link #registerListener(String, String)}注册
     *
     * @param eventCode 注册时传入的event code
     * @param param     参数
     */
    @Override
    @JavascriptInterface
    public void triggerEvent(String eventCode, String param) {
        //注意，当js调上这里时，当前Thread既不是UI thread也不是普通的thread，是js thread
        // TODO: 16/8/30
//        JsEventCenter eventCenter = mWebContainer.getJsEventCenter();
//        if (eventCenter != null) {
//            eventCenter.triggerEvent(eventCode, param);
//        }
//        IEventDispatcher dispatcher = mWebContainer.getDispatcher();
//        if (dispatcher != null) {
//            Json2Std std = new Json2Std(param);
//            dispatcher.triggerEvent(getIcomponentContext(), eventCode, std.getResultMap(), JsEventCenterManager.getInstance());
//        }
    }

//    private IComponentContext getIcomponentContext() {
//        return new IComponentContext() {
//
//            @Override
//            public ComponentType getComponentType() {
//                return ComponentType.Http;
//            }
//
//            @Override
//            public String getComponentId() {
//                // TODO: 16/3/17
//                return "test_http_id";
//            }
//
//            @Override
//            public Context getContext() {
//                return mActivity.getContext();
//            }
//        };
//    }

    /**
     * 注册event，js端可以通过{@link #triggerEvent(String, String)}来触发注册的事件
     *
     * @param eventCode code
     * @param callback  callbcak
     */
    @Override
    @JavascriptInterface
    public void registerListener(String eventCode, String callback) {
        JsEventCenter eventCenter = mWebContainer.getJsEventCenter();
        if (eventCenter != null) {
            eventCenter.registerListener(eventCode, callback);
        }
    }

    /**
     * 解除注册
     *
     * @param eventCode {@link #registerListener(String, String)}
     * @param callback  {@link #registerListener(String, String)}
     */
    @Override
    @JavascriptInterface
    public void unRegisterListener(String eventCode, String callback) {
        JsEventCenter eventCenter = mWebContainer.getJsEventCenter();
        if (eventCenter != null) {
            eventCenter.unRegisterListener(eventCode, callback);
        }
    }

    /**
     * 输出log
     *
     * @param str 输出的内容
     */
    @Override
    @JavascriptInterface
    public void printLog(String str) {
        Log.i(TAG, "webview print log: " + str);
    }

    @JavascriptInterface
    public String require(String entryName) {
        if (entryName != null && mJsBridgeMap.containsKey(entryName)) {
            Object object = mJsBridgeMap.get(entryName);
            JsEntryBuilder builder = new JsEntryBuilder();
            builder.entryName = entryName;
            builder.methodList = object.getClass().getMethods();
            return builder.build();
        }
        return "";
    }

    private JsInterfaceWrapper addJsInterfaceWrapper(String entry) {
        if (!mJsBridgeMap.containsKey(entry)) {
            return null;
        }
        Class jsInterfaceClass = mJsBridgeMap.get(entry).getClass();
        if (jsInterfaceClass == null) {
            return null;
        }
        JsInterfaceWrapper wrapper = new JsInterfaceWrapper();
        wrapper.createClassIntance(mJsBridgeMap.get(entry));
        Method[] methods = jsInterfaceClass.getMethods();
        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(JsMethod.class);
            if (annotation != null) {
                wrapper.addMethod(method.getName(), method);
            }
        }
        mJsInterfaceWrapperMap.put(entry, wrapper);
        return wrapper;
    }

    private class JsAsyncTask extends AsyncTask<String, Object, JsAsyncTask.AsyncTaskResult> {
        private String mEntry;
        private String methodName;
        private String mCallback;
        private AbsNativeContext mNativeContext;

        public JsAsyncTask(String entry, String methodName, String callback) {
            this.mEntry = entry;
            this.methodName = methodName;
            this.mCallback = callback;
        }

        public class AsyncTaskResult {
            public INativeContext context;
            public String result;

            public AsyncTaskResult(INativeContext context, String result) {
                this.context = context;
                this.result = result;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNativeContext = new NativeContext(mActivity, mCallback, mWebContainer);
            mNativeContext.putContextObjectMap(mContextObjects);
            mWebContainer.getWebView().getUrl();
            InvokeDelegate.getInstance().handleInvoke(mNativeContext);
        }

        @Override
        protected AsyncTaskResult doInBackground(String... params) {
            if (!mJsBridgeMap.containsKey(mEntry)) {
                return null;
            }
            JSONObject obj = null;
            if (params != null && params.length > 0 && params[0] != null && !"undefined".equals(params[0])) {
                try {
                    obj = new JSONObject(params[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                obj = new JSONObject();
            }
            JsInterfaceWrapper wrapper = null;
            wrapper = mJsInterfaceWrapperMap.containsKey(mEntry) ? mJsInterfaceWrapperMap.get(mEntry) : addJsInterfaceWrapper(mEntry);

            if (wrapper != null) {
                String result = wrapper.invokeMethod(methodName, mNativeContext, obj);
                return new AsyncTaskResult(mNativeContext, result);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult result) {
            try {
                String resultString = result.result;
                if (StringUtils.isEmpty(resultString)) {
                    return;
                }
                JSONObject resultJson = new JSONObject(result.result);
                if (!resultJson.has("access_deny")) {
                    INativeContext nativeContext = result.context;
                    nativeContext.success(result.result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void listenBridgeJS(BridgeListener listener) {
        mListenerList.add(listener);
    }

    @Override
    public void stopListenBridgeJs(BridgeListener listener) {
        mListenerList.remove(listener);
    }

    @Override
    @JavascriptInterface
    public void onInjectSuccess() {
        for (BridgeListener listener : mListenerList) {
            listener.onBridgeInjectSuccess();
        }
    }
}
