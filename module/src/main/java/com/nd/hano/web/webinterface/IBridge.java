package com.nd.hano.web.webinterface;

import java.util.Map;

/**
 * Bridge接口。定义了Natvie提供给JS的服务
 * Created by caimk on 15-10-29 下午8:45.
 */
public interface IBridge {
    /**
     * 注入原生方法对Javascript
     *
     * @param component
     * @param obj
     */
    void injectToJs(String component, Object obj);

    /**
     * 注入原生方法对Javascript
     *
     * @param bridgeMap
     */
    void injectToJs(Map<String, Object> bridgeMap);

    /**
     * 注入上下文变量
     *
     * @param key
     * @param obj
     */
    void injectContextObject(String key, Object obj);

    /**
     * 监听Bridge状态
     * @param listener
     */
    void listenBridgeJS(BridgeListener listener);

    void stopListenBridgeJs(BridgeListener listener);

    public interface BridgeListener {
        void onBridgeInjectSuccess();
    }
}
