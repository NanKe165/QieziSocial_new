package com.eggplant.qiezisocial.ui.setting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.layout_logout.*

/**
 * Created by Administrator on 2020/11/12.
 */

class LogoutDialog(context: Context,listenedItems:IntArray?) : BaseDialog(context, R.layout.layout_logout, listenedItems){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER)
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        dlg_logout_close.setOnClickListener {
            dismiss()
        }
        dlg_logout_cancel.setOnClickListener {
            dismiss()
        }
    }
}
