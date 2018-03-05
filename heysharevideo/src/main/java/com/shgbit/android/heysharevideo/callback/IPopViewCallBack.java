package com.shgbit.android.heysharevideo.callback;


import com.shgbit.android.heysharevideo.bean.MemberInfo;

/**
 * Created by Administrator on 2017/9/22 0022.
 */

public interface IPopViewCallBack {
    void onClickPopBtn();
    void onClickMenuBtn(String type);
    void onClickMenuBtn(String type, boolean recordFlag);
    void onClickPerson(MemberInfo memberInfo);
    void onMqttMsg(String Object, String Operation, MemberInfo memberInfo);
}
