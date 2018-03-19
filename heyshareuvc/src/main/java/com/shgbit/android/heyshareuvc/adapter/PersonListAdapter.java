package com.shgbit.android.heyshareuvc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shgbit.android.heysharevideo.R;
import com.shgbit.android.heysharevideo.bean.VI;
import com.shgbit.android.heysharevideo.util.Common;

import java.util.List;

/**
 * Created by Eric on 2017/6/20.
 */

public class PersonListAdapter extends BaseAdapter {

    private Context mContext;
    private List<VI> mData;
    private LayoutInflater mInflater;

    public PersonListAdapter(Context context, List<VI> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setmViewLayouts(List<VI> mViewLayouts) {
        mData = mViewLayouts;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_person_list, null);
            viewHolder = new ViewHolder();
            viewHolder.mPersonName = (TextView) view.findViewById(R.id.tv_person_name);
            viewHolder.mViedeoStatus = (ImageView) view.findViewById(R.id.img_video_status);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lv_item);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Common.SCREENWIDTH / 7 , ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(lp);

        if (mData.get(i).getDisplayName() != null) {
            if (mData.get(i).isContent()){
                viewHolder.mPersonName.setText(mData.get(i).getDisplayName() + "的电脑");
            }else {
                viewHolder.mPersonName.setText(mData.get(i).getDisplayName());
            }
            viewHolder.mPersonName.setTextSize(TypedValue.COMPLEX_UNIT_PX, Common.SCREENHEIGHT / 40);
        }

        LinearLayout.LayoutParams imgLP = new LinearLayout.LayoutParams(Common.SCREENWIDTH / 14, Common.SCREENHEIGHT / 19);
        imgLP.gravity = Gravity.CENTER_HORIZONTAL;
//        imgLP.topMargin = Common.mScreenHeight / 15;
        viewHolder.mViedeoStatus.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        viewHolder.mViedeoStatus.setLayoutParams(imgLP);

        LinearLayout.LayoutParams tvLP = new LinearLayout.LayoutParams(Common.SCREENWIDTH / 7, ViewGroup.LayoutParams.MATCH_PARENT);
        tvLP.gravity = Gravity.CENTER;
//        tvLP.bottomMargin = Common.mScreenHeight/15;

        viewHolder.mPersonName.setLayoutParams(tvLP);
        viewHolder.mPersonName.setMaxEms(3);
        viewHolder.mPersonName.setMaxLines(1);
        viewHolder.mPersonName.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.mPersonName.setTextSize(TypedValue.COMPLEX_UNIT_PX, Common.SCREENHEIGHT / 60);


//        linearLayout.addView(viewHolder.mPersonName);
//        linearLayout.addView(viewHolder.mViedeoStatus);

        if (mData.get(i).isContent()){
            viewHolder.mViedeoStatus.setImageResource(R.drawable.open_pc);
        }else {
            if (mData.get(i).getDataSourceID() == null || mData.get(i).getDataSourceID().equals("")) {
                viewHolder.mViedeoStatus.setImageResource(R.drawable.open_novideo);
            }else {
                viewHolder.mViedeoStatus.setImageResource(R.drawable.open_video);
            }
        }

        return view;
    }

    static class ViewHolder {
        ImageView mViedeoStatus;
        TextView mPersonName;
    }
}
