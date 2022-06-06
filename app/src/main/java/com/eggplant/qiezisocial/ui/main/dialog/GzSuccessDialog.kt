package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dlg_gz_success.*

/**
 * Created by Administrator on 2021/11/22.
 */

class GzSuccessDialog(context: Context, listenedItems: IntArray?) : BaseDialog(context, R.layout.dlg_gz_success, listenedItems) {
    var neverHint=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER)
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        setCanceledOnTouchOutside(false)
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        dlg_gz_close.setOnClickListener {
            dismiss()
        }
        dlg_gz_never_hint.setOnClickListener {
            neverHint=!neverHint
            if (neverHint){
                dlg_gz_never_hint.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.mipmap.icon_never_hint_select,0)
            }else{
                dlg_gz_never_hint.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.mipmap.icon_never_hint_unselect,0)
            }
        }
//        dlg_gz_sure.setOnClickListener {
//
//        }
    }

}
