package com.shgbit.android.heysharevideo.interactmanager;


import com.shgbit.android.heysharevideo.json.HotFixConfig;
import com.shgbit.android.heysharevideo.json.PushConfig;
import com.shgbit.android.heysharevideo.json.XiaoYuConfig;

/**
 * Created by Administrator on 2018/2/13.
 */

public interface ServerConfigCallback {
    void configXiaoyu(XiaoYuConfig config);
    void configHotfix(HotFixConfig config);
    void configPush(PushConfig config);
}
