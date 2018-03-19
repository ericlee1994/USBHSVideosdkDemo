package com.shgbit.android.heyshareuvc.callback;

import com.shgbit.android.heysharevideo.json.InvitedMeeting;

/**
 * Created by Eric on 2018/2/26.
 */

public interface HSSDKListener {
    void initState(boolean state);
    void connectState(boolean state);
    void disconnectState(boolean state);
    void inviteMeeting(InvitedMeeting invitedMeeting);
}
