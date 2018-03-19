package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/13.
 */

public class PicDownRequest extends BaseRequest {
    public PicDownRequest(Context context, String meetingId) {
        super(context, HttpMethod.GET, BaseConst.DOWNLOAD, meetingId);
    }
}
