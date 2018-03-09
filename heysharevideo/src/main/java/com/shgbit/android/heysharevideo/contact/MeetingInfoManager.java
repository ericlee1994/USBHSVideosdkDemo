package com.shgbit.android.heysharevideo.contact;

import com.ainemo.sdk.otf.VideoInfo;
import com.google.gson.Gson;
import com.shgbit.android.heysharevideo.bean.Command;
import com.shgbit.android.heysharevideo.bean.Command.Cmd;
import com.shgbit.android.heysharevideo.bean.CtrlStatus;
import com.shgbit.android.heysharevideo.bean.CurrentMeetingInfo;
import com.shgbit.android.heysharevideo.bean.DISPLAY_MODE;
import com.shgbit.android.heysharevideo.bean.DeviceState;
import com.shgbit.android.heysharevideo.bean.DisplayType;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.SESSIONTYPE;
import com.shgbit.android.heysharevideo.bean.STATUS;
import com.shgbit.android.heysharevideo.bean.VI;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.interactmanager.StructureDataCollector;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.shgbit.android.heysharevideo.util.VCUtils;
import com.wa.util.WAJSONTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingInfoManager {

	private static MeetingInfoManager mInstance;

	public static MeetingInfoManager getInstance() {

		if (mInstance == null) {
			mInstance = new MeetingInfoManager();
		}
		return mInstance;
	}

	public static void destory() {
		if (mInstance != null) {
			mInstance.Finalize();
			mInstance = null;
		}
	}

	private boolean mMeetingInfoThreadLoop = false;
	private MeetingInfoThread mThread;

	private List<MemberInfo> mMember;

	private ArrayList<MemberInfo> mScreenMember;
	private ArrayList<MemberInfo> mOtherMember;
	private ArrayList<MemberInfo> mUnjoinedMember;

	private List<MemberInfo> mNemoData;
	private List<MemberInfo> mServerData;

	private List<Command> mCommand;

	private boolean mNemoUpdate;

	private OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(4, TimeUnit.SECONDS).build();

	private int SCREEN_NUMBER = 6;

	private int duration = 0;

	private int memberSize = 0;
	private int mJoinSize = 0;

	private List<MemberInfo> mTotalMember;
	private boolean isModeVoice;
	private boolean isExChange;
	private boolean isPopDown;
	private boolean isPopUp;
	private boolean isPopUpDown;

	private boolean isMemberContent;
	private boolean isPicShare;
	private boolean isNotFirst;

	private DISPLAY_MODE DisplayMode;

//	private RequestCtrl requestCtrl;

	private String TAG = "MeetingInfoManager";

	private MeetingInfoManager() {

		GBLog.i(TAG, "MeetingInfoManager start");

		mNemoUpdate = false;
		isModeVoice = false;
		isExChange = false;
		isPopDown = false;
		isPopUp = false;
		isPopUpDown = false;
		isPicShare = false;
		isMemberContent = false;
		isNotFirst = false;

		mMember = new ArrayList<>();

		mScreenMember = new ArrayList<>();
		mOtherMember = new ArrayList<>();
		mUnjoinedMember = new ArrayList<>();

		mNemoData = new ArrayList<>();
		mServerData = new ArrayList<>();

		mCommand = new ArrayList<>();

		mTotalMember = new ArrayList<>();

//		requestCtrl = new RequestCtrl(VCApplication.getContext());
//		requestCtrl.setiSyncCallBack(iSyncCallBack);
//		requestCtrl.sync();

//		MQTTClient.getInstance().setIMqttCtrlDeviceCallback(iMqttCtrlDeviceCallback);

		mMeetingInfoThreadLoop = true;
		mThread = new MeetingInfoThread();
		mThread.start();
	}

	public void init() {

		mNemoUpdate = false;
		isModeVoice = false;
		isPicShare = false;
		isExChange = false;
		isPopDown = false;
		isPopUp = false;
		isPopUpDown = false;
		isMemberContent = false;
		isNotFirst = false;

		mMember.clear();
		mScreenMember.clear();
		mOtherMember.clear();
		mUnjoinedMember.clear();
		mNemoData.clear();
		mServerData.clear();
		mCommand.clear();
		mTotalMember.clear();

	}

//	private IMqttCtrlDeviceCallback iMqttCtrlDeviceCallback = new IMqttCtrlDeviceCallback() {
//		@Override
//		public void onCtrlDevice(String msg) {
//			GBLog.e(TAG,"msg success=   "+msg);
//			try {
//				CtrlStatus update = WAJSONTool.parseObject(msg, CtrlStatus.class);
//				mergeCtrl(update);
//				CallBack();
//			} catch (Throwable e) {
//				GBLog.e(TAG, "onCtrlDevice Throwable:" + VCUtils.CaughtException(e));
//			}
//		}
//	};

//	private ISyncCallBack iSyncCallBack = new ISyncCallBack() {
//		@Override
//		public void onSuccess(String result) {
//			GBLog.e(TAG,"result success=" + result);
//			try {
//				SyncResult syncResult = WAJSONTool.parseObject(result, SyncResult.class);
//				if (syncResult.getResult().equals("200")){
//
//					ArrayList<CtrlStatus> users = syncResult.getUsers();
//					if (users != null && users.size() > 0 ){
//						for (int i = 0; i < users.size(); i++) {
//							mergeCtrl(users.get(i));
//						}
//						CallBack();
//					}
//
//				}
//			} catch (Throwable e) {
//				GBLog.e(TAG, "ISyncCallBack Throwable:" + VCUtils.CaughtException(e));
//			}
//		}
//
//		@Override
//		public void onFailure(String result) {
//			GBLog.e(TAG,"result failure=   "+result);
//
//		}
//	};

	private void mergeCtrl(CtrlStatus ctrlStatus){
		String[] displayName = ctrlStatus.getUserID().split("[?]t_");
		String username = displayName[0];
		String sessiontype = displayName[1];
		boolean isContinue = false;
		for (int j = 0; j < mScreenMember.size(); j++) {
			if (mScreenMember.get(j).getId().equals(username) && mScreenMember.get(j).getSessionType() == changeToSessionType(sessiontype)){
				mScreenMember.get(j).setCtrlStatus(ctrlStatus);

				for (int i = 0; i < ctrlStatus.getStatusList().size(); i++) {
					if (ctrlStatus.getStatusList().get(i).getObject().equals("Microphone") && ctrlStatus.getStatusList().get(i).getStatus().equals("Mute")){
						mScreenMember.get(j).getStatusCtrl().setMicMute(true);
					}
					if (ctrlStatus.getStatusList().get(i).getObject().equals("Microphone") && ctrlStatus.getStatusList().get(i).getStatus().equals("Unmute")){
						mScreenMember.get(j).getStatusCtrl().setMicMute(false);
					}
					if (ctrlStatus.getStatusList().get(i).getObject().equals("Speaker") && ctrlStatus.getStatusList().get(i).getStatus().equals("SetVol")){
						mScreenMember.get(j).getStatusCtrl().setSpeakerSetVol(true);
					}
					if (ctrlStatus.getStatusList().get(i).getObject().equals("Camera") && ctrlStatus.getStatusList().get(i).getStatus().equals("Mute")){
						mScreenMember.get(j).getStatusCtrl().setCameraMute(true);
					}
					if (ctrlStatus.getStatusList().get(i).getObject().equals("Camera") && ctrlStatus.getStatusList().get(i).getStatus().equals("Unmute")){
						mScreenMember.get(j).getStatusCtrl().setCameraMute(false);
					}
				}

				isContinue = true;
				break;
			}
		}
		if (!isContinue){
			for (int j = 0; j < mOtherMember.size(); j++) {
				if (mOtherMember.get(j).getId().equals(username) && mOtherMember.get(j).getSessionType() == changeToSessionType(sessiontype)){
					mOtherMember.get(j).setCtrlStatus(ctrlStatus);

					for (int i = 0; i < ctrlStatus.getStatusList().size(); i++) {
						if (ctrlStatus.getStatusList().get(i).getObject().equals("Microphone") && ctrlStatus.getStatusList().get(i).getStatus().equals("Mute")){
							mOtherMember.get(j).getStatusCtrl().setMicMute(true);
						}
						if (ctrlStatus.getStatusList().get(i).getObject().equals("Microphone") && ctrlStatus.getStatusList().get(i).getStatus().equals("unMute")){
							mOtherMember.get(j).getStatusCtrl().setMicMute(false);
						}
						if (ctrlStatus.getStatusList().get(i).getObject().equals("Speaker") && ctrlStatus.getStatusList().get(i).getStatus().equals("SetVol")){
							mOtherMember.get(j).getStatusCtrl().setSpeakerSetVol(true);
						}
						if (ctrlStatus.getStatusList().get(i).getObject().equals("Camera") && ctrlStatus.getStatusList().get(i).getStatus().equals("Mute")){
							mOtherMember.get(j).getStatusCtrl().setCameraMute(true);
							GBLog.e(TAG,"CameraMute");
						}
						if (ctrlStatus.getStatusList().get(i).getObject().equals("Camera") && ctrlStatus.getStatusList().get(i).getStatus().equals("unMute")){
							mOtherMember.get(j).getStatusCtrl().setCameraMute(false);
							GBLog.e(TAG,"CameraUnMute");
						}
					}

					break;
				}
			}
		}
	}

	private void Finalize() {

		try {

			mMeetingInfoThreadLoop = false;
			if (mThread != null) {
				mThread.join(100);
				mThread.interrupt();
				mThread = null;
			}

			mMember.clear();
			mMember = null;

			mScreenMember.clear();
			mScreenMember = null;
			mOtherMember.clear();
			mOtherMember = null;
			mUnjoinedMember.clear();
			mUnjoinedMember = null;

			mNemoData.clear();
			mNemoData = null;
			mServerData.clear();
			mServerData = null;
			mTotalMember.clear();
			mTotalMember = null;

			mCommand.clear();
			mCommand = null;

		} catch (Throwable e) {
			GBLog.e(TAG, "Finalize Throwable:" + VCUtils.CaughtException(e));
		}

	}

	private class MeetingInfoThread extends Thread {

		@Override
		public void run() {

			while (mMeetingInfoThreadLoop) {

				try {

					if (OperateCommand()) {
						CallBack();
					}

					mNemoUpdate = false;

					GetCurrentMeeting();
					if (mOnMeetingInfoUpdateListener != null) {
						mOnMeetingInfoUpdateListener.onMemberSizeChanged(mJoinSize, memberSize);
					}
					mMember = MergeServerNemo(mServerData, mNemoData);
					CheckUnJoinedList(mMember);

					if (CheckinList(mMember)) {
						CallBack();
					}

				} catch (Throwable e) {
					GBLog.e(TAG, "MeetingInfoThread operate exception:" + VCUtils.CaughtException(e));
				}

				try {

					for (int i = 0; i < 100; i++) {

						if (mNemoUpdate) {
							GBLog.i(TAG, "Found nemo update");
							break;
						}

						if (mCommand.size() > 0) {
							GBLog.i(TAG, "Found new command");
							break;
						}

						if (!mMeetingInfoThreadLoop) {
							return;
						}

						Thread.sleep(50);
					}
				} catch (Throwable e) {
					GBLog.e(TAG, "MeetingInfoThread operate exception:" + e.getMessage());
				}
			}
		}
	}

	private boolean GetCurrentMeeting() {

		CurrentMeetingInfo cmi = null;
		try {

//			String userName = Common.USERNAME;

//			String url = Common.SERVICEIP + "/currentMeeting?userName=" + userName + "&sessionId=" + sessionId + "&sessionType=" + Common.SessionType;

//			String result = httpGet(url);
			String result = ServerInteractManager.getInstance().getSyncCurrentMeeting();
//			GBLog.i(TAG,"result="+result);

			cmi = WAJSONTool.parseObject(result, CurrentMeetingInfo.class);

			if (cmi == null || cmi.getMeeting() == null || cmi.getMeeting().getUsers() == null) {
				return false;
			}

			User[] users = cmi.getMeeting().getUsers();
			duration = cmi.getMeeting().getDuration();
			memberSize = users.length;
			mServerData.clear();
			mJoinSize = 0;
			GBLog.i(TAG, "---GetCurrentMeeting---,memberSize=" + memberSize);

			for (int i = 0; i < users.length; i++) {
				boolean isJoin = false;
				User item = users[i];
				if (item.getStatus() == null){
					if (isMemberInList(mUnjoinedMember,item.getUserName()) == -1){
						MemberInfo noStatus = new MemberInfo();
						noStatus.setId(item.getUserName());
						noStatus.setUserName(item.getUserName());
						noStatus.setDisplayName(item.getDisplayName());
						noStatus.setStatus(changeToStatus("waiting"));
						noStatus.setNet_status(MemberInfo.NET_STATUS.NULL);
						mUnjoinedMember.add(noStatus);
					}
				} else {
					// joined
					if (item.getSessionType().getContentOnlyState() != null){
						if (item.getSessionType().getContentOnlyState().getStatus().equals("joined")){
							isJoin = true;
							MemberInfo m = new MemberInfo();
//							m.setId(item.getUserName() + "?t_contentonly_content");
							m.setId(item.getUserName());
							m.setUserName(item.getUserName());
//							m.setDisplayName(item.getDisplayName() + "的共享桌面");
							m.setDisplayName(item.getDisplayName());
							m.setStatus(changeToStatus("joined"));
							m.setSessionType(SESSIONTYPE.CONTENTONLY);
							m.setNet_status(MemberInfo.NET_STATUS.Lost);
							if (item.getUserName().equals(Common.USERNAME)) {
								m.setLocal(true);
							}

							mServerData.add(m);
						}
					}
					if (item.getSessionType().getMobileState() != null){
						if (item.getSessionType().getMobileState().getStatus().equals("joined")){
							isJoin = true;
							MemberInfo m = new MemberInfo();
							m.setId(item.getUserName());
							m.setUserName(item.getUserName());
							m.setDisplayName(item.getDisplayName());
							m.setStatus(changeToStatus("joined"));
							m.setSessionType(SESSIONTYPE.MOBILE);
							m.setNet_status(MemberInfo.NET_STATUS.Lost);
							if (item.getUserName().equals(Common.USERNAME)) {
								m.setLocal(true);
							}

							mServerData.add(m);
						}
					}
					if (item.getSessionType().getPCState() != null){
						if (item.getSessionType().getPCState().getStatus().equals("joined")){
							isJoin = true;
							MemberInfo m = new MemberInfo();
							m.setId(item.getUserName());
							m.setUserName(item.getUserName());
							m.setDisplayName(item.getDisplayName());
							m.setStatus(changeToStatus("joined"));
							m.setSessionType(SESSIONTYPE.PC);
							m.setNet_status(MemberInfo.NET_STATUS.Lost);
							if (item.getUserName().equals(Common.USERNAME)) {
								m.setLocal(true);
							}

							mServerData.add(m);
						}
					}
					if (isJoin){
						// joined
						mJoinSize++;
					} else {
						// not join .
						if (isMemberInList(mUnjoinedMember,item.getUserName()) == -1){
							MemberInfo unJoined = new MemberInfo();
							unJoined.setId(item.getUserName());
							unJoined.setUserName(item.getUserName());
							unJoined.setDisplayName(item.getDisplayName());
							unJoined.setStatus(changeToStatus(item.getStatus()));
							unJoined.setNet_status(MemberInfo.NET_STATUS.NULL);
							mUnjoinedMember.add(unJoined);
						}
					}
				}
			}
			GBLog.i(TAG, "---GetCurrentMeeting---,mServerData.size=" + mServerData.size());

			//mServerData 把自己放最后一个(自己在第一个的情况下)
			if (mServerData.size() > 1){
				if (mServerData.get(0).getId().equals(Common.USERNAME) && mServerData.get(0).getSessionType() == SESSIONTYPE.MOBILE){
					mServerData.add(mServerData.get(0));
					mServerData.remove(0);
				}
			}

		} catch (Throwable e) {
			GBLog.e(TAG, "GetCurrentMeeting Throwable: " + VCUtils.CaughtException(e));
		}

		return true;
	}

	public void NemoChange(List<VideoInfo> videoInfos, String localSId) {

		try {
			if (videoInfos != null) {
				GBLog.i(TAG, "---NemoChange---,videoinfo.size=" + videoInfos.size());
				for (int i = 0; i < videoInfos.size(); i++) {
					VideoInfo item = videoInfos.get(i);
                    GBLog.i(TAG, "---NemoChange---,i=" + i + ",RemoteName:" + item.getRemoteName()  + ",isContent=" + item.isContent() + ",DataSource=" + item.getDataSourceID()  + ",isVideoMute=" + item.isVideoMute());
				}
			}

			mNemoData.clear();

			if (videoInfos != null) {
				for (VideoInfo videoInfo : videoInfos) {
					String nemoDisplayname = videoInfo.getRemoteName();
					String[] displayName = null;
					String remoteName = null;
					String sessionType = null;

					if (nemoDisplayname.contains("?t_")) {
						displayName = nemoDisplayname.split("[?]t_");
						remoteName = displayName[0];
						sessionType = displayName[1];
					} else {
						remoteName = videoInfo.getRemoteName();
						sessionType = "unknow";
					}

					MemberInfo m = new MemberInfo();
					m.setLocal(false);
					m.setParticipantId(videoInfo.getParticipantId());
					m.setDataSourceID(videoInfo.getDataSourceID());
					m.setRemoteName(remoteName);
					m.setContent(videoInfo.isContent());
					m.setAudioMute(videoInfo.isAudioMute());
					//关闭视频
					if (videoInfo.isVideoMute()){
						m.setNet_status(MemberInfo.NET_STATUS.VideoMute);
					}
					m.setVideoMute(videoInfo.isVideoMute());
					m.setSessionType(changeToSessionType(sessionType));

					if (videoInfo.isContent()) {
						if (sessionType.equals("pc")){
							m.setSessionType(changeToSessionType("pc_content"));
						}
//						m.setId(videoInfo.getRemoteName() + "_content");
						m.setId(remoteName);
						String name = StructureDataCollector.getInstance().transformName(m.getRemoteName());
						if (name != null && !name.isEmpty()) {
//							m.setDisplayName(name + "的共享桌面");
							m.setDisplayName(name);
						} else {
							m.setDisplayName(m.getRemoteName()/* + "的共享桌面"*/);
						}
					} else {
						if (m.getSessionType() == SESSIONTYPE.CONTENTONLY){
							m.setNet_status(MemberInfo.NET_STATUS.ContentOnlyUnsend);
//							m.setId(videoInfo.getRemoteName() + "_content");
							m.setId(remoteName);
							String name = StructureDataCollector.getInstance().transformName(m.getRemoteName());
							if (name != null && !name.isEmpty()) {
								m.setDisplayName(name/* + "的共享桌面"*/);
							} else {
								m.setDisplayName(m.getRemoteName()/* + "的共享桌面"*/);
							}
						} else {
							m.setId(remoteName);
						}
					}

					mNemoData.add(m);

					//sb join,mUnjoinedMember remove
					int index = isMemberInList(mUnjoinedMember,remoteName);
					if (index != -1){
						mUnjoinedMember.remove(index);
					}
//					GBLog.i(TAG, "mNemoData.add("+ m.getRemoteName() +")");
				}
			}

			MemberInfo m = new MemberInfo();
			m.setLocal(true);
			m.setParticipantId(-1);
			m.setDataSourceID(localSId);
			m.setId(Common.USERNAME);
			m.setRemoteName(Common.USERNAME);
			m.setContent(false);
			m.setAudioMute(false);
			m.setVideoMute(false);
			m.setSessionType(changeToSessionType("mobile"));

			mNemoData.add(m);
			ArrayList<String> ids = new ArrayList<>();
			for (MemberInfo memberInfo : mNemoData){
				if (memberInfo.getSessionType() == SESSIONTYPE.CONTENTONLY && memberInfo.getDataSourceID() != null && !memberInfo.getDataSourceID().isEmpty()){
					ids.add(memberInfo.getId());
				}
			}
			if (ids != null && ids.size() > 0){
				for (String id : ids){
					for (int i = 0; i < mNemoData.size(); i++){
						if (id.equalsIgnoreCase(mNemoData.get(i).getId())){
							if (mNemoData.get(i).getDataSourceID() == null || mNemoData.get(i).getDataSourceID().isEmpty()){
								mNemoData.remove(i);
								i--;
							}
						}

					}
				}
			}
			GBLog.i(TAG, "---NemoChange---,mNemoData.size=" + mNemoData.size());
			mNemoUpdate = true;

		} catch (Throwable e) {
			GBLog.e(TAG, "NemoChange Throwable:" + VCUtils.CaughtException(e));
		}
	}

	private List<MemberInfo> MergeServerNemo(List<MemberInfo> server, List<MemberInfo> nemo) {

		try {
			List<MemberInfo> result = new ArrayList<>();

			result.addAll(server);
			GBLog.i(TAG, "---MergeServerNemo---,result.size()=" + result.size());
			for (int i = 0; i < nemo.size(); i++) {

				MemberInfo n = nemo.get(i);

				if (n.getNet_status() != MemberInfo.NET_STATUS.VideoMute
						&& n.getNet_status() != MemberInfo.NET_STATUS.ContentOnlyUnsend
						&& n.getNet_status() != MemberInfo.NET_STATUS.VoiceMode){
					if (n.getDataSourceID() != null && !n.getDataSourceID().isEmpty()) {
						n.setNet_status(MemberInfo.NET_STATUS.Normal);
					} else {
						n.setNet_status(MemberInfo.NET_STATUS.Loading);
					}
				}

				int index = isMemberInList(result, n);
				if (index == -1) {
					if (n.getDisplayName() == null || n.getDisplayName().isEmpty()) {
						String name = StructureDataCollector.getInstance().transformName(n.getRemoteName());
						n.setDisplayName(name);
					}
					n.setStatus(changeToStatus("joined"));
					result.add(n);
					GBLog.i(TAG, "---MergeServerNemo---add nemo=" + n.getDisplayName() + ",type=" + n.getSessionType());
				} else {
					MemberInfo s = result.get(index);
					s.setLocal(n.isLocal());
					s.setAudioMute(n.isAudioMute());
					s.setContent(n.isContent());
					s.setDataSourceID(n.getDataSourceID());
					s.setParticipantId(n.getParticipantId());
					s.setRemoteName(n.getRemoteName());
					s.setVideoMute(n.isVideoMute());
					s.setNet_status(n.getNet_status());
					GBLog.i(TAG, "---MergeServerNemo---exist nemo=" + s.getDisplayName() + ",type=" + s.getSessionType());
				}
			}
			GBLog.i(TAG, "---MergeServerNemo---,result.size="+result.size());
			//语音模式
			if (isModeVoice){
				for (int i = 0;i < result.size(); i++){
					if (result.get(i).getStatus().equals(changeToStatus("joined"))){
						result.get(i).setNet_status(MemberInfo.NET_STATUS.VoiceMode);
					}
				}
			}

			return result;

		} catch (Throwable e) {
			GBLog.e(TAG, "MergeServerNemo Throwable:" + VCUtils.CaughtException(e));
		}

		return null;
	}

	private void CheckUnJoinedList(List<MemberInfo> list){
		if (list == null) {
			return;
		}
		for (int i = mUnjoinedMember.size() - 1;  i >= 0; i--){
			int index = isMemberInList(list, mUnjoinedMember.get(i).getId());
			if (index != -1){
				mUnjoinedMember.remove(i);
			}
		}
	}

	private boolean CheckinList(List<MemberInfo> list) {

		boolean needUpdate = false;

		if (list == null) {
			return false;
		}
		GBLog.i(TAG, "---CheckinList---");
		try {
			for (int i = 0; i < mScreenMember.size(); i++) {
				int index = isMemberInList(mMember, mScreenMember.get(i));
//				GBLog.e(TAG, "CheckinList index=" + index + ",screen id=" + mScreenMember.get(i).getId() + ",sessiontype=" + mScreenMember.get(i).getSessionType()  + ",screen status=" + mScreenMember.get(i).getStatus());
				if (index == -1){
					// Screen Member exit
					if (mScreenMember.get(i).getStatus().equals(STATUS.JOINED) && !isPicShare) {
						GBLog.i(TAG,"exit member is " + mScreenMember.get(i).getRemoteName());
						if (mScreenMember.get(i).isComment()){
							mOnMeetingInfoUpdateListener.onMemberCommentExit(mScreenMember.get(i));
						}
						mScreenMember.remove(i);
						i--;
						needUpdate = true;
					}
				} else {
					// Update Screen Member
					if (UpdateInfo(mScreenMember.get(i), mMember.get(index))) {
						needUpdate = true;
					}
				}
//				if (mScreenMember.get(i).getStatus().equals(STATUS.JOINED)) {
//					if (index == -1) {
//						mScreenMember.remove(i);
//						i--;
//						needUpdate = true;
////						GBLog.i(TAG, "Screen Member exit," + mScreenMember.get(i).getUserName());
//					}
//				}
			}

			for (int i = 0; i < mOtherMember.size(); i++) {
				int index = isMemberInList(mMember, mOtherMember.get(i));
				if (index == -1){
					// Other Member exit
					if (mOtherMember.get(i).getStatus().equals(STATUS.JOINED)) {
						mOtherMember.remove(i);
						i--;
						needUpdate = true;
					}
				} else {
					// Update Other Member
					if (UpdateInfo(mOtherMember.get(i), mMember.get(index))) {
						needUpdate = true;
					}
				}

//				if (mOtherMember.get(i).getStatus().equals(STATUS.JOINED)) {
//					if (index == -1) {
//						mOtherMember.remove(i);
//						i--;
//						needUpdate = true;
////						GBLog.i(TAG, "Other Member exit," + mScreenMember.get(i).getUserName());
//					}
//				}
			}

			for (int i = 0; i < list.size(); i++) {

				MemberInfo item = list.get(i);

				int index_screen = isMemberInList(mScreenMember, item);
				int index_other = isMemberInList(mOtherMember, item);
				int screen_len = mScreenMember.size();

				// New Member
				if (index_screen == -1 && index_other == -1) {
					GBLog.i(TAG, "---CheckinList---,New Member,i="+i + ",id=" + item.getId());
					if (item.isContent()) {
						mScreenMember.add(0, item);
						while (mScreenMember.size() > SCREEN_NUMBER) {
							MemberInfo info = mScreenMember.get(mScreenMember.size() - 1);
							mScreenMember.remove(mScreenMember.size() - 1);
							mOtherMember.add(0, info);
						}
						needUpdate = true;
					} else {
						if (screen_len < SCREEN_NUMBER) {
							mScreenMember.add(item);
//							GBLog.i(TAG, "mScreenMember.add(" + item.getUserName() + ")");
							needUpdate = true;
						} else {
							mOtherMember.add(item);
//							GBLog.i(TAG, "mOtherMember.add(" + item.getUserName() + ")");
							needUpdate = true;
						}
					}
				}
//				// Update Screen Member
//				else if (index_screen >= 0 && index_other == -1) {
//					if (UpdateInfo(mScreenMember.get(index_screen), item)) {
////						GBLog.i(TAG, "Update Screen Member" + item.getUserName());
//						needUpdate = true;
//					}
//				}
//				// Update Other Member
//				else if (index_screen == -1 && index_other >= 0) {
//					if (UpdateInfo(mOtherMember.get(index_other), item)) {
////						GBLog.i(TAG, "Update Other Member" + item.getUserName());
//						needUpdate = true;
//					}
//
//				}
			}
		} catch (Throwable e) {
			GBLog.e(TAG, "CheckinList exception:" + VCUtils.CaughtException(e));
		}

		return needUpdate;
	}

	private boolean OperateCommand() {

		boolean needUpdate = false;

		while (mCommand.size() > 0) {

			try {
				Command cmd = mCommand.get(0);
				Object[] args = cmd.getArgs();
				GBLog.i(TAG, "---OperateCommand---,cmd=" +cmd.getName());
				if (cmd.getName().equals(Cmd.Exchange)) {

					int index1, index2;
					if (args[0] == null || ((String) args[0]).isEmpty()) {
						index1 = 0;
					} else {
						index1 = isMemberInList(mScreenMember, (String) args[0], (SESSIONTYPE) args[2]);
					}
					index2 = isMemberInList(mScreenMember, (String) args[1], (SESSIONTYPE) args[3]);

					if (index1 != -1 && index2 != -1) {
						Collections.swap(mScreenMember, index1, index2);
						isExChange = true;
						needUpdate = true;
					}

				} else if (cmd.getName().equals(Cmd.PopDown)) {

					int index = isMemberInList(mScreenMember, (String) args[0], (SESSIONTYPE) args[1]);

					if (index != -1) {
						MemberInfo m = mScreenMember.get(index);
						mOtherMember.add(m);
						mScreenMember.remove(index);
						isPopDown = true;
						needUpdate = true;
					}

				} else if (cmd.getName().equals(Cmd.PopUp)) {

					int index = isMemberInList(mOtherMember, (String) args[0], (SESSIONTYPE) args[1]);

					if (index != -1 && mScreenMember.size() < SCREEN_NUMBER) {
						MemberInfo m = mOtherMember.get(index);
						mScreenMember.add(m);
						mOtherMember.remove(index);
						isPopUp = true;
						needUpdate = true;
					}

				} else if (cmd.getName().equals(Cmd.AudioMute)) {

					for (int i = 0; i < mScreenMember.size(); i++) {
						if (mScreenMember.get(i).isLocal()) {
							mScreenMember.get(i).setAudioMute((Boolean) args[1]);
							needUpdate = true;
							break;
						}
					}

					for (int i = 0; i < mOtherMember.size(); i++) {
						if (mOtherMember.get(i).isLocal()) {
							mOtherMember.get(i).setAudioMute((Boolean) args[1]);
							needUpdate = true;
							break;
						}
					}

				} else if(cmd.getName().equals(Cmd.ModeVoice)){
					isModeVoice = (Boolean) args[0];
					for (int i = 0; i < mScreenMember.size(); i++) {
						if (mScreenMember.get(i).getStatus().equals(changeToStatus("joined")) && isModeVoice && mScreenMember.get(i).getmDisplayType().equals(DisplayType.VIDEO)){
							mScreenMember.get(i).setNet_status(MemberInfo.NET_STATUS.VoiceMode);
							needUpdate = true;
						}
					}
					for (int i = 0; i < mOtherMember.size(); i++) {
						if (mOtherMember.get(i).getStatus().equals(changeToStatus("joined")) && isModeVoice && mScreenMember.get(i).getmDisplayType().equals(DisplayType.VIDEO)){
							mOtherMember.get(i).setNet_status(MemberInfo.NET_STATUS.VoiceMode);
							needUpdate = true;
						}
					}
				} else if (cmd.getName().equals(Cmd.PicShare)){
					isPicShare = (boolean) args[0];
					if (mScreenMember.size() > 0){
						for (int i = 0; i < mScreenMember.size(); i++) {
							if ((boolean)args[0] && mScreenMember.get(i).getmDisplayType().equals(DisplayType.PICTURE)){
								mScreenMember.get(i).setmUrls((ArrayList<String>) args[1]);
								break;
							} else if ((boolean)args[0]){
								MemberInfo m = new MemberInfo();
								m.setDisplayName("图片共享");
								m.setStatus(STATUS.JOINED);
								m.setSessionType(SESSIONTYPE.UNKNOW);
								m.setNet_status(MemberInfo.NET_STATUS.Normal);
								m.setmDisplayType(DisplayType.PICTURE);
								m.setmUrls((ArrayList<String>) args[1]);
								mScreenMember.add(0, m);
								while (mScreenMember.size() > SCREEN_NUMBER) {
									MemberInfo info = mScreenMember.get(mScreenMember.size() - 1);
									mScreenMember.remove(mScreenMember.size() - 1);
									mOtherMember.add(0, info);
								}
								break;
							} else if (!(boolean)args[0] && mScreenMember.get(i).getmDisplayType().equals(DisplayType.PICTURE)){
								mScreenMember.remove(i);
							}
						}
						needUpdate = true;
					} else {
						MemberInfo m = new MemberInfo();
						m.setDisplayName("图片共享");
						m.setStatus(STATUS.JOINED);
						m.setSessionType(SESSIONTYPE.UNKNOW);
						m.setNet_status(MemberInfo.NET_STATUS.Normal);
						m.setmDisplayType(DisplayType.PICTURE);
						m.setmUrls((ArrayList<String>) args[1]);
						mScreenMember.add(0, m);
						needUpdate = true;
					}

				} else if (cmd.getName().equals(Cmd.CommentStart)){
					String resId = (String) args[0];
					String[] resIdSplit1 = null;
					String[] resIdSplit2 = null;
					String name = null;
					resIdSplit1 = resId.split("@");
					name = resIdSplit1[1];
					resIdSplit2 = name.split("[?]t_");
					name = resIdSplit2[0];
					for (int i = 0; i < mScreenMember.size(); i++) {
						if (resId.startsWith("image") && mScreenMember.get(i).getmDisplayType().equals(DisplayType.PICTURE)){
							mScreenMember.get(i).setResId(resId);
							mScreenMember.get(i).setComment(true);
							needUpdate = true;
							break;
						} else if (resId.startsWith("content") && resId.endsWith("pc")){
							if (name.equals(mScreenMember.get(i).getUserName()) && mScreenMember.get(i).getSessionType().equals(SESSIONTYPE.PC_CONTENT)){
								mScreenMember.get(i).setResId(resId);
								mScreenMember.get(i).setComment(true);
								needUpdate = true;
								break;
							}
						} else if (resId.startsWith("content") && resId.endsWith("contentonly")){
							if (name.equals(mScreenMember.get(i).getUserName()) && mScreenMember.get(i).getSessionType().equals(SESSIONTYPE.CONTENTONLY)){
								mScreenMember.get(i).setResId(resId);
								mScreenMember.get(i).setComment(true);
								needUpdate = true;
								break;
							}

						} else if (resId.startsWith("camera") && resId.endsWith("pc")){
							if (name.equals(mScreenMember.get(i).getUserName()) && mScreenMember.get(i).getSessionType().equals(SESSIONTYPE.PC)){
								mScreenMember.get(i).setResId(resId);
								mScreenMember.get(i).setComment(true);
								needUpdate = true;
								break;
							}
						} else if (resId.startsWith("camera") && resId.endsWith("mobile")){
							if (name.equals(mScreenMember.get(i).getUserName()) && mScreenMember.get(i).getSessionType().equals(SESSIONTYPE.MOBILE)){
								mScreenMember.get(i).setResId(resId);
								mScreenMember.get(i).setComment(true);
								needUpdate = true;
								break;
							}
						}
					}
				} else if (cmd.getName().equals(Cmd.CommentEnd)) {
					for (int i = 0; i < mScreenMember.size(); i++) {
						mScreenMember.get(i).setResId("");
						mScreenMember.get(i).setComment(false);
					}
					needUpdate = true;
				} else if (cmd.getName().equals(Cmd.VideoMute)) {

					for (int i = 0; i < mScreenMember.size(); i++) {
						if (mScreenMember.get(i).isLocal()) {
							mScreenMember.get(i).setVideoMute((Boolean) args[1]);
							if ((Boolean) args[1]){
								mScreenMember.get(i).setNet_status(MemberInfo.NET_STATUS.VideoMute);
							} else if (!isModeVoice){
								mScreenMember.get(i).setNet_status(MemberInfo.NET_STATUS.Normal);
							} else if (isModeVoice){
								mScreenMember.get(i).setNet_status(MemberInfo.NET_STATUS.VoiceMode);
							}
							needUpdate = true;
							break;
						}
					}

					for (int i = 0; i < mOtherMember.size(); i++) {
						if (mOtherMember.get(i).isLocal()) {
							mOtherMember.get(i).setVideoMute((Boolean) args[1]);
							if ((Boolean) args[1]){
								mOtherMember.get(i).setNet_status(MemberInfo.NET_STATUS.VideoMute);
							} else  if (!isModeVoice){
								mOtherMember.get(i).setNet_status(MemberInfo.NET_STATUS.Normal);
							} else if (isModeVoice){
								mScreenMember.get(i).setNet_status(MemberInfo.NET_STATUS.VoiceMode);
							}
							needUpdate = true;
							break;
						}
					}

				} else if (cmd.getName().equals(Cmd.StateChange)) {

					int indexScreen = isMemberInList(mScreenMember, (String) args[0]);
					int indexOther = isMemberInList(mOtherMember, (String) args[0]);

					if (indexScreen == -1 && indexOther == -1) {
						boolean isInviting = args[1].equals(STATUS.INVITING);
						if (isInviting){
							// not join
							MemberInfo m = new MemberInfo();
							m.setId((String) args[0]);
							m.setUserName((String) args[0]);
							m.setRemoteName((String) args[0]);
							String name = StructureDataCollector.getInstance().transformName(m.getRemoteName());
							m.setDisplayName(name);
							m.setStatus((STATUS) args[1]);
							m.setNet_status(MemberInfo.NET_STATUS.NULL);

							if (mScreenMember.size() < SCREEN_NUMBER) {
								mScreenMember.add(m);
							} else {
								mOtherMember.add(m);
							}
							if (isMemberInList(mUnjoinedMember,m.getUserName()) == -1){
								mUnjoinedMember.add(m);
							}
							GBLog.i(TAG, "---OperateCommand---,mScreenMember.size=" + mScreenMember.size() +
									",mOtherMember.size=" + mOtherMember.size() + ",mUnjoinedMember.size=" + mUnjoinedMember.size());
							needUpdate = true;
						}

					} else if (indexScreen != -1 && indexOther == -1) {
						// inside 4 (not join)
						MemberInfo m = mScreenMember.get(indexScreen);
						if (args[1].equals(STATUS.WAITING)) {
							m.setStatus(changeToStatus("waiting"));
							mScreenMember.remove(indexScreen);
							needUpdate = true;
						} else if (args[1].equals(STATUS.TIMEOUT)) {
							m.setStatus(changeToStatus("invitetimeout"));
							needUpdate = true;
						} else if (args[1].equals(STATUS.BUSY)) {
							m.setStatus(changeToStatus("busy"));
							needUpdate = true;
						} else if (args[1].equals(STATUS.INVITING)) {
							m.setStatus(changeToStatus("inviting"));
							needUpdate = true;
						}
					} else if (indexScreen == -1 && indexOther != -1) {
						// inside other (not join)
						MemberInfo m = mOtherMember.get(indexOther);
						if (args[1].equals(STATUS.WAITING)) {
							m.setStatus(changeToStatus("waiting"));
							mOtherMember.remove(indexOther);
							needUpdate = true;
						} else if (args[1].equals(STATUS.TIMEOUT)) {
							m.setStatus(changeToStatus("invitetimeout"));
							needUpdate = true;
						} else if (args[1].equals(STATUS.BUSY)) {
							m.setStatus(changeToStatus("busy"));
							needUpdate = true;
						} else if (args[1].equals(STATUS.INVITING)) {
							m.setStatus(changeToStatus("inviting"));
							if (mScreenMember.size() < 6){
								mScreenMember.add(m);
								mOtherMember.remove(indexOther);
							}
							needUpdate = true;
						}
					}

					int unJoined = isMemberInList(mUnjoinedMember, (String) args[0]);
					if (unJoined >= 0){
						mUnjoinedMember.get(unJoined).setStatus((STATUS) args[1]);
						needUpdate = true;
					}

				} else if (cmd.getName().equals(Cmd.ModeChange)) {
					if (mScreenMember.size() != 0 || mOtherMember.size() != 0 || mUnjoinedMember.size() != 0){
						if (args[0] == DISPLAY_MODE.NOT_FULL_QUARTER){
							SCREEN_NUMBER = 4;
						} else {
							SCREEN_NUMBER = 6;
						}
//						SCREEN_NUMBER = (Integer) args[0];
						getModeList();
						needUpdate = true;
					}
				} else if (cmd.getName().equals(Cmd.CommentModeChange)){
					// one screen with comment
					for (int i = 0; i < mScreenMember.size(); i++) {
						if (mScreenMember.get(i).isComment()){
							mScreenMember.add(0,mScreenMember.remove(i));
							SCREEN_NUMBER = 1;
							getModeList();
							needUpdate = true;
							break;
						}
					}

				} else if(cmd.getName().equals(Cmd.PopUpDown)){

					if (isMemberInList(mOtherMember,(String)args[0], (SESSIONTYPE) args[1]) == -1){
						//popdown
						int indexDown = isMemberInList(mScreenMember, (String) args[0], (SESSIONTYPE) args[1]);

						if (indexDown != -1) {
							MemberInfo m = mScreenMember.get(indexDown);
							mOtherMember.add(m);
							mScreenMember.remove(indexDown);
							isPopUpDown = true;
							needUpdate = true;
						}
					}else {
						//popup
						int indexUp = isMemberInList(mOtherMember, (String) args[0], (SESSIONTYPE) args[1]);

						if (indexUp != -1 && mScreenMember.size() < SCREEN_NUMBER) {
							MemberInfo m = mOtherMember.get(indexUp);
							mScreenMember.add(m);
							mOtherMember.remove(indexUp);
							isPopUpDown = true;
							needUpdate = true;
						}
					}

				}
			} catch (Throwable e) {
				GBLog.e(TAG, "OperateCommand Exception=" + VCUtils.CaughtException(e));
			}

			mCommand.remove(0);
		}

		return needUpdate;
	}

	private void CallBack() {
		if (mOnMeetingInfoUpdateListener != null && mScreenMember != null && mOtherMember != null) {
			GBLog.i(TAG, "Callback ============== "+",duration = "+duration);

			if ((DisplayMode == DISPLAY_MODE.FULL_PIP_SIX) && !isExChange && !isPopDown && !isPopUp && !isPopUpDown) {
				isExChange = false;
				isPopDown = false;
				isPopUp = false;
				isPopUpDown = false;
				mScreenMember.addAll(mOtherMember);

				for (int i = 0; i < mScreenMember.size(); i++){
					MemberInfo item = mScreenMember.get(i);
					if (!item.isContent() && item.getNet_status() == MemberInfo.NET_STATUS.Normal) {
						mScreenMember.remove(i);
						mScreenMember.add(0, item);
						break;
					}
				}

				for (int i = 0; i < mScreenMember.size(); i++){
					MemberInfo item = mScreenMember.get(i);
					if (!item.isContent() && item.getNet_status() == MemberInfo.NET_STATUS.Normal
							&& !item.isLocal()) {
						mScreenMember.remove(i);
						mScreenMember.add(0, item);
						break;
					}
				}
				for (int i = 0; i < mScreenMember.size(); i++){
					MemberInfo item = mScreenMember.get(i);
					if (item.isContent()) {
						mScreenMember.remove(i);
						mScreenMember.add(0, item);
						break;
					}
				}

				mOtherMember.clear();
				while (mScreenMember.size() > 6) {
					MemberInfo item = mScreenMember.get(6);
					mScreenMember.remove(6);
					mOtherMember.add(item);
				}
			}

			//check isContent
			isMemberContent = false;

			for (int i = 0; i < mScreenMember.size(); i++) {
				MemberInfo item = mScreenMember.get(i);
				if (item.isContent()){
					isMemberContent = true;
				}
				GBLog.i(TAG, "mScreen = " + i + " ,Id=" + item.getId() + " ,Status=" + item.getStatus() + ",sessionType=" + item.getSessionType() + ",Displayname=" + item.getDisplayName() + ",NET_STATUS=" + item.getNet_status() + ",DataSourceId=" + item.getDataSourceID() + ",DisplayType=" + item.getmDisplayType() + ",resId=" + item.getResId());
				GBLog.e(TAG, "CallBack-CtrlStatus-Screen = " + new Gson().toJson(item.getStatusCtrl()));
			}
			for (int i = 0; i < mOtherMember.size(); i++) {
				MemberInfo item = mOtherMember.get(i);
				if (item.isContent()){
					isMemberContent = true;
				}
				GBLog.i(TAG, "mOther = " + i + " ,Id=" + item.getId() + " ,Status=" + item.getStatus() + ",sessionType=" + item.getSessionType() + ",NET_STATUS=" + item.getNet_status() + ",DataSourceId=" + item.getDataSourceID());
			}
			GBLog.i(TAG, "----------------- ");
			for (int i = 0; i < mUnjoinedMember.size(); i++) {
				MemberInfo item = mUnjoinedMember.get(i);
				GBLog.i(TAG, "mUnjoinedMember = " + i + " ,Id=" + item.getId() + " ,Status=" + item.getStatus() + ",sessionType=" + item.getSessionType() + ",NET_STATUS=" + item.getNet_status() + ",DataSourceId=" + item.getDataSourceID());
			}
			GBLog.i(TAG, "----------------- ");

			if (!isNotFirst){
//				requestCtrl.sync();
				isNotFirst = true;
			}

			mOnMeetingInfoUpdateListener.onMemberChanged(mScreenMember, mOtherMember, mUnjoinedMember, duration ,isMemberContent);
		}
	}

	public void StateChange(String[] users, STATUS state) {
		String userId;
		if (users != null) {
			GBLog.i(TAG, "***StateChange*** users=" +users.length);
			for (int i = 0; i < users.length; i++) {
				userId = users[i];
				Command cmd = new Command();
				cmd.setName(Cmd.StateChange);
				Object[] args = new Object[2];
				args[0] = userId;
				args[1] = state;
				cmd.setArgs(args);

				GBLog.i(TAG, "i="+i+",user="+userId+",state="+state);
				mCommand.add(cmd);
			}
		}
	}

	public void ModeChange(DISPLAY_MODE mode) {
		GBLog.i(TAG, "***modeChange*** mode=" + mode);
		Command cmd = new Command();
		cmd.setName(Cmd.ModeChange);
		Object[] args = new Object[1];
		args[0] = mode;
		cmd.setArgs(args);
		DisplayMode = mode;

		mCommand.add(cmd);
	}

	public void CommetModeChange() {
		GBLog.i(TAG, "***CommetModeChange***");
		Command cmd = new Command();
		cmd.setName(Cmd.CommentModeChange);

		mCommand.add(cmd);
	}

	public void PicShare(boolean isPicShare, ArrayList<String> urls) {
		GBLog.i(TAG, "***PicShare***");
		Command cmd = new Command();
		cmd.setName(Cmd.PicShare);
		Object[] args = new Object[2];
		args[0] = isPicShare;
		args[1] = urls;
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void CommentStart(String resId){
		GBLog.i(TAG, "***CommentStart***,resId=" + resId );
		Command cmd = new Command();
		cmd.setName(Cmd.CommentStart);
		Object[] args = new Object[1];
		args[0] = resId;
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void CommentEnd(){
		GBLog.i(TAG, "***CommentEnd***");
		Command cmd = new Command();
		cmd.setName(Cmd.CommentEnd);

		mCommand.add(cmd);
	}

	public void ScreenExchange(VI vi1, VI vi2) {
		GBLog.i(TAG, "***ScreenExchange*** id1=" + vi1.getId() +",id2=" + vi2.getId() + ",type1=" + vi1.getSessionType() + ",type2=" + vi2.getSessionType() + ",DisplayType1=" + vi1.getmDisplayType() + ",DisplayType2=" + vi2.getmDisplayType());
		Command cmd = new Command();
		cmd.setName(Cmd.Exchange);
		Object[] args = new Object[6];
		args[0] = vi1.getId();
		args[1] = vi2.getId();
		args[2] = vi1.getSessionType();
		args[3] = vi2.getSessionType();
		args[4] = vi1.getmDisplayType();
		args[5] = vi2.getmDisplayType();

		cmd.setArgs(args);

		mCommand.add(cmd);

	}

	public void PopDown(VI vi) {
		GBLog.i(TAG, "***PopDown*** id=" + vi.getId() + ",type=" + vi.getSessionType());
		Command cmd = new Command();
		cmd.setName(Cmd.PopDown);
		Object[] args = new Object[2];
		args[0] = vi.getId();
		args[1] = vi.getSessionType();
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void PopUp(VI vi) {
		GBLog.i(TAG, "***PopUp*** id=" + vi.getId() + ",type=" + vi.getSessionType());
		Command cmd = new Command();
		cmd.setName(Cmd.PopUp);
		Object[] args = new Object[2];
		args[0] = vi.getId();
		args[1] = vi.getSessionType();
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void PopUpDown(MemberInfo m){
		GBLog.i(TAG, "***popUpDown*** id=" + m.getId() + ",type=" + m.getSessionType());
		Command cmd = new Command();
		cmd.setName(Cmd.PopUpDown);

		String id = m.getId();
		Object[] args = new Object[2];
		args[0] = id;
		args[1] = m.getSessionType();
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void AudioMute(String id, boolean mute) {
		GBLog.i(TAG, "***AudioMute***,id=" +id);
		Command cmd = new Command();
		cmd.setName(Cmd.AudioMute);
		Object[] args = new Object[2];
		args[0] = id;
		args[1] = mute;
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void ModeVoice(boolean voice){
		GBLog.i(TAG, "***ModeAudio***");
		Command cmd = new Command();
		cmd.setName(Cmd.ModeVoice);
		Object[] args = new Object[1];
		args[0] = voice;
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	public void VideoMute(String id, boolean mute) {
		GBLog.i(TAG, "***VideoMute***,id=" + id);
		Command cmd = new Command();
		cmd.setName(Cmd.VideoMute);
		Object[] args = new Object[2];
		args[0] = id;
		args[1] = mute;
		cmd.setArgs(args);

		mCommand.add(cmd);
	}

	private void getModeList() {
		mTotalMember.clear();

		mTotalMember.addAll(mScreenMember);
		mTotalMember.addAll(mOtherMember);

		int mTotalSize = mTotalMember.size();
		mScreenMember.clear();
		mOtherMember.clear();

		if (mTotalSize < SCREEN_NUMBER) {
			for (int i = 0; i < mTotalSize; i++) {
				mScreenMember.add(mTotalMember.get(i));
			}
		} else {
			for (int i = 0; i < SCREEN_NUMBER; i++) {
				mScreenMember.add(mTotalMember.get(i));
			}
			for (int j = SCREEN_NUMBER; j < mTotalSize; j++) {
				mOtherMember.add(mTotalMember.get(j));
			}
		}
	}

	private STATUS changeToStatus(String status) {
		if (status.equals("busy")) {
			return STATUS.BUSY;
		} else if (status.equals("inviting")) {
			return STATUS.INVITING;
		} else if (status.equals("invitetimeout")) {
			return STATUS.TIMEOUT;
		} else if (status.equals("waiting")) {
			return STATUS.WAITING;
		} else if (status.equals("joined")) {
			return STATUS.JOINED;
		}
		return null;
	}

	private SESSIONTYPE changeToSessionType(String sessionType) {
		if (sessionType.equals("all")) {
			return SESSIONTYPE.ALL;
		} else if (sessionType.equals("contentonly")) {
			return SESSIONTYPE.CONTENTONLY;
		} else if (sessionType.equals("mobile")) {
			return SESSIONTYPE.MOBILE;
		} else if (sessionType.equals("pc")) {
			return SESSIONTYPE.PC;
		} else if (sessionType.equals("unknow")) {
			return SESSIONTYPE.UNKNOW;
		} else if (sessionType.equals("pc_content")) {
			return SESSIONTYPE.PC_CONTENT;
		}
		return null;
	}

	private String httpGet(String url) {
		String result = "";
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = mOkHttpClient.newCall(request).execute();
			result = response.body().string();
		} catch (Throwable e) {
			GBLog.e(TAG, "httpGet Throwable:" + VCUtils.CaughtException(e));
		}
		return result;
	}

	private int isMemberInList(List<MemberInfo> list, MemberInfo m) {
		int index = -1;
		if (m.getSessionType() == SESSIONTYPE.ALL){
			index = isMemberInList(list, m.getId());
		} else {
			index = isMemberInList(list, m.getId(), m.getSessionType());
		}
		return index;
	}

	private int isMemberInList(List<MemberInfo> list, String id, SESSIONTYPE sessionType) {

		for (int i = 0; i < list.size(); i++) {
			MemberInfo item = list.get(i);

			if (item.getId().equals(id)) {
				if (item.getSessionType() == sessionType){
					return i;
				}
			}
		}
		return -1;
	}

	private int isMemberInList(List<MemberInfo> list, String id) {

		for (int i = 0; i < list.size(); i++) {
			MemberInfo item = list.get(i);

			if (item.getId().equals(id)) {
				return i;
			}
		}
		return -1;
	}

	private boolean UpdateInfo(MemberInfo info, MemberInfo newInfo) {

		boolean needUpdate = false;

		try {

			if (info.isBlank() != newInfo.isBlank()) {
				info.setBlank(newInfo.isBlank());
				needUpdate = true;
			}

			if (info.isContent() != newInfo.isContent()) {
				info.setContent(newInfo.isContent());
				needUpdate = true;
			}

			if (info.getParticipantId() != newInfo.getParticipantId()) {
				info.setParticipantId(newInfo.getParticipantId());
				needUpdate = true;
			}

			if (info.isAudioMute() != newInfo.isAudioMute() && !info.isLocal()) {
				info.setAudioMute(newInfo.isAudioMute());
				needUpdate = true;
			}

			if (!info.getStatus().equals(newInfo.getStatus())) {
				info.setStatus(newInfo.getStatus());
				needUpdate = true;
			}

			if (info.getSessionType() != newInfo.getSessionType()) {
				info.setSessionType(newInfo.getSessionType());
				needUpdate = true;
			}

//			if (!info.isLocal()) {
//				 GBLog.e(TAG, "### info.isVideoMute()=" + info.isVideoMute() +
//				 ", newInfo.isVideoMute()=" + newInfo.isVideoMute());
//			}
			if (info.isVideoMute() != newInfo.isVideoMute() && !info.isLocal()) {
				info.setVideoMute(newInfo.isVideoMute());
				needUpdate = true;
			}

			if (info.isLocal() != newInfo.isLocal()) {
				info.setLocal(newInfo.isLocal());
				needUpdate = true;
			}

			if (info.getNet_status() != newInfo.getNet_status() && !(info.isLocal() && info.getNet_status() == MemberInfo.NET_STATUS.VideoMute)) {
				info.setNet_status(newInfo.getNet_status());
				needUpdate = true;
			}

			if (newInfo.getDataSourceID() != null) {
				if (!newInfo.getDataSourceID().equals(info.getDataSourceID())) {
					info.setDataSourceID(newInfo.getDataSourceID());
					needUpdate = true;
				}
			} else {
				if (info.getDataSourceID() != null) {
					info.setDataSourceID(newInfo.getDataSourceID());
					needUpdate = true;
				}
			}

		} catch (Throwable e) {
			GBLog.e(TAG, "UpdateInfo Throwable:" + VCUtils.CaughtException(e));
		}

		return needUpdate;
	}

	public void setOnMeetingInfoUpdateListener(OnMeetingInfoUpdateListener listener) {
		mOnMeetingInfoUpdateListener = listener;
	}

	private OnMeetingInfoUpdateListener mOnMeetingInfoUpdateListener = null;

	public interface OnMeetingInfoUpdateListener {
		void onMemberChanged(ArrayList<MemberInfo> mScreen, ArrayList<MemberInfo> mOther, ArrayList<MemberInfo> mUnjoined, int duration, boolean isMemberContent);
		void onMemberSizeChanged(int joinedSize, int memberSize);
		void onMemberCommentExit(MemberInfo mExit);
	}

	public class SessionType {
		private DeviceState ContentOnlyState;
		private DeviceState MobileState;
		private DeviceState PCState;

		public DeviceState getContentOnlyState() {
			return ContentOnlyState;
		}

		public void setContentOnlyState(DeviceState contentOnlyState) {
			ContentOnlyState = contentOnlyState;
		}

		public DeviceState getMobileState() {
			return MobileState;
		}

		public void setMobileState(DeviceState mobileState) {
			MobileState = mobileState;
		}

		public DeviceState getPCState() {
			return PCState;
		}

		public void setPCState(DeviceState PCState) {
			this.PCState = PCState;
		}
	}
}
