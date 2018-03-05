package com.shgbit.android.heysharevideo.util;


import com.shgbit.android.heysharevideo.bean.SESSIONTYPE;

/**
 * Created by Eric on 2018/1/23.
 */

public class DeviceName {
    public static String addSuffix(SESSIONTYPE sessiontype, String name){
        String deviceName = null;
        if (sessiontype.equals(SESSIONTYPE.PC)){
            deviceName = name + "?t_pc";
        }else if (sessiontype.equals(SESSIONTYPE.MOBILE)){
            deviceName = name + "?t_mobile";
        }else if (sessiontype.equals(SESSIONTYPE.PC_CONTENT)) {
            deviceName = name + "?t_content";
        }
        return deviceName;
    }
}
