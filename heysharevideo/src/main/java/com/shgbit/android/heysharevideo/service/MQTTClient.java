package com.shgbit.android.heysharevideo.service;


import com.shgbit.android.heysharevideo.callback.IMQTTCallBack;
import com.shgbit.android.heysharevideo.callback.IMqttCmtCallback;
import com.shgbit.android.heysharevideo.callback.IMqttCtrlDeviceCallback;
import com.shgbit.android.heysharevideo.callback.IMqttCtrlOperateCallback;
import com.shgbit.android.heysharevideo.callback.IMqttEventCallback;
import com.shgbit.android.heysharevideo.util.Common;
import com.shgbit.android.heysharevideo.util.GBLog;
import com.shgbit.android.heysharevideo.util.TOPIC;
import com.shgbit.android.heysharevideo.util.VCUtils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Created by Eric on 2017/12/8.
 */

public class MQTTClient{
    private static final String TAG = "MQTTClient";
    static MqttClient client;
    static MqttTopic topic;
//    String[] topics = {"mevent_" + mRecallMeeting.getId(), "mctrl_" + mRecallMeeting.getId(), "mcmt_" + mRecallMeeting.getId()};
    static int[] ints = {1, 1, 1, 1};
    private static IMQTTCallBack mimqttCallBack;
    private static IMqttEventCallback mEventCallBack;
    private static IMqttCtrlDeviceCallback mCtrlDeviceCallBack;
    private static IMqttCtrlOperateCallback mCtrlOperateCallBack;
    private static IMqttCmtCallback mCmtCallBack;
    private String serverIP = "tcp://116.62.221.80:1883";
    private static MQTTClient mInstance = null;
    public static MQTTClient getInstance() {
        if (mInstance == null) {
            mInstance = new MQTTClient();
        }
        return mInstance;
    }

    public static void release() {
        try {
            if (mInstance != null) {
                mInstance.closeMqtt();
                mInstance = null;
            }
        }catch (Throwable e) {
            GBLog.e(TAG, "release Throwable:" + VCUtils.CaughtException(e));
        }
    }

    public boolean connectMQTTServer (String id) {
        try {
            client = new MqttClient(serverIP, Common.USERNAME + Common.deviceType, null);
            client.setCallback(new CallBack());
            MqttConnectOptions conOptions = new MqttConnectOptions();
//            conOptions.setUserName(MyApplication.gUserName);
//            conOptions.setPassword(Pssword.toCharArray());
            conOptions.setCleanSession(false);
//            char[] ddd = conOptions.getPassword();
            client.connect(conOptions);
            String[] topics = {TOPIC.EVENT + id, TOPIC.CTRL_DEVICE + id, TOPIC.CTRL_OPERATE + id, TOPIC.CMT + id};
//            GBLog.e(TAG, topics[0]);
            client.subscribe(topics, ints);
        } catch (Throwable e) {
            GBLog.e(TAG, "connectMQTTServer Throwable:" + VCUtils.CaughtException(e));
            return false;
        }
        return true;
    }

    public void closeMqtt() {
        try {
            client.disconnect();
        } catch (Throwable e) {
            GBLog.e(TAG, "closeMqtt Throwable:" + VCUtils.CaughtException(e));
        }
    }

    public boolean publishMsg(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
//        message.setQos(Qos);
//        MqttDeliveryToken token;
        try {
//            token = topic.publish(message);
//            while (!token.isComplete()) {
//                token.waitForCompletion(1000);
//            }
            client.publish(topic, message);
        } catch (Throwable e) {
            GBLog.e(TAG, "publishMsg Throwable:" + VCUtils.CaughtException(e));
            return false;
        }
        return true;
    }
    private class CallBack implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            GBLog.e(TAG, "connectionLost:" + throwable.toString());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
//            mimqttCallBack.mqtt(topic, message.toString());
            parseMsg(topic, message.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            GBLog.i(TAG, "deliveryComplete:" + token.toString());
        }

    }

    private void parseMsg(String topic, String msg){
        try {
            GBLog.e(TAG, "messageArrived topic:" + topic + ", msg:" + msg);
            if (topic.contains(TOPIC.EVENT)) {
                if (mEventCallBack != null){
                    mEventCallBack.onEvent(msg);
                }
            } else if (topic.contains(TOPIC.CTRL_DEVICE)) {
                if (mCtrlDeviceCallBack != null){
                    mCtrlDeviceCallBack.onCtrlDevice(msg);
                }
            } else if (topic.contains(TOPIC.CTRL_OPERATE)) {
                if (mCtrlOperateCallBack != null){
                    mCtrlOperateCallBack.onCtrlOperate(msg);
                }
            } else if (topic.contains(TOPIC.CMT)) {
                if (mCmtCallBack != null){
                    mCmtCallBack.onCmt(msg);
                }
            }
        } catch (Throwable e){
            GBLog.e(TAG, "messageArrived parseMsg Throwable:" + VCUtils.CaughtException(e));
        }
    }

//    public void setImqttCallBack(IMQTTCallBack imqttCallBack){
//        mimqttCallBack = imqttCallBack;
//    }
    public void setIMqttEventCallBack(IMqttEventCallback eventCallBack){
        mEventCallBack = eventCallBack;
    }
    public void setIMqttCtrlDeviceCallback(IMqttCtrlDeviceCallback ctrlDeviceCallback){
        mCtrlDeviceCallBack = ctrlDeviceCallback;
    }
    public void setIMqttCtrlOperateCallback(IMqttCtrlOperateCallback ctrlOperateCallBack){
        mCtrlOperateCallBack = ctrlOperateCallBack;
    }
    public void setIMqttCmtCallback(IMqttCmtCallback cmtCallBack){
        mCmtCallBack = cmtCallBack;
    }
}
