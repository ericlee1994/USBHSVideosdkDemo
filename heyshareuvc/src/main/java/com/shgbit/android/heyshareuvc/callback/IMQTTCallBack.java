package com.shgbit.android.heyshareuvc.callback;

/**
 * Created by Eric on 2017/12/8.
 */

public interface IMQTTCallBack {
    void mqtt(String topic, String msg);
}
