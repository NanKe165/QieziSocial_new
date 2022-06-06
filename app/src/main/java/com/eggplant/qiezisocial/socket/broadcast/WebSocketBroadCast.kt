package com.eggplant.qiezisocial.socket.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.TextUtils

/**
 * Created by Administrator on 2018/11/21.
 */

class WebSocketBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (TextUtils.equals("WEBS_BROAD", intent.action)) {
            val intent1 = Intent(context, AlarmReceiver::class.java)
            intent1.action = "arui.alarm.action"
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0 )
            val firstime = SystemClock.elapsedRealtime()
            val am = context
                    .getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //设置重复闹钟，时间间隔不固定，更节能,系统可能将几个差不多的闹钟合并为一个执行,减少唤醒次数
            // 5*60秒一个周期,不停的发送广播
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, (5 * 60 * 1000).toLong(), pendingIntent)
        }
    }
}
