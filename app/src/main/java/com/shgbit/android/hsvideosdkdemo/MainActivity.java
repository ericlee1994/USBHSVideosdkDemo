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
import android.widget.TextView;

import com.shgbit.android.heysharevideo.activity.HSVideoSDK;
import com.shgbit.android.heysharevideo.callback.HSSDKInstantListener;
import com.shgbit.android.heysharevideo.callback.HSSDKListener;
import com.shgbit.android.heysharevideo.callback.HSSDKReserveListener;
import com.shgbit.android.heysharevideo.json.Meeting;
import com.shgbit.android.heysharevideo.json.User;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button button;
    private EditText editText;
    private boolean isInit = false;
    private String meetingNumber;
    private TextView tvContent;
    private Button btn_reserve;
    private Button btn_instant;
    private String userName = "lizheng";
    private String[] inviteUsers = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        button = findViewById(R.id.btn_intent);

        btn_reserve = findViewById(R.id.btn_reserve);
        btn_instant = findViewById(R.id.btn_instant);
        tvContent = findViewById(R.id.tv_content);
        editText = findViewById(R.id.meetingid);
        tvContent = findViewById(R.id.tv_content);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInit) {
                    if (editText.getText().toString().equals("") || editText.getText().toString() == null) {
                        meetingNumber = "910058905918";
                    } else {
                        meetingNumber = editText.getText().toString();
                    }
                    HSVideoSDK.getInstance().startMeeting(meetingNumber, "603918", "abc", "00010");
                } else {
                    meetingNumber = editText.getText().toString();
                }
            }
        });


        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSVideoSDK.getInstance().createReservationMeeting(userName, "新会议", "2018-03-23 14:00",
                        "2018-03-23 21:00", inviteUsers, new HSSDKReserveListener() {
                            @Override
                            public void onReserveMeeting(boolean success, String error, Meeting meeting) {
                                tvContent.setText(meeting.getMeetingId());
                                editText.setText(meeting.getMeetingId());
                            }
                        });
            }
        });

        btn_instant.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   HSVideoSDK.getInstance().startInstantMeeting(userName, inviteUsers, "0001", true, new HSSDKInstantListener() {
                       @Override
                       public void onCreateMeetng(boolean b, String s, Meeting meeting) {

                       }
                   });
               }
           });





        // http://121.43.162.79:4005"zhin
        // http://www.shgbitcloud.com:4000
        HSVideoSDK.getInstance().setSDKListener(new HSSDKListener() {
            @Override
            public void initState(boolean state) {
                isInit = state;
                HSVideoSDK.getInstance().connect("lizheng", "123456");
            }

            @Override
            public void connectState(boolean state, User user) {

            }

            @Override
            public void disconnectState(boolean state, String info) {

            }
        });
        HSVideoSDK.getInstance().init("http://www.shgbitcloud.com:4005", this);
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
