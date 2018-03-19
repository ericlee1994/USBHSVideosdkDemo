package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;

import org.xutils.http.HttpMethod;

/**
 * Created by Eric on 2017/12/14.
 */

public class CmtTracksRequest extends BaseRequest {
    public CmtTracksRequest(Context context, String meetingId) {
        super(context, HttpMethod.GET, BaseConst.COMMENT_TRACKS, meetingId);
    }
}
