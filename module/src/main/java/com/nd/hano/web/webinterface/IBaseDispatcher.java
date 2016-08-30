package com.nd.hano.web.webinterface;

/**
 * 消息派发模块基础接口
 * Created by hano on 2015/11/30.
 */
public interface IBaseDispatcher {

    /**
     * 广播事件。异步调用，不阻塞调用者线程，可以有多个“订阅者"
     * @param eventCode 事件码
     * @param eventObject 消息对象
     * @return 消息是否派发成功
     */
    boolean triggerEvent(String eventCode, Object eventObject);

}
