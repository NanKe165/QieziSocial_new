package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_gchat_func.*

/**
 * Created by Administrator on 2021/7/23.
 */

class GchatFuncDialog<T>(context: Context, listenedItems: IntArray) : BaseDialog(context, R.layout.dialog_gchat_func, listenedItems) {
    var downloadFile=""
    var fileType=""
    var postion=-1
    var bean:T?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.BOTTOM) // 此处可以设置dialog显示的位置为底部
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        dlg_gchat_cancel.setOnClickListener {
            dismiss()
        }
    }
}
