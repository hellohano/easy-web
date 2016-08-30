package com.nd.hano.web.js;

import java.util.Map;

/**
 * 有动态添加菜单需求的Activity需要实现该接口
 * Created by lyy on 16/7/20.
 */
public interface IMenuRegisterListener {

    boolean registerMenu(Map<String, String> params);

    boolean unRegisterMenu(String menuId);
}
