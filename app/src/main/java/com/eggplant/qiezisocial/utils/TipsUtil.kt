package com.eggplant.qiezisocial.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by Administrator on 2017/12/25.
 */
object TipsUtil {
    //    public static void showToast(Context context, String msg){
    //        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    //    }


    /** 之前显示的内容  */
    private var oldMsg: String? = null
    /** Toast对象  */
    private var toast: Toast? = null
    /** 第一次时间  */
    private var oneTime: Long = 0
    /** 第二次时间  */
    private var twoTime: Long = 0

    /**
     * 显示Toast
     * @param context
     * @param message
     */
    fun showToast(context: Context?, message: String) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast!!.show()
            oneTime = System.currentTimeMillis()
        } else {
            twoTime = System.currentTimeMillis()
            if (message == oldMsg) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast!!.show()
                }
            } else {
                oldMsg = message
                toast!!.setText(message)
                toast!!.show()
            }
        }
        oneTime = twoTime
    }
}
