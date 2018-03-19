package com.shgbit.android.heyshareuvc.interactmanager;

import com.shgbit.android.heysharevideo.json.Favorite;
import com.shgbit.android.heysharevideo.json.Group;
import com.shgbit.android.heysharevideo.json.OnlineUser;
import com.shgbit.android.heysharevideo.json.Organization;
import com.shgbit.android.heysharevideo.json.UserInfo;

/**
 * Created by Administrator on 2018/2/13.
 */

public interface ServerAddressCallback {
    void onAddressChanged(Organization[] organizations, UserInfo[] userInfos);
    void onOnlineChanged(OnlineUser[] onlineUsers);
    void onPostContactUser(boolean success, String error);
    void onCreateGroup(boolean success, String error);
    void onDeleteGroup(boolean success, String error);
    void onUpdateGroup(boolean success, String error);
    void onQueryGroup(boolean success, String error, Group[] groups);
    void onAddToGroup(boolean success, String error);
    void onDeleFrmGroup(boolean success, String error);
    void eventContactUser(Favorite[] fcu);
}
