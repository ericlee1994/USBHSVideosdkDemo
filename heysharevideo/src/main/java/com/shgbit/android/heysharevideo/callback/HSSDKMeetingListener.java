package com.shgbit.android.heysharevideo.callback;

import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;

public interface HSSDKMeetingListener {
    void inviteMeeting(InvitedMeeting invitedMeeting);
    void onCheckPwd(boolean result, String error);
    void onMotifyPwd(boolean result, String error);
    void onDeleteMeeting(boolean success, String error);
    void onUpdateMeeting(boolean success, String error);
    void onMeetings();
    void eventDifferentPlaceLogin();
    void eventInvitingCancle(InviteCancledInfo ici);
    void eventUserStateChanged(RefuseInfo[] refuseInfo, TimeoutInfo[] timeoutInfos);
}
