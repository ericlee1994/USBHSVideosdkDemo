package com.shgbit.android.heysharevideo.callback;

import com.shgbit.android.heysharevideo.json.Meeting;

public interface HSSDKReserveListener {
    void onReserveMeeting(boolean success, String error, Meeting meeting);
}
