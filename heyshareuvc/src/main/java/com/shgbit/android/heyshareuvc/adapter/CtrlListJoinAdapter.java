package com.shgbit.android.heyshareuvc.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.bean.CtrlCmd;
import com.shgbit.android.heysharevideo.bean.CtrlInfo;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.SESSIONTYPE;
import com.shgbit.android.heysharevideo.bean.StatusCtrl;
import com.shgbit.android.heysharevideo.callback.ICtrlBtnCallBack;
import com.shgbit.android.heysharevideo.contact.MeetingInfoManager;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.DeviceName;

import java.util.ArrayList;

import static com.shgbit.android.heysharevideo.activity.VideoActivity.mRecallMeeting;


/**
 * Created by Eric on 2018/1/12.
 */

public class CtrlListJoinAdapter extends BaseAdapter {

    private static final String TAG = "CtrlListAdapter";

    private ArrayList<MemberInfo> memberInfos;
    private ArrayList<MemberInfo> mShowList;
    private ArrayList<MemberInfo> mHideList;
    private Context mContext;
    private LayoutInflater mInflater;
    private StatusCtrl mStstusCtrl;
    private String mic;
    private String video;
	private String topic = "mctrl_" + mRecallMeeting.getId();
    private boolean isHost;

    private ICtrlBtnCallBack iCtrlBtnCallBack;

    public CtrlListJoinAdapter(Context mContext, ArrayList<MemberInfo> memberInfos, ArrayList<MemberInfo> mShowList, ArrayList<MemberInfo> mHideList,boolean isHost) {
        this.memberInfos = memberInfos;
        this.mShowList = mShowList;
	    this.mHideList = mHideList;
        this.mContext = mContext;
        this.isHost = isHost;
		mInflater = LayoutInflater.from(mContext);
    }

    public void update(ArrayList<MemberInfo> memberInfos, ArrayList<MemberInfo> mShowList, ArrayList<MemberInfo> mHideList,boolean isHost) {
        this.memberInfos = memberInfos;
        this.mShowList = mShowList;
	    this.mHideList = mHideList;
        this.isHost = isHost;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return memberInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return memberInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_ctrl_person, null);
            viewHolder = new ViewHolder();
            viewHolder.ctrl_icon = (ImageView) convertView.findViewById(R.id.ctrl_icon);
            viewHolder.mPersonName = (TextView) convertView.findViewById(R.id.txt_ctrl_name);
            viewHolder.btn_mic = (ImageView) convertView.findViewById(R.id.btn_ctrl_mic);
            viewHolder.btn_stream = (ImageView) convertView.findViewById(R.id.btn_ctrl_video);
            viewHolder.btn_speaker = (ImageView) convertView.findViewById(R.id.btn_ctrl_speaker);
            viewHolder.btn_camera = (ImageView) convertView.findViewById(R.id.btn_ctrl_camera);
            viewHolder.btn_exit = (ImageView) convertView.findViewById(R.id.btn_ctrl_exit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mPersonName.setTextSize(TypedValue.COMPLEX_UNIT_PX, Common.SCREENHEIGHT / 45);

        if (memberInfos.get(position).getSessionType().equals(SESSIONTYPE.MOBILE)) {
            viewHolder.ctrl_icon.setImageResource(R.drawable.ctrl_icon_mobile);
        } else if (memberInfos.get(position).getSessionType().equals(SESSIONTYPE.PC)) {
            viewHolder.ctrl_icon.setImageResource(R.drawable.ctrl_icon_pc);
        } else if (memberInfos.get(position).getSessionType().equals(SESSIONTYPE.CONTENTONLY)
                || memberInfos.get(position).getSessionType().equals(SESSIONTYPE.PC_CONTENT)) {
            viewHolder.ctrl_icon.setImageResource(R.drawable.ctrl_icon_content);
        }

        viewHolder.mPersonName.setText(memberInfos.get(position).getDisplayName());

        mStstusCtrl = memberInfos.get(position).getStatusCtrl();

        if(mStstusCtrl == null){
            if(memberInfos.get(position).getUserName().equals(Common.USERNAME)){
                viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_pre);
                viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera);
            }else {
                viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_pre_g);
                viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera_g);
            }
            viewHolder.btn_stream.setImageResource(R.drawable.ctrl_img_video);
            viewHolder.btn_exit.setImageResource(R.drawable.ctrl_img_exit);
        }else {
            if (mStstusCtrl.isMicMute()) {
                if (isHost) {
                    viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_pre);
                } else {
                    if (memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                        viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_pre);
                    } else {
                        viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_pre_g);
                    }
                }
                mic = "Mute";

            } else {
                if (isHost) {
                    viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic);
                } else {
                    if (memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                        viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic);
                    } else {
                        viewHolder.btn_mic.setImageResource(R.drawable.ctrl_img_mic_g);
                    }

                }
                mic = "Unmute";
            }
            if (!mStstusCtrl.isCameraMute()) {
                if (isHost) {
                    viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera);
                } else {
                    if (memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                        viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera);
                    } else {
                        viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera_g);
                    }
                }
                video = "Unmute";
            } else {
                if (isHost) {
                    viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera_pre);
                } else {
                    if (memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                        viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera_pre);
                    } else {
                        viewHolder.btn_camera.setImageResource(R.drawable.ctrl_img_camera_pre_g);
                    }
                }
                video = "Mute";
            }

            if (isHost) {
                viewHolder.btn_exit.setImageResource(R.drawable.ctrl_img_exit);
            } else {
                if (memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                    viewHolder.btn_exit.setImageResource(R.drawable.ctrl_img_exit);
                } else {
                    viewHolder.btn_exit.setImageResource(R.drawable.ctrl_img_exit_g);
                }
            }

            for (int i = 0; i < mShowList.size(); i++) {
                if (mShowList.get(i).equals(memberInfos.get(position))) {
                    viewHolder.btn_stream.setImageResource(R.drawable.ctrl_img_video);
                    break;
                } else {
                    viewHolder.btn_stream.setImageResource(R.drawable.ctrl_img_video_pre);
                }
            }
        }




        viewHolder.btn_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHost == true || memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                    iCtrlBtnCallBack.mqttMsg("Microphone", mic, memberInfos.get(position));
//                    MQTTClient.getInstance().publishMsg(TOPIC.CTRL_OPERATE + mRecallMeeting.getId(), getCommand("Microphone", mic, position));
                }
            }
        });
        viewHolder.btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHost == true || memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                    iCtrlBtnCallBack.mqttMsg("Camera", video, memberInfos.get(position));
//                    MQTTClient.getInstance().publishMsg(TOPIC.CTRL_OPERATE + mRecallMeeting.getId(), getCommand("Camera", video, position));
                }
            }
        });
        viewHolder.btn_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.btn_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingInfoManager.getInstance().PopUpDown(memberInfos.get(position));
            }
        });
        viewHolder.btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHost == true || memberInfos.get(position).getUserName().equals(Common.USERNAME)) {
                    iCtrlBtnCallBack.mqttMsg("Meeting", "Exit", memberInfos.get(position));
//                    MQTTClient.getInstance().publishMsg(TOPIC.CTRL_OPERATE + mRecallMeeting.getId(), getCommand("Meeting", "Exit", position));
                }
            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView ctrl_icon;
        TextView mPersonName;
        ImageView btn_mic;
        ImageView btn_speaker;
        ImageView btn_camera;
        ImageView btn_stream;
        ImageView btn_exit;
    }

    private String getCommand(String Object, String Operation, int position){
        String json = null;
        MemberInfo mi = memberInfos.get(position);
        ArrayList<String> mReceiverIDs = new ArrayList<>();
        ArrayList<CtrlCmd> mCommands = new ArrayList<>();

        CtrlCmd ctrlCmd = new CtrlCmd();
        ctrlCmd.setObject(Object);
        ctrlCmd.setOperation(Operation);
        CtrlInfo ctrlInfo = new CtrlInfo();
        ctrlInfo.setMeetingID(mRecallMeeting.getId());
        ctrlInfo.setSenderID(Common.USERNAME + Common.deviceType);

        mReceiverIDs.add(DeviceName.addSuffix(mi.getSessionType(), mi.getUserName()));
        ctrlInfo.setReceiverID(mReceiverIDs);

        mCommands.add(ctrlCmd);
        ctrlInfo.setCommand(mCommands);

        Gson gson = new Gson();
        json = gson.toJson(ctrlInfo);

        return json;
    }

    public void setiCtrlBtnCallBack(ICtrlBtnCallBack iCtrlBtnCallBack) {
        this.iCtrlBtnCallBack = iCtrlBtnCallBack;
    }
}
