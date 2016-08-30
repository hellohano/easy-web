package com.nd.hano.web;

import android.util.Log;

import com.nd.hano.web.js.annotation.JsMethod;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 用于构建js对象文本
 * http://doc.sdp.nd/index.php?title=JSBridge%E6%B3%A8%E5%85%A5%E5%8A%A8%E6%80%81%E7%94%9F%E6%88%90%E4%BB%A3%E7%A0%81
 * Created by hano on 2015/12/10.
 */
class JsEntryBuilder {

    private static final String TAG = "JsEntryBuilder";
    public String entryName;

    public Method[] methodList;

//    @BridgeJs(file = "./module/src/main/assets/NativeInterface.js", name = "Bridge")
//    private static String bridge = new com.nd.smartcan.webview.Bridge().content;
//
//    @BridgeJs(file = "./module/src/main/assets/index.js", name = "Promise")
//    private static String promiseJs = new Promise().content;

    public String build() {
        StringBuilder sb = new StringBuilder();
        String randomEntryClassName = UUID.randomUUID().toString();
        randomEntryClassName = "_" + randomEntryClassName.replace("-", "_");
        sb.append("function ").append(randomEntryClassName).append("(){return this;};\n");
        sb.append(randomEntryClassName).append(".prototype.isPromise = false;");
        sb.append(randomEntryClassName).append(".prototype.promise = function(){");
        sb.append("var obj = Bridge.promiseEntry[\"").append(entryName).append("\"];");
        sb.append("if(obj){ return obj;}");
        sb.append("else { obj = new ").append(randomEntryClassName).append("(); obj.isPromise = true; Bridge.promiseEntry[\"")
                .append(entryName).append("\"] = obj; return obj;}");
        sb.append("};");
        for (Method method : methodList) {
            if (method.getAnnotation(JsMethod.class) != null) {
                JsMethod bridge = method.getAnnotation(JsMethod.class);
                String methodName = method.getName();
                sb.append(randomEntryClassName).append(".prototype.").append(methodName).append("= function(param, callback){");
                sb.append("return Bridge.invokeCallExec(\"").append(entryName).append("\",\"")
                        .append(methodName).append("\", param, callback, this.isPromise, ")
                        .append(bridge.sync()).append(");");
                sb.append("};");
            }
        }
        sb.append("var entryObj = new ").append(randomEntryClassName).append("();");
        sb.append("Bridge.entry[\"").append(entryName).append("\"] = entryObj;");
        Log.i(TAG, "the inject js entry is : \n" + sb.toString());
        return sb.toString();
    }

}
