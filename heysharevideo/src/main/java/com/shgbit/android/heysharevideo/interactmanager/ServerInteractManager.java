package com.shgbit.android.heysharevideo.interactmanager;

import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shgbit.android.heysharevideo.bean.MeetingRecord;
import com.shgbit.android.heysharevideo.json.AddToGroupInfo;
import com.shgbit.android.heysharevideo.json.BusyMeetingInfo;
import com.shgbit.android.heysharevideo.json.CancelInviteInfo;
import com.shgbit.android.heysharevideo.json.CheckPwdResponse;
import com.shgbit.android.heysharevideo.json.Contacts;
import com.shgbit.android.heysharevideo.json.CreateGroupInfo;
import com.shgbit.android.heysharevideo.json.CreateInfo;
import com.shgbit.android.heysharevideo.json.CreateMeetingInfo;
import com.shgbit.android.heysharevideo.json.DeleFrmGroupInfo;
import com.shgbit.android.heysharevideo.json.DeleteGroupInfo;
import com.shgbit.android.heysharevideo.json.DeleteInfo;
import com.shgbit.android.heysharevideo.json.EndMeetingInfo;
import com.shgbit.android.heysharevideo.json.EndYunDeskInfo;
import com.shgbit.android.heysharevideo.json.EndYunDeskResponse;
import com.shgbit.android.heysharevideo.json.FrequentContactsInfoSet;
import com.shgbit.android.heysharevideo.json.FrequentContactsPost;
import com.shgbit.android.heysharevideo.json.HeartBeatEvents;
import com.shgbit.android.heysharevideo.json.HeartBeatInfo;
import com.shgbit.android.heysharevideo.json.HotFixConfig;
import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InviteMeetingInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.JoinMeetingInfo;
import com.shgbit.android.heysharevideo.json.KickoutInfo;
import com.shgbit.android.heysharevideo.json.LoginInfo;
import com.shgbit.android.heysharevideo.json.LoginResponse;
import com.shgbit.android.heysharevideo.json.LogoutInfo;
import com.shgbit.android.heysharevideo.json.MeetingDetail;
import com.shgbit.android.heysharevideo.json.MeetingInfo;
import com.shgbit.android.heysharevideo.json.Online;
import com.shgbit.android.heysharevideo.json.OnlineUser;
import com.shgbit.android.heysharevideo.json.PushConfig;
import com.shgbit.android.heysharevideo.json.QueryGroupInfo;
import com.shgbit.android.heysharevideo.json.QueryGroupResponse;
import com.shgbit.android.heysharevideo.json.QuiteMeetingInfo;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.ReserveInfo;
import com.shgbit.android.heysharevideo.json.ReserveMeetingInfo;
import com.shgbit.android.heysharevideo.json.ReserveRepsonse;
import com.shgbit.android.heysharevideo.json.Result;
import com.shgbit.android.heysharevideo.json.SendInfo;
import com.shgbit.android.heysharevideo.json.StartYunDeskInfo;
import com.shgbit.android.heysharevideo.json.StartYunDeskResponse;
import com.shgbit.android.heysharevideo.json.SyncPidInfo;
import com.shgbit.android.heysharevideo.json.SystemConfig;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.UpdateGroupInfo;
import com.shgbit.android.heysharevideo.json.XiaoYuConfig;
import com.shgbit.android.heysharevideo.json.YunDesktop;
import com.wa.util.WAJSONTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerInteractManager {
	private final String TAG = "ServerInteractManager";
	private final String SecretKey = "c3230d18-599c-486f-94e4-615921ca5e46";
	private final String SessionType = "MobileState";
	
	public enum INTERACTTYPE {LOGIN,LOGINOUT,CONTACTS,ONLINE,GFCUSER,CREATE,JOIN,INVITE,KICKOUT,QUITE,END,STARTYUN,ENDYUN,RESERVE
		,DELETE,UPDATE,BUSY,CONFIG,MEETING,CHECKPWD,MOTIFYPWD,PFCUSER,CANCLEINVITE,SYNCPID,SENDMSG,CREATEGROUP,DELETEGROUP,UPDATEGROUP
		,QUERYGROUP,ADDTOGROUP,DELEFRMGROUP,START,STOP}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
	
	private HeartBeatThread mHeartBeatThread;
	private boolean mHeartBeatThreadLooper = false;
	private boolean mIsFinishHeartbeat = false;

	private long timeStamp = 0;

	private String mServiceUrl = "";
	private String mSessionId = "";
	private String mUserName = "";

	private OnlineUser[] onlineUsers;
	private InvitedMeeting mInvitedMeeting;

	public static ServerInteractManager instance;


	private ArrayList<ServerConfigCallback> mConfigCallbacks = new ArrayList<>();
	public void setServiceConfigCallback (ServerConfigCallback callback) {
		if (callback == null) {
			return;
		}
		if (mConfigCallbacks == null) {
			mConfigCallbacks = new ArrayList<>();
		}
		mConfigCallbacks.add(callback);
	}

	public void removeServiceConfigCallback (ServerConfigCallback callback) {
		if (mConfigCallbacks != null) {
			mConfigCallbacks.remove(callback);
		}
	}

	public void removeAllServiceConfigCallbacks () {
		if (mConfigCallbacks != null) {
			mConfigCallbacks.clear();
		}
	}

	private ArrayList<ServerAddressCallback> mAddressCallbacks = new ArrayList<>();
	public void setServerAddressCallback (ServerAddressCallback callback) {
		if (callback == null) {
			return;
		}

		if (mAddressCallbacks == null) {
			mAddressCallbacks = new ArrayList<>();
		}
		mAddressCallbacks.add(callback);
	}

	public void removeServerAddressCallback (ServerAddressCallback callback) {
		if (mAddressCallbacks != null) {
			mAddressCallbacks.remove(callback);
		}
	}

	public void removeAllServerAddressCallbacks () {
		if (mAddressCallbacks != null) {
			mAddressCallbacks.clear();
		}
	}

	private ArrayList<ServerInteractCallback> mInteractCallbacks = new ArrayList<>();
	public void setServerInteractCallback (ServerInteractCallback callback) {
		if (callback == null) {
			return;
		}
		if (mInteractCallbacks == null) {
			mInteractCallbacks = new ArrayList<>();
		}
		mInteractCallbacks.add(callback);
	}

	public void removeServerInteractCallback (ServerInteractCallback callback) {
		if (mInteractCallbacks != null) {
			mInteractCallbacks.remove(callback);
		}
	}

	public void removeAllServerInteractCallbacks () {
		if (mInteractCallbacks != null) {
			mInteractCallbacks.clear();
		}
	}

	private ArrayList<ServerRecordCallback> mRecordCallbacks = new ArrayList<>();
	public void setServerRecordCallback (ServerRecordCallback callback) {
		if (callback == null) {
			return;
		}
		if (mRecordCallbacks == null) {
			mRecordCallbacks = new ArrayList<>();
		}
		mRecordCallbacks.add(callback);
	}

	public void removeServerRecordCallback (ServerRecordCallback callback) {
		if (mRecordCallbacks != null) {
			mRecordCallbacks.remove(callback);
		}
	}

	public void removeAllServerRecordCallbacks () {
		if (mRecordCallbacks != null) {
			mRecordCallbacks.clear();
		}
	}

	public ServerInteractManager () {

	}

	public static ServerInteractManager getInstance () {
		if (instance == null) {
			instance = new ServerInteractManager();
		}
		return instance;
	}

	public void init (String serviceurl, String userName) {
		if (serviceurl == null || userName == null) {
			return;
		}

		mServiceUrl = serviceurl;
		mUserName = userName;
		StructureDataCollector.getInstance().init(mUserName);
	}
	@Override
	public void finalize () {
		try {
			if (mHeartBeatThread != null) {
				mHeartBeatThreadLooper = false;
				mIsFinishHeartbeat = true;
				mHeartBeatThread.join(100);
				mHeartBeatThread.interrupt();
				mHeartBeatThread = null;
			}

			onlineUsers = null;
			mInvitedMeeting = null;

			removeAllServiceConfigCallbacks();
			mConfigCallbacks = null;
			removeAllServerInteractCallbacks();
			mInteractCallbacks = null;
			removeAllServerAddressCallbacks();
			mAddressCallbacks.clear();
			removeAllServerRecordCallbacks();
			mRecordCallbacks = null;

			instance = null;

		} catch (Throwable e) {
			Log.e(TAG, "finalize Throwable: " + e.toString());
		}
	}

	private void startHeartBeat () {
		try {
			if (mHeartBeatThread != null) {
				mHeartBeatThreadLooper = false;
				mIsFinishHeartbeat = true;
				mHeartBeatThread.join(100);
				mHeartBeatThread.interrupt();
				mHeartBeatThread = null;
			}
		} catch (Throwable e) {
			Log.e(TAG, "finalize heartbeat Throwable:" + e.toString());
		}
			
		mHeartBeatThread = new HeartBeatThread();
		mHeartBeatThreadLooper = true;
		mIsFinishHeartbeat = false;
		mHeartBeatThread.start();
	}
	
	public void getSystemConfig () {
		String url = mServiceUrl + "/settings/v1";
		new GetTask(url, INTERACTTYPE.CONFIG).execute();
	}
	
	public void getContacts () {
		String url = mServiceUrl + "/contacts?sessionId=" + mSessionId;
		new GetTask(url, INTERACTTYPE.CONTACTS).execute();
	}
	
	public void getOnline () {
		String url = mServiceUrl + "/user/online";
		new GetTask(url, INTERACTTYPE.ONLINE).execute();
	}
	
	public void getContactUser () {
		String url = mServiceUrl + "/user/contact/favorites?sessionId="+ mSessionId;
		new GetTask(url, INTERACTTYPE.GFCUSER).execute();
	}
	
	public void login (LoginInfo li) {
		if (li == null) {
			return;
		}

		String url = mServiceUrl + "/login";
		String data = getsecritString(li, SecretKey);

		JsonObject object = new JsonObject();
		try {
			object.addProperty("data", data);
			object.addProperty("sessionType", SessionType);
		} catch (Throwable e) {
			Log.e(TAG, "Add JSONObject Throwable: " + e.toString());
		}
		
		new PostTask(url, object.toString(), INTERACTTYPE.LOGIN).execute();
	}
	
	public void checkPwd(LoginInfo li){
		if (li == null) {
			return;
		}

		String url = mServiceUrl + "/user/password/validation";
		String data = getsecritString(li, SecretKey);

		JsonObject object = new JsonObject();
		try {
			object.addProperty("data", data);
			object.addProperty("sessionId", mSessionId);
		} catch (Throwable e) {
			Log.e(TAG, "Add JSONObject Throwable: " + e.toString());
		}
		
		new PostTask(url, object.toString(), INTERACTTYPE.CHECKPWD).execute();
	}
	
	public void motifyPwd(LoginInfo li){
		if (li == null) {
			return;
		}

		String url = mServiceUrl + "/user/password/update";
		String data = getsecritString(li, SecretKey);

		JsonObject object = new JsonObject();
		try {
			object.addProperty("data", data);
			object.addProperty("sessionId", mSessionId);
		} catch (Throwable e) {
			Log.e(TAG, "Add JSONObject Throwable: " + e.toString());
		}
		
		new PostTask(url, object.toString(), INTERACTTYPE.MOTIFYPWD).execute();
	}
	
	private String getsecritString (LoginInfo li, String secret) {
		JsonObject jObject = new JsonObject();
		try {
			jObject.addProperty("userName", li.getUserName());
			jObject.addProperty("password", li.getPassword());
		} catch (Throwable e) {
			Log.e(TAG, "Add JSONObject Throwable: " + e.toString());
		}
		return GBUE1.encode(jObject.toString(), secret);
	}
	
	public void logout () {
		LogoutInfo logoutInfo = new LogoutInfo();
		logoutInfo.setSessionId(mSessionId);
		logoutInfo.setSessionType(SessionType);
		
		String url = mServiceUrl + "/logout";
		
		new PostTask(url, new Gson().toJson(logoutInfo), INTERACTTYPE.LOGINOUT).execute();
	}
	
	public void createMeeting (CreateMeetingInfo cmi) {
		if (cmi != null) {
			cmi.setSessionType(SessionType);
			cmi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/creating";
		
		new PostTask(url, new Gson().toJson(cmi), INTERACTTYPE.CREATE).execute();
	}
	
	public void joinMeeting (JoinMeetingInfo jmi) {
		if (jmi != null) {
			jmi.setSessionType(SessionType);
			jmi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/joining";
		
		new PostTask(url, new Gson().toJson(jmi), INTERACTTYPE.JOIN).execute();
	}
	
	public void FrequentContacts (FrequentContactsPost fmi) {
		if (fmi != null) {
			fmi.setSessionId(mSessionId);
		}
		String url = mServiceUrl + "/user/contact/favorites";
		
		new PostTask(url, new Gson().toJson(fmi), INTERACTTYPE.PFCUSER).execute();
	}
	
	public void inviteMeeting (InviteMeetingInfo imi) {
		if (imi != null) {
			imi.setSessionId(mSessionId);
			imi.setSessionType(SessionType);
		}
		
		String url = mServiceUrl + "/meeting/inviting";
		
		new PostTask(url, new Gson().toJson(imi), INTERACTTYPE.INVITE).execute();
	}
	
	public void kickoutMeeting (KickoutInfo ki) {
		if (ki != null) {
			ki.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/kickout";
		
		new PostTask(url, new Gson().toJson(ki), INTERACTTYPE.KICKOUT).execute();
	}
	
	public void quiteMeeting (QuiteMeetingInfo qmi) {
		if (qmi != null) {
			qmi.setSessionType(SessionType);
			qmi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/quiting";
		
		new PostTask(url, new Gson().toJson(qmi), INTERACTTYPE.QUITE).execute();
	}
	
	public void endMeeting (EndMeetingInfo emi) {
		if (emi != null) {
			emi.setSessionType(SessionType);
			emi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/end";
		
		new PostTask(url, new Gson().toJson(emi), INTERACTTYPE.END).execute();
	}
	
	public void startYunDesktop (StartYunDeskInfo syi) {
		if (syi != null) {
			syi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/yunDesktop/starting";
		
		new PostTask(url, new Gson().toJson(syi), INTERACTTYPE.STARTYUN).execute();
	}
	
	public void endYunDesktop (EndYunDeskInfo ei) {
		if (ei != null) {
			ei.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/yunDesktop/end";
		
		new PostTask(url, new Gson().toJson(ei), INTERACTTYPE.ENDYUN).execute();
	}
	
	public void reserveMeeting (ReserveInfo ri) {
		if (ri != null) {
			ri.setSessionType(SessionType);
			ri.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/reserving";
		
		new PostTask(url, new Gson().toJson(ri), INTERACTTYPE.RESERVE).execute();
	}
	
	public void deleteMeeting (DeleteInfo di) {
		if (di != null) {
			di.setSessionType(SessionType);
			di.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/delete";
		
		new PostTask(url, new Gson().toJson(di), INTERACTTYPE.DELETE).execute();
	}
	
	public void updateMeeting (ReserveMeetingInfo rmi) {
		if (rmi != null) {
			rmi.setSessionType(SessionType);
			rmi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/update";
		
		new PostTask(url, new Gson().toJson(rmi), INTERACTTYPE.UPDATE).execute();
	}
	
	public void busyMeeting (BusyMeetingInfo bmi) {
		if (bmi != null) {
			bmi.setSessionType(SessionType);
			bmi.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/busy";
		
		new PostTask(url, new Gson().toJson(bmi), INTERACTTYPE.BUSY).execute();
	}
	
	public void getMeeting (String meetingId) {
		String url = mServiceUrl + "/meeting/" + meetingId;
		new GetTask(url, INTERACTTYPE.MEETING).execute();
	}
	public String getSyncCurrentMeeting () {
		String url = mServiceUrl + "/currentMeeting?userName=" + mUserName + "&sessionId=" +
				mSessionId + "&sessionType=" + SessionType;

		return httpGet(url);
	}

	public String getSyncGetMeeting(String meetingId){
		String url = mServiceUrl + "/meeting/" + meetingId;
		return  httpGet(url);
	}
	
	public void cancleMeeting (CancelInviteInfo cii) {
		if (cii != null) {
			cii.setSessionType(SessionType);
			cii.setSessionId(mSessionId);
		}
		
		String url = mServiceUrl + "/meeting/inviting/cancel";
		
		new PostTask(url, new Gson().toJson(cii), INTERACTTYPE.CANCLEINVITE).execute();
	}
	
	public void syncPid (SyncPidInfo spi) {
		if (spi != null) {
			spi.setSessionId(mSessionId);
			spi.setSessionType(SessionType);
		}

		String url = mServiceUrl + "/meeting/syncpid";
		
		new PostTask(url, new Gson().toJson(spi), INTERACTTYPE.SYNCPID).execute();
	}

	public void sendMessage (SendInfo si) {
		if (si != null) {
			si.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/meeting/sms/send";

		new PostTask(url, new Gson().toJson(si), INTERACTTYPE.SENDMSG).execute();
	}

	public void createGroup (CreateGroupInfo cgi) {
		if (cgi != null) {
			cgi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/create";

		new PostTask(url, new Gson().toJson(cgi), INTERACTTYPE.CREATEGROUP).execute();
	}

	public void deleteGroup (DeleteGroupInfo dgi) {
		if (dgi != null) {
			dgi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/delete";

		new PostTask(url, new Gson().toJson(dgi), INTERACTTYPE.DELETEGROUP).execute();
	}

	public void updateGroup (UpdateGroupInfo ugi) {
		if (ugi != null) {
			ugi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/update";

		new PostTask(url, new Gson().toJson(ugi), INTERACTTYPE.UPDATEGROUP).execute();
	}

	public void queryGroup (QueryGroupInfo qgi) {
		if (qgi != null) {
			qgi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/query";

		new PostTask(url, new Gson().toJson(qgi), INTERACTTYPE.QUERYGROUP).execute();
	}

	public void addToGroup (AddToGroupInfo atgi) {
		if (atgi != null) {
			atgi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/members/add";

		new PostTask(url, new Gson().toJson(atgi), INTERACTTYPE.ADDTOGROUP).execute();
	}

	public void deleFrmGroup (DeleFrmGroupInfo dfgi) {
		if (dfgi != null) {
			dfgi.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/group/members/delete";

		new PostTask(url, new Gson().toJson(dfgi), INTERACTTYPE.DELEFRMGROUP).execute();
	}

	public void startRecord (MeetingRecord mr) {
		if (mr != null) {
			mr.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/meeting/record/start";
		new PostTask(url, new Gson().toJson(mr), INTERACTTYPE.START).execute();
	}

	public void endRecord (MeetingRecord mr) {
		if (mr != null) {
			mr.setSessionId(mSessionId);
		}

		String url = mServiceUrl + "/meeting/record/end";
		new PostTask(url, new Gson().toJson(mr), INTERACTTYPE.STOP).execute();
	}

	public static String getTimeStr2(long time) {
		SimpleDateFormat sTimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		return sTimeSDF.format(time);
	}
	
	private class HeartBeatThread extends Thread {

		@Override
		public void run() {
			try {
				while (mHeartBeatThreadLooper) {
					Log.i(TAG, "HeartBeatThread is running !!!");
					
					 Time time = new Time();
					 time.setToNow();
					 String starttime = getTimeStr2(time.toMillis(false));
					 time.year +=1;
					 time.hour = 23;
					 time.minute = 59;
					 String endTime = getTimeStr2(time.toMillis(false));

					 String url = mServiceUrl + "/meeting?userName=" + mUserName + "&sessionId=" + mSessionId
							 + "&startTime=" + starttime + "&endTime=" + endTime + "&sessionType=" + SessionType;
					 
					 try {
						 String response = httpGet(url);
						 if (response != null && response.equals("") == false) {
							 MeetingInfo mi = new Gson().fromJson(response, MeetingInfo.class);
							 if (mi != null && mi.getResult().equalsIgnoreCase("success") == true) {
								 MeetingCeche.getInstance().setMeetings(mi.getMeetings());
								 for (ServerInteractCallback callback : mInteractCallbacks) {
									 if (callback == null) {
										 continue;
									 }
									 callback.onMeetings();
								 }
							 }
						 }
					 } catch (Throwable e) {
						 Log.e(TAG, "Get meetings Throwable: " + e.toString());
					 }
					 
					 String url2 = mServiceUrl + "/heartbeat?userName=" + mUserName + "&sessionId=" + mSessionId
							 + (timeStamp == 0 ? "":"&timeStamp=" + timeStamp);
					 
					 try {
						 String response = httpGet(url2);
						 if (response != null && response.equals("") == false) {
							 HeartBeatInfo hbi = WAJSONTool.parseObject(response, HeartBeatInfo.class);
							 if (hbi != null) {
								 if (hbi.getResult().equals("failed") == true) {
									 Log.e(TAG, "heartbeat failed: " + response);
								 }
								 if (hbi.getHeartbeatEvents() != null && hbi.getHeartbeatEvents().length > 0) {
									 ArrayList<RefuseInfo> refuselist = new ArrayList<RefuseInfo>();
									 ArrayList<TimeoutInfo> timeoutlist = new ArrayList<TimeoutInfo>();
									 for (HeartBeatEvents hbEvents : hbi.getHeartbeatEvents()) {
										 if (hbEvents.getEventName().equalsIgnoreCase("meetingJoined")) {
											 //user
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingBusied")) {
											 refuselist.add(new Gson().fromJson(hbEvents.getEventParams(),RefuseInfo.class));
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingInvitingTimeout")) {
											 timeoutlist.add(new Gson().fromJson(hbEvents.getEventParams(), TimeoutInfo.class));
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingInvited")) {
											 if (hbEvents.getEventParams() != null) {
												 mInvitedMeeting = new Gson().fromJson(hbEvents.getEventParams(), InvitedMeeting.class);
												 if (mInvitedMeeting != null) {
													 getMeeting(mInvitedMeeting.getMeetingId());
												 }
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("byKicking")) {
											 
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingKickouted")) {
											 //kickInfo
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingQuited")) {
											 //user
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingEnded")) {
											 //endmeeting
										 } else if (hbEvents.getEventName().equalsIgnoreCase("yunStarted")) {
											 if (hbEvents.getEventParams() != null) {
												 for (ServerInteractCallback callback : mInteractCallbacks) {
													 if (callback == null) {
														 continue;
													 }
													 callback.eventStartYunDesk(new Gson().fromJson(hbEvents.getEventParams(), YunDesktop.class));
												 }
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("yunEnded")) {
											 for (ServerInteractCallback callback : mInteractCallbacks) {
												 if (callback == null) {
													 continue;
												 }
												 callback.eventEndYunDesk();
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("whiteStarted")) {
											 if (hbEvents.getEventParams() != null) {
												 for (ServerInteractCallback callback : mInteractCallbacks) {
													 if (callback == null) {
														 continue;
													 }
													 callback.eventStartWhiteBoard();
												 }
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("whiteEnded")) {
											 if (hbEvents.getEventParams() != null) {
												 for (ServerInteractCallback callback : mInteractCallbacks) {
													 if (callback == null) {
														 continue;
													 }
													 callback.eventEndWhiteBoard();
												 }
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("fileResultNoted")) {
											 //fileResultInfo
										 } else if (hbEvents.getEventName().equalsIgnoreCase("onlineChange")) {
//											 getContacts();
										 } else if (hbEvents.getEventName().equalsIgnoreCase("differentPlaceLogin")) {
											 if (hbEvents.getEventParams() != null) {
												 for (ServerInteractCallback callback : mInteractCallbacks) {
													 if (callback == null) {
														 continue;
													 }
													 callback.eventDifferentPlaceLogin();
												 }
											 }
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingInvitingTimeout")) {
											 
										 } else if (hbEvents.getEventName().equalsIgnoreCase("meetingChanged")) {
											 
										 } else if (hbEvents.getEventName().equalsIgnoreCase("invitingCancel")) {
											 if (hbEvents.getEventParams() != null) {
												 for (ServerInteractCallback callback : mInteractCallbacks) {
													 if (callback == null) {
														 continue;
													 }
													 callback.eventInvitingCancle(new Gson().fromJson(hbEvents.getEventParams(), InviteCancledInfo.class));
												 }
											 }
										 }
										 timeStamp = hbEvents.getTimeStamp();
									 }
									 
									 if (refuselist.size() > 0 || timeoutlist.size() > 0) {
										 for (ServerInteractCallback callback : mInteractCallbacks) {
											 if (callback == null) {
												 continue;
											 }
											 callback.eventUserStateChanged(refuselist.toArray(new RefuseInfo[0]), timeoutlist.toArray(new TimeoutInfo[0]));
										 }
									 }
								 }
							 }
						 }
					 } catch (Throwable e) {
						 Log.e(TAG, "HeartBeatThread Throwable1:" + e.toString());
					 }
					 
					 getOnline();
					 //getContactUser(SystemParams.getSessionId());
					 
					 mIsFinishHeartbeat = false;
					for (int i = 0; i < 10; i++) {
						if (mIsFinishHeartbeat) {
							mIsFinishHeartbeat = false;
							break;
						}
						Thread.sleep(1000);
					}
				}
			} catch (Throwable e) {
				Log.e(TAG, "HeartBeatThread Throwable2:" + e.toString());
			}
			super.run();
		}
	}
	
	private class PostTask extends AsyncTask<Void, Void, String> {
		private String mUrl = "";
		private String mObject;
		private INTERACTTYPE mType;
		public PostTask (String url, String object, INTERACTTYPE type) {
			mUrl = url;
			mObject = object;
			mType = type;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				return httpPost(mUrl,mObject);
			} catch (Throwable e) {
				Log.e(TAG, "doInBackground Throwable: " + e.toString());
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				switch (mType) {
				case LOGIN:
					LoginResponse lr = null;

					try {
						lr = new Gson().fromJson(result, LoginResponse.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse LoginResponse Throwable: " + e.toString());
					}

					if (lr == null || lr.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onLogin(false, lr == null?"unknow error":lr.getFailedMessage(), null);
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onLogin(true, "", lr.getUser());
						}

						mSessionId = lr.getSessionId();
						getContacts();
						startHeartBeat();
					}
					break;
				case CHECKPWD:
					CheckPwdResponse cpwd = null;

					try {
						cpwd = new Gson().fromJson(result, CheckPwdResponse.class);

					} catch (Throwable e) {
						Log.e(TAG, "parse CheckPwdResponse Throwable: " + e.toString());
					}

					if (cpwd == null || cpwd.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCheckPwd(false, cpwd == null?"check failed":cpwd.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCheckPwd(true, "");
						}
					}
					break;
				case MOTIFYPWD:
					CheckPwdResponse cp = null;

					try {
						cp = new Gson().fromJson(result, CheckPwdResponse.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse MotifyPwdResponse Throwable: " + e.toString());
					}

					if (cp == null || cp.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onMotifyPwd(false, cp == null?"motify failed":cp.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onMotifyPwd(true, "");
						}
					}
					break;
				case LOGINOUT:
					Result r = null;

					try {
						r = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r == null || r.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onLogout(false, r == null?"unknow error":r.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onLogout(true,"");
						}
					}
					break;
				case CREATE:
					CreateInfo ci = null;

					try {
						ci = new Gson().fromJson(result, CreateInfo.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse CreateInfo Throwable: " + e.toString());
					}

					if (ci == null || ci.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCreateMeeting(false, ci == null?"unknow error":ci.getFailedMessage(), null);
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCreateMeeting(true, "", ci.getMeeting());
						}
					}
					break;
				case JOIN:
					Result r2 = null;

					try {
						r2 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r2 == null || r2.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onJoinMeeting(false, r2 == null?"unknow error":r2.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onJoinMeeting(true,"");
						}
					}
					break;
				case INVITE:
					Result r3 = null;

					try {
						r3 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r3 == null || r3.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onInviteMeeting(false, r3 == null?"unknow error":r3.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onInviteMeeting(true, "");
						}
					}
					break;
				case KICKOUT:
					Result r4 = null;

					try {
						r4 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r4 == null || r4.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onKickoutMeeting(false, r4 == null?"unknow error":r4.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onKickoutMeeting(true, "");
						}
					}
					break;
				case QUITE:
					Result r5 = null;

					try {
						r5 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r5 == null || r5.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onQuiteMeeting(false, r5 == null?"unknow error":r5.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onQuiteMeeting(true, "");
						}
					}
					break;
				case END:
					Result r6 = null;

					try {
						r6 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r6 == null || r6.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onEndMeeting(false, r6 == null?"unknow error":r6.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onEndMeeting(true, "");
						}
					}
					break;
				case STARTYUN:
					StartYunDeskResponse syr = null;

					try {
						syr = new Gson().fromJson(result, StartYunDeskResponse.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse StartYunDeskResponse Throwable: " + e.toString());
					}

					if (syr == null || syr.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onStartYunDesk(false, syr == null?"unknow error":syr.getFailedMessage(), null);
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onStartYunDesk(true, "", syr.getYunDesktop());
						}
					}
					break;
				case ENDYUN:
					EndYunDeskResponse eyr = null;

					try {
						eyr = new Gson().fromJson(result, EndYunDeskResponse.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse EndYunDeskResponse Throwable: " + e.toString());
					}

					if (eyr == null || eyr.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onEndYunDesk(false, eyr == null?"unknow error":eyr.getFailedMessage(), null);
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onEndYunDesk(true, "", eyr.getYunDesktop());
						}
					}
					break;
				case RESERVE:
					ReserveRepsonse rr = null;

					try {
						rr = new Gson().fromJson(result, ReserveRepsonse.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse ReserveRepsonse Throwable: " + e.toString());
					}

					if (rr == null || rr.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onReserveMeeting(false, rr == null?"unknow error":rr.getFailedMessage(), null);
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onReserveMeeting(true, "", rr.getMeeting());
						}
					}
					break;
				case DELETE:
					Result r7 = null;

					try {
						r7 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r7 == null || r7.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleteMeeting(false, r7 == null?"unknow error":r7.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleteMeeting(true, "");
						}
					}
					break;
				case UPDATE:
					Result r8 = null;

					try {
						r8 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r8 == null || r8.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onUpdateMeeting(false, r8 == null?"unknow error":r8.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onUpdateMeeting(true, "");
						}
					}
					break;
				case BUSY:
					Result r9 = null;

					try {
						r9 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r9 == null || r9.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onBusyMeeting(false, r9 == null?"unknow error":r9.getFailedMessage());
						}
					} else {
						for (ServerInteractCallback callback : mInteractCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onBusyMeeting(true, "");
						}
					}
					break;
				case CANCLEINVITE:
                    break;
				case PFCUSER:
					Result r10 = null;

					try {
						r10 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r10 == null || r10.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onPostContactUser(false, r10 == null?"unknow error":r10.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onPostContactUser(true, "");
						}
					}
					break;
				case SENDMSG:
					Log.i(TAG, "send message result: " + result.toString());
					break;
				case CREATEGROUP:
					Result r11 = null;
					try {
						r11 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r11 == null || r11.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCreateGroup(false,r11 == null?"unknow error":r11.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onCreateGroup(true, "");
						}
					}
					break;
				case DELETEGROUP:
					Result r12 = null;

					try {
						r12 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r12 == null || r12.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleteGroup(false, r12 == null?"unknow error":r12.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleteGroup(true, "");
						}
					}
					break;
				case UPDATEGROUP:
					Result r13 = null;

					try {
						r13 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r13 == null || r13.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onUpdateGroup(false, r13 == null?"unknow error":r13.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onUpdateGroup(true, "");
						}
					}
					break;
				case QUERYGROUP:
					QueryGroupResponse qgr = null;

					try {
						qgr = new Gson().fromJson(result, QueryGroupResponse.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse QueryGroupResponse Throwable: " + e.toString());
					}

					if (qgr == null || qgr.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onQueryGroup(false, qgr == null?"unknow error":qgr.getFailedMessage(),null);
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onQueryGroup(true, "", qgr.getGroups());
						}
					}
					break;
				case ADDTOGROUP:
					Result r14 = null;

					try {
						r14 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}


					if (r14 == null || r14.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onAddToGroup(false, r14 == null?"unknow error":r14.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onAddToGroup(true, "");
						}
					}
					break;
				case DELEFRMGROUP:
					Result r15 = null;

					try {
						r15 = new Gson().fromJson(result, Result.class);
					}catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (r15 == null || r15.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleFrmGroup(false, r15 == null?"unknow error":r15.getFailedMessage());
						}
					} else {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onDeleFrmGroup(true, "");
						}
					}
					break;
				case START:
					Result result1= null;
					try {
						result1 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (result1 == null || result1.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerRecordCallback callback : mRecordCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.startRecord(false, result1 == null?"unknow error":result1.getFailedMessage());
						}
					} else {
						for (ServerRecordCallback callback : mRecordCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.startRecord(true, "");
						}
					}
					break;
				case STOP:
					Result result2 = null;
					try {
						result2 = new Gson().fromJson(result, Result.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Result Throwable: " + e.toString());
					}

					if (result2 == null || result2.getResult().equalsIgnoreCase("failed") == true) {
						for (ServerRecordCallback callback : mRecordCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.endRecord(false, result2 == null?"unknow error":result2.getFailedMessage());
						}
					} else {
						for (ServerRecordCallback callback : mRecordCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.endRecord(true, "");
						}
					}
					break;
				default:
					break;
				}
			} catch (Throwable e) {
				Log.e(TAG, "onPostExecute Throwable:" + e.toString());
			}
		}
	}
	
	private String httpGet (String url) {
		String result = "";
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = mOkHttpClient.newCall(request).execute();
			result = response.body().string();
		}catch (Throwable e) {
			Log.e(TAG, "httpGet Throwable:" + e.toString());
			if (e.toString().toLowerCase().contains("timeout")) {
				Result re = new Result();
				re.setResult("failed");
				re.setFailedMessage("Timeout");
				result = new Gson().toJson(re);
			}
		}
		return result;
	}
	private String httpPost (String url, String json) {
		String result = "";
		try {
			RequestBody body = RequestBody.create(JSON, json);
	        Request request = new Request.Builder().url(url).post(body).build();
	        Response response = mOkHttpClient.newCall(request).execute();
	        result = response.body().string();
		}catch (Throwable e) {
			Log.e(TAG, "httpPost Throwable:" + e.toString());
			if (e.toString().toLowerCase().contains("timeout")) {
				Result re = new Result();
				re.setResult("failed");
				re.setFailedMessage("Timeout");
				result = new Gson().toJson(re);
			}
		}
		return result;
	} 
	
	private class GetTask extends AsyncTask<Void,Void,String> {
		private String mUrl = "";
		private INTERACTTYPE mType;
		public GetTask(String url, INTERACTTYPE type) {
			mUrl = url;
			mType = type;
		}              

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				
				return httpGet(mUrl);
				
				
			} catch (Throwable e) {
				Log.e(TAG, "doInBackground Throwable: " + e.toString());
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				switch (mType) {
				case CONFIG:
					SystemConfig config = null;
					try {
						config = new Gson().fromJson(result, SystemConfig.class);
					}catch (Throwable e){
						Log.e(TAG, "parse Serverconfig Throwable: " + e.toString());
					}
					
					XiaoYuConfig xiyuConfig = null;
					HotFixConfig hotfixConfig = null;
					PushConfig pushConfig = null;
					
					if (config != null && config.getResult().equalsIgnoreCase("success") == true) {
						if (config.getServiceSettings() != null) {
							xiyuConfig = config.getServiceSettings().getXiaoyuConfig();
							hotfixConfig = config.getServiceSettings().getGAHFSConfig();
							pushConfig = config.getServiceSettings().getGBPushConfig();
						}
					}

					for (ServerConfigCallback callback : mConfigCallbacks) {
						if (callback == null) {
							continue;
						}
						callback.configXiaoyu(xiyuConfig);
						callback.configHotfix(hotfixConfig);
						callback.configPush(pushConfig);
					}
					break;
				case CONTACTS:
					Contacts contacts = null;
					try {
						contacts = new Gson().fromJson(result, Contacts.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Contacts Throwable: " + e.toString());
					}
					
					if (contacts != null && contacts.getResult().equalsIgnoreCase("success") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.onAddressChanged(contacts.getOrganization(), contacts.getUsers());
						}
					}
					break;
				case ONLINE:
					Online online = null;
					try {
						online = new Gson().fromJson(result, Online.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse Online Throwable: " + e.toString());
					}
					
					if (online != null && online.getResult().equalsIgnoreCase("success") == true) {
						if (online.getOnlineUsers() == null || online.getOnlineUsers().length <= 0) {
							break;
						}
						
						boolean needChange = false;
						if (onlineUsers == null || onlineUsers.length <= 0) {
							needChange = true;
							onlineUsers = online.getOnlineUsers();
							
						} else {
							if (online.getOnlineUsers().length != onlineUsers.length) {
								needChange = true;
								onlineUsers = online.getOnlineUsers();
							} else{
								for (int i = 0; i < onlineUsers.length; i++) {
									boolean isSame = false; 
									for (int j = 0; j < online.getOnlineUsers().length; j++) {
										if (onlineUsers[i].getUserName().equals(online.getOnlineUsers()[j].getUserName()) == true) {
											if (onlineUsers[i].getStatus().equals(online.getOnlineUsers()[j].getStatus()) == true) {
												isSame = true;
												break;
											}
										}
									}
									if (!isSame) {
										needChange = true;
										onlineUsers = online.getOnlineUsers();
										break;
									}
								}
							}
						}
						
						if (needChange) {
							for (ServerAddressCallback callback : mAddressCallbacks) {
								if (callback == null) {
									continue;
								}
								callback.onOnlineChanged(onlineUsers);
							}
						}
					}
					break;
				case MEETING:
					MeetingDetail md = null;
					try {
						md = new Gson().fromJson(result, MeetingDetail.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse currentmeeting Throwable: " + e.toString());
					}
					
					if (md != null && md.getResult().equalsIgnoreCase("success") == true) {
						if (md.getMeeting() != null && md.getMeeting().getStatus().equalsIgnoreCase("end") == false) {
							for (ServerInteractCallback callback : mInteractCallbacks) {
								if (callback == null) {
									continue;
								}
								callback.eventInvitedMeeting(mInvitedMeeting);
							}
						}
					}
					break;
				case GFCUSER:
					FrequentContactsInfoSet fc = null;
					try {
						fc = new Gson().fromJson(result, FrequentContactsInfoSet.class);
					} catch (Throwable e) {
						Log.e(TAG, "parse frequentcontacts Throwable: " + e.toString());
					}
					if (fc != null && fc.getResult().equalsIgnoreCase("success") == true) {
						for (ServerAddressCallback callback : mAddressCallbacks) {
							if (callback == null) {
								continue;
							}
							callback.eventContactUser(fc.getFavorites());
						}
					}
					break;
				default:
					break;
				}
			} catch (Throwable e) {
				Log.e(TAG, "onPostExecute Throwable: " + e.toString());
			}
		}
	}
}
