package com.nd.hano.web.js;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

/**
 * 用于向下隔离，避免对原生业务开发者暴露真正的activity从而导致泄漏
 * Created by hano on 16/2/22.
 */
public interface IActivityProxy {

    void finish();

    void openHardwareAccelerate();

    Context getContext();

    void startActivityForResult(Intent intent, ActivityResultCallback callback);

    boolean registerMenu(Map<String, String> menuParams);

    boolean unRegisterMenu(String menuId);
}
