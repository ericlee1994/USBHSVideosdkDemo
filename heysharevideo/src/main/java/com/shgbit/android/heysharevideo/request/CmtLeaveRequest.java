package com.shgbit.android.heysharevideo.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;
import com.shgbit.android.heysharevideo.util.Common;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/14.
 */

public class CmtLeaveRequest extends BaseRequest {
    public CmtLeaveRequest(Context context, String meetingId, String Pizhuid) {
        super(context, HttpMethod.POST, BaseConst.COMMENT_LEAVE, "");
        params.addBodyParameter("meeting_id", meetingId);
        params.addBodyParameter("username", Common.USERNAME + Common.deviceType);
        params.addBodyParameter("resource", Pizhuid);
    }
}
