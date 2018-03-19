package com.shgbit.android.heyshareuvc.request;


import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;
import com.shgbit.android.heysharevideo.util.Common;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/14.
 */

public class CmtEnterRequest extends BaseRequest {
    public CmtEnterRequest(Context context, String meetingId, String Pizhuid) {
        super(context, HttpMethod.POST, BaseConst.COMMENT_ENTER, "");
        params.addBodyParameter("meeting_id", meetingId);
        params.addBodyParameter("username", Common.USERNAME + Common.deviceType);
        params.addBodyParameter("resource", Pizhuid);
    }
}
