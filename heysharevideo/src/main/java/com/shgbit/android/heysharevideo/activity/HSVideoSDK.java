package com.shgbit.android.heysharevideo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.log.L;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.Settings;
import com.shgbit.android.heysharevideo.addressaar.AddressCallBack;
import com.shgbit.android.heysharevideo.addressaar.Syntony;
import com.shgbit.android.heysharevideo.callback.HSSDKInstantListener;
import com.shgbit.android.heysharevideo.callback.HSSDKListener;
import com.shgbit.android.heysharevideo.callback.HSSDKReserveListener;
import com.shgbit.android.heysharevideo.interactmanager.ServerConfigCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.json.BusyMeetingInfo;
import com.shgbit.android.heysharevideo.json.CreateMeetingInfo;
import com.shgbit.android.heysharevideo.json.HotFixConfig;
import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.LoginInfo;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.PushConfig;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.ReserveInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.ValidateInfo;
import com.shgbit.android.heysharevideo.json.XiaoYuConfig;
import com.shgbit.android.heysharevideo.json.YunDesktop;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.wa.util.WALog;

import java.io.File;

/**
 * Created by Eric on 2018/2/12.
 * @author Eric
 */

public class HSVideoSDK {
    private static final String TAG = "HSVideoSDK";
    private String userName;
    private String settingNum;
    private String serverIP;
    private Context mContext;
    private static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();

    private HSSDKListener sdkListener;
    private HSSDKReserveListener hssdkReserveListener;
    private HSSDKInstantListener hssdkInstantListener;
    private NemoSDK nemoSDK;
    private Syntony syntony;

    static boolean hasStartMeeting = false;
    private boolean hasInstantMeeting = false;
    private boolean isMainView = false;
    private boolean isVideoRecord = false;
    private boolean isMic = false;
    private boolean isVideo = true;

    private HSVideoSDK() {
    }

    public static HSVideoSDK getInstance() {
        return Holder.mInstance;
    }

    private static class Holder {
        private static final HSVideoSDK mInstance = new HSVideoSDK();
    }

    public void init(String serverIP, String userName, Context context, HSSDKListener hssdkListener) {

        this.mContext = context.getApplicationContext();
        this.userName = userName;
        this.serverIP = serverIP;
        this.sdkListener = hssdkListener;
        writeLog(mContext);
        GBLog.i(TAG, "HSVideoSDK init.");
        ServerInteractManager.getInstance().setServerInteractCallback(serverInteractCallback);
//        checkID();
        try {
            ServerInteractManager.getInstance().init(serverIP, userName, mContext);
            ServerInteractManager.getInstance().getSystemConfig();
            ServerInteractManager.getInstance().setServiceConfigCallback(new ServerConfigCallback() {
                @Override
                public void configXiaoyu(XiaoYuConfig xiaoYuConfig, boolean result) {
                    if (result) {
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
                            GBLog.e(TAG, id + ", " + url);
                            nemoSDK = NemoSDK.getInstance();
                            nemoSDK.init(mContext, settings);
                            sdkListener.initState(true);
                        }
                    }else {
                        sdkListener.initState(false);
                        GBLog.e(TAG, "服务器获取小鱼配置信息失败！");
                    }
                }

                @Override
                public void configHotfix(HotFixConfig hotFixConfig) {

                }

                @Override
                public void configPush(PushConfig pushConfig) {

                }
            });
        }catch (Exception e) {
            sdkListener.initState(false);
            GBLog.e(TAG, "init:" + e.toString());
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Common.SCREENHEIGHT = dm.widthPixels;
        Common.SCREENWIDTH = dm.heightPixels;

    }

    public void connect(String userName, String userPwd) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(userName);
        loginInfo.setPassword(userPwd);
        ServerInteractManager.getInstance().login(loginInfo);

    }

    public void disconnect() {
        ServerInteractManager.getInstance().logout();
    }

    public void startMeeting(String meetingNumber, String meetingPwd, String meetingName, String settingNum) {
        this.settingNum = settingNum;

        parseSettingNum(settingNum);

        if (meetingName == null || meetingName.equals("")){
            meetingName = "默认会议";
        }

        Intent intent = new Intent(mContext, VideoActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("password", meetingPwd);
        intent.putExtra("meetingName", meetingName);
        intent.putExtra("number", meetingNumber);
        intent.putExtra("mainView", isMainView);
        intent.putExtra("videoRecord", isVideoRecord);
        intent.putExtra("isMic", !isMic);
        intent.putExtra("isVideo", !isVideo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        hasStartMeeting = true;
    }

    public void finalizeSDK() {
        ServerInteractManager.getInstance().finalize();
    }

    public void refuseMeeting(String meetingId, String userName, String inviter) {
        BusyMeetingInfo busyMeetingInfo = new BusyMeetingInfo();
        busyMeetingInfo.setMeetingId(meetingId);
        busyMeetingInfo.setUserName(userName);
        busyMeetingInfo.setInviter(inviter);
        ServerInteractManager.getInstance().busyMeeting(busyMeetingInfo);
    }

    public void openAddress(Context context, int layoutId, int layoutId2, String userName, AddressCallBack addressCallBack) {

		 if(syntony != null){
            syntony.destroy();
        }
		
	    syntony = new Syntony();
        Syntony syntony = new Syntony();
        syntony.init(context, layoutId, layoutId2,userName);
        syntony.startAddressList(false, "vertical", false, null, null);
        syntony.setExCallBack(addressCallBack);
    }

    public void startInstantMeeting(String createUser, String[] inviteUsers, String settingNum, HSSDKInstantListener hssdkInstantListener) {
        this.settingNum = settingNum;
        this.hssdkInstantListener = hssdkInstantListener;
        CreateMeetingInfo createMeetingInfo = new CreateMeetingInfo();
        createMeetingInfo.setCreatedUser(createUser);
        createMeetingInfo.setInvitedUsers(inviteUsers);
        ServerInteractManager.getInstance().createMeeting(createMeetingInfo);
        hasInstantMeeting = true;
    }

    public void createReservationMeeting(String createUser, String meetingName, String startTime, String endTime, String[] inviteUsers, HSSDKReserveListener hssdkReserveListener){
        this.hssdkReserveListener = hssdkReserveListener;
        ReserveInfo reserveInfo = new ReserveInfo();
        reserveInfo.setCreatedUser(createUser);
        reserveInfo.setMeetingName(meetingName);
        reserveInfo.setStartTime(startTime);
        reserveInfo.setEndTime(endTime);
        reserveInfo.setInvitedUsers(inviteUsers);
        ServerInteractManager.getInstance().reserveMeeting(reserveInfo);
    }


    private void parseSettingNum(String settingNum) {
        if (settingNum == null) {
           return;
        } else {
            char[] simpleNums = settingNum.toCharArray();

            for (int i = 0; i < simpleNums.length; i++) {
                if (i == 0) {
                    if (simpleNums[0] == '1') {
                        isMainView = true;
                    }else {
                        isMainView = false;
                    }
                }else if (i == 1) {
                    if (simpleNums[1] == '1'){
                        isVideoRecord = true;
                    }else {
                        isVideoRecord = false;
                    }
                }else if (i == 2) {
                    if (simpleNums[2] == '1') {
                        isMic = true;
                    } else {
                        isMic = false;
                    }
                }else if (i == 3) {
                    if (simpleNums[3] == '0') {
                        isVideo = false;
                    }else {
                        isVideo = true;
                    }
                }
            }

        }
        GBLog.e(TAG, "isMainView:" + isMainView + ", isVideoRecord:" + isVideoRecord + ", isMic:" + isMic  + ", isVideo:" + isVideo);
    }

    private void writeLog(Context context){

        try {
            String pathName = SDCARD_DIR + "/HSSDK/" + context.getPackageName() + "/log";
            File dir = new File(pathName);
            if (!dir.exists()){
                dir.mkdirs();
            }

            GBLog.filepath = SDCARD_DIR + "/HSSDK/" + context.getPackageName() + "/log";
            WALog.filepath = GBLog.filepath;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void checkID() {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
                    PackageManager.GET_META_DATA);
            String id = appInfo.metaData.getString("HS_APP_ID");
            if (id == null) {
                sdkListener.initState(false);
                GBLog.e(TAG, "APP_ID:null");
            }
            ValidateInfo validateInfo = new ValidateInfo();
            validateInfo.setPkgId(mContext.getPackageName());
            validateInfo.setAppId(id);
            GBLog.e(TAG, "pkgId:" + mContext.getPackageName() + ",appId:" + id);
            ServerInteractManager.getInstance().validate(validateInfo);
        }catch (Exception e) {
            GBLog.e(TAG, e.toString());
        }

    }

    private ServerInteractCallback serverInteractCallback = new ServerInteractCallback() {
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

            hssdkInstantListener.onCreateMeetng(result, error);

            if (result && hasInstantMeeting) {
                startMeeting(meeting.getMeetingId(), meeting.getPassword(),"", settingNum);
            }else {
                GBLog.e(TAG, "onCreateMeeting: false," + error );
            }
            hasInstantMeeting = false;

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
//            sdkListener.onReserveMeeting(success, meeting);
            hssdkReserveListener.onReserveMeeting(success, error, meeting);
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
            if (!hasStartMeeting) {
                sdkListener.inviteMeeting(meeting);
            }
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

        @Override
        public void onValidate(boolean result, String err) {
            if (result) {
                GBLog.e(TAG, "onValidate success");

            }else {
                sdkListener.initState(false);
                GBLog.e(TAG, "onValidate:" + err);
            }
        }
    };

}
