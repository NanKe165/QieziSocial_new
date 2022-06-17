package com.eggplant.qiezisocial.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.event.HomeMsgEvent;
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean;
import com.eggplant.qiezisocial.ui.chat.ChatActivity;
import com.eggplant.qiezisocial.ui.main.FriendActivity;
import com.eggplant.qiezisocial.ui.notify.EmptyActivity;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2018/12/12.
 */

public class NotifycationUtils {
    Context context;
    boolean isNotifyMsg, isAllowVoice, isShake, isVoice;
    private NotificationManager notificationManager;
    public static NotifycationUtils instance;

    public NotifycationUtils(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService
                (NOTIFICATION_SERVICE);
        //是否显示语音通知
        isAllowVoice = (boolean) StorageUtil.getParam(context, "allowVoice", true);

    }

    public static NotifycationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotifycationUtils(context);
        }
        return instance;
    }


    public void addChatMsgNotify(String title, MainInfoBean bean) {
        //是否显示聊天通知
        isNotifyMsg = (boolean) StorageUtil.getParam(context, "notifyMsg", true);
        //是否震动
        isShake = (boolean) StorageUtil.getParam(context, "shake", true);
        //是否有声音
        isVoice = (boolean) StorageUtil.getParam(context, "voice", true);

        if (!isNotifyMsg) {
            return;
        }


        String face = bean.getFace();
        String nick = bean.getNick();
        long created = bean.getCreated();
        if (TextUtils.isEmpty(nick)) {
            nick = "";
        }
        //设置点击事件
        Intent mintent = new Intent(context, ChatActivity.class);
        mintent.putExtra("bean", bean);
        long gsid = bean.getGsid();
        long qsid = bean.getQsid();
        String qsTxt = bean.getQsTxt();
        if ((qsid!=0||gsid!=0)&&!TextUtils.isEmpty(qsTxt)){
            mintent.putExtra("qs",qsTxt);
        }
//        if (TextUtils.equals("ganswerlist", bean.getType())) {
//            mintent = new Intent(context, AnswerActivity.class);
//            mintent.putExtra("uid", bean.getUid() + "");
//            return;
//        }


        int requestID = (int) System.currentTimeMillis();

        PendingIntent pintent = PendingIntent.getActivity(context, requestID, mintent, PendingIntent.FLAG_UPDATE_CURRENT);
        EventBus.getDefault().post(new HomeMsgEvent(title, bean));

        String id = "my_channel_01";
        String name = "msg_channel";
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(nick)
                    .setContentText(title)
                    .setSmallIcon(R.mipmap.box_ic)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pintent)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(nick)
                    .setContentText(title)
                    .setSmallIcon(R.mipmap.box_ic)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pintent)
                    .setChannelId(id);//无效
            notification = notificationBuilder.build();
        }
        notificationManager.notify("msg", 1, notification);

    }
    public void systemNotify(String title,String type){
        //是否震动
        isShake = (boolean) StorageUtil.getParam(context, "shake", true);
        //是否有声音
        isVoice = (boolean) StorageUtil.getParam(context, "voice", true);

        Intent mintent=new Intent(context, EmptyActivity.class);
        //secretlove
        if (type=="apply"){
            mintent=  new Intent(context, FriendActivity.class);
            mintent.putExtra("from","apply");
        }else {

        }
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pintent = PendingIntent.getActivity(context, requestID, mintent, PendingIntent.FLAG_UPDATE_CURRENT);
        String id = "my_channel_01";
        String name = "msg_channel";
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(title)
//                    .setContentText(title)
                    .setSmallIcon(R.mipmap.box_ic)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pintent)
                    .build();


            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
//                    .setContentText(title)
                    .setSmallIcon(R.mipmap.box_ic)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pintent)
                    .setChannelId(id);//无效
            notification = notificationBuilder.build();
        }
        notificationManager.notify("msg", 2, notification);

    }

    public void cancelNotify(int id) {
        notificationManager.cancel("msg", id);
    }


}
