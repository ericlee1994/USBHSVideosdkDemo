package com.shgbit.android.heysharevideo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.NemoKickOutListener;
import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKErrorCode;
import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.RosterWrapper;
import com.ainemo.sdk.otf.VideoInfo;
import com.google.gson.Gson;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.addressaar.Syntony;
import com.shgbit.android.heysharevideo.addressaar.VideoCallBack;
import com.shgbit.android.heysharevideo.bean.CallMeetingInfo;
import com.shgbit.android.heysharevideo.bean.CtrlStatus;
import com.shgbit.android.heysharevideo.bean.CtrlStatusParam;
import com.shgbit.android.heysharevideo.bean.DISCONNECT_STATE;
import com.shgbit.android.heysharevideo.bean.DISPLAY_MODE;
import com.shgbit.android.heysharevideo.bean.DisplayType;
import com.shgbit.android.heysharevideo.bean.MainImage;
import com.shgbit.android.heysharevideo.bean.MeetingRecord;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.SESSIONTYPE;
import com.shgbit.android.heysharevideo.bean.STATUS;
import com.shgbit.android.heysharevideo.bean.StatisticsInfo;
import com.shgbit.android.heysharevideo.bean.VI;
import com.shgbit.android.heysharevideo.callback.IModeCallBack;
import com.shgbit.android.heysharevideo.callback.IPhoneCallback;
import com.shgbit.android.heysharevideo.callback.IPopViewCallBack;
import com.shgbit.android.heysharevideo.callback.ITitleCallBack;
import com.shgbit.android.heysharevideo.callback.IVideoInviteCallBack;
import com.shgbit.android.heysharevideo.callback.IVideoViewCallBack;
import com.shgbit.android.heysharevideo.contact.MeetingInfoManager;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.json.BusyMeetingInfo;
import com.shgbit.android.heysharevideo.json.CancelInviteInfo;
import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InviteMeetingInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.JoinMeetingInfo;
import com.shgbit.android.heysharevideo.json.KickoutInfo;
import com.shgbit.android.heysharevideo.json.LoginInfo;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.QuiteMeetingInfo;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.SendInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.YunDesktop;
import com.shgbit.android.heysharevideo.request.RequestCtrl;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.shgbit.android.heysharevideo.util.Reason;
import com.shgbit.android.heysharevideo.util.VCUtils;
import com.shgbit.android.heysharevideo.widget.CustomImageView;
import com.shgbit.android.heysharevideo.widget.ModeDialog;
import com.shgbit.android.heysharevideo.widget.MyVideoVIew;
import com.shgbit.android.heysharevideo.widget.PopupOldView;
import com.shgbit.android.heysharevideo.widget.PopupView;
import com.shgbit.android.heysharevideo.widget.TitleLayout;
import com.shgbit.android.heysharevideo.widget.VCDialog;
import com.shgbit.android.heysharevideo.widget.VideoRecord;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import vulture.module.call.nativemedia.NativeDataSourceManager;

import static android.view.Window.FEATURE_NO_TITLE;

//import com.shgbit.android.whiteboard.CustomPaintView;
//import com.shgbit.android.whiteboard.ICustomViewCallBack;

/**
 * Created by Eric on 2017/9/21.
 *
 * @author Eric
 */

public class VideoActivity extends BaseActivity implements IPopViewCallBack, IPhoneCallback, ServerInteractCallback, CameraDialog.CameraDialogParent {

    private static final String TAG = "VideoActivity";

    public static CallMeetingInfo mRecallMeeting;

    private NemoSDK nemoSDK;
    private Toast mToast;
    private MyVideoVIew videoView;
    private VCDialog mDialog;
    private ModeDialog modeDialog;

    private InvitedMeeting invitedMeeting;
    private static final int CONNECT_NEMO_ERROR = 0x001;
    private static final int DIALOGINVITED = 0x003;
    private static final int HANGUP = 0x004;
    private static final int UPDATAVIEW = 0x005;
    private static final int AUDIOMODE = 0x006;
    private static final int TOAST = 0x007;
    private static final int CLOSEMIC = 0x008;
    private static final int CALL_NEMO_ERROR = 0x009;
    private static final int RE_MAKECALL = 0x011;
    private static final int HANGUP_NEMO_ERROR = 0x012;
    private static final int MEETING_TIME = 0x013;
    private static final int MAKE_CALL = 0x014;
    private static final int MESSAGE_MEMBER = 0x015;
    private static final int SHOW_HIDE_LAYOUT = 0x017;
    private static final int HIDE_HIDE_LAYOUY = 0x018;
    private static final int HIDE_OTHER_TITLE = 0x019;
    private static final int UPDATE_POPDATA = 0x020;
    private static final int NOCALLBACK_TIME = 8;
    private static final int ENTER_COMMENT = 0x021;
    private static final int EXIT_COMMENT = 0x022;
    private static final int PICUPLOADSTATE = 0x023;
    private static final int SENDCOMMENT = 0x024;
    private static final int CLEANPZ = 0x025;
    private static final int ClOSEVIDEO = 0x026;

    private static final int MSG_BTN_SWITCH_MIC = 0x100;
    private static final int MSG_BTN_SWITCH_VIDEO = 0x200;
    private static final int MSG_BTN_VOICE_MODE = 0x300;
    private static final int MSG_BTN_SWITCH_CAMERA = 0x400;
    private static final int MSG_BTN_VOICE_MODE_UNCHANGED = 0x500;

    private static String[] TRACKS;

    private boolean foregroundCamera = true;
    private boolean muteCamera = false;
    private boolean muteMic = true;
    private boolean isHangup = false;
    private boolean isGetTime = false;
    private boolean isInPIP = false;
    private boolean isModeChange = false;
    private boolean hasContent = false;
    private boolean autoContent = true;
    private boolean audioMode = false; //true 语音，false 视频
    private boolean openPic = false;
    private boolean inComment = false;
    private boolean hasComment = false;
    private boolean clickPizhu = false;
    private boolean picState = false;
    private boolean hasEndMqtt = false;
    private boolean isVideoRecord = false;
    private boolean isMainView = false;

    private ArrayList<MemberInfo> mScreenList;
    private ArrayList<MemberInfo> mOtherList;
    private ArrayList<MemberInfo> mUnjoinedList;
    private String[] mUserInvites;
    private String[] mUsers;
    private String mHangupwarn;
    private String curResource;
    private String clientId = Common.USERNAME + "?t_mobile";
    private int memberSum;
    private int joinSum;

    private FrameLayout frameLayout;
    private RelativeLayout mWholeLayout;
    private TitleLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private PopupOldView popupView;
    private DISPLAY_MODE lastMode;
    private DISPLAY_MODE lastModeFlag;

    private DISPLAY_MODE contentlastmode = DISPLAY_MODE.NOT_FULL_ONEFIVE;


    private DISCONNECT_STATE mExitState;

    private FragmentTransaction transaction;
    private RequestCtrl requestCtrl;
    //    private CustomPaintView mainView;
    private Syntony syntony;


    private final Object mSync = new Object();
    // for accessing USB and USB camera
    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;
    private TextureView mUVCCameraView;
    // for open&start / stop&close camera preview
    private Surface mPreviewSurface;
    private boolean isUvcCamera;
    private int currentCamera = 2;
    private int currentCamType = 0;
    private USBMonitor.UsbControlBlock mCtrlBlock;
    private boolean isFirstReceive = true;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GBLog.i(TAG, "VideoActivity onCreate");
        isHangup = false;
        muteMic = Common.ISMUTEMIC;
        audioMode = Common.ISAUDIOMODE;
        mExitState = DISCONNECT_STATE.NORMAL;
        requestCtrl = new RequestCtrl(this);

        mContext = this;

        Intent intentInfo = getIntent();
        final String number = intentInfo.getStringExtra("number");
        String password = intentInfo.getStringExtra("password");
        String meetingName = intentInfo.getStringExtra("meetingName");
        String username = intentInfo.getStringExtra("username");
        isMainView = intentInfo.getBooleanExtra("mainView", false);
        isVideoRecord = intentInfo.getBooleanExtra("videoRecord", false);
        muteMic = intentInfo.getBooleanExtra("isMic", true);
        muteCamera = intentInfo.getBooleanExtra("isVideo", false);
        GBLog.e(TAG, "isMainView:" + isMainView + ", isVideoRecord:" + isVideoRecord + ", closeMic:" + muteMic + ", closeVideo:" + muteCamera);
        Common.USERNAME = username;
        Common.PASSWORD = password;

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        Common.SCREENHEIGHT = display.heightPixels;
        Common.SCREENWIDTH = display.widthPixels;

        mUsers = intentInfo.getStringArrayExtra("users");

        mRecallMeeting = new CallMeetingInfo();
        mRecallMeeting.setId(number);
        mRecallMeeting.setPw(password);
        mRecallMeeting.setName(meetingName);

        nemoSDK = NemoSDK.getInstance();

        try {
            nemoSDK.shutdown();
            GBLog.i(TAG, "nemoSDK shutdown");
        } catch (Exception e) {
            GBLog.e(TAG, "nemoSDK.shutdown:" + e.toString());
        }

        ServerInteractManager.getInstance().setServerInteractCallback(this);

        Common.isNemoConnected = false;

        connectNemo(number, password);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_vc);
        initView();

        nemoSDK.setNemoSDKListener(mNemoSDKListener);
        nemoSDK.setNemoKickOutListener(mNemoKickOutListener);

        MeetingInfoManager.getInstance().setOnMeetingInfoUpdateListener(meetingInfoUpdateListener);
        MeetingInfoManager.getInstance().NemoChange(null, NemoSDK.getLocalVideoStreamID());

//        MQTTClient.getInstance().setIMqttCmtCallback(iMqttCmtCallback);
//        MQTTClient.getInstance().setIMqttEventCallBack(iMqttEventCallback);
//        MQTTClient.getInstance().setIMqttCtrlOperateCallback(iMqttCtrlOperateCallback);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean ret = MQTTClient.getInstance().connectMQTTServer(number);
//            }
//        }).start();

//        requestCtrl.sync();
//        requestCtrl.setiCtrlCallBack(ictrlCallback);
        syntony = new Syntony();
        syntony.init(this, R.id.video_fragment, R.id.llyt_button2, Common.USERNAME);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        if (isMainView) {
            MainImage mi = new MainImage();
            mi.setMeetingId(mRecallMeeting.getId());
            mi.setUserName(Common.USERNAME);
            ServerInteractManager.getInstance().setMainImage(mi);
        }

    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            GBLog.e(TAG, "UVC onAttach!!!");

            synchronized (mSync) {
                onDialogResult(true);
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            GBLog.e(TAG, "UVC onConnect!!!");
            isUvcCamera = true;
            mCtrlBlock = ctrlBlock;

            MeetingInfoManager.getInstance().LocalChange(true);

        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            // XXX you should check whether the comming device equal to camera device that currently using
            GBLog.e(TAG, "UVC onDisconnect!!!");
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    releaseCamera();
                }
            }, 0);
        }

        @Override
        public void onDettach(final UsbDevice device) {
            GBLog.e(TAG, "UVC onDettach!!!");

            isUvcCamera = false;

            MeetingInfoManager.getInstance().LocalChange(false);

            releaseCamera();

            isUvcCamera = false;
            currentCamera = 0;
            videoView.updateCamera(false);
            NemoSDK.getInstance().updateCamera(false);
            videoView.getmLocalVideoCell().notifyRender();

            videoView.requestLocalFrame();
            NemoSDK.getInstance().requestCamera();


        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };

    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled && mUSBMonitor != null) {
            final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getApplicationContext(), com.serenegiant.uvccamera.R.xml.device_filter);
            if (filter != null && filter.size() > 0) {
                List<UsbDevice> devices = mUSBMonitor.getDeviceList(filter.get(0));
                if (devices != null && devices.size() > 0) {
                    mUSBMonitor.requestPermission(devices.get(0));
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // FIXME
                }
            });
        }
    }


    private synchronized void releaseCamera() {
        if (mUVCCamera != null) {
            try {
                mUVCCamera.close();
                mUVCCamera.destroy();
            } catch (final Exception e) {
                //
                e.printStackTrace();
            }
            mUVCCamera = null;
        }
        if (mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }
    }

    private synchronized void releaseUsbMonitor() {
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
    }


    private final IFrameCallback mIFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {

            frame.clear();

            int len = frame.capacity();

            int captureWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
            int captureHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;

            byte[] yuv = new byte[len];

            frame.get(yuv);

            if (NemoSDK.getInstance().getDataSourceId() != null) {
                NativeDataSourceManager.putVideoData(NemoSDK.getInstance().getDataSourceId(), yuv, captureWidth * captureHeight * 3 / 2,
                        captureWidth, captureHeight, 0, true);
            }
            NativeDataSourceManager.putVideoData("LocalPreviewID", yuv, captureWidth * captureHeight * 3 / 2,
                    captureWidth, captureHeight, 0, true);
        }
    };


    private VideoCallBack videoCallBack = new VideoCallBack() {
        @Override
        public void invite(User[] users) {
            GBLog.i(TAG, "##oninvite users##" + users.length);
            String[] userName = new String[users.length];
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < users.length; i++) {
                userName[i] = users[i].getUserName();
                arrayList.add(users[i].getUserName());
                mUserInvites = new String[arrayList.size()];
                arrayList.toArray(mUserInvites);
                mUsers = userName;
            }
            try {
                if (mRecallMeeting != null) {
                    InviteMeetingInfo inviteMeetingInfo = new InviteMeetingInfo();
                    inviteMeetingInfo.setInvitedUsers(userName);
                    inviteMeetingInfo.setMeetingId(mRecallMeeting.getId());
                    inviteMeetingInfo.setUserName(Common.USERNAME);
                    ServerInteractManager.getInstance().inviteMeeting(inviteMeetingInfo);
                }
            } catch (Exception e) {
                GBLog.e(TAG, "invite Throwable:" + VCUtils.CaughtException(e));
            }
        }

        @Override
        public void onDesFragment() {
            syntony.des();
        }

    };

    private MeetingInfoManager.OnMeetingInfoUpdateListener meetingInfoUpdateListener = new MeetingInfoManager.OnMeetingInfoUpdateListener() {
        @Override
        public void onMemberChanged(ArrayList<MemberInfo> mScreen, ArrayList<MemberInfo> mOther, ArrayList<MemberInfo> mUnjoined, int duration, boolean isMemberContent, String meetingName) {

            for (int i = 0; i < mScreen.size(); i++) {
                GBLog.e(TAG, "onMemberChanged isLocal["+ i +"]" + mScreen.get(i).isLocal());
                GBLog.e(TAG, "onMemberChanged isUvc[" + i + "]" + mScreen.get(i).isUvc());
                GBLog.e(TAG, "onMemberChanged getDataSourceID["+ i +"]" + mScreen.get(i).getDataSourceID());
                GBLog.e(TAG, "onMemberChanged getDisplayName["+ i +"]" + mScreen.get(i).getDisplayName());
            }
            if (mScreen != null && mScreen.size() > 0) {
                if (!mScreen.get(0).getDataSourceID().equalsIgnoreCase(NemoSDK.getLocalVideoStreamID())) {
//                    nemoSDK.focusVideoStream(mScreen.get(0).getDataSourceID());
                    nemoSDK.forceLayout(mScreen.get(0).getParticipantId());
                }
            }

            if (videoView != null) {
                videoView.updateViewList(mScreen, mOther);
            }
            if (mScreenList == null) {
                mScreenList = new ArrayList<>();
            }
            mScreenList.clear();
            for (int i = 0; i < mScreen.size(); i++) {
                mScreenList.add(new MemberInfo(mScreen.get(i)));
            }

            if (mOtherList == null) {
                mOtherList = new ArrayList<>();
            }
            mOtherList.clear();
            for (int i = 0; i < mOther.size(); i++) {
                mOtherList.add(new MemberInfo(mOther.get(i)));
            }

            if (mUnjoinedList == null) {
                mUnjoinedList = new ArrayList<>();
            }
            mUnjoinedList.clear();
            for (int i = 0; i < mUnjoined.size(); i++) {
                mUnjoinedList.add(new MemberInfo(mUnjoined.get(i)));
            }

            for (int i = 0; i < mScreenList.size(); i++) {
                GBLog.i(TAG, "ScreenList name:" + mScreenList.get(i).getDisplayName());
            }

            if (!isGetTime || isModeChange) {
                Message msg = Message.obtain(mUIHandler, MEETING_TIME, duration);
                msg.sendToTarget();
                isGetTime = true;
            }


            hasContent = isMemberContent;

            mUIHandler.sendEmptyMessage(UPDATE_POPDATA);
            mUIHandler.sendEmptyMessage(UPDATAVIEW);

//            if (isFirstReceive) {
//                videoView.requestLocalFrame();
//                NemoSDK.getInstance().requestCamera();
//                isFirstReceive = false;
//            }

        }

        @Override
        public void onMemberSizeChanged(int joinedSize, int memberSize) {
            memberSum = 0;
            memberSum = memberSize;
            joinSum = 0;
            joinSum = joinedSize;
            String tMember = joinSum + "/" + memberSum;

            Message msg = Message.obtain(mUIHandler, MESSAGE_MEMBER, tMember);
            msg.sendToTarget();

        }

        @Override
        public void onMemberCommentExit(MemberInfo mExit) {

            if (curResource.equals(mExit.getResId())) {
                if (hasComment) {
                    requestCtrl.postCmtLeave(curResource);
                    mUIHandler.sendEmptyMessage(EXIT_COMMENT);
                }
            }
        }
    };

    private NemoSDKListener mNemoSDKListener = new NemoSDKListener() {
        @Override
        public void onContentStateChanged(NemoSDKListener.ContentState contentState) {

        }

        @Override
        public void onCallFailed(int i) {
            Observable.just(i).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) {
                            mUIHandler.removeMessages(CALL_NEMO_ERROR);
                            String info = "n:unknown";
                            if (NemoSDKErrorCode.WRONG_PASSWORD == integer) {
                                info = "系统出错，请重启软件(n:wrong password)";
                            } else if (NemoSDKErrorCode.INVALID_PARAM == integer) {
                                info = "系统出错，请重启软件(n:wrong param)";
                            } else if (NemoSDKErrorCode.NETWORK_UNAVAILABLE == integer) {
                                info = "系统出错，请重启软件(n:net work unavailable)";
                            } else if (NemoSDKErrorCode.HOST_ERROR == integer) {
                                info = "系统出错，请重启软件(n:host error)";
                            }
                            Message msg = Message.obtain(mUIHandler, CALL_NEMO_ERROR, "呼叫失败：" + info);
                            msg.sendToTarget();
                        }
                    });
        }

        @Override
        public void onNewContentReceive(Bitmap bitmap) {

        }

        @Override
        public void onCallStateChange(CallState callState, final String s) {
            Observable.just(callState).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CallState>() {
                        @Override
                        public void accept(CallState callState) {
                            switch (callState) {
                                case CONNECTING:
                                    mUIHandler.removeMessages(CALL_NEMO_ERROR);
                                    mUIHandler.removeMessages(RE_MAKECALL);
                                    hideSoftKeyboard();
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    mUIHandler.sendEmptyMessage(MSG_BTN_VOICE_MODE_UNCHANGED);


                                    MeetingInfoManager.getInstance().StateChange(mUsers, STATUS.INVITING);


                                    GBLog.i(TAG, "Nemo call is connecting :" + s);
                                    break;
                                case CONNECTED:
                                    GBLog.e(TAG, "Nemo call has connected" + s);
                                    hideSoftKeyboard();
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                                    mUIHandler.sendEmptyMessage(AUDIOMODE);
                                    mUIHandler.removeMessages(CALL_NEMO_ERROR);
                                    if (muteMic == true) {
                                        mUIHandler.sendEmptyMessage(CLOSEMIC);
                                    }
                                    if (muteCamera == true) {
                                        mUIHandler.sendEmptyMessage(ClOSEVIDEO);
                                    }

                                    if (isVideoRecord) {
                                        GBLog.e(TAG, "Video record at connected");
                                        MeetingRecord meetingRecord = new MeetingRecord();
                                        meetingRecord.setMeetingId(mRecallMeeting.getId());
                                        VideoRecord.getInstance(mContext).startRecord(meetingRecord);
                                    }
//                                    isFirstReceive = true;


//                                    requestCtrl.getCmtStatus();


//                                    Runnable runnable = new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            GBLog.e(TAG, nemoSDK.getStatisticsInfo());
//                                            parseData(nemoSDK.getStatisticsInfo());
//                                            mUIHandler.postDelayed(this, 5000);
//                                        }
//                                    };
//                                    mUIHandler.postDelayed(runnable, 5000);
//                                    SyncPidInfo syncPidInfo = new SyncPidInfo();
//                                    syncPidInfo.setMeetingId(mRecallMeeting.getId());
//                                    vcApplication.getInteractManager().syncPid(syncPidInfo);

//                                    MQTTClient.getInstance().publishMsg(TOPIC.CTRL_DEVICE + mRecallMeeting.getId(), getLocalCtrlStatus());
                                    break;
                                case DISCONNECTED:
                                    GBLog.i(TAG, "Nemo call has disconnected :" + s);
                                    mUIHandler.removeMessages(CALL_NEMO_ERROR);
                                    mUIHandler.removeMessages(HANGUP_NEMO_ERROR);

                                    videoView.updateViewList(new ArrayList<MemberInfo>(), new ArrayList<MemberInfo>());

                                    if (!isHangup && s != null) {
                                        if ("status_ok".equalsIgnoreCase(s)) {
//                                            quitMeeting();
                                        } else {
                                            Message msg = Message.obtain(mUIHandler, HANGUP_NEMO_ERROR, s == null ? "连接断开！(n:unknow)" : Reason.getReason(s));
                                            GBLog.e(TAG, "disconnect" + Reason.getReason(s));
                                            msg.sendToTarget();
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }

        @Override
        public void onRosterChange(RosterWrapper rosterWrapper) {

        }

        @Override
        public void onVideoDataSourceChange(List<VideoInfo> list) {
            Observable.just(list).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<VideoInfo>>() {
                        @Override
                        public void accept(List<VideoInfo> videoInfos) {
                            GBLog.i(TAG, "videoinfos from Nemo length:" + videoInfos.size());
                            MeetingInfoManager.getInstance().NemoChange(videoInfos, NemoSDK.getLocalVideoStreamID());
                        }
                    });
        }

        @Override
        public void onConfMgmtStateChanged(int i, String s, boolean b) {

        }

        @Override
        public void onRecordStatusNotification(int i, boolean b, String s) {

        }

        @Override
        public void onKickOut(int i, int i1) {

        }

        @Override
        public void onNetworkIndicatorLevel(int i) {

        }

        @Override
        public void onVideoStatusChange(int i) {

        }
    };

    private ConnectNemoCallback mConnectNemoCallback = new ConnectNemoCallback() {
        @Override
        public void onFailed(int i) {

            Common.isNemoConnected = false;
            mUIHandler.removeMessages(CONNECT_NEMO_ERROR);
            String info = "unknown";
            if (i == 1) {
                info = "(n:INVALID_PARAM)";
            } else if (i == 2) {
                info = "(n:NETWORK_UNAVAILABLE)";
            } else if (i == 3) {
                info = "(n:WRONG_PASSWORD)";
            } else if (i == 4) {
                info = "(n:HOST_ERROR)";
            }
            GBLog.i(TAG, "connect Nemo failed! :" + info);
            Message msg = Message.obtain(mUIHandler, CONNECT_NEMO_ERROR, "登陆失败!" + info);
            msg.sendToTarget();
        }

        @Override
        public void onSuccess(String s) {
            Common.isNemoConnected = true;
            GBLog.i(TAG, "connect Nemo success! :" + s);
            mUIHandler.removeMessages(CONNECT_NEMO_ERROR);

            joinMeeting(mRecallMeeting.getId(), mRecallMeeting.getPw());
        }
    };

    private NemoKickOutListener mNemoKickOutListener = new NemoKickOutListener() {
        @Override
        public void onKickOut(int i, int i1) {
            if (mUIHandler != null) {
                mUIHandler.sendEmptyMessage(TOAST);
            }
//            quitMeeting();
            hangup();
            mExitState = DISCONNECT_STATE.KICKOUT;

            GBLog.i(TAG, "kick out!");
        }
    };

    private IVideoViewCallBack mVideoViewListener = new IVideoViewCallBack() {
        @Override
        public void backToDefaultMode(VI vi0, VI vi) {
//                if (!isSelfExit) {
//                    autoContent = false;
//                }
            if (lastMode.equals(DISPLAY_MODE.NOT_FULL_ONEFIVE)) {
                lastModeFlag = DISPLAY_MODE.NOT_FULL_ONEFIVE;
                changeMode(DISPLAY_MODE.FULL);

            } else if (lastMode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)) {
                lastModeFlag = DISPLAY_MODE.NOT_FULL_QUARTER;
                MeetingInfoManager.getInstance().ScreenExchange(vi0, vi);
                changeMode(DISPLAY_MODE.FULL);
            }
//                else if (lastMode.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
//                    isInPIP = false;
//                    changeMode(lastModeFlag);
//                }
            else if (lastMode.equals(DISPLAY_MODE.FULL)) {
//                    if (lastModeFlag.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
//                        changeMode(DISPLAY_MODE.NOT_FULL_ONEFIVE);
//                    } else {
                changeMode(lastModeFlag);
//                    }
//                if (clickPizhu) {
//                    requestCtrl .postCmtLeave(curResource);
//                    mUIHandler.sendEmptyMessage(EXIT_COMMENT);
//                    clickPizhu = false;
//                }
            }
        }

        @Override
        public void closePic() {
            openPic = false;
            MeetingInfoManager.getInstance().PicShare(false, null);
        }

        @Override
        public void receiveLocal() {

            GBLog.e(TAG, "receiveLocal, local camera has changed isFirstReceive:" + isFirstReceive + ", isUvc:" + isUvcCamera);

            mUVCCameraView = videoView.getmLocalVideoCell();

            if (isFirstReceive) {
                videoView.requestLocalFrame();
                nemoSDK.requestCamera();
                isFirstReceive = false;
            }
//

            if (isUvcCamera) {
                GBLog.e(TAG, "isUvcCamera render");
                currentCamera = 2;
                videoView.updateCamera(true);
                nemoSDK.updateCamera(isUvcCamera);
                releaseCamera();
                videoView.getmLocalVideoCell().releaseRender();
                GBLog.e(TAG, "isUvcCamera releaseRender:" + videoView.getmLocalVideoCell());

                queueEvent(new Runnable() {
                    /**
                     *
                     */
                    @Override
                    public void run() {
                        final UVCCamera camera = new UVCCamera();
                        try {
                            camera.open(mCtrlBlock);
//                        isActive = true;
                            Log.i(TAG, "supportedSize:" + camera.getSupportedSize());
                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_YUYV);
                        } catch (final IllegalArgumentException e) {
                            // fallback to YUV mode
                            try {
                                camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                            } catch (final IllegalArgumentException e1) {
                                camera.destroy();
                                return;
                            }
                        }

                        final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                        if (st != null) {
                            mPreviewSurface = new Surface(st);
                            camera.setPreviewDisplay(mPreviewSurface);
                            camera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_YUV420SP);
                            camera.startPreview();
                        }

                        synchronized (mSync) {
                            mUVCCamera = camera;
                        }
                    }
                }, 0);
            }
        }
    };

    private ITitleCallBack mTitleListener = new ITitleCallBack() {
        @Override
        public void clickBackBtn() {
            mUIHandler.sendEmptyMessage(HANGUP);
        }
    };

    private VCDialog.DialogCallback mDialogCallback = new VCDialog.DialogCallback() {

        @Override
        public void onOk(VCDialog.DialogType type, Object object) {
            GBLog.i(TAG, "Dialog OK");
            try {
                if (popupView != null) {
                    popupView.StopVideotape();
                }
                if (type == VCDialog.DialogType.Invite) {

                    InvitedMeeting m = (InvitedMeeting) object;

                    mExitState = DISCONNECT_STATE.RECALL;

                    if (m != null && m.getMeetingId() != null && m.getPassword() != null) {

                        mRecallMeeting = new CallMeetingInfo();
                        mRecallMeeting.setId(m.getMeetingId());
                        mRecallMeeting.setPw(m.getPassword());
                        mRecallMeeting.setName(m.getMeetingName());

                        hangup();

                    }

                } else if (type == VCDialog.DialogType.Handup) {
                    hangup();
                    Finish();

                } else if (type == VCDialog.DialogType.Normal) {
                    Finish();

                } else if (type == VCDialog.DialogType.ErrorHangup) {
                    quitMeeting();
                    Finish();

                } else if (type == VCDialog.DialogType.Recall) {
                    hangup();

                } else if (type == VCDialog.DialogType.ReStart) {
                    hangup();
//                    Intent intent = new Intent();
//                    intent.setClass(VideoActivity.this, SplashActivity.class);
//                    VideoActivity.this.startActivity(intent);
//
//                    VideoActivity.this.finish();
//                    VCApplication.getInstance().exit();
//
//                } else if (type == VCDialog.DialogType.CleanPZ) {
//                    mainView.clearStroke();
                }
            } catch (Throwable e) {
                GBLog.e(TAG, "onOk Throwable: " + e.toString());
            }
        }

        @Override
        public void onCancle(VCDialog.DialogType type, Object object) {
            GBLog.i(TAG, "Dialog Cancel");
            try {
                if (type == VCDialog.DialogType.Invite) {
                    BusyMeetingInfo bmi = new BusyMeetingInfo();
                    bmi.setMeetingId(((InvitedMeeting) object).getMeetingId());
                    bmi.setInviter(((InvitedMeeting) object).getInviter());
                    bmi.setUserName(Common.USERNAME);
//                    vcApplication.getInteractManager().busyMeeting(bmi);
                    ServerInteractManager.getInstance().busyMeeting(bmi);
                } else if (type == VCDialog.DialogType.Normal) {
                    if (popupView != null) {
                        popupView.StopVideotape();
                    }
                    Finish();
                } else if (type == VCDialog.DialogType.ErrorHangup || type == VCDialog.DialogType.Recall) {
                    if (popupView != null) {
                        popupView.StopVideotape();
                    }
                    hangup();
                } else {

                }
            } catch (Throwable e) {
                GBLog.e(TAG, "onCancle Throwable:" + VCUtils.CaughtException(e));
            }
        }
    };

    private IModeCallBack iModeCallBack = new IModeCallBack() {
        @Override
        public void getDisplayMode(DISPLAY_MODE displayMode) {

            if (!displayMode.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
                autoContent = false;
                isInPIP = false;
            }
            changeMode(displayMode);

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mUIHandler.sendEmptyMessage(SHOW_HIDE_LAYOUT);
            popupView.setMeetingUserData(mScreenList, mOtherList, mUnjoinedList);

        }
    };

    private View.OnClickListener mClickViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUIHandler.sendEmptyMessage(HIDE_HIDE_LAYOUY);
        }
    };

//    private IMqttCmtCallback iMqttCmtCallback = new IMqttCmtCallback() {
//        @Override
//        public void onCmt(String msg) {
//            GBLog.e(TAG, "receive pizhu");
//            Message message = Message.obtain(mUIHandler, SENDCOMMENT, msg);
//            message.sendToTarget();
//        }
//    };

//    private IMqttEventCallback iMqttEventCallback = new IMqttEventCallback() {
//        @Override
//        public void onEvent(String msg) {
//            if (msg.contains("events")) {
//                Gson g = new Gson();
//                Mqtt mqtt = g.fromJson(msg, Mqtt.class);
//                if (mqtt.getEvents().get(0).equals("image_share")) {
//                    requestCtrl.downloadPic();
//                    openPic = true;
//                } else if (mqtt.getEvents().get(0).equals("comment_start")) {
//                    hasComment = true;
//                    mUIHandler.sendEmptyMessage(ENTER_COMMENT);
//
//                    curResource = mqtt.getResource();
//                    if (curResource != null) {
//                        GBLog.e(TAG, "send to infoManager :" + curResource);
//                        MeetingInfoManager.getInstance().CommentStart(curResource);
//                    } else {
//                        Toast.makeText(VideoActivity.this, "MQTT resource 未获取", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (mqtt.getEvents().get(0).equals("comment_end")) {
//                    hasComment = false;
//                    hasEndMqtt = true;
//                    mUIHandler.sendEmptyMessage(EXIT_COMMENT);
//                    MeetingInfoManager.getInstance().CommentEnd();
//                }
//            }
//        }
//    };

//    private IMqttCtrlOperateCallback iMqttCtrlOperateCallback = new IMqttCtrlOperateCallback() {
//        @Override
//        public void onCtrlOperate(String msg) {
//            Gson g = new Gson();
//            CtrlInfo ctrlInfo = g.fromJson(msg, CtrlInfo.class);
//            for (String reciver : ctrlInfo.getReceiverID()) {
//                if (reciver.equals(clientId) && (ctrlInfo.getMeetingID().equals(mRecallMeeting.getId()))) {
//                    String command = ctrlInfo.getCommand().get(0).getObject();
//                    String operation = ctrlInfo.getCommand().get(0).getOperation();
//                    if (command.equals("Microphone")) {
//                        mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_MIC);
//                    }else if (command.equals("Camera")){
//                        mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_VIDEO);
//                    }else if (command.equals("Speaker")) {
//
//                    }else if (command.equals("Meeting")){
//                        if (operation.equals("Exit")) {
//                            hangup();
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//    };

//    private IMQTTCallBack imqttCallBack = new IMQTTCallBack() {
//        @Override
//        public void mqtt(String topic, String msg) {
//            GBLog.e(TAG, "topic:" + topic + "mqtt:" + msg + "inComment:" + inComment);
//            if (topic.contains("mevent") && msg.contains("events")) {
//                Gson g = new Gson();
//                Mqtt mqtt = g.fromJson(msg, Mqtt.class);
//                if (mqtt.getEvents().get(0).equals("image_share")) {
//                    requestCtrl.downloadPic();
//                    openPic = true;
//                } else if (mqtt.getEvents().get(0).equals("comment_start")) {
//                    hasComment = true;
//                    mUIHandler.sendEmptyMessage(ENTER_COMMENT);
//
//                    curResource = mqtt.getResource();
//                    if (curResource != null) {
//                        GBLog.e(TAG, "send to infoManager :" + curResource);
//                        MeetingInfoManager.getInstance().CommentStart(curResource);
//                    } else {
//                        Toast.makeText(VideoActivity.this, "MQTT resource 未获取", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (mqtt.getEvents().get(0).equals("comment_end")) {
//                    hasComment = false;
//                    hasEndMqtt = true;
//                    mUIHandler.sendEmptyMessage(EXIT_COMMENT);
//                    MeetingInfoManager.getInstance().CommentEnd();
//                }
//            } else if (topic.contains("mctrl")) {
//                Gson g = new Gson();
//                CtrlInfo ctrlInfo = g.fromJson(msg, CtrlInfo.class);
//                for (String reciver : ctrlInfo.getReceiverID()) {
//
//                    if (reciver.equals(clientId) && (ctrlInfo.getMeetingID().equals(mRecallMeeting.getId()))) {
//                        String command = ctrlInfo.getCommand().get(0).getObject();
//                        String operation = ctrlInfo.getCommand().get(0).getOperation();
//                        if (command.equals("Microphone")) {
//                            mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_MIC);
//                        }else if (command.equals("Camera")){
//                            if (operation.equals("Switch")) {
//                                clickMenuBtn("btn_switch_camera");
//                            }else {
//                                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_VIDEO);
//                            }
//                        }else if (command.equals("Speaker")) {
//
//                        }else if (command.equals("Meeting")){
//                            if (operation.equals("Exit")) {
//                                hangup();
//                            }
//                        }
//                        break;
//                    }
//                }
//            } else if (topic.contains("mcmt")) {
//                GBLog.e(TAG, "receive pizhu");
//                Message message = Message.obtain(mUIHandler, SENDCOMMENT, msg);
//                message.sendToTarget();
//            } else if (topic.contains("status")) {
//                Gson g = new Gson();
//                CtrlStatus ctrlStatus = g.fromJson(msg, CtrlStatus.class);
//                if (!SystemParams.getUsername().equals(ctrlStatus.getUserID())) {
//                    popupView.updateCtrlStatus(ctrlStatus);
//                }
//            }
//        }
//    };

//    private ICtrlCallBack ictrlCallback = new ICtrlCallBack() {
//        @Override
//        public void getTracks(String[] tracks) {
//            GBLog.e(TAG, "getTracks");
//            if (tracks != null) {
//                mUIHandler.sendEmptyMessage(ENTER_COMMENT);
//                TRACKS = tracks;
////                mainView.followList(tracks);
//                MeetingInfoManager.getInstance().CommentStart(curResource);
//                hasComment = true;
//            }
//        }
//
//        @Override
//        public void getResource(String resource) {
//            curResource = resource;
//        }
//
//        @Override
//        public void exit() {
////            mUIHandler.sendEmptyMessage(EXIT_COMMENT);
//        }
//
//        @Override
//        public void getUploadState(boolean success) {
//            picState = success;
//            mUIHandler.sendEmptyMessage(PICUPLOADSTATE);
//        }
//    };

//    private ICustomViewCallBack iMainViewCallBack = new ICustomViewCallBack() {
//        @Override
//        public void onMqttPublish(String tracks) {
//            MQTTClient.getInstance().publishMsg(TOPIC.CMT + mRecallMeeting.getId(), tracks);
//        }
//
//        @Override
//        public void onPaintExit() {
//            requestCtrl.postCmtLeave(curResource);
//            mUIHandler.sendEmptyMessage(EXIT_COMMENT);
//        }
//
//        @Override
//        public void onClearStroke() {
//            mUIHandler.sendEmptyMessage(CLEANPZ);
//        }
//    };


    private static class UIHandler extends Handler {
        private final WeakReference<VideoActivity> mVideoActivity;

        public UIHandler(VideoActivity videoActivity) {
            mVideoActivity = new WeakReference<>(videoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoActivity activity = mVideoActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case CONNECT_NEMO_ERROR:
                        activity.showDialog(msg.obj, VCDialog.DialogType.Normal);
                        break;
                    case DIALOGINVITED:
                        InvitedMeeting invitedMeeting = (InvitedMeeting) msg.obj;
                        activity.showDialog(invitedMeeting, VCDialog.DialogType.Invite);
                        break;
                    case CLOSEMIC:
                        GBLog.e(TAG, "CLOSEMIC");
                        activity.muteMic = true;
                        activity.nemoSDK.enableMic(activity.muteMic, true);
                        activity.videoView.closeLocalMic(activity.muteMic);
                        if (activity.muteMic) {
                            Toast.makeText(activity, R.string.tip_45, Toast.LENGTH_SHORT).show();
                        }
//                        MQTTClient.getInstance().publishMsg(TOPIC.CTRL_DEVICE + mRecallMeeting, activity.getLocalCtrlStatus());
                        break;
                    case ClOSEVIDEO:
                        GBLog.e(TAG, "ClOSEVIDEO");
                        activity.muteCamera = true;
                        activity.nemoSDK.setVideoMute(activity.muteCamera);
                        activity.videoView.closeLocalView(activity.muteCamera);
                        break;
                    case MSG_BTN_SWITCH_MIC:
                        GBLog.e(TAG, "MSG_BTN_SWITCH_MIC");
                        activity.muteMic = !activity.muteMic;
                        activity.videoView.closeLocalMic(activity.muteMic);
                        activity.nemoSDK.enableMic(activity.muteMic, true);
//                        MQTTClient.getInstance().publishMsg(TOPIC.CTRL_DEVICE + mRecallMeeting.getId(), activity.getLocalCtrlStatus());
                        break;
                    case MSG_BTN_SWITCH_VIDEO:
                        GBLog.e(TAG, "MSG_BTN_SWITCH_VIDEO");
                        activity.muteCamera = !activity.muteCamera;
                        activity.videoView.closeLocalView(activity.muteCamera);
                        activity.nemoSDK.setVideoMute(activity.muteCamera);
//                        MQTTClient.getInstance().publishMsg(TOPIC.CTRL_DEVICE + mRecallMeeting.getId(), activity.getLocalCtrlStatus());
                        break;
                    case MSG_BTN_SWITCH_CAMERA:
                        GBLog.e(TAG, "MSG_BTN_SWITCH_CAMERA");
                        activity.foregroundCamera = !activity.foregroundCamera;
                        activity.nemoSDK.switchCamera(activity.foregroundCamera ? 1 : 0);
                        break;
                    case MSG_BTN_VOICE_MODE:
                        GBLog.e(TAG, "MSG_BTN_VOICE_MODE");
                        activity.audioMode = !activity.audioMode;
                        activity.nemoSDK.switchCallMode(activity.audioMode);
                        MeetingInfoManager.getInstance().ModeVoice(activity.audioMode);
                        if (!activity.audioMode) {
                            activity.videoView.closeLocalView(activity.muteCamera);
                            activity.nemoSDK.setVideoMute(activity.muteCamera);
                        }
                        break;
                    case MSG_BTN_VOICE_MODE_UNCHANGED:
                        activity.nemoSDK.switchCallMode(activity.audioMode);
                        MeetingInfoManager.getInstance().ModeVoice(activity.audioMode);
                        break;
                    case TOAST:
                        Toast.makeText(activity, activity.getString(R.string.tip_19), Toast.LENGTH_LONG).show();
                        break;
                    case CALL_NEMO_ERROR:
                        activity.showDialog(msg.obj, VCDialog.DialogType.ReStart);
                        break;
                    case HANGUP_NEMO_ERROR:
                        activity.showDialog(msg.obj, VCDialog.DialogType.ErrorHangup);
                        break;

                    case MAKE_CALL:
                        activity.reCall();
                        break;

                    case MEETING_TIME:
                        activity.mTopLayout.setmClockView((Integer) msg.obj);
                        break;

                    case MESSAGE_MEMBER:
                        activity.mTopLayout.setmTitleTxt(mRecallMeeting.getName(), (String) msg.obj);
                        break;

                    case SHOW_HIDE_LAYOUT:
                        activity.mUIHandler.removeMessages(SHOW_HIDE_LAYOUT);
                        activity.mUIHandler.removeMessages(HIDE_HIDE_LAYOUY);
                        activity.mBottomLayout.setVisibility(View.INVISIBLE);
                        activity.popupView.setVisibility(View.VISIBLE);
//                        activity.popupView.showPopupView();
                        activity.mUIHandler.sendEmptyMessageDelayed(HIDE_HIDE_LAYOUY, 5000);
                        break;

                    case HIDE_HIDE_LAYOUY:
                        activity.dismissPopup();
                        activity.mBottomLayout.setVisibility(View.VISIBLE);
                        break;

                    case HIDE_OTHER_TITLE:

                        break;

                    case UPDATE_POPDATA:

                        if (activity.popupView != null) {
                            activity.popupView.setMeetingUserData(activity.mScreenList, activity.mOtherList, activity.mUnjoinedList);
                        }
                        break;

                    case HANGUP:
//                    showDialog(getString(R.string.tip_22), VCDialog.DialogType.Handup);
                        if (activity.popupView != null && activity.popupView.getRecordingStatus()) {
                            activity.mHangupwarn = activity.getString(R.string.tip_41);
                        } else {
                            activity.mHangupwarn = activity.getString(R.string.tip_22);
                        }
                        activity.showDialog(activity.mHangupwarn, VCDialog.DialogType.Handup);
//                    showHangupDialog(getString(R.string.tip_22));
                        break;

                    case UPDATAVIEW:
                        if (activity.hasContent) {
                            if (activity.autoContent) {
                                if (!activity.lastMode.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
                                    activity.contentlastmode = activity.lastMode;
                                }
                                activity.changeMode(DISPLAY_MODE.FULL_PIP_SIX);
                                activity.isInPIP = true;
                            }
                        } else {
                            if (activity.isInPIP) {
                                activity.changeMode(activity.contentlastmode);
                                activity.isInPIP = false;
                            }
                            activity.autoContent = true;
                        }

                        if (activity.lastMode.equals(DISPLAY_MODE.FULL) && activity.mScreenList.size() == 0) {
                            activity.changeMode(DISPLAY_MODE.NOT_FULL_ONEFIVE);
                        }
                        break;

//                    case ENTER_COMMENT:
//                        if (activity.mainView == null) {
//                            activity.mainView = new CustomPaintView(activity,
//                                    activity.clientId, false);
//                            if (TRACKS != null) {
//                                activity.mainView.followList(TRACKS);
//                                TRACKS = null;
//                            }
//                            activity.mainView.setCallBack(activity.iMainViewCallBack);
//                            activity.videoView.setPizhu(activity.mainView);
//                        }
//                        if (activity.clickPizhu) {
//                            if (activity.mainView != null) {
//                                activity.mainView.setDraw(true);
//                            }
//
//                        } else {
//                            if (activity.mainView != null) {
//                                activity.mainView.setDraw(false);
//                            }
//                        }
//                        break;
//
//                    case EXIT_COMMENT:
//                        activity.clickPizhu = false;
//                        activity.changeMode(DISPLAY_MODE.NOT_FULL_ONEFIVE);
//                        if (activity.mainView != null) {
//                            activity.mainView.setDraw(false);
//                        }
//
//                        if (activity.hasEndMqtt == true && activity.videoView != null && activity.mainView != null) {
//                            activity.mainView.Finalize();
//                            activity.mainView = null;
//                            activity.hasEndMqtt = false;
//                        }
//                        break;

                    case PICUPLOADSTATE:
                        if (activity.picState) {
                            Toast.makeText(activity, "图片上传成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "图片上传失败！", Toast.LENGTH_SHORT).show();
                        }
                        break;
//                    case SENDCOMMENT:
//                        if (activity.mainView != null) {
//                            activity.mainView.Synchronize(msg.obj.toString());
//                        }
//                        break;
                    case CLEANPZ:
                        activity.showDialog("确认清空批注？", VCDialog.DialogType.CleanPZ);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private final UIHandler mUIHandler = new UIHandler(this);

    private void connectNemo(String number, String password) {
        try {
            if (!Common.isNemoConnected) {
                if (mUIHandler != null) {
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = CONNECT_NEMO_ERROR;
                    msg.obj = "连接视频服务器无响应！(n: no response!)";
                    mUIHandler.sendMessageDelayed(msg, NOCALLBACK_TIME * 1000);
                }
                GBLog.i(TAG, "try to connect Nemo...");
//                nemoSDK.connectNemo(SystemParams.getUsername() + "?t_mobile", SystemParams.getUsername() + "?t_mobile", mConnectNemoCallback);
                nemoSDK.loginExternalAccount(Common.USERNAME + "?t_mobile", Common.USERNAME + "?t_mobile", mConnectNemoCallback);

            } else {
                joinMeeting(number, password);
            }
        } catch (Exception e) {
            GBLog.e(TAG, "connect Throwable:" + VCUtils.CaughtException(e));
        }
    }

    private void initView() {

        frameLayout = (FrameLayout) findViewById(R.id.video_fragment);
        mWholeLayout = (RelativeLayout) findViewById(R.id.whole_layout);
        lastMode = DISPLAY_MODE.FULL_FOUR;
        lastModeFlag = DISPLAY_MODE.NOT_FULL_ONEFIVE;
        changeMode(DISPLAY_MODE.NOT_FULL_ONEFIVE);

    }

    private void dismissPopup() {
        if (popupView != null) {
            popupView.setVisibility(View.INVISIBLE);
            popupView.dismissPopupView();
        }
    }

    private void changeMode(DISPLAY_MODE displayMode) {
        GBLog.i(TAG, "change mode to " + displayMode.toString());
        if (lastMode.equals(displayMode)) {
            return;
        }

//        lastModeFlag = lastMode;
        isModeChange = true;
        mWholeLayout.removeAllViews();
        if (videoView != null) {
            videoView.destroy();
            videoView.removeAllViews();
            videoView = null;
        }
        if (displayMode.equals(DISPLAY_MODE.NOT_FULL_ONEFIVE)
                || displayMode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)) {
            mTopLayout = new TitleLayout(this);
            mBottomLayout = new LinearLayout(this);
            popupView = new PopupOldView(this, muteMic, muteCamera, audioMode);
            videoView = new MyVideoVIew(this, displayMode);

            dismissPopup();
            mWholeLayout.addView(mTopLayout);
            mWholeLayout.addView(videoView);
            mWholeLayout.addView(popupView);
            mWholeLayout.addView(mBottomLayout);

            videoView.setiVideoViewCallBack(mVideoViewListener);
            mTopLayout.setITitleCallBack(mTitleListener);
            RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT / 10);
            mTopLayout.setLayoutParams(topParams);
            mTopLayout.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT * 5 / 6);
            videoParams.topMargin = Common.SCREENHEIGHT / 10;
            videoView.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT / 20);
            bottomParams.topMargin = Common.SCREENHEIGHT * 19 / 20;
            mBottomLayout.setLayoutParams(bottomParams);
            mBottomLayout.setBackgroundResource(R.drawable.btn_arrow_up);
            mBottomLayout.setOnClickListener(mClickListener);

            RelativeLayout.LayoutParams popParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT);
            popupView.setLayoutParams(popParams);

        } else if (displayMode.equals(DISPLAY_MODE.FULL_PIP)) {
            videoView = new MyVideoVIew(this, displayMode);
            videoView.setiVideoViewCallBack(mVideoViewListener);
            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT);
            videoView.setLayoutParams(videoParams);
            mWholeLayout.addView(videoView);
        } else if (displayMode.equals(DISPLAY_MODE.FULL)) {
            videoView = new MyVideoVIew(this, displayMode);
            videoView.setiVideoViewCallBack(mVideoViewListener);
            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            videoView.setLayoutParams(videoParams);
            mWholeLayout.addView(videoView);
        } else if (displayMode.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
            videoView = new MyVideoVIew(this, displayMode);
            popupView = new PopupOldView(this, muteMic, muteCamera, audioMode);
            mBottomLayout = new LinearLayout(this);
            dismissPopup();
            videoView.setiVideoViewCallBack(mVideoViewListener);

            mWholeLayout.addView(videoView);
            mWholeLayout.addView(popupView);
            mWholeLayout.addView(mBottomLayout);

            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT);
            videoView.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT / 20);
            bottomParams.topMargin = Common.SCREENHEIGHT * 19 / 20;

            mBottomLayout.setLayoutParams(bottomParams);
            mBottomLayout.setBackgroundResource(R.drawable.btn_arrow_up);
            mBottomLayout.setOnClickListener(mClickListener);

            RelativeLayout.LayoutParams popParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Common.SCREENHEIGHT);
            popupView.setLayoutParams(popParams);

        }
        lastMode = displayMode;
//        if (mainView != null) {
//            videoView.setPizhu(mainView);
//        }
    }

    private void makeCall() {
        GBLog.i(TAG, "After joining meeting, then try to make call");
        isHangup = false;
        try {
//            if ("0".equals(SystemParams.getSavemode())) {
//                GBLog.i(TAG, "关闭省流量模式");
//                nemoSDK.setSaveNetMode(false);
//            } else {
//                GBLog.i(TAG, "开启省流量模式");
//                nemoSDK.setSaveNetMode(true);
//            }
            if (mUIHandler != null) {
                Message msg = mUIHandler.obtainMessage();
                msg.what = CALL_NEMO_ERROR;
                msg.obj = "系统出错，请重启软件(n:make call failed!)";
                mUIHandler.sendMessageDelayed(msg, NOCALLBACK_TIME * 1000);
            }
            GBLog.i(TAG, "nemo make call...." + "number : " + mRecallMeeting.getId() + "password" + mRecallMeeting.getPw());
            nemoSDK.makeCall(mRecallMeeting.getId(), mRecallMeeting.getPw());
        } catch (Exception e) {
            GBLog.e(TAG, "makeCall Throwable:" + VCUtils.CaughtException(e));
        }
    }

    private void joinMeeting(String number, String password) {
        GBLog.i(TAG, "SIM:joinMeeting, number:" + number + "password:" + password);
        JoinMeetingInfo joinMeetingInfo = new JoinMeetingInfo();
        joinMeetingInfo.setUserName(Common.USERNAME);
        joinMeetingInfo.setPassword(password);
        joinMeetingInfo.setMeetingId(number);

        ServerInteractManager.getInstance().joinMeeting(joinMeetingInfo);

    }

    private void quitMeeting() {
        GBLog.i(TAG, "SIM:quitMeeting");
        QuiteMeetingInfo qmi = new QuiteMeetingInfo();

        qmi.setMeetingId(mRecallMeeting.getId());
        qmi.setUserName(Common.USERNAME);

        ServerInteractManager.getInstance().quiteMeeting(qmi);
    }

    public void hangup() {
        GBLog.i(TAG, "hangup and quitMeeting!");
        isHangup = true;
        isGetTime = false;
        if (mUIHandler != null) {
            Message msg = mUIHandler.obtainMessage();
            msg.what = HANGUP_NEMO_ERROR;
            msg.obj = "请重新加入会议！";
            mUIHandler.sendMessageDelayed(msg, NOCALLBACK_TIME * 1000);
        }
//        if (hasComment) {
//            requestCtrl.postCmtLeave(curResource);
//        }
        if (mUnjoinedList != null) {
            for (int i = 0; i < mUnjoinedList.size(); i++) {
                if (mUnjoinedList.get(i).getStatus().equals(STATUS.INVITING)) {
                    for (int j = 0; j < mUsers.length; j++) {
                        if (mUnjoinedList.get(i).getId().equals(mUsers[j])) {
                            String[] user1 = {mUnjoinedList.get(i).getId()};
                            MeetingInfoManager.getInstance().StateChange(user1, STATUS.WAITING);
                            CancelInviteInfo cancelInviteInfo = new CancelInviteInfo();
                            cancelInviteInfo.setMeetingId(mRecallMeeting.getId());
                            cancelInviteInfo.setSessionType(Common.SessionType);
                            cancelInviteInfo.setInvitedUser(mUnjoinedList.get(i).getId());
                            ServerInteractManager.getInstance().cancleMeeting(cancelInviteInfo);
                            GBLog.i(TAG, "SIM:cancel invite:" + mUnjoinedList.get(i).getId());
                        }
                    }
                }
            }
        }

        nemoSDK.hangup();
        quitMeeting();
    }

    private void reCall() {
        try {

            MeetingInfoManager.getInstance().init();

            mExitState = DISCONNECT_STATE.NORMAL;

            mTopLayout.setmConferenceIDTxt(mRecallMeeting.getId());
            videoView.closeLocalView(false);

            if (!Common.isNemoConnected) {
                if (mUIHandler != null) {
                    GBLog.i(TAG, "reCall connect mUIHandler send");
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = CONNECT_NEMO_ERROR;
                    msg.obj = "连接视频服务器无响应！";
                    mUIHandler.sendMessageDelayed(msg, NOCALLBACK_TIME * 1000);
                }
                nemoSDK.loginExternalAccount(Common.USERNAME, Common.USERNAME, mConnectNemoCallback);
            } else {
                makeCall();
            }
        } catch (Exception e) {
            GBLog.e(TAG, "reCall Throwable:" + VCUtils.CaughtException(e));
        }

    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    final String BTN_SHOW_PERSON = "btn_show_person";
    final String BTN_ADD_PERSON = "btn_add_person";
    final String BTN_BAN_MIC = "btn_ban_mic";
    final String BTN_CLOSE_CAMERA = "btn_voice_mode";
    final String BTN_VOICE_MODE = "btn_audio_mode";
    final String BTN_SWITCH_CAMERA = "btn_switch_camera";
    final String BTN_DISPLAY_MODE = "btn_display_mode";
    final String BTN_HANGUP = "btn_hangup";
    final String BTN_UPLOAD_PIC = "btn_uploadpic";
    final String BTN_OPEN_PIC = "btn_openpic";
    final String BTN_PIZHU = "btn_pizhu";

    private void clickMenuBtn(String type) {
        if (BTN_SHOW_PERSON.equals(type)) {
            mUIHandler.removeMessages(HIDE_HIDE_LAYOUY);
        } else {
            if (BTN_ADD_PERSON.equals(type)) {
                GBLog.i(TAG, "[user operation]click add person");
                syntony.startAddressList(true, "horizontal", false, null, null);
                syntony.setExCallBack(videoCallBack);
            } else if (BTN_BAN_MIC.equals(type)) {
                GBLog.e(TAG, "[user operation]click muteMic btn:" + muteMic);
                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_MIC);
            } else if (BTN_CLOSE_CAMERA.equals(type)) {
                GBLog.i(TAG, "[user operation]click off camera");
                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_VIDEO);
            } else if (BTN_VOICE_MODE.equals(type)) {
                GBLog.i(TAG, "[user operation]click into Voice Mode");
                mUIHandler.sendEmptyMessage(MSG_BTN_VOICE_MODE);
            } else if (BTN_SWITCH_CAMERA.equals(type)) {
                GBLog.i(TAG, "[user operation]click switch camera");
                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_CAMERA);
            } else if (BTN_DISPLAY_MODE.equals(type)) {
                GBLog.i(TAG, "[user operation]click display mode");
                showModeDialog();
            } else if (BTN_HANGUP.equals(type)) {
                GBLog.i(TAG, "[user operation]click hangup btn");
                mUIHandler.sendEmptyMessage(HANGUP);
//                requestCtrl.postCmtLeave(curResource);
            } else if (BTN_UPLOAD_PIC.equals(type)) {
                GBLog.i(TAG, "[user operation]click upload pic btn");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            } else if (BTN_OPEN_PIC.equals(type)) {
                GBLog.i(TAG, "[user operation]click open pic btn");
                openPic = !openPic;
                if (openPic) {
                    requestCtrl.downloadPic();
                } else {
                    MeetingInfoManager.getInstance().PicShare(false, null);
                }
            } else if (BTN_PIZHU.equals(type)) {
                GBLog.i(TAG, "[user operation]click pizhu btn");
                if (mScreenList.size() > 0 && mScreenList.get(0) != null) {

                    clickPizhu = true;
                    String codeName = codeName(mScreenList);
                    for (int i = 0; i < mScreenList.size(); i++) {
                        if ((hasComment && curResource.equals(mScreenList.get(i).getResId())) || !hasComment) {
                            if (hasComment) {
                                MeetingInfoManager.getInstance().CommetModeChange();
                            }
                            if (lastMode.equals(DISPLAY_MODE.NOT_FULL_ONEFIVE)) {
                                lastModeFlag = DISPLAY_MODE.NOT_FULL_ONEFIVE;
                                changeMode(DISPLAY_MODE.FULL);

                            } else if (lastMode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)) {
                                lastModeFlag = DISPLAY_MODE.NOT_FULL_QUARTER;
                                changeMode(DISPLAY_MODE.FULL);
                            }
                            if (hasComment) {
                                mUIHandler.sendEmptyMessage(ENTER_COMMENT);
                            }
                            GBLog.e(TAG, "share resource :" + codeName(mScreenList));
                            requestCtrl.postCmtEnter(codeName);
                            break;
                        }
                    }
                }
            }
            mUIHandler.sendEmptyMessage(SHOW_HIDE_LAYOUT);
        }
    }

    private void showToast(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    private void showDialog(Object content, VCDialog.DialogType type) {

        try {

            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            mDialog = new VCDialog(this, R.style.Dialog, type);
            mDialog.setContent(content);
            mDialog.setDialogCallback(mDialogCallback);
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        } catch (Throwable e) {
            GBLog.e(TAG, "showDialog Throwable:" + VCUtils.CaughtException(e));
        }
    }

    private void showModeDialog() {

        modeDialog = new ModeDialog(this, R.style.ModeSwitchDialog, true);
        modeDialog.setIModeCallBack(iModeCallBack);
        modeDialog.show();

    }

    public static void reInvited(String[] users) {
        if (mRecallMeeting != null) {
            InviteMeetingInfo inviteMeetingInfo = new InviteMeetingInfo();
            inviteMeetingInfo.setInvitedUsers(users);
            inviteMeetingInfo.setMeetingId(mRecallMeeting.getId());
            inviteMeetingInfo.setUserName(Common.USERNAME);
            ServerInteractManager.getInstance().inviteMeeting(inviteMeetingInfo);
        }
    }

    public static void sendMsg(String[] users) {
        if (mRecallMeeting != null) {
            SendInfo sendInfo = new SendInfo();
            sendInfo.setMeetingId(mRecallMeeting.getId());
            sendInfo.setUsers(users);
            ServerInteractManager.getInstance().sendMessage(sendInfo);
        }
    }

    public static void kickPerson(String[] users) {
        if (mRecallMeeting != null) {
            KickoutInfo kickoutInfo = new KickoutInfo();
            kickoutInfo.setMeetingId(mRecallMeeting.getId());
            kickoutInfo.setKickoutUsers(users);
            ServerInteractManager.getInstance().kickoutMeeting(kickoutInfo);
        }
    }

    public static void cancelInvite(String[] users) {
        if (mRecallMeeting != null) {
            CancelInviteInfo cl = new CancelInviteInfo();
            cl.setMeetingId(VideoActivity.mRecallMeeting.getId());
            cl.setInvitedUser(users[0]);
            ServerInteractManager.getInstance().cancleMeeting(cl);
        }
    }

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            StatisticsInfo statisticsInfo = new StatisticsInfo();
            StatisticsInfo.NetworkInfo networkInfo = statisticsInfo.new NetworkInfo();
            JSONObject jsonNetwork = jsonObject.getJSONObject("networkInfo");
            networkInfo.setRtt(jsonNetwork.getInt("rtt"));
            networkInfo.setRxDetectBw(jsonNetwork.getInt("rxDetectBw"));
            networkInfo.setRxJitter(jsonNetwork.getInt("rxJitter"));
            networkInfo.setRxLost(jsonNetwork.getInt("rxLost"));
            networkInfo.setTxDetectBw(jsonNetwork.getInt("txDetectBw"));
            networkInfo.setTxJitter(jsonNetwork.getInt("txJitter"));
            networkInfo.setTxLost(jsonNetwork.getInt("txLost"));

            statisticsInfo.setNetworkInfo(networkInfo);
//            GBLog.e(TAG, "rtt:" + statisticsInfo.getNetworkInfo().getRtt());
//            GBLog.e(TAG, "rxDetectBw:" + statisticsInfo.getNetworkInfo().getRxDetectBw());
        } catch (Exception e) {

        }
    }

    private String codeName(List<MemberInfo> mScreenList) {
        String mResourceName = null;
        MemberInfo memberInfo = mScreenList.get(0);

        if (memberInfo.getmDisplayType().equals(DisplayType.PICTURE)) {
            mResourceName = "image" + videoView.getPicturePage() + "@gallery";
        } else if (memberInfo.getmDisplayType().equals(DisplayType.VIDEO)) {
            if (memberInfo.getSessionType().equals(SESSIONTYPE.MOBILE)) {
                mResourceName = "camera" + "@" + memberInfo.getId() + "?t_mobile";
            } else if (memberInfo.getSessionType().equals(SESSIONTYPE.PC_CONTENT)) {
                mResourceName = "content" + "@" + memberInfo.getId() + "?t_pc";
            } else if (memberInfo.getSessionType().equals(SESSIONTYPE.PC)) {
                mResourceName = "camera" + "@" + memberInfo.getId() + "?t_pc";
            } else if (memberInfo.getSessionType().equals(SESSIONTYPE.CONTENTONLY)) {
                mResourceName = "content" + "@" + memberInfo.getId() + "?t_contentonly";
            }

        }
        return mResourceName;
    }

    private String getLocalCtrlStatus() {
        CtrlStatusParam micCtrlStatusParam = new CtrlStatusParam();
        if (muteMic) {
            micCtrlStatusParam.setStatus("Mute");
        } else {
            micCtrlStatusParam.setStatus("Unmute");
        }
        micCtrlStatusParam.setObject("Microphone");

        CtrlStatusParam camCtrlStatusParam = new CtrlStatusParam();
        if (muteCamera) {
            camCtrlStatusParam.setStatus("Mute");
        } else {
            camCtrlStatusParam.setStatus("Unmute");
        }
        camCtrlStatusParam.setObject("Camera");

        ArrayList<CtrlStatusParam> ctrlStatusParamArrayList = new ArrayList<>();
        ctrlStatusParamArrayList.add(micCtrlStatusParam);
        ctrlStatusParamArrayList.add(camCtrlStatusParam);

        CtrlStatus ctrlStatus = new CtrlStatus();
        ctrlStatus.setMeetingID(mRecallMeeting.getId());
        ctrlStatus.setUserID(clientId);
        ctrlStatus.setStatusList(ctrlStatusParamArrayList);

        Gson gson = new Gson();
        String json = gson.toJson(ctrlStatus);

        return json;
    }

    private void Finish() {
        try {
            nemoSDK.switchCamera(1);
            mTopLayout.finishClock();
            mDialog = null;
            modeDialog = null;

            nemoSDK.setNemoSDKListener(null);
            nemoSDK.setNemoKickOutListener(null);

            releaseCamera();
            GBLog.e(TAG, "finish releaseCamera");

            synchronized (mSync) {
                if (mUSBMonitor != null) {
                    mUSBMonitor.unregister();
                    GBLog.e(TAG, "mUSBMonitor.unregister()");
                }
            }

            nemoSDK.releaseCamera();

//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    releaseCamera();
//                    releaseUsbMonitor();
//                    GBLog.e(TAG, "finish release");
//                }
//            }, 0);

            videoView.setOnClickListener(null);
            ServerInteractManager.getInstance().removeServerInteractCallback(this);

            mUIHandler.removeCallbacksAndMessages(null);
            MeetingInfoManager.destory();

//            MQTTClient.release();

            videoView.destroy();
            popupView.destroy();
            syntony.destroy();
            syntony = null;
            finish();
        } catch (Throwable e) {
            GBLog.e(TAG, "Finish Throwable:" + VCUtils.CaughtException(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && Activity.RESULT_OK == resultCode && data != null) {
            try {
                Uri uri = data.getData();
                String imgPath = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                            sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        imgPath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                } else {//4.4以下，即4.4以上获取路径的方法
                    Cursor cursor = getContentResolver().query(uri, null, null,
                            null, null);
                    cursor.moveToFirst();
                    imgPath = cursor.getString(1);
                    cursor.close();
                }
                requestCtrl.uploadPic(imgPath);
            } catch (Exception e) {
                GBLog.e(TAG, e.toString());
            }
        }
    }

//    @Override
//    public void invite(User[] users) {
//        GBLog.i(TAG, "##oninvite users##");
//        String[] userName = new String[users.length];
//        ArrayList arrayList = new ArrayList();
//        for (int i = 0; i < users.length; i++) {
//            userName[i] = users[i].getUserName();
//            arrayList.add(users[i].getUserName());
//            mUserInvites = new String[arrayList.size()];
//            arrayList.toArray(mUserInvites);
//            mUsers = userName;
//        }
//        try {
//            if (mRecallMeeting != null) {
//                InviteMeetingInfo inviteMeetingInfo = new InviteMeetingInfo();
//                inviteMeetingInfo.setInvitedUsers(userName);
//                inviteMeetingInfo.setMeetingId(mRecallMeeting.getId());
//                inviteMeetingInfo.setUserName(Common.USERNAME);
//                ServerInteractManager.getInstance().inviteMeeting(inviteMeetingInfo);
//            }
//        } catch (Exception e) {
//            GBLog.e(TAG, "invite Throwable:" + VCUtils.CaughtException(e));
//        }
//    }


    @Override
    public void onLogin(boolean b, String s, User user) {

    }

    @Override
    public void onLogout(boolean result, String error) {
//        ((MainActivity) MainActivity.mContext).finish();
//        Finish();
//        Intent intent = new Intent();
//        intent.setClass(VideoActivity.this, LoginActivity.class);
//        startActivity(intent);
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
        GBLog.i(TAG, "SR:onJoinMeeting " + result);
        try {
            if (result) {
                GBLog.i(TAG, "Join meeting successfully,then try to make call....");
                if (popupView != null) {
                    popupView.QueryStatus();
                }
                Message msg = Message.obtain(mUIHandler, MAKE_CALL, mRecallMeeting);
                msg.sendToTarget();

            } else {
                GBLog.i(TAG, "Join meeting failed, then show dialog_hint");
                GBLog.e(TAG, "server:" + error);
                if (error != null && getString(R.string.error_state).equals(error)) {
                    showDialog(getString(R.string.error_meeting), VCDialog.DialogType.ErrorHangup);
                } else if (error == null || error.isEmpty()) {
                    showDialog(getString(R.string.unknown_reason), VCDialog.DialogType.ErrorHangup);
                } else {
                    if (error.contains(mRecallMeeting.getId())) {
                        GBLog.i(TAG, "Already in meeting,then try to make call....");
                        if (popupView != null) {
                            popupView.QueryStatus();
                        }
                        Message msg = Message.obtain(mUIHandler, MAKE_CALL, mRecallMeeting);
                        msg.sendToTarget();

                    } else {
                        showDialog("s:" + error, VCDialog.DialogType.ErrorHangup);
                    }
                }
            }
        } catch (Exception e) {
            GBLog.e(TAG, "onJoinMeeting Throwable:" + VCUtils.CaughtException(e));
        }
    }

    @Override
    public void onInviteMeeting(boolean success, String error) {
        GBLog.i(TAG, "SR:onInviteMeeting");
        try {
            if (success) {
                if (mUserInvites != null) {
                    MeetingInfoManager.getInstance().StateChange(mUserInvites, STATUS.INVITING);
                    mUserInvites = null;
                }
                showToast(getResources().getString(R.string.tip_111));

            } else {
                showToast(getResources().getString(R.string.tip_12) + error);
            }
        } catch (Throwable e) {
            GBLog.e(TAG, "onInviteMeeting Throwable:" + VCUtils.CaughtException(e));
        }
    }

    @Override
    public void onKickoutMeeting(boolean success, String error) {

    }

    @Override
    public void onQuiteMeeting(boolean success, String error) {
        GBLog.i(TAG, "SR:onQuiteMeeting " + success);
        try {
            if (mExitState == DISCONNECT_STATE.KICKOUT) {
                GBLog.i(TAG, "onQuiteMeeting : kick out");
                ServerInteractManager.getInstance().logout();
            } else if (mExitState == DISCONNECT_STATE.NORMAL) {
                GBLog.i(TAG, "onQuiteMeeting : finish");
                Finish();
            } else if (mExitState == DISCONNECT_STATE.RECALL) {
                GBLog.i(TAG, "onQuiteMeeting : recall");
                joinMeeting(mRecallMeeting.getId(), mRecallMeeting.getPw());
            }
        } catch (Throwable e) {
            GBLog.e(TAG, "onQuiteMeeting Throwable:" + VCUtils.CaughtException(e));
        }
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
        GBLog.i(TAG, "SR:eventUserStateChanged");
        if (refuseInfos != null) {
            ArrayList refuseList = new ArrayList();
            for (int i = 0; i < refuseInfos.length; i++) {
                if (refuseInfos[i].getMeetingId().equals(mRecallMeeting.getId())) {
                    refuseList.add(refuseInfos[i].getUserName());
                }
            }
            String[] refuses = new String[refuseList.size()];
            refuseList.toArray(refuses);

            MeetingInfoManager.getInstance().StateChange(refuses, STATUS.BUSY);
        }

        if (timeoutInfos != null) {

            ArrayList timeoutList = new ArrayList();
            for (int i = 0; i < timeoutInfos.length; i++) {
                if (timeoutInfos[i].getMeetingId().equals(mRecallMeeting.getId())) {
                    timeoutList.add(timeoutInfos[i].getInviter());
                }
            }
            String[] timeouts = new String[timeoutList.size()];
            timeoutList.toArray(timeouts);

            MeetingInfoManager.getInstance().StateChange(timeouts, STATUS.TIMEOUT);
        }
    }

    @Override
    public void eventInvitedMeeting(InvitedMeeting meeting) {
        GBLog.i(TAG, "SR:eventInvitedMeeting");
        try {
            if (meeting == null) {
                return;
            }

            if (meeting.getMeetingId().equals(mRecallMeeting.getId())) {
                return;
            }

            invitedMeeting = meeting;
            Message msg = Message.obtain(mUIHandler, DIALOGINVITED, invitedMeeting);
            msg.sendToTarget();
        } catch (Throwable e) {
            GBLog.e(TAG, "eventInvitedMeeting:" + e);
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
        GBLog.i(TAG, "SR:eventDifferentPlaceLogin");
        try {
            mUIHandler.sendEmptyMessage(TOAST);

            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setUserName(Common.USERNAME);
            loginInfo.setPassword(Common.PASSWORD);
            ServerInteractManager.getInstance().login(loginInfo);
        } catch (Throwable e) {
            GBLog.e(TAG, "eventDifferentPlaceLogin:" + e);
        }
    }


    @Override
    public void eventInvitingCancle(InviteCancledInfo ici) {

    }

    @Override
    public void onValidate(boolean result, String err) {

    }

    @Override
    public void onCheckPwd(boolean result, String error) {

    }

    @Override
    public void onMotifyPwd(boolean result, String error) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GBLog.i(TAG, "[user operation]click key down back");
            String warn = "";
            if (popupView != null && popupView.getRecordingStatus()) {
                warn = getString(R.string.tip_41);
            } else {
                warn = getString(R.string.tip_22);
            }
            showDialog(warn, VCDialog.DialogType.Handup);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUSBMonitor != null) {
            mUSBMonitor.register();
        }
        GBLog.i(TAG, "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        GBLog.i(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        GBLog.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GBLog.e(TAG, "onDestroy");
    }

    @Override
    public void onClickPopBtn() {
        mUIHandler.sendEmptyMessage(HIDE_HIDE_LAYOUY);
    }

    @Override
    public void onClickMenuBtn(String type) {
        clickMenuBtn(type);
    }

    @Override
    public void onClickMenuBtn(String type, boolean recordFlag) {

    }

    @Override
    public void onClickPerson(MemberInfo memberInfo) {
        if (memberInfo.getStatus().equals(STATUS.JOINED)) {
            MeetingInfoManager.getInstance().PopUpDown(memberInfo);
        } else {
            String[] user = {memberInfo.getId()};
            VideoActivity.reInvited(user);
            MeetingInfoManager.getInstance().StateChange(user, STATUS.INVITING);
        }
    }

    @Override
    public void onMqttMsg(String Object, String Operation, MemberInfo memberInfo) {
//        if(memberInfo.isLocal()){
//            if (Object.equals("Microphone")){
//                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_MIC);
//            }else if (Object.equals("Camera")) {
//                mUIHandler.sendEmptyMessage(MSG_BTN_SWITCH_VIDEO);
//            }
//        }else {
//            ArrayList<String> mReceiverIDs = new ArrayList<>();
//            ArrayList<CtrlCmd> mCommands = new ArrayList<>();
//
//            CtrlCmd ctrlCmd = new CtrlCmd();
//            ctrlCmd.setObject(Object);
//            ctrlCmd.setOperation(Operation);
//            CtrlInfo ctrlInfo = new CtrlInfo();
//            ctrlInfo.setMeetingID(mRecallMeeting.getId());
//            ctrlInfo.setSenderID(Common.USERNAME + "?t_mobile");
//
//            mReceiverIDs.add(DeviceName.addSuffix(memberInfo.getSessionType(), memberInfo.getUserName()));
//            ctrlInfo.setReceiverID(mReceiverIDs);
//
//            mCommands.add(ctrlCmd);
//            ctrlInfo.setCommand(mCommands);
//
//            Gson gson = new Gson();
//            String json = gson.toJson(ctrlInfo);
//            GBLog.e(TAG, "click ctrl btn:" + json);
//            MQTTClient.getInstance().publishMsg(TOPIC.CTRL_OPERATE + mRecallMeeting.getId(), json);
//        }
    }

    @Override
    public void setMicState(boolean isMicMute) {
        //isMicMute为true静音，关视频
        GBLog.e(TAG, "isMicMute= " + isMicMute + ", muteMic=" + muteMic);

        if (isMicMute) {
            if (!muteMic) {
                popupView.clickMic();
            }

            if (!muteCamera) {
                popupView.clickVideo();
            }
        }

    }
}
