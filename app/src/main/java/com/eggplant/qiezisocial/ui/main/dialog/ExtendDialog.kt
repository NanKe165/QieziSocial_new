package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog

/**
 * Created by Administrator on 2020/7/7.
 * 扩列-   缘分猜猜猜
 */

class ExtendDialog(context: Context, listenedItems: IntArray) : BaseDialog(context, R.layout.dialog_extend, listenedItems) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.BOTTOM) // 此处可以设置dialog显示的位置为底部
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp

        findViewById<View>(R.id.rootView).setOnTouchListener(object : View.OnTouchListener {
            internal var startX = 0f
            internal var startY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        startY = event.y
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val x = event.x
                        val y = event.y
                        if (Math.abs(x - startX) < Math.abs(y - startY) && y - startY > 200) {
                            dismiss()
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onClick(view: View) {
        super.onClick(view)
    }
}
