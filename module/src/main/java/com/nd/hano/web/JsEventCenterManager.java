package com.nd.hano.web;

import com.nd.hano.web.event.IComponentContext;
import com.nd.hano.web.event.IEventCenterManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于外部向js端触发事件
 * Created by hano on 2015/12/14.
 */
public class JsEventCenterManager implements IEventCenterManager {

    private static JsEventCenterManager sInstance;
    private List<JsEventCenter> mEventCenterList;

    private JsEventCenterManager() {
        mEventCenterList = new ArrayList<>();
    }

    public static JsEventCenterManager getInstance() {
        if (sInstance == null) {
            sInstance = new JsEventCenterManager();
        }
        return sInstance;
    }

    public void registerEventeCenter(JsEventCenter eventCenter) {
        mEventCenterList.add(eventCenter);
    }

    public void unRegisterEventCenter(JsEventCenter eventCenter) {
        mEventCenterList.remove(eventCenter);
    }

    public void triggerEvent(String eventCode, String param) {
        for (JsEventCenter eventCenter : mEventCenterList) {
            eventCenter.triggerEvent(eventCode, param);
        }

    }

    @Override
    public void triggerEvent(IComponentContext context, String event, Map param) {
        // TODO: 16/3/17
        for (JsEventCenter eventCenter : mEventCenterList) {
            if (param != null) {
                JSONObject json = new JSONObject(param);
                eventCenter.triggerEvent(event, json.toString());
            } else {
                eventCenter.triggerEvent(event, "{}");
            }
        }
    }

    @Override
    public String getType() {
        return "http";
    }
}
