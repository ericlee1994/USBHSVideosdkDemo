package com.shgbit.android.hsvideosdkdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private EditText editText;
    private boolean isInit = false;
    private String meetingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_intent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInit) {
                    if (editText.getText().toString().equals("") || editText.getText().toString() == null){
                        meetingNumber = "910031515193";
                    }else {
                        meetingNumber = editText.getText().toString();
                    }
                    HSVideoSDK.getInstance().startMeeting(meetingNumber, "603918");
                }
            }
        });

        editText = findViewById(R.id.meetingid);
        // http://121.43.162.79:4005"
        // http://www.shgbitcloud.com:4000
        HSVideoSDK.getInstance().init("http://121.43.162.79:4005", "lizheng", this,
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

        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HSVideoSDK.getInstance().finalizeSDK();
    }

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 0);
        }
    }
}
