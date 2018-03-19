package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.google.gson.Gson;
import com.shgbit.android.heysharevideo.bean.CmtEnter;
import com.shgbit.android.heysharevideo.bean.CmtStatus;
import com.shgbit.android.heysharevideo.bean.CmtTracks;
import com.shgbit.android.heysharevideo.bean.Pictures;
import com.shgbit.android.heysharevideo.callback.ICtrlCallBack;
import com.shgbit.android.heysharevideo.callback.ISyncCallBack;
import com.shgbit.android.heysharevideo.contact.MeetingInfoManager;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;

import java.util.ArrayList;

import static com.shgbit.android.heysharevideo.activity.VideoActivity.mRecallMeeting;


/**
 * Created by Eric on 2017/12/14.
 */

public class RequestCtrl {
    private static final String TAG = "RequestCtrl";
    Context context;
    public ICtrlCallBack iCtrlCallBack;
    public ISyncCallBack iSyncCallBack;
    public RequestCtrl (Context context) {
        this.context = context;
    }

    public void uploadPic (String imgPath) {
        PicUpRequest picUpRequest = new PicUpRequest(context, Common.USERNAME, mRecallMeeting.getId(), imgPath);
        picUpRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "uploadPic onSuccess:" + result);
                iCtrlCallBack.getUploadState(true);
            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "uploadPic onFailure:" + result);
                iCtrlCallBack.getUploadState(false);
            }
        });
    }

    public void downloadPic() {
        PicDownRequest picDownRequest = new PicDownRequest(context, mRecallMeeting.getId());
        picDownRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "downloadPic onSuccess:" + result);
                Gson gson = new Gson();
                Pictures pictures = gson.fromJson(result, Pictures.class);
                ArrayList<String> picUrls = new ArrayList<>();
                for (int i = 0; i < pictures.getData().size(); i++) {
                    picUrls.add(pictures.getData().get(i).getUrl());
                }
                MeetingInfoManager.getInstance().PicShare(true, picUrls);
            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "downloadPic onFailure:" + result);
            }
        });
    }

    public void sync(){
        SyncRequest syncRequest = new SyncRequest(context, Common.USERNAME, mRecallMeeting.getId());
        syncRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "sync onSuccess:" + result);
                iSyncCallBack.onSuccess(result);
            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "sync onFailure:" + result);
                iSyncCallBack.onFailure(result);
            }
        });
    }

    public void getCmtStatus() {
        CmtStatusRequest cmtStatusRequest = new CmtStatusRequest(context, mRecallMeeting.getId());
        cmtStatusRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "getCmtStatus onSuccess:" + result);
                Gson gson = new Gson();
                CmtStatus cmtStatus = gson.fromJson(result, CmtStatus.class);
                if (cmtStatus.getStatus() == 1 && cmtStatus.getResource() != null && cmtStatus.getResource().size() > 0) {
                    getCmtTracks();
                    iCtrlCallBack.getResource(cmtStatus.getResource().get(0));
                }

            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "getCmtStatus onFailure:" + result);
            }
        });
    }

    public void getCmtTracks() {
        CmtTracksRequest cmtTracksRequest = new CmtTracksRequest(context, mRecallMeeting.getId());
        cmtTracksRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "getCmtTracks onSuccess:" + result);
                Gson gson = new Gson();
                CmtTracks cmtTracks = gson.fromJson(result, CmtTracks.class);
                if (cmtTracks.getTracks() != null && !cmtTracks.getTracks().equals("")) {
                    iCtrlCallBack.getTracks(cmtTracks.getTracks());
                }
            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "getCmtTracks onFailure:" + result);
            }
        });
    }

    public void postCmtEnter(String curResource) {
        CmtEnterRequest cmtEnterRequest = new CmtEnterRequest(context, mRecallMeeting.getId(), curResource);
        cmtEnterRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "postCmtEnter onSuccess:" + result);
                Gson gson = new Gson();
                CmtEnter cmtEnter = gson.fromJson(result, CmtEnter.class);
                if (cmtEnter.getHost() != null) {
//                    iCtrlCallBack.getHost(cmtEnter.getHost());
                }
            }


            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "postCmtEnter onFailure:" + result);
            }
        });
    }

    public void postCmtLeave(String curResource) {
        CmtLeaveRequest cmtLeaveRequest = new CmtLeaveRequest(context, mRecallMeeting.getId(), curResource);
        cmtLeaveRequest.httpSend(new BaseRequest.IHttpCallback() {
            @Override
            public void onSuccess(String result) {
                GBLog.e(TAG, "postCmtLeave onSuccess:" + result);
                iCtrlCallBack.exit();
            }

            @Override
            public void onFailure(String result) {
                GBLog.e(TAG, "postCmtLeave onFailure:" + result);
            }
        });
    }

    public void setiCtrlCallBack(ICtrlCallBack iCtrlCallBack){
        this.iCtrlCallBack = iCtrlCallBack;
    }

    public void setiSyncCallBack(ISyncCallBack iSyncCallBack) {
        this.iSyncCallBack = iSyncCallBack;
    }
}
