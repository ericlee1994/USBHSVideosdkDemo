package com.shgbit.android.heysharevideo.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2018/1/8.
 */

public class CtrlCmd {
    public String Operation;
    public String Object;
    public ArrayList<String> Params;

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String operation) {
        Operation = operation;
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
