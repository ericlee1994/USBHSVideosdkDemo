package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;
import com.shgbit.android.heysharevideo.util.Common;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/14.
 */

public class SyncRequest extends BaseRequest {
    public SyncRequest(Context context, String username, String meetingId) {
        super(context, HttpMethod.POST, BaseConst.SYNC, "");
        params.addBodyParameter("username", username + Common.deviceType);
        params.addBodyParameter("meeting_id", meetingId);
    }
}
