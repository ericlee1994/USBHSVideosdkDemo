package com.shgbit.android.hsvideosdkdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shgbit.android.heysharevideo.activity.HSVideoSDK;
import com.shgbit.android.heysharevideo.callback.HSSDKListener;
import com.shgbit.android.heysharevideo.json.InvitedMeeting;
import com.shgbit.android.heysharevideo.util.GBLog;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button button;
    private Button btn_addr;
    private EditText editText;
    private boolean isInit = false;
    private String meetingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_intent);
        btn_addr = findViewById(R.id.btn_address);
        btn_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HSVideoSDK.getInstance().openAddress();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInit) {
                    if (editText.getText().toString().equals("") || editText.getText().toString() == null){
                        meetingNumber = "910091945713";
                    }else {
                        meetingNumber = editText.getText().toString();
                    }
                    HSVideoSDK.getInstance().startMeeting(meetingNumber, "603918");
                }
            }
        });

        editText = findViewById(R.id.meetingid);

        HSVideoSDK.getInstance().init("http://www.shgbitcloud.com:4005", "lizheng", this,
                new HSSDKListener() {
                    @Override
                    public void initState(boolean b) {
                        HSVideoSDK.getInstance().connect("lizheng", "123456");
                    }

                    @Override
                    public void connectState(boolean b) {
                        isInit = b;
                    }

                    @Override
                    public void disconnectState(boolean b) {
                        GBLog.e(TAG, "disconnectState:" +  b);
                    }

                    @Override
                    public void inviteMeeting(InvitedMeeting invitedMeeting) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HSVideoSDK.getInstance().finalizeSDK();
    }
}
