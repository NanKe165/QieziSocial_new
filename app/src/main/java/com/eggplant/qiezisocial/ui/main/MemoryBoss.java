package com.eggplant.qiezisocial.ui.main;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

/**
 * Created by Administrator on 2020/12/10.
 */


public class MemoryBoss implements ComponentCallbacks2 {
    public boolean isAppWentToBg=false;
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // 进入后台
            isAppWentToBg=true;
        }
        // 如果有必要，你也可以进行一些清理内存操作
    }
}
