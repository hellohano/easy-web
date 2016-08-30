package com.nd.hano.web.event;

import java.util.Map;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface IEventDispatcher {
    public void triggerEvent(IComponentContext context, String event, Map param, IEventCenterManager from);
}
