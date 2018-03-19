package com.shgbit.android.heyshareuvc.interactmanager;

import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.YunDesktop;

public interface ServerInteractCallback {
	void onLogin(boolean result, String error, User user);
	void onLogout(boolean result, String error);
	void onCheckPwd(boolean result, String error);
	void onMotifyPwd(boolean result, String error);
	void onStartYunDesk(boolean result, String error, YunDesktop yunDesktop);
	void onEndYunDesk(boolean result, String error, YunDesktop yunDesktop);
	void onCreateMeeting(boolean result, String error, Meeting meeting);
	void onJoinMeeting(boolean result, String error);
	void onInviteMeeting(boolean success, String error);
	void onKickoutMeeting(boolean success, String error);
	void onQuiteMeeting(boolean success, String error);
	void onEndMeeting(boolean success, String error);
	void onReserveMeeting(boolean success, String error, Meeting meeting);
	void onDeleteMeeting(boolean success, String error);
	void onUpdateMeeting(boolean success, String error);
	void onBusyMeeting(boolean success, String error);
	void onMeetings();
	void eventUserStateChanged(RefuseInfo[] refuseInfos, TimeoutInfo[] timeoutInfos);
	void eventInvitedMeeting(InvitedMeeting meeting);
	void eventStartYunDesk(YunDesktop yunDesktop);
	void eventEndYunDesk();
	void eventStartWhiteBoard();
	void eventEndWhiteBoard();
	void eventDifferentPlaceLogin();
	void eventInvitingCancle(InviteCancledInfo ici);
	void onValidate(boolean result, String err);

}
