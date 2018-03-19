package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2017/12/14.
 */

public class CmtStatus {
    public String result;
    public String message;
    public int status;
    public ArrayList<String> resource;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<String> getResource() {
        return resource;
    }

    public void setResource(ArrayList<String> resource) {
        this.resource = resource;
    }
}
