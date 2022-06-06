package com.eggplant.qiezisocial.ui.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.ui.WelcomeActivity;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

/**
 * Created by Administrator on 2022/1/6.
 */

public class MfrMessageActivity extends UmengNotifyClickActivity {

    private static final String TAG = "MfrMessageActivity1";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_notify);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.i(TAG, "bundle: " + bundle);
        }
        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, "body: " + body);
//        if (!TextUtils.isEmpty(body)) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ((TextView) findViewById(R.id.tv)).setText(body);
//                }
//            });
//
//        }
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        String topName = manager.getRunningTasks(1).get(0).topActivity.getClassName();
//        String baseName = manager.getRunningTasks(1).get(0).baseActivity.getClassName();
//        Log.i(TAG, "topName:" + topName + "  baseName:" + baseName);
//        List<ActivityManager.RunningTaskInfo> infoList= manager.getRunningTasks(10);
//        for (ActivityManager.RunningTaskInfo info:infoList){
//            Log.i(TAG, "id: " + info.id+"   "+info.numRunning+"  ");
//
//        }
//        Log.i(TAG, "size: " + infoList.size());
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }
}