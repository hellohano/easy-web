package com.nd.hano.web.js;

import java.util.ArrayList;
import java.util.List;

/**
 * appFactory可以通过这个单例类拦截js调用native操作
 * Created by hano on 16/5/4.
 */
public final class InvokeDelegate {

    private static volatile InvokeDelegate sInstance;

    private InvokeDelegate() {
        mHandlerList = new ArrayList<>();
    }

    private static class DelegateHolder {
        public static final InvokeDelegate delegate = new InvokeDelegate();
    }

    public static InvokeDelegate getInstance() {
        return DelegateHolder.delegate;
    }

    private List<InvokeHandler> mHandlerList;


    public void addInvokeHandler(InvokeHandler handler) {
        mHandlerList.add(handler);
    }

    public void removeInvokeHandler(InvokeHandler handler) {
        mHandlerList.remove(handler);
    }

    public void handleInvoke(INativeContext nativeContext) {
        for (InvokeHandler handler : mHandlerList) {
            handler.handleInvoke(nativeContext);
        }
    }

    public interface InvokeHandler {

        public void handleInvoke(INativeContext nativeContext);
    }
}
