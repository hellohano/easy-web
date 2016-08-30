package com.nd.hano.web;


import com.nd.hano.web.util.StringUtils;

/**
 * 对于在代码中执行的js方法统一管理
 * Created by lyy on 16/3/28.
 */
public class JsMethodUtil {

    private static final String JS_GET_CURRENT_PAGE_INFO = "var getCurrentPageInfo = function(eventCode) {\n" +
            "        var page_title = document.title;\n" +
            "        var page_description = document.getElementsByName('description')[0].content;\n" +
            "        //get html icon\n" +
            "        var ICON = \"icon\";\n" +
            "        var SHORTCUT_ICON = \"shortcut icon\";\n" +
            "        var APPLE = \"apple-touch-icon\";\n" +
            "        var APPLE_PRECOMPOSED = \"apple_touch_icon_precomposed\";\n" +
            "        var selectors = {\n" +
            "            \"link[rel='icon']\": ICON,\n" +
            "            \"link[rel='shortcut icon']\": SHORTCUT_ICON,\n" +
            "            \"link[rel='apple-touch-icon']\": APPLE,\n" +
            "            \"link[rel='apple-touch-icon-precomposed']\": APPLE_PRECOMPOSED\n" +
            "        };\n" +
            "\n" +
            "        var res = {};\n" +
            "        for (selector in selectors) {\n" +
            "            var icons = document.querySelectorAll(selector);\n" +
            "            if (icons.length) {\n" +
            "                var key = selectors[selector]\n" +
            "                if (key == APPLE || key == APPLE_PRECOMPOSED) {\n" +
            "                    res[key] = [];\n" +
            "                }\n" +
            "            }\n" +
            "            for (var i = 0; i < icons.length; i++) {\n" +
            "                var href = icons[i].href;\n" +
            "                if (res[selectors[selector]] instanceof Array) {\n" +
            "                    res[selectors[selector]].push(href);\n" +
            "                } else {\n" +
            "                    res[selectors[selector]] = href;\n" +
            "                }\n" +
            "\n" +
            "            }\n" +
            "        }\n";

    public static String getPageInfo(String eventName, String extMsg) {
        return JS_GET_CURRENT_PAGE_INFO +
                "        var param = { key_menu_extend_message :\"" + extMsg + "\", url: document.URL, title: page_title, description: page_description, icon: res[ICON], shortcut_icon: res[SHORTCUT_ICON], apple_touch_icon: res[APPLE], apple_touch_icon_precomposed: res[APPLE_PRECOMPOSED]};\n" +
                "        Bridge.trigger(eventCode, JSON.stringify(param));\n" +
                "    };\n" +
                "getCurrentPageInfo(\"" + eventName + "\");";
    }

    public static String toSuccessCallbackString(String callbackId, String message) {
        return "Bridge.callbackSuccess('" + callbackId + "','" + StringUtils.addSlashes(message)
                + "');";
    }

    public static String toErrorCallbackString(String callbackId, String message) {
        return "Bridge.callbackFail('" + callbackId + "', '" + StringUtils.addSlashes(message)
                + "');";
    }

    public static String toListenCallbackString(String callbackId, String message) {
        return "Bridge.callbackListen('" + callbackId + "', '" + StringUtils.addSlashes(message)
                + "');";
    }

    public static String onActivityResume() {
        return "if (typeof window.onPageShow === 'function') {\n" +
                "    window.onPageShow();\n" +
                "}";
    }

    public static String onActivityPause() {
        return "if (typeof window.onPageHide === 'function') {\n" +
                "    window.onPageHide();\n" +
                "}";
    }
}
