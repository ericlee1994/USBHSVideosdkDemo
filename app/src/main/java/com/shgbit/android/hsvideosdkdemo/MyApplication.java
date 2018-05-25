package com.shgbit.android.hsvideosdkdemo;

import android.app.Application;
import android.provider.SyncStateContract;

import com.shgbit.android.heysharevideo.activity.HSVideoSDK;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Eric on 2018/3/9.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    private RefWatcher mRefWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        HSVideoSDK.getInstance().init("http://www.shgbitcloud.com:4005", this);
    }
}
