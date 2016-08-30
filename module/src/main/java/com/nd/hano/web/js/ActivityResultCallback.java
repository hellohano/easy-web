package com.nd.hano.web.js;

import android.content.Intent;

/**
 * 用于module中发起startActivityForResult时注册回调函数
 * Created by Administrator on 2016/3/1.
 */
public interface ActivityResultCallback {

    void onActivityResult(int resultCode, Intent data);
}
