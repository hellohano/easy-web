package com.nd.hano.web.webinterface;

import android.os.Handler;

import com.nd.hano.web.js.IActivityProxy;


/**
 * 提供Handler等对象给wrapper内部
 * Created by lyy on 16/2/29.
 */
public abstract class AbsActivity implements IActivityProxy {

    private Handler mHandler;

    public Handler getHandler(){
        if(mHandler == null){
            mHandler = new Handler(getContext().getMainLooper());
        }
        return mHandler;
    }

}
