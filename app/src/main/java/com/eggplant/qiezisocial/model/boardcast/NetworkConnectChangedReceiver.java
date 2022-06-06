package com.eggplant.qiezisocial.model.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eggplant.qiezisocial.utils.NetWorkUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/11/14.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = NetWorkUtils.isConnected(context);
        EventBus.getDefault().post(new NetworkChangeEvent(connected));
    }
}
