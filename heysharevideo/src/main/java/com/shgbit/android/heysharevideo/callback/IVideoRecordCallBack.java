package com.shgbit.android.heysharevideo.callback;


/**
 * Created by Administrator on 2017/11/1 0001.
 */

public interface IVideoRecordCallBack {
    void initRecord(boolean isShow, String status, String meetingId);
    void startRecord(String result, String error);
    void endRecord(String result, String error);
}
