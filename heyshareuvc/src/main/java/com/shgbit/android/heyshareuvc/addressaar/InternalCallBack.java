package com.shgbit.android.heyshareuvc.addressaar;


import com.shgbit.android.heysharevideo.json.Favorite;
import com.shgbit.android.heysharevideo.json.Group;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.UserOrganization;

import java.util.List;

/**
 * Created by Administrator on 2018/2/13 0013.
 */

public interface InternalCallBack {
    void onPersonalAddressFragment(UserOrganization pInformatinal);
    void onPostContactsUser(Favorite mContacts, Favorite deleteFP);
    void onPersonalMeeting(User[] users, boolean isPerson);
    void onGroupAddMember(Group group1);
    void onGroupUsers();
    void onUpdataGroup();
    void onInvitedUsers(User[] users, boolean isPersonal);
    void onGroupFragment(Group group, List<UserOrganization> userOrganizations, boolean isMeeting, List<UserOrganization> selectUsers, String type);
}
