package com.nd.hano.web.webinterface;

/**
 * 用于外部传入当前app的一些状态给webview
 * Created by hano on 2016/1/13.
 */
public interface IWebConfigManager {

    String getPackageName();

    String getAppName();

    String getLanguage();

    NET_TYPE getNetState();

    enum NET_TYPE {
        WIFI("WIFI"),
        WWAN("wwan"),
        NONE("none"),
        MOBILE2G("2g"),
        MOBILE3G("3g"),
        MOBILE4G("4g"),
        MOBILE5G("5g");

        private String value;

        NET_TYPE(String string) {
            this.value = string;
        }

        public String getValue() {
            return value;
        }
    }

}
