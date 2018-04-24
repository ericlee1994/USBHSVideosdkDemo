package com.shgbit.android.heysharevideo.callback;

import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.Meeting;

/**
 * Created by Eric on 2018/2/26.
 * @author Eric
 */

public interface HSSDKListener {
    void initState(boolean state);
    void connectState(boolean state);
    void disconnectState(boolean state, String info);




}
