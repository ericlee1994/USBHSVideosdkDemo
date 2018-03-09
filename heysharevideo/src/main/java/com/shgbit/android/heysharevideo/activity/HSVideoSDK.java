package com.shgbit.android.heysharevideo.activity;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.Settings;
import com.shgbit.android.heysharevideo.addressaar.AddressCallBack;
import com.shgbit.android.heysharevideo.addressaar.Syntony;
import com.shgbit.android.heysharevideo.callback.HSSDKListener;
import com.shgbit.android.heysharevideo.interactmanager.ServerConfigCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.json.HotFixConfig;
import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.LoginInfo;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.PushConfig;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.XiaoYuConfig;
import com.shgbit.android.heysharevideo.json.YunDesktop;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;

/**
 * Created by Eric on 2018/2/12.
 */

public class HSVideoSDK {
    private static final String TAG = "HSVideoSDK";
    private String userName;
    private Context mContext;
    private static HSVideoSDK ourInstance;
    private HSSDKListener sdkListener;
    private NemoSDK nemoSDK;

    public static HSVideoSDK getInstance() {
        if (ourInstance == null) {
            ourInstance = new HSVideoSDK();
        }
        return ourInstance;
    }

    private HSVideoSDK() {
    }

    public void init(String serverIP, String userName, final Context context, HSSDKListener hssdkListener) {
        this.mContext = context.getApplicationContext();
        this.userName = userName;
        this.sdkListener = hssdkListener;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Common.SCREENHEIGHT = dm.widthPixels;
        Common.SCREENWIDTH = dm.heightPixels;
        try {
            ServerInteractManager.getInstance().init(serverIP, userName);
            ServerInteractManager.getInstance().getSystemConfig();
            ServerInteractManager.getInstance().setServiceConfigCallback(new ServerConfigCallback() {
                @Override
                public void configXiaoyu(XiaoYuConfig xiaoYuConfig) {
                    String url = xiaoYuConfig.getServiceUrl();
                    String id = xiaoYuConfig.getEnterpriseId();
                    if (url != null && id != null) {
                        if (url.startsWith("https://")) {
                            url = url.replace("https://", "");
                        } else if (url.startsWith("http://")) {
                            url = url.replace("http://", "");
                        }
                        Settings settings = new Settings(id);
                        settings.setPrivateCloudAddress(url);
                        nemoSDK = NemoSDK.getInstance();
                        nemoSDK.init(mContext, settings);
                        sdkListener.initState(true);
                    }
                }

                @Override
                public void configHotfix(HotFixConfig hotFixConfig) {

                }

                @Override
                public void configPush(PushConfig pushConfig) {

                }
            });
            ServerInteractManager.getInstance().setServerInteractCallback(serverInteractCallback);

        }catch (Exception e) {
            GBLog.e(TAG, e.toString());
            hssdkListener.initState(false);
        }

    }

    public void connect(String userName, String userPwd) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(userName);
        loginInfo.setPassword(userPwd);
        ServerInteractManager.getInstance().login(loginInfo);

    }

    public void disconnect() {
//        if (nemoSDK != null) {
//            nemoSDK.shutdown();
//        }
        ServerInteractManager.getInstance().logout();
    }

    public void startMeeting(String meetingNumber, String meetingPwd) {
        Intent intent = new Intent(mContext, VideoActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("password", meetingPwd);
        intent.putExtra("meetingName", "预约会议");
        intent.putExtra("number", meetingNumber);
        mContext.startActivity(intent);
    }

    public void finalizeSDK() {
        ServerInteractManager.getInstance().finalize();

    }
    public void openAddress(Context context, int layoutId, int layoutId2, String userName, AddressCallBack addressCallBack) {
        Syntony.getInstance().init(context, layoutId, layoutId2,userName);
        Syntony.getInstance().startAddressList(false, "vertical", false, null, null);
        Syntony.getInstance().setExCallBack(addressCallBack);
    }

    ServerInteractCallback serverInteractCallback = new ServerInteractCallback() {
        @Override
        public void onLogin(boolean result, String error, User user) {
            sdkListener.connectState(result);
        }

        @Override
        public void onLogout(boolean result, String error) {
            sdkListener.disconnectState(result);
        }

        @Override
        public void onCheckPwd(boolean result, String error) {

        }

        @Override
        public void onMotifyPwd(boolean result, String error) {

        }

        @Override
        public void onStartYunDesk(boolean result, String error, YunDesktop yunDesktop) {

        }

        @Override
        public void onEndYunDesk(boolean result, String error, YunDesktop yunDesktop) {

        }

        @Override
        public void onCreateMeeting(boolean result, String error, Meeting meeting) {

        }

        @Override
        public void onJoinMeeting(boolean result, String error) {

        }

        @Override
        public void onInviteMeeting(boolean success, String error) {

        }

        @Override
        public void onKickoutMeeting(boolean success, String error) {

        }

        @Override
        public void onQuiteMeeting(boolean success, String error) {

        }

        @Override
        public void onEndMeeting(boolean success, String error) {

        }

        @Override
        public void onReserveMeeting(boolean success, String error, Meeting meeting) {

        }

        @Override
        public void onDeleteMeeting(boolean success, String error) {

        }

        @Override
        public void onUpdateMeeting(boolean success, String error) {

        }

        @Override
        public void onBusyMeeting(boolean success, String error) {

        }

        @Override
        public void onMeetings() {

        }

        @Override
        public void eventUserStateChanged(RefuseInfo[] refuseInfos, TimeoutInfo[] timeoutInfos) {

        }

        @Override
        public void eventInvitedMeeting(InvitedMeeting meeting) {

        }

        @Override
        public void eventStartYunDesk(YunDesktop yunDesktop) {

        }

        @Override
        public void eventEndYunDesk() {

        }

        @Override
        public void eventStartWhiteBoard() {

        }

        @Override
        public void eventEndWhiteBoard() {

        }

        @Override
        public void eventDifferentPlaceLogin() {

        }

        @Override
        public void eventInvitingCancle(InviteCancledInfo ici) {

        }
    };

}
