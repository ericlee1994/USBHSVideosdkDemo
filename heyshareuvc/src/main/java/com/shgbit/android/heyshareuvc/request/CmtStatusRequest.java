package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/14.
 */

public class CmtStatusRequest extends BaseRequest {
    public CmtStatusRequest(Context context, String meetingId) {
        super(context, HttpMethod.GET, BaseConst.COMMENT_STATUS, meetingId);
    }
}
