package com.shgbit.android.heysharevideo.thread;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by Eric on 2018/3/19.
 */

public class MyAsyncTask extends AsyncTask<URL, Integer, Long> {
    @Override
    protected Long doInBackground(URL... urls) {
        int count = urls.length;
        long totalSize = 0;
        return null;
    }
}
