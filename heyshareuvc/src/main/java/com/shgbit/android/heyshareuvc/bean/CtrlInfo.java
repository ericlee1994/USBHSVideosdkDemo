package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2018/1/8.
 */

public class CtrlInfo {
    private String MeetingID;
    private String SenderID;
    private ArrayList<String> ReceiverID;
    private ArrayList<CtrlCmd> Command;

    public String getMeetingID() {
        return MeetingID;
    }

    public void setMeetingID(String meetingID) {
        MeetingID = meetingID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public ArrayList<String> getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(ArrayList<String> receiverID) {
        ReceiverID = receiverID;
    }

    public ArrayList<CtrlCmd> getCommand() {
        return Command;
    }

    public void setCommand(ArrayList<CtrlCmd> command) {
        Command = command;
    }
}
