package com.shgbit.android.heysharevideo.interactmanager;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface ServerRecordCallback {
    void startRecord (boolean result, String err);
    void endRecord (boolean result, String err);
}
