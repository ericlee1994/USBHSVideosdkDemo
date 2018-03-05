package com.shgbit.android.heysharevideo.widget;


import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.bean.CurrentMeetingInfo;
import com.shgbit.android.heysharevideo.bean.Meeting;
import com.shgbit.android.heysharevideo.bean.MeetingRecord;
import com.shgbit.android.heysharevideo.bean.Result;
import com.shgbit.android.heysharevideo.callback.IVideoRecordCallBack;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.shgbit.android.heysharevideo.util.VCUtils;
import com.wa.util.WAJSONTool;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class VideoRecord {
    private final String TAG = "VideoRecord";

    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private long startTime = 0;

    private Context mContext;

    private QueryStatusThread thread;

    private IVideoRecordCallBack iVideoRecordCallBack;

    private static VideoRecord mCollector;

    public enum INTERACTTYPE {START, STOP}

    public VideoRecord (Context context) {
        mContext = context;
    }

    public static VideoRecord getInstance(Context context) {
        if (mCollector == null) {
            mCollector = new VideoRecord(context);
        }
        return mCollector;
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
        String url = Common.SERVICEIP + "/meeting/record/start";
        new PostTask(url, WAJSONTool.toJSON(meetingRecord), INTERACTTYPE.START).execute();
    }

    public void endRecord (MeetingRecord meetingRecord) {
        String url = Common.SERVICEIP + "/meeting/record/end";
        new PostTask(url, WAJSONTool.toJSON(meetingRecord), INTERACTTYPE.STOP).execute();
    }

    private String httpGet(String url) {
        String result = "";
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (Throwable e) {
            GBLog.e(TAG, "httpGet Throwable:" + e.toString());
        }
        return result;
    }

    private String httpPost(String url, String json) {
        String result = "";
        try {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (Throwable e) {
            GBLog.e(TAG, "httpPost Throwable:" + e.toString());
        }
        return result;
    }

    private class PostTask extends AsyncTask<Void, Void, String> {
        private String mUrl = "";
        private String mObject;
        private INTERACTTYPE mType;

        public PostTask(String url, String object, INTERACTTYPE type) {
            mUrl = url;
            mObject = object;
            mType = type;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try {
                return httpPost(mUrl, mObject);
            } catch (Throwable e) {
                GBLog.e(TAG, "doInBackground Throwable: " + e.toString());
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                switch (mType) {
                    case START:
                        Result result1= null;
                        try {
                            result1 = WAJSONTool.parseObject(result, Result.class);
                        } catch (Throwable e) {
                            GBLog.e(TAG, "parse Result Throwable: " + VCUtils.CaughtException(e));
                        }

                        if (iVideoRecordCallBack != null) {
                            if (result1 == null || result1.getResult().equalsIgnoreCase("failed") == true) {
                                iVideoRecordCallBack.startRecord("failed", result1 == null ? mContext.getString(R.string.tip_40):result1.getFailedMessage());
                            } else {
                                iVideoRecordCallBack.startRecord("success", "");
                                startTime = SystemClock.elapsedRealtime();
                            }
                        }
                        break;
                    case STOP:
                        Result result2 = null;
                        try {
                            result2 = WAJSONTool.parseObject(result, Result.class);
                        } catch (Throwable e) {
                            GBLog.e(TAG, "parse Result Throwable: " + VCUtils.CaughtException(e));
                        }

                        if (iVideoRecordCallBack != null) {
                            if (result2 == null || result2.getResult().equalsIgnoreCase("failed") == true) {
                                iVideoRecordCallBack.endRecord("failed", result2 == null ? mContext.getString(R.string.tip_40):result2.getFailedMessage());
                            } else {
                                iVideoRecordCallBack.endRecord("success", "");
                                startTime = 0;
                            }
                        }
                        break;
                }
            } catch (Throwable e) {
                GBLog.e(TAG, "onPostExecute Throwable:" + e.toString());
            }
        }
    }

    private class QueryStatusThread extends Thread {
        @Override
        public void run() {
//            try {
//                String url = Common.SERVICEIP + "/currentMeeting?userName=" + Common.USERNAME + "&sessionId=" + Common.SESSIONID + "&sessionType=MobileState";
//                String result = "";
//                try {
//                    result = httpGet(url);
//                } catch (Throwable e) {
//                    GBLog.e(TAG, "httpGet Throwable: " + VCUtils.CaughtException(e));
//                }
//
//                CurrentMeetingInfo cmi = null;
//                try {
//                    cmi = WAJSONTool.parseObject(result, CurrentMeetingInfo.class);
//                } catch (Throwable e) {
//                    GBLog.e(TAG, "parse CurrentMeetingInfo Throwable: " + VCUtils.CaughtException(e));
//                }
//
//                if (iVideoRecordCallBack != null) {
//                    if (cmi != null && cmi.getMeeting() != null) {
//                        Meeting meeting = cmi.getMeeting();
//                        if (meeting.getCreatedUser().getUserName().equals(Common.USERNAME) == true) {
//                            iVideoRecordCallBack.initRecord(true, meeting.getRecord() == null?"":meeting.getRecord().getStatus(),meeting.getMeetingId());
//                        } else {
//                            iVideoRecordCallBack.initRecord(false, "", "");
//                        }
//                    }
//                }
//            } catch (Throwable e) {
//                GBLog.e(TAG, "QueryStatusThread Throwable:" + VCUtils.CaughtException(e));
//            }
        }
    }
}
