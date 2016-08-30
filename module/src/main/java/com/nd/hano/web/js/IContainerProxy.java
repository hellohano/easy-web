package com.nd.hano.web.js;

import android.view.View;
import android.view.ViewGroup;

/**
 * 通过{@link INativeContext}对外提供WebContainer能力
 * Created by lyy on 16/2/14.
 */
public interface IContainerProxy {

    void openFrame(View view, ViewGroup.LayoutParams layoutParams);

    void setContainerListener(IWebViewContainerListener listener);

    void stopContainerRefresh();

    void setContainerRefreshEnable(boolean enable);

    void handleTouchEventFromHtml();
}