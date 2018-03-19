package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import com.shgbit.android.heysharevideo.util.BaseConst;
import com.shgbit.android.heysharevideo.util.Common;

import org.xutils.http.HttpMethod;

import java.io.File;

/**
 * Created by Eric on 2017/12/14.
 */

public class PicUpRequest extends BaseRequest {
    public PicUpRequest(Context context, String username, String meetingId, String imgPath) {
        super(context, HttpMethod.POST, BaseConst.UPLOAD, "");
        params.addHeader("meetingid", meetingId);
        params.addHeader("username", username + Common.deviceType);
        params.setMultipart(true);
        params.addBodyParameter("imageFile", new File(imgPath), "multipart/form-data");
    }
}
