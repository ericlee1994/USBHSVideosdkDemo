package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

public class MemberInfo {
	
	private boolean isBlank;
	
	private boolean isLocal;
	
	private String id;

	private String DataSourceID;
	
	private int ParticipantId;
	
	private String RemoteName;//same as id
	
	private boolean isAudioMute;
	
	private boolean isContent;
	
	private boolean isVideoMute;
	
	private String userName;//same as id
	
	private String displayName;
	
	private STATUS status;
	
	private SESSIONTYPE sessionType;
	
	private NET_STATUS net_status;

	private CTRL_STATUS ctrl_status;

	private DisplayType mDisplayType;

	private ArrayList<String> mUrls;

	private String resId;

	private boolean isComment;

	private StatusCtrl statusCtrl;

	private CtrlStatus ctrlStatus;

	public enum CTRL_STATUS{
		NULL,
		Mic_Mute,
		Mic_unMute,
		Speaker_SetVol,
		Camera_Mute,
		Camera_unMute
	}
	
	public enum NET_STATUS{
		NULL, 	//初始
		Normal,	//正常
		Loading,//加载中
		Lost,	//带宽不足
		VideoMute, //
		ContentOnlyUnsend, //contentonly入会并且没点发送
		VoiceMode
	}
	
	public MemberInfo() {
		isBlank = false;
		isLocal = false;
		id = "";
		DataSourceID = "";
		ParticipantId = 0;
		RemoteName = "";
		isAudioMute = false;
		isContent = false;
		isVideoMute = false;
		userName = "";
		displayName = "";
		status= null;
		sessionType = SESSIONTYPE.ALL;
		net_status = NET_STATUS.NULL;
		mDisplayType = DisplayType.VIDEO;
		ctrl_status = CTRL_STATUS.NULL;
		statusCtrl = new StatusCtrl();
		mUrls = null;
		resId = null;
		isComment = false;
		ctrlStatus = null;
	}

	public MemberInfo(MemberInfo info) {
		isBlank = info.isBlank();
		isLocal = info.isLocal();
		id = info.getId();
		DataSourceID = info.getDataSourceID();
		ParticipantId = info.getParticipantId();
		RemoteName = info.getRemoteName();
		isAudioMute = info.isAudioMute();
		isContent = info.isContent();
		isVideoMute = info.isVideoMute();
		userName = info.getUserName();
		displayName = info.getDisplayName();
		status= info.getStatus();
		sessionType = info.getSessionType();
		net_status = info.getNet_status();
		mDisplayType = info.getmDisplayType();
		ctrl_status = info.getCtrl_status();
		mUrls = info.getmUrls();
		resId = info.getResId();
		isComment = info.isComment();
		ctrlStatus = info.getCtrlStatus();
		statusCtrl = info.getStatusCtrl();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public boolean isBlank() {
		return isBlank;
	}

	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}

	public String getDataSourceID() {
		return DataSourceID;
	}

	public void setDataSourceID(String dataSourceID) {
		DataSourceID = dataSourceID;
	}

	public int getParticipantId() {
		return ParticipantId;
	}

	public void setParticipantId(int participantId) {
		ParticipantId = participantId;
	}

	public String getRemoteName() {
		return RemoteName;
	}

	public void setRemoteName(String remoteName) {
		RemoteName = remoteName;
	}

	public boolean isAudioMute() {
		return isAudioMute;
	}

	public void setAudioMute(boolean isAudioMute) {
		this.isAudioMute = isAudioMute;
	}

	public boolean isContent() {
		return isContent;
	}

	public void setContent(boolean isContent) {
		this.isContent = isContent;
	}

	public boolean isVideoMute() {
		return isVideoMute;
	}

	public void setVideoMute(boolean isVideoMute) {
		this.isVideoMute = isVideoMute;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public SESSIONTYPE getSessionType() {
		return sessionType;
	}

	public void setSessionType(SESSIONTYPE sessiontype) {
		this.sessionType = sessiontype;
	}

	public NET_STATUS getNet_status() {
		return net_status;
	}

	public void setNet_status(NET_STATUS net_status) {
		this.net_status = net_status;
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

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public boolean isComment() {
		return isComment;
	}

	public void setComment(boolean comment) {
		isComment = comment;
	}

	public CtrlStatus getCtrlStatus() {
		return ctrlStatus;
	}

	public void setCtrlStatus(CtrlStatus ctrlStatus) {
		this.ctrlStatus = ctrlStatus;
	}

	public CTRL_STATUS getCtrl_status() {
		return ctrl_status;
	}

	public void setCtrl_status(CTRL_STATUS ctrl_status) {
		this.ctrl_status = ctrl_status;
	}

	public StatusCtrl getStatusCtrl() {
		return statusCtrl;
	}

	public void setStatusCtrl(StatusCtrl statusCtrl) {
		this.statusCtrl = statusCtrl;
	}

	public String toString() {
		return statusCtrl == null? "":""+statusCtrl.isCameraMute() + statusCtrl.isMicMute();
	}
}
