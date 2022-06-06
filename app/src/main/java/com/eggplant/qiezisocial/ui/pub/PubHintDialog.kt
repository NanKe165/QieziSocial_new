package com.eggplant.qiezisocial.ui.pub

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.eggplant.qiezisocial.QzApplication

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_pubhint.*

/**
 * Created by Administrator on 2020/12/7.
 */

class PubHintDialog(context: Context, listenedItems: IntArray?) : BaseDialog(context, R.layout.dialog_pubhint, listenedItems) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER) // 此处可以设置dialog显示的位置为底部
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        dlg_pubhint_close.setOnClickListener {
            dismiss()
        }
        dlg_pubhint_sure.setOnClickListener {
            dismiss()
        }
    }

    override fun show() {
        super.show()
        resetNumb()
    }

    private fun resetNumb() {
        var entry = QzApplication.get().loginEntry
        if (entry != null) {
            if (entry.chance > 0) {
                dlg_pubhint_txt.text = "您一天内有5次掷骰子得机会次日凌晨更新"
            } else {
                dlg_pubhint_txt.text = "您今天的掷骰子次数已经用完啦，亲自编辑有趣的内容会更受欢迎哦"
            }
        } else {
            dlg_pubhint_txt.text = "您今天的掷骰子次数已经用完啦，亲自编辑有趣的内容会更受欢迎哦"
        }

    }
}
