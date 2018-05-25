package com.shgbit.android.heysharevideo.callback;

import com.shgbit.android.heysharevideo.json.Meeting;

public interface HSSDKInstantListener {
    void onCreateMeetng(boolean result, String error, Meeting meeting);
}
