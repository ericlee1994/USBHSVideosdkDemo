package com.shgbit.android.heysharevideo.addressaar;

import com.shgbit.android.heysharevideo.json.User;


/**
 * Created by Administrator on 2018/2/13 0013.
 */

public interface VideoCallBack extends ExternalCallBack {
    void invite(User[] users);
    void onDesFragment();
}
