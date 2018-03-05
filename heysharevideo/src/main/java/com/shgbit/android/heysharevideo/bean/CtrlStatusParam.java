package com.shgbit.android.heysharevideo.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2018/1/24.
 */

public class CtrlStatusParam {
    public String Status;
    public String Object;
    public ArrayList<String> Params;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getObject() {
        return Object;
    }

    public void setObject(String object) {
        Object = object;
    }

    public ArrayList<String> getParams() {
        return Params;
    }

    public void setParams(ArrayList<String> params) {
        Params = params;
    }
}
