package com.shgbit.android.heysharevideo.widget;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.OpenGLTextureView;
import com.shgbit.android.heysharevideo.bean.DISPLAY_MODE;
import com.shgbit.android.heysharevideo.bean.DisplayType;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.VI;
import com.shgbit.android.heysharevideo.callback.IVideoViewCallBack;
import com.shgbit.android.heysharevideo.callback.IViewLayoutCallBack;
import com.shgbit.android.heysharevideo.contact.MeetingInfoManager;
import com.shgbit.android.heysharevideo.util.GBLog;

import java.util.ArrayList;
import java.util.List;

//import com.shgbit.android.whiteboard.CustomPaintView;


/**
 * Created by Eric on 2017/7/19.
 * @author Eric
 */

public class MyVideoVIew extends ViewGroup{

    private static final String TAG = "VideoVIew";
    private Context mContext;
    private int displayCount = 6;
    private static ArrayList<CellView> mScreenList;
    private ArrayList<VI> otherList = new ArrayList<VI>();
    private IVideoViewCallBack iVideoViewCallBack;
    private Handler handler = new Handler();
    private OpenGLTextureView localVideoView;

    private final int UPDATEVIEW = 0x001;
    private final int HIDELAYOUT = 0x002;
    private final int UPDATEOTHER = 0x003;

    private boolean isInVoiceMode = false;
    private boolean isFirstPIP = false;
    private boolean hasDataChanged = false;
    private boolean isUVC;
    private boolean isFirstInitLocal = true;
    public static boolean isMove = true;
    private int flag = 0;

//    private CustomPaintView mMainView;

    private DISPLAY_MODE display_mode = DISPLAY_MODE.NOT_FULL_ONEFIVE;

    public MyVideoVIew(Context context,DISPLAY_MODE displayMode) {
        super(context);
        mContext = context;
        this.display_mode = displayMode;
        if (display_mode.equals(DISPLAY_MODE.NOT_FULL_FOUR)
                || displayMode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)){
            displayCount = 4;
        }else if (display_mode.equals(DISPLAY_MODE.NOT_FULL_SIX)){
            displayCount = 6;
        }else if (display_mode.equals(DISPLAY_MODE.FULL_PIP)){
            displayCount = 2;
        }else if (displayMode.equals(DISPLAY_MODE.FULL_PIP_SIX)){
            displayCount = 6;
        }else if (displayMode.equals(DISPLAY_MODE.NOT_FULL_ONEFIVE)) {
            displayCount = 6;
        }else if (displayMode.equals(DISPLAY_MODE.FULL)) {
            displayCount = 1;
        }
        MeetingInfoManager.getInstance().ModeChange(displayMode);
        initView();
    }

    public MyVideoVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyVideoVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView(){
        mScreenList = new ArrayList<CellView>();
        for (int i = 0; i < displayCount; i++) {
            CellView viewLayout;
            viewLayout = new CellView(mContext);
            viewLayout.setInfo(null, display_mode);
            viewLayout.setPosition(i);
            viewLayout.setId(i);
            addView(viewLayout);
            mScreenList.add(viewLayout);
        }

//        localVideoView = new OpenGLTextureView(getContext(),true);
//        localVideoView.setSourceID(NemoSDK.getInstance().getLocalVideoStreamID());
//        localVideoView.setContent(false);
//        addView(localVideoView);
    }

    public void closeLocalMic(boolean hasVoice){
        if (hasVoice) {
            for (int i = 0; i < mScreenList.size(); i++) {
                if (mScreenList.get(i).getVideoInfo() != null
                        && mScreenList.get(i).getVideoInfo().isLocal()){
                    mScreenList.get(i).getVideoInfo().setAudioMute(true);
                    MeetingInfoManager.getInstance().AudioMute("", true);
                }
            }
        }else {
            for (int i = 0; i < mScreenList.size(); i++) {
                if (mScreenList.get(i).getVideoInfo() != null
                        && mScreenList.get(i).getVideoInfo().isLocal()){
                    mScreenList.get(i).getVideoInfo().setAudioMute(false);
                    MeetingInfoManager.getInstance().AudioMute("", false);
                }
            }
        }
    }

    public void setLocalMute(boolean isMute) {
        if (isMute){
            for (int i = 0; i < mScreenList.size(); i++){
                mScreenList.get(i).getVideoInfo().setAudioMode(true);
                MeetingInfoManager.getInstance().VideoMute("", true);
                isInVoiceMode = true;
            }
        }else {
            for (int i = 0; i < mScreenList.size(); i++){
                mScreenList.get(i).getVideoInfo().setAudioMode(false);
                MeetingInfoManager.getInstance().VideoMute("", false);
                isInVoiceMode = false;
            }
        }
    }

    public void closeLocalView (boolean isClose) {
        if (isClose) {
            for (int i = 0; i < mScreenList.size(); i++){
                if (mScreenList.get(i).getVideoInfo()!=null && mScreenList.get(i).getVideoInfo().isLocal()){
//                    mScreenList.get(i).getVideoInfo().setVideoMute(true);
                    MeetingInfoManager.getInstance().VideoMute("", true);
                }
            }
        }else {
            for (int i = 0; i < mScreenList.size(); i++){
                if (mScreenList.get(i).getVideoInfo()!=null && mScreenList.get(i).getVideoInfo().isLocal()){
//                    mScreenList.get(i).getVideoInfo().setVideoMute(false);
                    MeetingInfoManager.getInstance().VideoMute("", false);
                }
            }
        }
    }

    public void updateViewList(List<MemberInfo> mMember, List<MemberInfo> mOther){
        if (mMember == null){
            return;
        }
        Message msg = Message.obtain(UIHandler, UPDATEVIEW, mMember);
        msg.sendToTarget();
        Message msg1 = Message.obtain(UIHandler, UPDATEOTHER, mOther);
        msg1.sendToTarget();
    }

//    public void setPizhu (CustomPaintView mainView) {
//        if (mainView == null) {
//            return;
//        }
//        mMainView = mainView;
//    }

    public String getPicturePage () {
        String page = "";
        if (mScreenList == null) {
            return page;
        }

        for (int i = 0; i < mScreenList.size(); i++) {
            if (mScreenList.get(i) == null || mScreenList.get(i).getVideoInfo() == null) {
                continue;
            }
            if (mScreenList.get(i).getVideoInfo().getmDisplayType() == DisplayType.PICTURE) {
                page = mScreenList.get(i).getPicturePage();
                break;
            }
        }
        return page;
    }

//    private void endPizhu () {
//        if (mScreenList == null) {
//            return;
//        }
//
//        for (int i = 0; i < mScreenList.size(); i++) {
//            if (mScreenList.get(i) == null) {
//                continue;
//            }
//            mScreenList.get(i).endPizhu();
//        }
//    }

    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEVIEW:
                    List<MemberInfo> mMember = (List<MemberInfo>) msg.obj;
                    flag = 0;
                    GBLog.i(TAG, "mMember.size()=" + mMember.size());
                    for (int i = 0; i < mScreenList.size(); i++) {
                        VI vi = null;
                        if (i < mMember.size()) {
                            vi = new VI();
                            vi.setParticipantId(mMember.get(i).getParticipantId());
                            vi.setDisplayName(mMember.get(i).getDisplayName());
                            vi.setRemoteName(mMember.get(i).getRemoteName());
                            vi.setStatus(mMember.get(i).getStatus());
                            vi.setSessionType(mMember.get(i).getSessionType());
                            vi.setContent(mMember.get(i).isContent());
                            vi.setVideoMute(mMember.get(i).isVideoMute());
                            vi.setAudioMute(mMember.get(i).isAudioMute());
                            vi.setLocal(mMember.get(i).isLocal());
                            vi.setBlank(mMember.get(i).isBlank());
                            vi.setDataSourceID(mMember.get(i).getDataSourceID());
                            vi.setId(mMember.get(i).getId());
                            vi.setNet_status(mMember.get(i).getNet_status());
                            vi.setmDisplayType(mMember.get(i).getmDisplayType());
                            vi.setmUrls(mMember.get(i).getmUrls());
                            vi.setmResId(mMember.get(i).getResId());
                            vi.setComment(mMember.get(i).isComment());
                            vi.setUVC(mMember.get(i).isUvc());
                        }

                        mScreenList.get(i).setInfo(vi, display_mode);
//                        if (vi != null && vi.isComment()) {
//                            endPizhu();
//                            mScreenList.get(i).startPizhu(mMainView);
//                        } else {
//                            mScreenList.get(i).endPizhu();
//                        }
                    }
                        for (int i = 0; i < mScreenList.size(); i++) {
                            mScreenList.get(i).getVideoCellView().releaseRender();
                            mScreenList.get(i).getVideoCellView().notifyRender();

                        if (mScreenList.get(i).getVideoInfo() != null) {
                            if (mScreenList.get(i).getVideoInfo().isLocal()) {
                                if (isFirstInitLocal){
                                    isUVC = !mScreenList.get(i).getVideoInfo().isUVC();
                                    isFirstInitLocal = false;
                                }
                                localVideoView = null;
                                localVideoView = mScreenList.get(i).getVideoCellView();
                                if (!mScreenList.get(i).getVideoInfo().isUVC() == isUVC){
                                    iVideoViewCallBack.receiveLocal();
                                    isUVC = !isUVC;
                                }
                            }
                        }
                    }

                    requestLayout();
                    requestRender();
                    break;
                case UPDATEOTHER:
                    List<MemberInfo> mOther = (List<MemberInfo>) msg.obj;
                    otherList.clear();
                    for (int i = 0; i < mOther.size(); i++){
                        VI vi = new VI();
                        vi.setDataSourceID(mOther.get(i).getDataSourceID());
                        vi.setContent(mOther.get(i).isContent());
                        vi.setDisplayName(mOther.get(i).getDisplayName());
                        vi.setId(mOther.get(i).getId());
                        otherList.add(vi);
                    }
                    break;
                case HIDELAYOUT:
                    if (mScreenList != null) {
                        for (int i = 0; i < mScreenList.size(); i++) {
                            mScreenList.get(i).hideButtonLayout();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void onListItemClick(String id) {
//        MeetingInfoManager.getInstance().PopUp(id);
    }


    public void setiVideoViewCallBack(IVideoViewCallBack iVideoViewCallBack) {
        this.iVideoViewCallBack = iVideoViewCallBack;
    }

    IViewLayoutCallBack iViewLayoutCallBack = new IViewLayoutCallBack() {
        @Override
        public void closePic() {
            if (iVideoViewCallBack != null) {
                iVideoViewCallBack.closePic();
            }
        }

        @Override
        public void changeId(int id) {
            UIHandler.removeMessages(HIDELAYOUT);
            UIHandler.sendEmptyMessage(HIDELAYOUT);
                if (mScreenList.get(id).getVideoInfo() != null){
                        MeetingInfoManager.getInstance().ScreenExchange(mScreenList.get(0).getVideoInfo(), mScreenList.get(id).getVideoInfo());
            }
        }

        @Override
        public void changeStatus(int id) {
            UIHandler.removeMessages(HIDELAYOUT);
            UIHandler.sendEmptyMessage(HIDELAYOUT);
                if (mScreenList.get(id).getVideoInfo() != null) {
                    MeetingInfoManager.getInstance().PopDown(mScreenList.get(id).getVideoInfo());
            }
        }

        @Override
        public void hideViewLayout(String id) {
            for (int i = 0; i < mScreenList.size(); i++) {
                if (mScreenList.get(i).getVideoInfo() != null){
                    if (mScreenList.get(i).getVideoInfo().getId().equals(id)){
                    }else {
                        mScreenList.get(i).hideButtonLayout();
                    }
                }
            }
        }

        @Override
        public void backToDefaultMode(VI vi) {
            iVideoViewCallBack.backToDefaultMode(mScreenList.get(0).getVideoInfo(), vi);
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < mScreenList.size(); i++) {
            if (mScreenList.get(i) == null && mScreenList.get(i).getVideoCellView() != null) {
                continue;
            }

            int vl = 0, vt = 0, vr = 0, vb = 0 , x = 5;
            if (display_mode.equals(DISPLAY_MODE.NOT_FULL_ONEFIVE)) {
                switch (i) {
                    case 0:
                        vl = x;
                        vt = x;
                        vr = getWidth() * 5 / 6 - x;
                        vb = getHeight();
                        break;
                    case 1:
                        vl = getWidth() * 5 / 6;
                        vt = x;
                        vr = getWidth() - x;
                        vb = getHeight() / 5;
                        break;
                    case 2:
                        vl = getWidth() * 5 / 6;
                        vt = x+getHeight()/5;
                        vr = getWidth() - x;
                        vb = getHeight()*2/5;
                        break;
                    case 3:
                        vl = getWidth() * 5 / 6;
                        vt = x+getHeight()*2/5 ;
                        vr = getWidth() - x;
                        vb = getHeight()*3/5;
                        break;
                    case 4:
                        vl = getWidth() * 5 / 6;
                        vt = x+getHeight()*3/5;
                        vr = getWidth() - x;
                        vb = getHeight()*4/5;
                        break;
                    case 5:
                        vl = getWidth() * 5 / 6;
                        vt = x+getHeight()*4/5;
                        vr = getWidth() - x;
                        vb = getHeight();
                        break;
//                        vl = x;
//                        vt = x;
//                        vr = getWidth() * 5 / 6 - x;
//                        vb = getHeight();
//                        break;
                    default:
                        break;
                }
            } else if (display_mode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)) {
                int y = getWidth() / 100;
                switch (i) {
                    case 0:
                        vl = getWidth()/12;
                        vt = 0;
                        vr = getWidth()/2-x;
                        vb = getHeight() / 2 -x;
                        break;
                    case 1:
                        vl = getWidth()/2;
                        vt = 0;
                        vr = getWidth()*11/12;
                        vb = getHeight() / 2 -x;
                        break;
                    case 2:
                        vl = getWidth()/12;
                        vt = getHeight() / 2;
                        vr = getWidth()/2-x;
                        vb = getHeight();
                        break;
                    case 3:
                        vl = getWidth()/2;
                        vt = getHeight() / 2;
                        vr = getWidth()*11/12;
                        vb = getHeight();
                        break;
                    default:
                        break;
                }
            } else if (display_mode.equals(DISPLAY_MODE.FULL_PIP)) {
                switch (i) {
                    case 0:
                        vl = 0;
                        vt = 0;
                        vr = getWidth();
                        vb = getHeight();
                        break;
                    case 1:
                        vl = 0;
                        vt = getHeight() * 4 / 5;
                        vr = getWidth() / 5;
                        vb = getHeight();
                        break;
                    default:
                        break;
                }
            } else if (display_mode.equals(DISPLAY_MODE.FULL)) {
                switch (i) {
                    case 0:
                        vl = 0;
                        vt = 0;
                        vr = getWidth();
                        vb = getHeight();
                        break;
                    default:
                        break;
                }
            } else if (display_mode.equals(DISPLAY_MODE.FULL_PIP_SIX)) {
                int y = getWidth() / 40;
                int vTop = getHeight() * 4 / 5;
                int vWidth = (getWidth() - 6 * y) / 5;
                int vHeigt = vWidth * 9 / 16;
                switch (i) {
                    case 0:
                        vl = 0;
                        vt = 0;
                        vr = getWidth();
                        vb = getHeight();
                        break;
                    case 1:
                        vl = y;
                        vt = vTop;
                        vr = y + vWidth;
                        vb = vTop + vHeigt;
                        break;
                    case 2:
                        vl = 2 * y + vWidth;
                        vt = vTop;
                        vr = 2 * y + 2 * vWidth;
                        vb = vTop + vHeigt;
                        break;
                    case 3:
                        vl = 3 * y + 2 * vWidth;
                        vt = vTop;
                        vr = 3 * y + 3 * vWidth;
                        vb = vTop + vHeigt;
                        break;
                    case 4:
                        vl = 4 * y + 3 * vWidth;
                        vt = vTop;
                        vr = 4 * y + 4 * vWidth;
                        vb = vTop + vHeigt;
                        break;
                    case 5:
                        vl = 5 * y + 4 * vWidth;
                        vt = vTop;
                        vr = 5 * y + 5 * vWidth;
                        vb = vTop + vHeigt;
                        break;
                    default:
                        break;
                }
            }
//            mScreenList.get(i).setL(vl);
//            mScreenList.get(i).setR(vr);
//            mScreenList.get(i).setT(vt);
//            mScreenList.get(i).setB(vb);
            mScreenList.get(i).setIViewChangeCallBack(iViewLayoutCallBack);
            mScreenList.get(i).setPosition(i);
            mScreenList.get(i).init(mContext);
            mScreenList.get(i).updateView();
            mScreenList.get(i).setCtrlBtnSize(vr-vl,vb-vt);


            if (i != 0 || display_mode.equals(DISPLAY_MODE.NOT_FULL_QUARTER)) {
                mScreenList.get(i).setOnClickListener(mOnClickListener);
            }

//            mScreenList.get(i).layout(mScreenList.get(i).getL(),mScreenList.get(i).getT(),mScreenList.get(i).getR(),mScreenList.get(i).getB());
           if (!display_mode.equals(DISPLAY_MODE.FULL_PIP) || i == 0) {
               mScreenList.get(i).layout(vl, vt, vr, vb);
           }

           if (display_mode.equals(DISPLAY_MODE.FULL_PIP)){
//               if (!isFirstPIP && i > 0) {
//                   mScreenList.get(i).layout(vl, vt, vr, vb);
//                   isFirstPIP = true;
//               }
               if ((flag < 5 && i == 1)) {
                   if (mScreenList.get(i).getL() == 0 && mScreenList.get(i).getR() == 0 &&
                           mScreenList.get(i).getTop() == 0 && mScreenList.get(i).getBottom() == 0) {
                       mScreenList.get(i).setL(vl);
                       mScreenList.get(i).setT(vt);
                       mScreenList.get(i).setR(vr);
                       mScreenList.get(i).setB(vb);
                   }
                   if ((vl != mScreenList.get(i).getL()) || (vt != mScreenList.get(i).getT())
                           || (vr != mScreenList.get(i).getR()) || (vb != mScreenList.get(i).getB())) {

                       mScreenList.get(i).layout(mScreenList.get(i).getL(), mScreenList.get(i).getT(),
                               mScreenList.get(i).getR(), mScreenList.get(i).getB());
                       flag ++;
                   }else {
                       mScreenList.get(i).layout(vl, vt, vr, vb);
                       flag++;
                   }
//                   isMove = true;
               }
               }
           }

    }

    public static ArrayList<Point> getOtherPosition(int mPositon){
        ArrayList<Point> list = new ArrayList<Point>();
        for (int i = 1; i < mScreenList.size(); i++) {
            if (i != mPositon) {
                Point point = new Point((int)(mScreenList.get(i).getX()), (int)(mScreenList.get(i).getY()));
                list.add(point);
            }
        }
        return list;
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            GBLog.i(TAG, "[user operation]click mScreen view");
                UIHandler.removeMessages(HIDELAYOUT);

                for (int i = 0; i < mScreenList.size(); i++) {
                    mScreenList.get(i).hideButtonLayout();
                }
                if (view.getId() < mScreenList.size()) {
                    mScreenList.get(view.getId()).showButtonLayout();
                }
                UIHandler.sendEmptyMessageDelayed(HIDELAYOUT, 5 * 1000);
        }
    };

    private void requestRender() {
        handler.removeCallbacks(drawVideoFrameRunnable);
        handler.postDelayed(drawVideoFrameRunnable, 1000 / 15);
    }

    private Runnable drawVideoFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScreenList != null) {
                for (int i = 0; i < mScreenList.size(); i++) {
                    if (mScreenList.get(i).getVideoInfo() == null) {
                        continue;
                    }
                    if (mScreenList.get(i).getVideoCellView() != null && !mScreenList.get(i).getVideoInfo().isLocal()){
                        mScreenList.get(i).getVideoCellView().requestRender();
                        GBLog.e("VideoActivity", "drawVideoFrameRunnable" + i + "," + mScreenList.get(i).getVideoCellView());
                    }
                }
            }

            requestRender();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            View child = getChildAt(childIndex);
            child.measure(MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    height, MeasureSpec.UNSPECIFIED));
        }
    }

    public void stopRender(){
        handler.removeCallbacks(drawVideoFrameRunnable);
    }

    public void destroy() {
        GBLog.i(TAG, "NewVideoView destroy" );
        destroyDrawingCache();
        stopRender();
        stopLocalFrameRender();

        for (CellView newViewLayout : mScreenList) {
            newViewLayout.Finalize();
        }
        mScreenList.clear();
        handler.removeCallbacksAndMessages(null);
    }

    public ArrayList<VI> getVis() {
        return otherList;
    }

    public void updateCamera(boolean isUvc) {

        if (localVideoView != null) {
            localVideoView.updateCamrea(isUvc);
        }

    }

    public OpenGLTextureView getmLocalVideoCell() {
        return localVideoView;
    }

    public void stopLocalFrameRender() {
        handler.removeCallbacks(drawLocalVideoFrameRunnable);
    }

    public void requestLocalFrame() {
        handler.removeCallbacks(drawLocalVideoFrameRunnable);
        requestLocalVideoRender();
    }

    private void requestLocalVideoRender() {
        if (getVisibility() == VISIBLE) {
            handler.postDelayed(drawLocalVideoFrameRunnable, 1000 / 15);
        }
    }

    private Runnable drawLocalVideoFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (localVideoView != null) {
                localVideoView.requestRender();
                GBLog.e("VideoActivity", "localVideoView:" + localVideoView);
            }
            requestLocalVideoRender();
        }
    };


}
