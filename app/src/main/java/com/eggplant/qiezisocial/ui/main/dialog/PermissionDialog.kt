package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.ui.setting.AgreementActivity
import com.eggplant.qiezisocial.widget.dialog.BaseDialog

/**
 * Created by Administrator on 2019/11/26.
 */

class PermissionDialog(context: Activity,listenedItems:IntArray?) : BaseDialog(context, R.layout.dialog_permission, listenedItems) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = window.attributes
        lp.width = display.width * 2 / 3
        window.attributes = lp
        val keylistener = DialogInterface.OnKeyListener { _, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0
        }
        setOnKeyListener(keylistener)
        setCancelable(false)
        setCanceledOnTouchOutside(false)


        val content = findViewById<TextView>(R.id.content)
        val style = SpannableStringBuilder()
        content.movementMethod = ScrollingMovementMethod.getInstance()
        //设置文字
        style.append("本个人信息保护指引将通过《服务协议》和《隐私政策》帮助你了解我们如何收集、处理个人信息\n\t" +
                "1.我们可能会申请系统设备权限收集国际移动设备识别码，以及收集其他设备信息和网络设备硬件地址、日志信息，用识别设备进行信息推送和安全风控，并申请存储权限，用于下载及缓存相关文件\n\t" +
                "2.我们可能会申请摄像头、麦克风、相册（存储）、定位等权限用于信息发布等。以上权限均不会默认或强制开启收集信息。你有权拒绝开启，拒绝权限不会影响App提供基本功能服务。\n\t" +

                "如果你同意，请点击“同意”开始接受我们的服务")

        //设置部分文字点击事件
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                context.startActivity(Intent(context, AgreementActivity::class.java).putExtra("from", "agreement"))
            }
        }
        style.setSpan(clickableSpan, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        content.text = style

        //设置部分文字颜色
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#6596fa"))
        style.setSpan(foregroundColorSpan, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        //设置部分文字点击事件
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                context.startActivity(Intent(context, AgreementActivity::class.java).putExtra("from", "privacy"))
            }
        }
        style.setSpan(clickableSpan2, 19, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        content.text = style

        //设置部分文字颜色
        val foregroundColorSpan2 = ForegroundColorSpan(Color.parseColor("#6596fa"))
        style.setSpan(foregroundColorSpan2, 19, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        //配置给TextView
        content.movementMethod = LinkMovementMethod.getInstance()
        content.text = style
    }
}
