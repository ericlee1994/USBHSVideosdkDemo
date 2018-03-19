package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2017/7/19.
 */

public class VI {

    private String dataSourceID;
    private String displayName;
    private String remoteName;
    private STATUS status;
    private String id;
    private SESSIONTYPE sessionType;
    private int participantId;
    private boolean isContent;
    private boolean isVideoMute;
    private boolean isAudioMode;
    private boolean isAudioMute;
    private boolean isInBanList;
    private boolean isLocal;
    private boolean FullScreen;
    private boolean isBlank;
    private MemberInfo.NET_STATUS net_status;
    private DisplayType mDisplayType;
    private ArrayList<String> mUrls;
    private String mResId;
    private boolean isComment;

    public MemberInfo.NET_STATUS getNet_status() {
        return net_status;
    }

    public void setNet_status(MemberInfo.NET_STATUS net_status) {
        this.net_status = net_status;
    }

    public String getDataSourceID() {
        return dataSourceID;
    }

    public void setDataSourceID(String dataSourceID) {
        this.dataSourceID = dataSourceID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String remoteName) {
        this.displayName = remoteName;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public boolean isContent() {
        return isContent;
    }

    public void setContent(boolean content) {
        isContent = content;
    }

    public boolean isVideoMute() {
        return isVideoMute;
    }

    public void setVideoMute(boolean videoMute) {
        isVideoMute = videoMute;
    }

    public boolean isAudioMode() {
        return isAudioMode;
    }

    public void setAudioMode(boolean audioMode) {
        isAudioMode = audioMode;
    }

    public boolean isAudioMute() {
        return isAudioMute;
    }

    public void setAudioMute(boolean audioMute) {
        isAudioMute = audioMute;
    }

    public boolean getIsInBanList() {
        return isInBanList;
    }

    public void setInBanList(boolean inBanList) {
        isInBanList = inBanList;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isFullScreen() {
        return FullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        FullScreen = fullScreen;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isInBanList() {
        return isInBanList;
    }

    public boolean isBlank() {
        return isBlank;
    }

    public void setBlank(boolean blank) {
        isBlank = blank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public SESSIONTYPE getSessionType() {
        return sessionType;
    }

    public void setSessionType(SESSIONTYPE sessionType) {
        this.sessionType = sessionType;
    }

    public DisplayType getmDisplayType() {
        return mDisplayType;
    }

    public void setmDisplayType(DisplayType mDisplayType) {
        this.mDisplayType = mDisplayType;
    }

    public ArrayList<String> getmUrls() {
        return mUrls;
    }

    public void setmUrls(ArrayList<String> mUrls) {
        this.mUrls = mUrls;
    }

    public String getmResId() {
        return mResId;
    }

    public void setmResId(String mResId) {
        this.mResId = mResId;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }
}
