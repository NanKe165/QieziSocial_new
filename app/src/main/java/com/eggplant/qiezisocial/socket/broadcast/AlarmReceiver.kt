package com.eggplant.qiezisocial.socket.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.eggplant.qiezisocial.socket.WebSocketService


/**
 * Created by Administrator on 2018/11/21.
 */

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "arui.alarm.action") {
            val i = Intent(context,WebSocketService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                i.putExtra("from","alarm")
                context.startForegroundService(i)
            } else {
                context.startService(i)
            }
        }
    }
}
