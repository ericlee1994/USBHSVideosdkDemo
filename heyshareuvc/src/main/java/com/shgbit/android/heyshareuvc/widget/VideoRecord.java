package com.shgbit.android.heyshareuvc.widget;


import android.content.Context;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.shgbit.android.heysharevideo.bean.CurrentMeetingInfo;
import com.shgbit.android.heysharevideo.bean.Meeting;
import com.shgbit.android.heysharevideo.bean.MeetingRecord;
import com.shgbit.android.heysharevideo.callback.IVideoRecordCallBack;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.interactmanager.ServerRecordCallback;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.shgbit.android.heysharevideo.util.VCUtils;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class VideoRecord {
    private static final String TAG = "VideoRecord";

    private long startTime = 0;

    private Context mContext;

    private QueryStatusThread thread;

    private IVideoRecordCallBack iVideoRecordCallBack;

    private static VideoRecord mCollector;

    public VideoRecord (Context context) {
        mContext = context;
        ServerInteractManager.getInstance().setServerRecordCallback(mCallback);
    }

    public static VideoRecord getInstance(Context context) {
        if (mCollector == null) {
            mCollector = new VideoRecord(context.getApplicationContext());
        }
        return mCollector;
    }

    public void finish () {
        try {
            if (thread != null) {
                thread.join(100);
                thread.interrupt();
                thread = null;
            }
            mCollector = null;
            ServerInteractManager.getInstance().removeServerRecordCallback(mCallback);
        } catch (Throwable e) {
            GBLog.e(TAG, "finalize Throwable:" + VCUtils.CaughtException(e));
        }
    }

    public void setCallBack(IVideoRecordCallBack iV) {
        iVideoRecordCallBack = iV;
    }

    public long getStartTime () {
        return startTime;
    }

    public void startQueryStatusThread() {
        try {
            if (thread != null) {
                thread.join(100);
                thread.interrupt();
                thread = null;
            }

            thread = new QueryStatusThread();
            thread.start();
        } catch (Throwable e) {
            GBLog.e(TAG, "startThread Throwable:" + VCUtils.CaughtException(e));
        }
    }

    public void startRecord (MeetingRecord meetingRecord) {
        ServerInteractManager.getInstance().startRecord(meetingRecord);
    }

    public void endRecord (MeetingRecord meetingRecord) {
        ServerInteractManager.getInstance().endRecord(meetingRecord);
    }

    private ServerRecordCallback mCallback = new ServerRecordCallback() {
        @Override
        public void startRecord(boolean result, String err) {
            if (iVideoRecordCallBack != null) {
                iVideoRecordCallBack.startRecord(result, err);
                if (result) {
                    startTime = SystemClock.elapsedRealtime();
                }
            }
        }

        @Override
        public void endRecord(boolean result, String err) {
            if (iVideoRecordCallBack != null) {
                iVideoRecordCallBack.endRecord(result, err);
                if (result) {
                    startTime = 0;
                }
            }
        }
    };

    private class QueryStatusThread extends Thread {
        @Override
        public void run() {
            try {
                String result = "";
                try {
                    result = ServerInteractManager.getInstance().getSyncCurrentMeeting();
                } catch (Throwable e) {
                    GBLog.e(TAG, "httpGet Throwable: " + VCUtils.CaughtException(e));
                }

                CurrentMeetingInfo cmi = null;
                try {
                    cmi = new Gson().fromJson(result, CurrentMeetingInfo.class);
                } catch (Throwable e) {
                    GBLog.e(TAG, "parse CurrentMeetingInfo Throwable: " + VCUtils.CaughtException(e));
                }

                if (iVideoRecordCallBack != null) {
                    if (cmi != null && cmi.getMeeting() != null) {
                        Meeting meeting = cmi.getMeeting();
                        if (meeting.getCreatedUser().getUserName().equals(Common.USERNAME) == true) {
                            iVideoRecordCallBack.initRecord(true, meeting.getRecord() == null?"":meeting.getRecord().getStatus(),meeting.getMeetingId());
                        } else {
                            iVideoRecordCallBack.initRecord(false, "", "");
                        }
                    }
                }
            } catch (Throwable e) {
                GBLog.e(TAG, "QueryStatusThread Throwable:" + VCUtils.CaughtException(e));
            }
        }
    }
}
