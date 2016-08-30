package com.nd.hano.web.event;

import java.util.Map;

/**
 * Created by mk on 16-2-29.
 */
public interface IEventCenterManager {
    void triggerEvent(IComponentContext context, String event, Map param);
    String getType();
}
