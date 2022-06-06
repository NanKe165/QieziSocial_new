package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_normal.*

/**
 * Created by Administrator on 2021/7/6.
 */

class NormalDialog(context: Context, listenedItems: IntArray) : BaseDialog(context, R.layout.dialog_normal, listenedItems) {
    var mode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER)
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        setCanceledOnTouchOutside(false)// 点击Dialog外部消失


    }

    override fun show() {
        super.show()
        when (mode) {
            1 -> {
                dlg_normal_title.text="删除好友"
                dlg_normal_content.text = "删除后将清空该聊天的消息记录"
            }
            2 -> {
                dlg_normal_title.text="删除聊天记录"
                dlg_normal_content.text = "确定要删除的消息记录"
            }
            3 -> {
                dlg_normal_title.text="拉黑"
                dlg_normal_content.text = "拉黑后将不再接收对方消息"
            }
            4 -> {
                dlg_normal_title.text="拉黑"
                dlg_normal_content.text = "拉黑后将不再接收对方消息"
            }
            5 -> {
                dlg_normal_title.text="清除聊天记录"
                dlg_normal_content.text = "清除收件箱及好友的消息记录"
            }
            6->{
                dlg_normal_title.text="退出登录"
                dlg_normal_content.text = "退出登录后无法收到别人的信息是否继续"
            }
            7->{
                dlg_normal_title.text="注销账号"
                dlg_normal_content.text = getContext().getString(R.string.close_user)
            }
        }
    }
}
