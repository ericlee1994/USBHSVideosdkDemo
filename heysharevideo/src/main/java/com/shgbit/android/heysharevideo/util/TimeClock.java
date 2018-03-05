package com.shgbit.android.heysharevideo.util;

import android.os.Handler;

/**
 * Created by Eric on 2017/6/20.
 */

public class TimeClock {
    int timeSecond = 0;
    Handler handler = new Handler();
    ISplitTime iSplitTime;
    int one = 0;
    int two = 0;
    int three = 0;
    int four = 0;
    private static final String TAG = "TimeClock";

    public void setiSplitTime(ISplitTime iSplitTime) {
        this.iSplitTime = iSplitTime;
    }

    public TimeClock() {

    }

    public void startTimer() {

        splitTime(timeSecond/60);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 60 * 1000);
                timeSecond += 60;
                splitTime(timeSecond/60);
            }
        };
        handler.postDelayed(runnable, 60 * 1000);
    }

    public void splitTime(int minutes) {
//        if (minutes >= 100) {
//            hundred = minutes / 100;
//            ten = (minutes - (100 * hundred)) / 10;
//            unit = minutes - (100 * hundred + 10 * ten);
//        } else if (minutes < 100 || minutes >= 10) {
//            unit = minutes % 10;
//            ten = minutes / 10;
//            hundred = 0;
//        } else {
//            ten = 0;
//            hundred = 0;
//            unit = minutes;
//        }
//        if (minutes >= 600) {
            one = minutes / 600;
            two = minutes / 60 - (one * 10);
            three = (minutes - (10 * one + two) * 60) / 10;
            four = minutes - (10 * one + two) * 60 - 10 * three;
//        }
        iSplitTime.returnTime(one, two, three, four);
    }

    public interface ISplitTime {
        void returnTime(int firstHour, int SecondHour, int firstMin, int SecondMin);
    }

    public void setTimeSecond(int timeSecond) {
        this.timeSecond = timeSecond;
    }
}
