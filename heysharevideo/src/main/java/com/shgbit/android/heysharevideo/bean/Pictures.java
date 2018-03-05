package com.shgbit.android.heysharevideo.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2017/12/11.
 */

public class Pictures {
    String meeting_id;
    ArrayList<PicInfo> data;

    public String getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(String meeting_id) {
        this.meeting_id = meeting_id;
    }

    public ArrayList<PicInfo> getData() {
        return data;
    }

    public void setData(ArrayList<PicInfo> data) {
        this.data = data;
    }
}
