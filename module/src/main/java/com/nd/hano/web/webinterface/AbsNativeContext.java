package com.nd.hano.web.webinterface;


import com.nd.hano.web.js.INativeContext;

import java.util.Map;

/**
 *
 * Created by hano on 16/2/23.
 */
public abstract class AbsNativeContext implements INativeContext {

    public abstract void putContextObjectMap(Map<String, Object> map);

    public abstract void putContextObject(String key, Object object);
}
