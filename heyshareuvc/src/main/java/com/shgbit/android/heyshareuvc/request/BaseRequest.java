package com.shgbit.android.heyshareuvc.request;

import android.content.Context;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Eric on 2017/12/13.
 */

public class BaseRequest {
    private Context context;
    private HttpMethod httpMethod;
    private String function;
    public RequestParams params;
    private String url = "http://172.17.19.14:8560";
//    private String url = "http://172.17.16.140:8560";

    public BaseRequest(Context context, HttpMethod method, String function, String meetingId) {
        this.context = context;
        this.httpMethod = method;
        this.function = function;
        params = new RequestParams(url + function + meetingId);
    }

    public void httpSend(final IHttpCallback iHttpCallback){
        doRequest(this.httpMethod, iHttpCallback);
    }

    private void doRequest(HttpMethod method, final IHttpCallback callback) {
        x.http().request(method, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                callback.onSuccess(s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                callback.onFailure(throwable.toString());
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public interface IHttpCallback {
        void onSuccess(String result);
        void onFailure(String result);
    }
}
