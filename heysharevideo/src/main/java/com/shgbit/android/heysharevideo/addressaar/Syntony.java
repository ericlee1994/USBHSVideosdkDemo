package com.shgbit.android.heysharevideo.addressaar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.shgbit.android.heysharevideo.interactmanager.ServerInteractCallback;
import com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager;
import com.shgbit.android.heysharevideo.interactmanager.StructureDataCollector;
import com.shgbit.android.heysharevideo.json.CreateMeetingInfo;
import com.shgbit.android.heysharevideo.json.Favorite;
import com.shgbit.android.heysharevideo.json.FrequentContactsPost;
import com.shgbit.android.heysharevideo.json.Group;
import com.shgbit.android.heysharevideo.json.InviteCancledInfo;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.RefuseInfo;
import com.shgbit.android.heysharevideo.json.TimeoutInfo;
import com.shgbit.android.heysharevideo.json.User;
import com.shgbit.android.heysharevideo.json.UserOrganization;
import com.shgbit.android.heysharevideo.json.YunDesktop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/13 0013.
 */

public class Syntony {
    private final String TAG = "Syntony";
    private static Syntony mSyntony;
    private Context mContext;
    private int mBackgroundId;
    private int mBackgroundId2;
    private AllAddressListFragment mAllAddressListFrag;
    private AllAddressListFragment mMeetingAllAddressListFrag;
    private PersonalAddressListFragment mPersonalAddressListFrag;
    private PersonalMeetingFragment mPersonalMeetingFrag;
    private GroupFragment mGroupFrag;
    private boolean ismeeting;
    private String type;
    private boolean isReserve;
    private ArrayList<User> mRuser;
    private Group group;
    private String LoginName;
    private UserOrganization mPerson;
    private ArrayList<Favorite> mLocalFreq;
    private Favorite[] mFUsers;
    private String name;
    private Group mGroup;
    private String ScreenType;
    private List<UserOrganization> mSelectUsers;
    private boolean normal;
    private List<UserOrganization> mContactList;
    private String[] mUsers;
    private boolean ispersonal;

//    public static Syntony getInstance () {
//        if (mSyntony == null) {
//            mSyntony = new Syntony();
//        }
//        return mSyntony;
//    }

    public StructureDataCollector.ContactsUpdateListener mCUpdateListener = new StructureDataCollector.ContactsUpdateListener() {

        @Override
        public void onContactsUpdate() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(MESSAGE_1);
            }
        }

        @Override
        public void onGroupUpdate() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(MESSAGE_2);
            }
        }
    };

    private final int MESSAGE_1 = 0x005;
    private final int MESSAGE_2 = 0x006;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_1:
                    mFUsers = StructureDataCollector.getInstance().getContact();
                    break;
                case MESSAGE_2:
                    if(mGroupFrag != null){
                        mGroupFrag.setListener();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }};


    public void init(Context c, int id, int id2, String name){
        mContext = c;
        mBackgroundId = id;
        mBackgroundId2 = id2;
        LoginName = name;
        ServerInteractManager.getInstance().setServerInteractCallback(mInteractCallback);
        StructureDataCollector.getInstance().setContactsUpdateListener(mCUpdateListener);
    }

    private ServerInteractCallback mInteractCallback = new ServerInteractCallback(){

        @Override
        public void onLogin(boolean result, String error, User user) {

        }

        @Override
        public void onLogout(boolean result, String error) {

        }

        @Override
        public void onCheckPwd(boolean result, String error) {

        }

        @Override
        public void onMotifyPwd(boolean result, String error) {

        }

        @Override
        public void onStartYunDesk(boolean result, String error, YunDesktop yunDesktop) {

        }

        @Override
        public void onEndYunDesk(boolean result, String error, YunDesktop yunDesktop) {

        }

        @Override
        public void onCreateMeeting(boolean result, String error, Meeting meeting) {
            if(result){
                if(ispersonal){
                    mPersonalMeetingFrag.setCalling(meeting,true);
                }
            }else {
                mPersonalMeetingFrag.showToast();
            }
        }

        @Override
        public void onJoinMeeting(boolean result, String error) {

        }

        @Override
        public void onInviteMeeting(boolean success, String error) {

        }

        @Override
        public void onKickoutMeeting(boolean success, String error) {

        }

        @Override
        public void onQuiteMeeting(boolean success, String error) {

        }

        @Override
        public void onEndMeeting(boolean success, String error) {

        }

        @Override
        public void onReserveMeeting(boolean success, String error, Meeting meeting) {

        }

        @Override
        public void onDeleteMeeting(boolean success, String error) {

        }

        @Override
        public void onUpdateMeeting(boolean success, String error) {

        }

        @Override
        public void onBusyMeeting(boolean success, String error) {

        }

        @Override
        public void onMeetings() {

        }

        @Override
        public void eventUserStateChanged(RefuseInfo[] refuseInfos, TimeoutInfo[] timeoutInfos) {
            if (mPersonalMeetingFrag != null) {
                mPersonalMeetingFrag.setTimeOut(timeoutInfos);
            }
        }

        @Override
        public void eventInvitedMeeting(InvitedMeeting meeting) {

        }

        @Override
        public void eventStartYunDesk(YunDesktop yunDesktop) {

        }

        @Override
        public void eventEndYunDesk() {

        }

        @Override
        public void eventStartWhiteBoard() {

        }

        @Override
        public void eventEndWhiteBoard() {

        }

        @Override
        public void eventDifferentPlaceLogin() {

        }

        @Override
        public void eventInvitingCancle(InviteCancledInfo ici) {

        }

        @Override
        public void onValidate(boolean result, String err) {

        }
    };

    public void des () {
        Log.e(TAG, "############des");
        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().remove(mAllAddressListFrag).commit();
    }

    private void ChangeFragment(int index,Object object){
        FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
        if(index == 0){
            mAllAddressListFrag = new AllAddressListFragment();
            mAllAddressListFrag.setLayout(ismeeting,type,isReserve,mRuser,group);
            mAllAddressListFrag.setLoginName(LoginName);
            transaction.replace(mBackgroundId, mAllAddressListFrag).commit();
            mAllAddressListFrag.setInCallBack(mInCallBack);
        }else if(index == 1){
            mPersonalAddressListFrag = new PersonalAddressListFragment();
            mPersonalAddressListFrag.setOrgData((UserOrganization) object,LoginName,mInCallBack);
            transaction.add(mBackgroundId2, mPersonalAddressListFrag).addToBackStack(null).commit();
        }else if(index == 2){
            mPersonalMeetingFrag = new PersonalMeetingFragment();
            mPersonalMeetingFrag.setMeetingData((User[]) object, mPerson,LoginName,mInCallBack);
            transaction.add(mBackgroundId2, mPersonalMeetingFrag).addToBackStack(null).commit();
        }else if(index == 3){
            mGroupFrag = new GroupFragment();
            mGroupFrag.setGroupFrag(name,mGroup,mContactList,normal,mSelectUsers,ScreenType,LoginName,mInCallBack);
            transaction.add(mBackgroundId, mGroupFrag).addToBackStack(null).commit();
        }else if(index == 4){
            mMeetingAllAddressListFrag = new AllAddressListFragment();
            mMeetingAllAddressListFrag.setLayout(true, "vertical", false,null,(Group) object);
            transaction.add(mBackgroundId2, mMeetingAllAddressListFrag).addToBackStack(null).commit();
            mMeetingAllAddressListFrag.setInCallBack(mInCallBack);
            mMeetingAllAddressListFrag.setExCallBack(addressCallBack);
        }
    }

    public void startAddressList(boolean ismeeting, String type, boolean isReserve, ArrayList<User> mRuser, Group group){
        this.ismeeting =ismeeting;
        this.isReserve = isReserve;
        this.type = type;
        this.mRuser = mRuser;
        this.group = group;
        ChangeFragment(0,null);
    }

    public void setExCallBack(ExternalCallBack mExternalCallBack){
        if(mAllAddressListFrag != null){
            mAllAddressListFrag.setExCallBack(mExternalCallBack);
        }
    }

  private InternalCallBack mInCallBack = new InternalCallBack() {
      @Override
      public void onPersonalAddressFragment(UserOrganization pInformatinal) {
          if(pInformatinal == null){
              return;
          }
          mPerson = pInformatinal;
          ChangeFragment(1, pInformatinal);
      }


      @Override
      public void onPostContactsUser(Favorite mContacts, Favorite deleteFP) {
          FrequentContactsPost fp = new FrequentContactsPost();

          if (mLocalFreq == null) {
              mLocalFreq = new ArrayList<>();
          }
          mLocalFreq.clear();
          for (int i = 0; mFUsers!=null && i < mFUsers.length; i++) {
              mLocalFreq.add(mFUsers[i]);
          }

          if (mContacts != null) {
              mLocalFreq.add(mContacts);
              mFUsers = mLocalFreq.toArray(new Favorite[0]);
          }

          if (deleteFP != null) {
              for (int i = 0; i < mLocalFreq.size(); i++) {
                  if (mLocalFreq.get(i).getUserName().equals(deleteFP.getUserName())) {
                      mLocalFreq.remove(i);
                      i--;
                  }
              }
              mFUsers = mLocalFreq.toArray(new Favorite[0]);
          }
          Favorite[] favorite = mLocalFreq.toArray(new Favorite[0]);
          StructureDataCollector.getInstance().setContactUsers(favorite);

          fp.setFavorites(favorite);
          ServerInteractManager.getInstance().FrequentContacts(fp);
      }


      @Override
      public void onPersonalMeeting(User[] users, boolean isPerson) {

          if(users == null){
              return;
          }
          ChangeFragment(2,users);
      }

      @Override
      public void onGroupAddMember(Group group1) {
          ChangeFragment(4,group1);
      }

      @Override
      public void onGroupUsers() {
          if(mAllAddressListFrag != null){
              mAllAddressListFrag.setGroupSelect();
          }
      }

      @Override
      public void onUpdataGroup() {
          if (mAllAddressListFrag != null) {
              mAllAddressListFrag.setListener();
          }
      }

      @Override
      public void onDesFragment() {
          ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().remove(mAllAddressListFrag).commit();
      }

      @Override
      public void onInvitedUsers(User[] users, boolean isPersonal) {
          ispersonal = isPersonal;
          ArrayList arrayList = new ArrayList();
          String[] userName = new String[users.length];
          for(int i = 0; i < users.length; i++){
              userName[i] = users[i].getUserName();
              if (users[i].getStatus().equals("online")){
                  arrayList.add(users[i].getUserName());
                  mUsers = new String[arrayList.size()];
                  arrayList.toArray(mUsers);
              }
          }
          CreateMeetingInfo createMeetingInfo = new CreateMeetingInfo();
          createMeetingInfo.setCreatedUser(LoginName);
          createMeetingInfo.setInvitedUsers(userName);
          ServerInteractManager.getInstance().createMeeting(createMeetingInfo);
      }

      @Override
      public void onGroupFragment(Group group, List<UserOrganization> userOrganizations, boolean isMeeting, List<UserOrganization> selectUsers, String type) {
          if(group == null){
              name="contact";
          }else {
              name = null;
          }
          mGroup = group;
          mSelectUsers = selectUsers;
          normal = isMeeting;
          mContactList=userOrganizations;
          ScreenType = type;
          ChangeFragment(3,null);
      }
  };

    private AddressCallBack addressCallBack = new AddressCallBack() {
        @Override
        public void onReserveUsers(User[] users) {

        }

        @Override
        public void onInvitedUsers(User[] users, boolean isPerson) {
            ArrayList arrayList = new ArrayList();
            String[] userName = new String[users.length];
            for(int i = 0; i < users.length; i++){
                userName[i] = users[i].getUserName();
                if (users[i].getStatus().equals("online")){
                    arrayList.add(users[i].getUserName());
                    mUsers = new String[arrayList.size()];
                    arrayList.toArray(mUsers);
                }
            }
            CreateMeetingInfo createMeetingInfo = new CreateMeetingInfo();
            createMeetingInfo.setCreatedUser(LoginName);
            createMeetingInfo.setInvitedUsers(userName);
            ServerInteractManager.getInstance().createMeeting(createMeetingInfo);
        }
    };

    public void setGroupListener(){
        if(mGroupFrag != null){
            mGroupFrag.setListener();
        }
    }

    public void destroy(){
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_1);
            mHandler.removeMessages(MESSAGE_2);
            mHandler = null;
        }

        ServerInteractManager.getInstance().removeServerInteractCallback(mInteractCallback);
        FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
        transaction.remove(mAllAddressListFrag);
        transaction.remove(mPersonalAddressListFrag);
        transaction.remove(mPersonalMeetingFrag);
        transaction.remove(mMeetingAllAddressListFrag);
        transaction.remove(mGroupFrag);
        mContext = null;
        mPersonalMeetingFrag = null;
        mGroupFrag = null;
        mAllAddressListFrag = null;
        mMeetingAllAddressListFrag = null;
        mPersonalAddressListFrag = null;
        ServerInteractManager.getInstance().removeServerInteractCallback(mInteractCallback);

    }
}
