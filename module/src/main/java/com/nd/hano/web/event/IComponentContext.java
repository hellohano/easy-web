package com.nd.hano.web.event;

import android.content.Context;

/**
 * Created by carl on 2016/3/4.
 */
public interface IComponentContext {
    public enum ComponentType {Cmp, Http, React};
    public ComponentType getComponentType();
    public String getComponentId();
    public Context getContext();
}
