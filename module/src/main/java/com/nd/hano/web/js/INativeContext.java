package com.nd.hano.web.js;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

/**
 * 封装NativeContext
 * <p/>
 * Created by lyy on 16/2/14.
 */
public interface INativeContext {

    Context getContext();

    IActivityProxy getActivity();

    IContainerProxy getContainer();

    Object getValue(String key);

    void putContextObjectMap(Map<String, Object> map);

    void putContextObject(String key, Object object);

    void success(String message) throws IllegalStateException;

    void success(JSONObject json) throws IllegalStateException;

    void fail(String message) throws IllegalStateException;

    void fail(JSONObject json) throws IllegalStateException;

    @Deprecated
    void callListener(String message) throws IllegalStateException;
    @Deprecated
    void callListener(JSONObject json) throws IllegalStateException;

    void notify(String message) throws IllegalStateException;

    void notify(JSONObject json) throws IllegalStateException;



}
