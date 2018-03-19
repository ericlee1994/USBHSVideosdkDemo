package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2018/1/24.
 */

public class CtrlStatus {
    public String MeetingID;
    public String UserID;
    public ArrayList<CtrlStatusParam> StatusList;

    public String getMeetingID() {
        return MeetingID;
    }

    public void setMeetingID(String meetingID) {
        MeetingID = meetingID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ArrayList<CtrlStatusParam> getStatusList() {
        return StatusList;
    }

    public void setStatusList(ArrayList<CtrlStatusParam> statusList) {
        StatusList = statusList;
    }
}
