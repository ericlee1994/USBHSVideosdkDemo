package com.shgbit.android.hsvideosdkdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shgbit.android.heysharevideo.activity.HSVideoSDK;
import com.shgbit.android.heysharevideo.callback.HSSDKListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HSVideoSDK.getInstance().init("http://www.shgbitcloud.com:4005", "lizheng", this,
                new HSSDKListener() {
            @Override
            public void initState(boolean b) {
                HSVideoSDK.getInstance().connect("lizheng","123456");
            }

            @Override
            public void connectState(boolean b) {
                HSVideoSDK.getInstance().startMeeting("910069414403", "603918");
            }

            @Override
            public void disconnectState(boolean b) {

            }
        });
//        HSVideoSDK.getInstance().setSDKListener(new HSSDKListener() {
//            @Override
//            public void initState(boolean b) {
//
//            }
//
//            @Override
//            public void connectState(boolean b) {
//
//            }
//
//            @Override
//            public void disconnectState(boolean b) {
//
//            }
//        });
//        HSVideoSDK.getInstance().connect("lizheng","123456");
//        HSVideoSDK.getInstance().startMeeting("910010569941", "603918");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HSVideoSDK.getInstance().finalizeSDK();
    }
}
