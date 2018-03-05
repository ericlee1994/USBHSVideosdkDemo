package com.shgbit.android.heysharevideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.activity.VideoActivity;
import com.shgbit.android.heysharevideo.bean.MemberInfo;
import com.shgbit.android.heysharevideo.bean.STATUS;
import com.shgbit.android.heysharevideo.contact.MeetingInfoManager;

import java.util.ArrayList;


/**
 * Created by Eric on 2018/1/16.
 */

public class CtrlListUnjoinAdapter extends BaseAdapter{

    private static final String TAG = "CtrlListUnjoinAdapter";

    private ArrayList<MemberInfo> memberInfos;
    private Context mContext;
    private LayoutInflater mInflater;

    public CtrlListUnjoinAdapter (ArrayList<MemberInfo> memberInfos, Context mContext) {
        this.memberInfos = memberInfos;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public void update(ArrayList<MemberInfo> memberInfos) {
        this.memberInfos = memberInfos;
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
            convertView = mInflater.inflate(R.layout.item_ctrl_unjoin, null);
            viewHolder = new ViewHolder();
            viewHolder.mPersonStatus = (ImageView) convertView.findViewById(R.id.img_ctrl_status);
            viewHolder.mPersonName = (TextView) convertView.findViewById(R.id.txt_ctrl_unjoinname);
            viewHolder.btn_invite = (ImageView) convertView.findViewById(R.id.btn_ctrl_invite);
            viewHolder.btn_msg = (ImageView) convertView.findViewById(R.id.btn_ctrl_msg);
            viewHolder.btn_delete = (ImageView) convertView.findViewById(R.id.btn_ctrl_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (memberInfos.get(position).getStatus().equals(STATUS.INVITING)){
            viewHolder.btn_invite.setImageResource(R.drawable.ctrl_img_inviting);
        }else {
            viewHolder.btn_invite.setImageResource(R.drawable.ctrl_img_invite);
        }

//        if (StructureDataCollector.getInstance().getUserStatus(memberInfos.get(position).getUserName()).equals("online")) {
//            viewHolder.mPersonStatus.setImageResource(R.drawable.icon_online);
//        }else if (StructureDataCollector.getInstance().getUserStatus(memberInfos.get(position).getUserName()).equals("busy")) {
//            viewHolder.mPersonStatus.setImageResource(R.drawable.icon_busy);
//        }else if (StructureDataCollector.getInstance().getUserStatus(memberInfos.get(position).getUserName()).equals("offline")) {
//            viewHolder.mPersonStatus.setImageResource(R.drawable.icon_offline);
//        }

        viewHolder.mPersonName.setText(memberInfos.get(position).getDisplayName());
        viewHolder.btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] user = {memberInfos.get(position).getId()};

                if (memberInfos.get(position).getStatus().equals(STATUS.INVITING)){
                    VideoActivity.cancelInvite(user);
                    MeetingInfoManager.getInstance().StateChange(user, STATUS.WAITING);
                }else {
                    VideoActivity.reInvited(user);
                    MeetingInfoManager.getInstance().StateChange(user, STATUS.INVITING);
                }
            }
        });

        viewHolder.btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] user = {memberInfos.get(position).getId()};
                VideoActivity.sendMsg(user);
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] user = {memberInfos.get(position).getId()};
                VideoActivity.kickPerson(user);
            }
        });

        return convertView;
    }

     public class ViewHolder {
        ImageView mPersonStatus;
        TextView mPersonName;
        ImageView btn_invite;
        ImageView btn_msg;
        ImageView btn_delete;
    }
}
