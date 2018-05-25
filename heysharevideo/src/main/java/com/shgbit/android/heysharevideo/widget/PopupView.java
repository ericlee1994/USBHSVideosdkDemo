package com.shgbit.android.heysharevideo.widget;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.adapter.CtrlListJoinAdapter;
import com.shgbit.android.heysharevideo.adapter.CtrlListUnjoinAdapter;
import com.shgbit.android.heysharevideo.adapter.GridViewAdapter;
import com.shgbit.android.heysharevideo.bean.DisplayType;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.STATUS;
import com.shgbit.android.heysharevideo.callback.ICtrlBtnCallBack;
import com.shgbit.android.heysharevideo.callback.IPopViewCallBack;
import com.shgbit.android.heysharevideo.callback.IVideoRecordCallBack;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/21 0021.
 */

public class PopupView extends LinearLayout implements IVideoRecordCallBack {
    private static final String TAG = "PopupView";
    private LinearLayout mTopLayout;
    private LinearLayout mCtrlLayout;
    private TextView mJoinTextView;
    private TextView mRefuseTextView;
    private GridView mJoinedGridView;
    private GridView mRefuseGridView;
    private ListView mJoinCtrlList;
    private ListView mUnjoinCtrlList;
    private View mLineView;
    private LinearLayout mImageBtn;
    private ImageView mImageAdd;
    private ImageView mImageShow;
    private ImageView mImageDisplay;
    private ImageView mImageSharepic;
    private ImageView mImagePizhu;
    private ImageView mImageCtrl;
    private ImageView mImageHang;
    private ImageView mImageSwith;
    private ImageView mImageMode;
    private ImageView mImageVoice;
    private ImageView mImageMic;
    private ImageView mImageOpenpic;
    private ImageView mImageUploadpic;
    private ImageView mVideotape;
    private ImageView mVideotapePoint;
    private TextView mChronometer;
    private RelativeLayout mVideotapeLayout;

    private ArrayList<MemberInfo> mScreenList;
    private ArrayList<MemberInfo> mOtherList;
    private ArrayList<MemberInfo> mJoinUser;
    private ArrayList<MemberInfo> mOutUser;
    private ArrayList<MemberInfo> mUnjoinedList;
    private ArrayList<MemberInfo> mAllUser;

    private GridViewAdapter mJoinAdapter;
    private GridViewAdapter mRefuseAdapter;
    private CtrlListJoinAdapter mCtrlListJoinAdapter;
    private CtrlListUnjoinAdapter mCtrlListUnjoinAdapter;
    private boolean isShow;
    private Context mContext;
    private boolean MicStatus;
    private boolean ModeStatus;
    private boolean VoiceMode;
    private boolean isHost;
    private boolean isRecording = false;
    private String meetingId = "";
    private boolean mIsShow = false;
    private Timer mTimer;

    private PopupWindow mCtrlPopView;
    private PopupWindow mSharePicPopView;

    private boolean isShowing = false;

    public PopupView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public PopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PopupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMeetingUserData(ArrayList<MemberInfo> mShowUser, ArrayList<MemberInfo> mHideUser, ArrayList<MemberInfo> mRefuseUser) {
        if(mScreenList == null){
            mScreenList = new ArrayList<>();
        }else {
            mScreenList.clear();
        }
        if(mShowUser != null){
            for(int i = 0;i<mShowUser.size();i++){
                if (mShowUser.get(i) == null) {
                    continue;
                }
                if (mShowUser.get(i).getmDisplayType() == DisplayType.PICTURE) {
                    continue;
                }
                mScreenList.add(mShowUser.get(i));
            }
        }

        if(mOtherList == null){
            mOtherList = new ArrayList<>();
        }else {
            mOtherList.clear();
        }
        if(mHideUser != null){
            for(int i = 0;i<mHideUser.size();i++){
                mOtherList.add(mHideUser.get(i));
            }
        }

        if(mUnjoinedList == null){
            mUnjoinedList = new ArrayList<>();
        }else {
            mUnjoinedList.clear();
        }
        if(mRefuseUser != null){
            for(int i = 0;i<mRefuseUser.size();i++){
                mUnjoinedList.add(mRefuseUser.get(i));
            }
        }
        updateGridView();
        updateCtrlListView();
        updateCtrlUnjoinListView();
		updateImage();
    }


    private void init(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_popup, null);
        addView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTopLayout = (LinearLayout) v.findViewById(R.id.person_lay);
        mCtrlLayout = (LinearLayout) v.findViewById(R.id.ctrl_lay);
        mJoinTextView = (TextView) v.findViewById(R.id.popup_joined);
        mRefuseTextView = (TextView) v.findViewById(R.id.popup_refuse);
        mJoinedGridView = (GridView) v.findViewById(R.id.gv_view_meeting);
        mRefuseGridView = (GridView) v.findViewById(R.id.gv_view_no);
        mLineView = (View) v.findViewById(R.id.horizontal_line);
        mImageBtn = (LinearLayout) v.findViewById(R.id.btn_dropdown);
        mImageAdd = (ImageView) v.findViewById(R.id.btn_add_person);
        mImageShow = (ImageView) v.findViewById(R.id.btn_show_person);
        mImageDisplay = (ImageView) v.findViewById(R.id.btn_display_mode);
        mImageSharepic = (ImageView) v.findViewById(R.id.btn_sharepic);
        mImagePizhu = (ImageView) v.findViewById(R.id.btn_pizhu);
        mImageCtrl = (ImageView) v.findViewById(R.id.btn_ctrl);
        mImageHang = (ImageView) v.findViewById(R.id.btn_hangup);
        mVideotape = (ImageView) v.findViewById(R.id.btn_videotape);
        mVideotapePoint = (ImageView) v.findViewById(R.id.videotape_point);
        mChronometer = (TextView) v.findViewById(R.id.videotape_time);
        mVideotapeLayout = (RelativeLayout) v.findViewById(R.id.videotape_linear);
        mJoinCtrlList = (ListView) v.findViewById(R.id.ctrl_join_lv);
        mUnjoinCtrlList = (ListView) v.findViewById(R.id.ctrl_unjoin_lv);

        mTopLayout.setVisibility(View.INVISIBLE);
        mCtrlLayout.setVisibility(INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        isShow = false;
        mJoinTextView.setText(getResources().getString(R.string.popup_joined));
        mRefuseTextView.setText(getResources().getString(R.string.popup_refuse));

        mJoinTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Common.SCREENWIDTH / 40);
        mRefuseTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Common.SCREENWIDTH / 40);

        VideoRecord.getInstance(mContext).setCallBack(this);
        VideoRecord.getInstance(mContext).startQueryStatusThread();


        mJoinedGridView.setOnItemClickListener(mItemClickListener);
        mRefuseGridView.setOnItemClickListener(mItemClickListener);
        mImageBtn.setOnClickListener(mClickListener);
        mImageAdd.setOnClickListener(mClickListener);
        mImageShow.setOnClickListener(mClickListener);
        mImageDisplay.setOnClickListener(mClickListener);
        mImageSharepic.setOnClickListener(mClickListener);
        mImagePizhu.setOnClickListener(mClickListener);
        mImageCtrl.setOnClickListener(mClickListener);
        mImageHang.setOnClickListener(mClickListener);
        mVideotape.setOnClickListener(mClickListener);
    }

    public boolean getRecordingStatus () {
        return isRecording;
    }

    private void setFlickerAnimation(ImageView iv_chat_head, boolean isRecording) {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);//
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        iv_chat_head.setAnimation(animation);
        if (isRecording == true) {
            animation.cancel();
        }
    }

    public void QueryStatus () {
        VideoRecord.getInstance(mContext).startQueryStatusThread();
    }

    public void StartVideotapePoint() {
        mVideotape.setImageResource(R.drawable.videotape_pre);
        mVideotapePoint.setVisibility(View.INVISIBLE);
        mVideotapePoint.setVisibility(View.VISIBLE);
        setFlickerAnimation(mVideotapePoint, false);
    }

    public void StopVideotapePoint() {
        mVideotape.setImageResource(R.drawable.videotape);
        setFlickerAnimation(mVideotapePoint, true);
    }

    public void dismissPopupView () {
        if (mCtrlPopView != null) {
            mCtrlPopView.dismiss();
        }
        if (mSharePicPopView != null) {
            mSharePicPopView.dismiss();
        }
        isShowing = false;
    }

    public void showPopupView () {
        isShowing = true;
    }

    public void StopVideotape() {
        if (isRecording) {
//            MeetingRecord meetingRecord = new MeetingRecord();
//            meetingRecord.setMeetingId(meetingId);
//            meetingRecord.setSessionId(Common.SESSIONID);
//            VideoRecord.getInstance(mContext).endRecord(meetingRecord);
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btn_videotape){
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
//                MeetingRecord meetingRecord = new MeetingRecord();
//                meetingRecord.setMeetingId(meetingId);
//                meetingRecord.setSessionId(Common.SESSIONID);
//                if (isRecording == false) {
//                    VideoRecord.getInstance(mContext).startRecord(meetingRecord);
//                } else {
//                    VideoRecord.getInstance(mContext).endRecord(meetingRecord);
//                }
            }else if (id == R.id.btn_dropdown){
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickPopBtn();
                isShow = true;
                updateMenueImgAdTxt(2);
            }else if (id == R.id.btn_add_person){
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_add_person");
                updateMenueImgAdTxt(1);
            }else if (id == R.id.btn_show_person) {
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                updateMenueImgAdTxt(2);
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_show_person");
            }else if (id == R.id.btn_sharepic) {
                if (!isShowing) {

                }else {
                    mTopLayout.setVisibility(View.INVISIBLE);
                    mCtrlLayout.setVisibility(INVISIBLE);
                    mImageShow.setImageResource(R.drawable.btn_meetingctrl);
                    isShow = false;
                    if (mCtrlPopView != null) {
                        mCtrlPopView.dismiss();
                    }
                    createPicPopupView();
                }
            }else if (id == R.id.btn_openpic) {
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_openpic");
            }else if (id == R.id.btn_uploadpic) {
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_uploadpic");
            }else if (id == R.id.btn_pizhu) {
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_pizhu");
            }else if (id == R.id.btn_ctrl) {
                if (!isShowing) {

                }else {
                    mTopLayout.setVisibility(View.INVISIBLE);
                    mCtrlLayout.setVisibility(INVISIBLE);
                    mImageShow.setImageResource(R.drawable.btn_meetingctrl);
                    isShow = false;
                    if (mSharePicPopView != null) {
                        mSharePicPopView.dismiss();
                    }
                    createCtrlPopupView();
                }
            }else if (id == R.id.btn_ban_mic){
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_ban_mic");
                updateMenueImgAdTxt(3);
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
            }else if (id == R.id.btn_voice_mode) {
                if(VoiceMode == false) {
                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_voice_mode");
                    updateMenueImgAdTxt(4);
                    if (mCtrlPopView != null) {
                        mCtrlPopView.dismiss();
                    }
                }
            }else if (id == R.id.btn_speech_sounds){
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_audio_mode");
                updateMenueImgAdTxt(7);
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
            }else if (id == R.id.btn_switch_camera){
                createCtrlPopupView();
                if(VoiceMode == false){
                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_switch_camera");
                    updateMenueImgAdTxt(5);
                    if (mCtrlPopView != null) {
                        mCtrlPopView.dismiss();
                    }
                }
            }else if (id == R.id.btn_display_mode){
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_display_mode");
                updateMenueImgAdTxt(6);
            }else if (id == R.id.btn_hangup){
                if (mCtrlPopView != null) {
                    mCtrlPopView.dismiss();
                }
                if (mSharePicPopView != null) {
                    mSharePicPopView.dismiss();
                }
                ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_hangup");
            }
//            switch (view.getId()) {
//                case R.id.btn_videotape:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    MeetingRecord meetingRecord = new MeetingRecord();
//                    meetingRecord.setMeetingId(meetingId);
//                    meetingRecord.setSessionId(SystemParams.getSessionId());
//                    if (isRecording == false) {
//                        VideoRecord.getInstance(mContext).startRecord(meetingRecord);
//                    } else {
//                        VideoRecord.getInstance(mContext).endRecord(meetingRecord);
//                    }
//                    break;
//                case R.id.btn_dropdown:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickPopBtn();
//                    isShow = true;
//                    updateMenueImgAdTxt(2);
//                    break;
//                case R.id.btn_add_person:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_add_person");
//                    updateMenueImgAdTxt(1);
//                    break;
//                case R.id.btn_show_person:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    updateMenueImgAdTxt(2);
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_show_person");
//                    break;
//                case R.id.btn_sharepic:
//                    if (!isShowing) {
//                        break;
//                    }
//                    mTopLayout.setVisibility(View.INVISIBLE);
//                    mCtrlLayout.setVisibility(INVISIBLE);
//                    mImageShow.setImageResource(R.drawable.btn_meetingctrl);
//                    isShow = false;
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    createPicPopupView();
//                    break;
//                case R.id.btn_openpic:
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_openpic");
//                    break;
//                case R.id.btn_uploadpic:
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_uploadpic");
//                    break;
//                case R.id.btn_pizhu:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_pizhu");
//                    break;
//                case R.id.btn_ctrl:
//                    if (!isShowing) {
//                        break;
//                    }
//                    mTopLayout.setVisibility(View.INVISIBLE);
//                    mCtrlLayout.setVisibility(INVISIBLE);
//                    mImageShow.setImageResource(R.drawable.btn_meetingctrl);
//                    isShow = false;
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    createCtrlPopupView();
//                    break;
//                case R.id.btn_ban_mic:
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_ban_mic");
//                    updateMenueImgAdTxt(3);
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    break;
//                case R.id.btn_voice_mode:
//                    if(VoiceMode == false) {
//                        ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_voice_mode");
//                        updateMenueImgAdTxt(4);
//                        if (mCtrlPopView != null) {
//                            mCtrlPopView.dismiss();
//                        }
//                    }
//                    break;
//                case R.id.btn_speech_sounds:
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_audio_mode");
//                    updateMenueImgAdTxt(7);
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    break;
//                case R.id.btn_switch_camera:
//                    createCtrlPopupView();
//                    if(VoiceMode == false){
//                        ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_switch_camera");
//                        updateMenueImgAdTxt(5);
//                        if (mCtrlPopView != null) {
//                            mCtrlPopView.dismiss();
//                        }
//                    }
//                    break;
//                case R.id.btn_display_mode:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_display_mode");
//                    updateMenueImgAdTxt(6);
//                    break;
//                case R.id.btn_hangup:
//                    if (mCtrlPopView != null) {
//                        mCtrlPopView.dismiss();
//                    }
//                    if (mSharePicPopView != null) {
//                        mSharePicPopView.dismiss();
//                    }
//                    ((IPopViewCallBack) getContext()).onClickMenuBtn("btn_hangup");
//                    break;
//                default:
//                    break;
//            }
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int layoutid = parent.getId();{
                if (layoutid == R.id.gv_view_meeting) {
                    ((IPopViewCallBack) getContext()).onClickPerson(mJoinUser.get(position));
                }else if (layoutid == R.id.gv_view_no) {
                    if (mOutUser.get(position).getStatus().equals(STATUS.INVITING) == false) {
                        ((IPopViewCallBack) getContext()).onClickPerson(mOutUser.get(position));
                    }
                }
            }
//            switch (parent.getId()) {
//                case R.id.gv_view_meeting:
//                    ((IPopViewCallBack) getContext()).onClickPerson(mJoinUser.get(position));
//                    break;
//                case R.id.gv_view_no:
//                    if (mOutUser.get(position).getStatus().equals(STATUS.INVITING) == false) {
//                        ((IPopViewCallBack) getContext()).onClickPerson(mOutUser.get(position));
//                    }
//                    break;
//                default:
//                    break;
//            }
        }
    };

    private void updateGridView() {
        if (mJoinUser == null) {
            mJoinUser = new ArrayList<MemberInfo>();
        } else {
            mJoinUser.clear();
        }

        if (mOutUser == null) {
            mOutUser = new ArrayList<MemberInfo>();
        } else {
            mOutUser.clear();
        }

        if (mScreenList != null) {
            for (int i = 0; i < mScreenList.size(); i++) {
                if (mScreenList.get(i).getStatus().equals(STATUS.JOINED)) {
                    mJoinUser.add(mScreenList.get(i));
                }
            }
        }

        if (mOtherList != null) {
            for (int i = 0; i < mOtherList.size(); i++) {
                if (mOtherList.get(i).getStatus().equals(STATUS.JOINED)) {
                    mJoinUser.add(mOtherList.get(i));
                }
            }
        }

        if (mUnjoinedList != null) {
            for (int i = 0; i < mUnjoinedList.size(); i++) {
                mOutUser.add(mUnjoinedList.get(i));
            }
        }

        if (mJoinAdapter == null) {
            mJoinAdapter = new GridViewAdapter(mContext, mJoinUser, mScreenList, mOtherList);
            mJoinedGridView.setAdapter(mJoinAdapter);
        } else {
            mJoinAdapter.update(mJoinUser,mScreenList,mOtherList);
        }

        if (mRefuseAdapter == null) {
            mRefuseAdapter = new GridViewAdapter(mContext, mOutUser, null, null);
            mRefuseGridView.setAdapter(mRefuseAdapter);
        } else {
            mRefuseAdapter.update(mOutUser,null,null);
        }
    }

    private void updateImage(){
        if(mImageMic != null && mImageVoice != null && mImageMode!= null && mImageSwith != null) {
            if (mScreenList != null) {
                for (int i = 0; i < mScreenList.size(); i++) {
                    if (mScreenList.get(i).getUserName().equals(Common.USERNAME)) {
                        if (mScreenList.get(i).isAudioMute()) {
                            mImageMic.setImageResource(R.drawable.btn_mic);
                        } else {
                            mImageMic.setImageResource(R.drawable.btn_mic_pre);
                        }

                        if (mScreenList.get(i).isVideoMute()) {
                            mImageMode.setImageResource(R.drawable.btn_mode);
                        } else {
                            mImageMode.setImageResource(R.drawable.btn_mode_pre);
                        }

                        ModeStatus = mScreenList.get(i).isVideoMute();

                        if (mScreenList.get(i).getNet_status().equals(MemberInfo.NET_STATUS.VoiceMode)) {
                            mImageVoice.setImageResource(R.drawable.btn_speech_pre);
                            mImageMode.setClickable(false);
                            mImageMode.setImageResource(R.drawable.btn_mode_g);
                            mImageSwith.setClickable(false);
                            mImageSwith.setImageResource(R.drawable.btn_switch_g);
                        } else {
                            mImageVoice.setImageResource(R.drawable.btn_speech);
                            mImageMode.setClickable(true);
                            mImageSwith.setClickable(true);
                            mImageSwith.setImageResource(R.drawable.btn_switch);
                            if (ModeStatus == false) {
                                mImageMode.setImageResource(R.drawable.btn_mode);
                            } else {
                                mImageMode.setImageResource(R.drawable.btn_mode_pre);
                            }
                        }
                    }
                }
            }

            if (mOtherList != null) {
                for (int i = 0; i < mOtherList.size(); i++) {
                    if (mOtherList.get(i).getUserName().equals(Common.USERNAME)) {
                        if (mOtherList.get(i).isAudioMute()) {
                            mImageMic.setImageResource(R.drawable.btn_mic);
                        } else {
                            mImageMic.setImageResource(R.drawable.btn_mic_pre);
                        }

                        if (mOtherList.get(i).isVideoMute()) {
                            mImageMode.setImageResource(R.drawable.btn_mode);
                        } else {
                            mImageMode.setImageResource(R.drawable.btn_mode_pre);
                        }

                        ModeStatus = mOtherList.get(i).isVideoMute();

                        if (mOtherList.get(i).getNet_status().equals(MemberInfo.NET_STATUS.VoiceMode)) {
                            mImageVoice.setImageResource(R.drawable.btn_speech_pre);
                            mImageMode.setClickable(false);
                            mImageMode.setImageResource(R.drawable.btn_mode_g);
                            mImageSwith.setClickable(false);
                            mImageSwith.setImageResource(R.drawable.btn_switch_g);

                        } else {
                            mImageVoice.setImageResource(R.drawable.btn_speech);
                            mImageMode.setClickable(true);
                            mImageSwith.setClickable(true);
                            mImageSwith.setImageResource(R.drawable.btn_switch);
                            if (ModeStatus == false) {
                                mImageMode.setImageResource(R.drawable.btn_mode);
                            } else {
                                mImageMode.setImageResource(R.drawable.btn_mode_pre);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateCtrlListView() {
        for (int i =0; i < mJoinUser.size(); i++) {
            GBLog.e(TAG, "###########"+mJoinUser.get(i).toString());
        }
        if (mCtrlListJoinAdapter == null) {
            mCtrlListJoinAdapter = new CtrlListJoinAdapter(mContext, mJoinUser, mScreenList, mOtherList,isHost);
            mJoinCtrlList.setAdapter(mCtrlListJoinAdapter);
//            mCtrlListJoinAdapter.setiCtrlBtnCallBack(iCtrlBtnCallBack);

        } else {
            mCtrlListJoinAdapter.update(mJoinUser, mScreenList, mOtherList,isHost);
        }
    }

    private void updateCtrlUnjoinListView() {
        if (mCtrlListUnjoinAdapter == null) {
            mCtrlListUnjoinAdapter = new CtrlListUnjoinAdapter(mUnjoinedList, mContext);
            mUnjoinCtrlList.setAdapter(mCtrlListUnjoinAdapter);
        } else {
            mCtrlListUnjoinAdapter.update(mUnjoinedList);
        }
    }

    private void updateMenueImgAdTxt(int index) {
        mImageAdd.setImageResource(R.drawable.btn_add_person_selector);
        mImageShow.setImageResource(R.drawable.btn_show_person_selector);
        mImageDisplay.setImageResource(R.drawable.btn_mode_selector);

//        mTextAdd.setTextColor(getResources().getColor(R.color.white_color));
//        mTextShow.setTextColor(getResources().getColor(R.color.white_color));
//        mTextMic.setTextColor(getResources().getColor(R.color.white_color));
//        mTextMode.setTextColor(getResources().getColor(R.color.white_color));
//        mTextSwith.setTextColor(getResources().getColor(R.color.white_color));
//        mTextDisplay.setTextColor(getResources().getColor(R.color.white_color));

        if (index == 1) {
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
        } else if (index == 2) {
            if (isShow == false) {
                mImageShow.setImageResource(R.drawable.btn_meetingctrl_pre);
                //mTextShow.setTextColor(getResources().getColor(R.color.yellow_color));
//                mTopLayout.setVisibility(View.VISIBLE);
                mCtrlLayout.setVisibility(VISIBLE);
                mLineView.setVisibility(View.VISIBLE);
                isShow = true;
                //  updateGridView();
            } else {
                mImageShow.setImageResource(R.drawable.btn_meetingctrl);
                //mTextShow.setTextColor(getResources().getColor(R.color.white_color));
                mTopLayout.setVisibility(View.INVISIBLE);
                mCtrlLayout.setVisibility(INVISIBLE);
                mLineView.setVisibility(View.INVISIBLE);
                isShow = false;
            }
        } else if (index == 3) {
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
//            if (MicStatus == false) {
//                mImageMic.setImageResource(R.drawable.btn_mic);
//                // mTextMic.setText(getResources().getString(R.string.txt_ban_mic_pre));
//                MicStatus = true;
//            } else {
//                mImageMic.setImageResource(R.drawable.btn_mic_pre);
//                //mTextMic.setText(getResources().getString(R.string.txt_ban_mic));
//                MicStatus = false;
//            }
        } else if (index == 4) {
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
//            if (ModeStatus == false) {
//                mImageMode.setImageResource(R.drawable.btn_mode_pre);
//                // mTextMode.setText(getResources().getString(R.string.txt_voice_mode_pre));
//                ModeStatus = true;
//            } else {
//                mImageMode.setImageResource(R.drawable.btn_mode);
//                // mTextMode.setText(getResources().getString(R.string.txt_voice_mode));
//                ModeStatus = false;
//            }
        } else if (index == 5) {
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
        } else if (index == 6) {
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
        }else if(index == 7){
            mTopLayout.setVisibility(View.INVISIBLE);
            mCtrlLayout.setVisibility(INVISIBLE);
            isShow = false;
//            if (VoiceMode == false) {
//                mImageVoice.setImageResource(R.drawable.btn_speech_pre);
//                // mTextMode.setText(getResources().getString(R.string.txt_voice_mode_pre));
//                VoiceMode = true;
//                mImageMode.setClickable(false);
//                mImageMode.setImageResource(R.drawable.btn_mode_g);
//                mImageSwith.setClickable(false);
//                mImageSwith.setImageResource(R.drawable.btn_switch_g);
//            } else {
//                mImageVoice.setImageResource(R.drawable.btn_speech);
//                VoiceMode = false;
//                mImageMode.setClickable(true);
//                mImageSwith.setClickable(true);
//                mImageSwith.setImageResource(R.drawable.btn_switch);
//                if (ModeStatus == false) {
//                    mImageMode.setImageResource(R.drawable.btn_mode);
//                } else {
//                    mImageMode.setImageResource(R.drawable.btn_mode_pre);
//                }
//           }
        }
    }

    @Override
    public void initRecord(boolean isShow, String status, String meetingId) {
        this.meetingId = meetingId;
        this.mIsShow = isShow;
        isHost = isShow;
        Message msg = new Message();
        msg.obj = status;
        msg.what = MESSAGE_1;
        mRecordHandler.sendMessage(msg);
    }

    @Override
    public void startRecord(boolean result, String error) {
        if (result == true) {
            Toast.makeText(mContext,"启动录像失败：" + error,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext,"开始录制！",Toast.LENGTH_SHORT).show();
            isRecording = true;
            StartVideotapePoint();
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(new RecordTask(),0,1000L);
        }
    }

    @Override
    public void endRecord(boolean result, String error) {
        if (result == true) {
            Toast.makeText(mContext,"停止录像失败：" + error,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext,"结束录制！",Toast.LENGTH_SHORT).show();
            isRecording = false;
            StopVideotapePoint();
            mChronometer.setText("00:00:00");
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    private final int MESSAGE_1 = 0x001;
    private final int MESSAGE_2 = 0x002;
    private Handler mRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MESSAGE_1:
                if (mIsShow) {
                    mVideotapeLayout.setVisibility(VISIBLE);
                    if (((String)msg.obj).equalsIgnoreCase("starting")) {
                        isRecording = true;
                        StartVideotapePoint();
                        if (VideoRecord.getInstance(mContext).getStartTime() == 0) {
                            mChronometer.setText("--:--:--");
                        } else {
                            if (mTimer == null) {
                                mTimer = new Timer();
                            }
                            mTimer.schedule(new RecordTask(),0,1000L);
                        }
                    } else {
                        mChronometer.setText("00:00:00");
                    }
                } else {
                    mVideotapeLayout.setVisibility(INVISIBLE);
                }
            break;
            case MESSAGE_2:
                mChronometer.setText((String) msg.obj);
            break;
            default:
                break;
            }
        }
    };

    private class RecordTask extends TimerTask {

        @Override
        public void run() {
            int time = (int) ((SystemClock.elapsedRealtime() - VideoRecord.getInstance(mContext).getStartTime()) / 1000);
            String hh = new DecimalFormat("00").format(time / 3600);
            String mm = new DecimalFormat("00").format(time % 3600 / 60);
            String ss = new DecimalFormat("00").format(time % 60);
            String timeFormat = new String(hh + ":" + mm + ":" + ss);

            Message msg = new Message();
            msg.obj = timeFormat;
            msg.what = MESSAGE_2;
            mRecordHandler.sendMessage(msg);
        }
    }

    private void createCtrlPopupView () {
        if (mCtrlPopView == null) {
            mCtrlPopView = new PopupWindow();
            mCtrlPopView.setWidth(mImageCtrl.getMeasuredWidth());
            mCtrlPopView.setHeight(mImageCtrl.getMeasuredHeight()*4);
            LinearLayout llyt = new LinearLayout(mContext);
            llyt.setOrientation(VERTICAL);
            llyt.setBackgroundResource(R.drawable.bg_pop);

            mImageSwith = new ImageView(mContext);
            mImageSwith.setId(R.id.btn_switch_camera);
            mImageSwith.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageSwith.setImageResource(R.drawable.btn_switch_camera_selector);

            mImageMode = new ImageView(mContext);
            mImageMode.setId(R.id.btn_voice_mode);
            mImageMode.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageMode.setImageResource(R.drawable.btn_camera_selector);

            mImageVoice = new ImageView(mContext);
            mImageVoice.setId(R.id.btn_speech_sounds);
            mImageVoice.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageVoice.setImageResource(R.drawable.btn_speech_selector);

            mImageMic = new ImageView(mContext);
            mImageMic.setId(R.id.btn_ban_mic);
            mImageMic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageMic.setImageResource(R.drawable.btn_ban_mic_selector);

            ImageView imgLine1 = new ImageView(mContext);
            imgLine1.setBackgroundColor(Color.WHITE);

            ImageView imgLine2 = new ImageView(mContext);
            imgLine1.setBackgroundColor(Color.WHITE);

            ImageView imgLine3 = new ImageView(mContext);
            imgLine1.setBackgroundColor(Color.WHITE);

            LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
            lp1.weight = 1;

            LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
            lp2.leftMargin = 10;
            lp2.rightMargin = 10;

            llyt.addView(mImageSwith, lp1);
            llyt.addView(imgLine1, lp2);
            llyt.addView(mImageMode, lp1);
            llyt.addView(imgLine2, lp2);
            llyt.addView(mImageVoice, lp1);
            llyt.addView(imgLine3, lp2);
            llyt.addView(mImageMic, lp1);

            llyt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            mCtrlPopView.setContentView(llyt);

//            if (MicStatus == false) {
//                mImageMic.setImageResource(R.drawable.btn_mic_pre);
//            } else {
//                mImageMic.setImageResource(R.drawable.btn_mic);
//            }
//
//            if (ModeStatus == false) {
//                mImageMode.setImageResource(R.drawable.btn_mode);
//            } else {
//                mImageMode.setImageResource(R.drawable.btn_mode_pre);
//            }
//
//            if(VoiceMode == false){
//                mImageVoice.setImageResource(R.drawable.btn_speech);
//                mImageMode.setClickable(true);
//                mImageMode.setImageResource(R.drawable.btn_mode);
//                mImageSwith.setImageResource(R.drawable.btn_switch);
//                mImageSwith.setClickable(true);
//            }else {
//                mImageVoice.setImageResource(R.drawable.btn_speech_pre);
//                mImageMode.setImageResource(R.drawable.btn_mode_g);
//                mImageMode.setClickable(false);
//                mImageSwith.setImageResource(R.drawable.btn_switch_g);
//                mImageSwith.setClickable(false);
//            }

            mImageSwith.setOnClickListener(mClickListener);
            mImageMode.setOnClickListener(mClickListener);
            mImageVoice.setOnClickListener(mClickListener);
            mImageMic.setOnClickListener(mClickListener);
        }

        if (mCtrlPopView.isShowing()) {
            mCtrlPopView.dismiss();
        } else {
            int x,y;
            int[] location = new  int[2] ;
            mImageCtrl.getLocationOnScreen(location);
            x = location[0];
            y = Common.SCREENHEIGHT-(5*mImageCtrl.getMeasuredHeight())-mImageBtn.getMeasuredHeight()*2;
            mCtrlPopView.showAtLocation(mImageCtrl,Gravity.LEFT|Gravity.TOP,x,y);
        }
    }
    private void createPicPopupView () {
        if (mSharePicPopView == null) {
            mSharePicPopView = new PopupWindow();
            mSharePicPopView.setWidth(mImageSharepic.getMeasuredWidth());
            mSharePicPopView.setHeight(mImageSharepic.getMeasuredHeight()*2);
            LinearLayout llyt = new LinearLayout(mContext);
            llyt.setOrientation(VERTICAL);
            llyt.setBackgroundResource(R.drawable.bg_pop);

            mImageOpenpic = new ImageView(mContext);
            mImageOpenpic.setId(R.id.btn_openpic);
            mImageOpenpic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageOpenpic.setImageResource(R.drawable.btn_openpic_selector);

            mImageUploadpic = new ImageView(mContext);
            mImageUploadpic.setId(R.id.btn_uploadpic);
            mImageUploadpic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageUploadpic.setImageResource(R.drawable.btn_uploadpic_selector);

            ImageView imgLine1 = new ImageView(mContext);
            imgLine1.setBackgroundColor(Color.WHITE);

            LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
            lp1.weight = 1;

            LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
            lp2.leftMargin = 10;
            lp2.rightMargin = 10;

            llyt.addView(mImageUploadpic, lp1);
            llyt.addView(imgLine1, lp2);
            llyt.addView(mImageOpenpic, lp1);

            llyt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            mSharePicPopView.setContentView(llyt);

            mImageUploadpic.setOnClickListener(mClickListener);
            mImageOpenpic.setOnClickListener(mClickListener);
        }

        if (mSharePicPopView.isShowing()) {
            mSharePicPopView.dismiss();
        } else {
            int x,y;
            int[] location = new  int[2] ;
            mImageSharepic.getLocationOnScreen(location);
            x = location[0];
            y = Common.SCREENWIDTH-(5*mImageSharepic.getMeasuredHeight())-mImageBtn.getMeasuredHeight()*2;
            mSharePicPopView.showAtLocation(mImageSharepic,Gravity.LEFT|Gravity.TOP,x,y);
        }
    }

}


