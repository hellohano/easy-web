package com.nd.hano.web.module;

import android.content.Intent;

import com.nd.hano.web.js.IContainerProxy;
import com.nd.hano.web.js.IJsModule;
import com.nd.hano.web.js.INativeContext;
import com.nd.hano.web.js.IWebViewContainerListener;
import com.nd.hano.web.js.annotation.JsMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 封装WebContainer相关能力
 * Created by lyy on 16/2/15.
 */
public class WebContainerModule implements IJsModule {

    public static final String MODULE_NAME = "webcontainer";
    protected final static String JSONOBJECT_NULL = "{}";

    @JsMethod
    public void openHardwareAccelerate(INativeContext context, JSONObject param) {
        context.getActivity().openHardwareAccelerate();
    }

    @JsMethod
    public void setWebViewContainerRefreshEnable(INativeContext context, JSONObject param) {
        try {
            boolean enable = param.getBoolean("enable");
            IContainerProxy containerProxy = context.getContainer();
            containerProxy.setContainerRefreshEnable(enable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JsMethod
    public void setWebViewContainerRefreshListener(final INativeContext context, JSONObject param) {
        IContainerProxy proxy = context.getContainer();
        proxy.setContainerListener(new IWebViewContainerListener() {
            @Override
            public void onRefresh() {
                context.callListener(JSONOBJECT_NULL);
            }
        });
    }

    @JsMethod
    public void stopWebViewContainerRefresh(final INativeContext context, JSONObject param) {
        IContainerProxy proxy = context.getContainer();
        proxy.stopContainerRefresh();
    }

    public void showWebImageViewer(INativeContext context, JSONObject param) {
        try {
            JSONArray array = param.getJSONArray("img_list");
            int index = param.getInt("index");
            ArrayList<String> imgList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                imgList.add(array.getString(i));
            }
//            Intent intent = new Intent(context.getContext(), ImageViewerActivity.class);
            Intent intent = new Intent();
            String sdCache = (String) context.getValue("sd_cache_dir");
            if (sdCache != null) {
                intent.putExtra("sd_cache_dir", sdCache);
            }
            intent.putStringArrayListExtra("img_list", imgList);
            intent.putExtra("index", index);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JsMethod
    public void handleTouchEvent(INativeContext context, JSONObject param) {
        context.getContainer().handleTouchEventFromHtml();
    }

    @Override
    public String getEntryName() {
        return MODULE_NAME;
    }
}
