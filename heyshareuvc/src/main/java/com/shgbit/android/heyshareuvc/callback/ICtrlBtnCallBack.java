package com.shgbit.android.heyshareuvc.callback;

import com.shgbit.android.heysharevideo.bean.MemberInfo;

/**
 * Created by Eric on 2018/1/29.
 */

public interface ICtrlBtnCallBack {
    void mqttMsg(String Object, String Operation, MemberInfo memberInfo);
}
