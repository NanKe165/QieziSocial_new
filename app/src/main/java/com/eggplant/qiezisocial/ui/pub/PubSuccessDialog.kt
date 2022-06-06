package com.eggplant.qiezisocial.ui.pub

import android.content.Context
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import java.util.*

/**
 * Created by Administrator on 2020/6/19.
 */

class PubSuccessDialog(context: Context) : BaseDialog(context, R.layout.dialog_pub_success, intArrayOf(R.id.pub_success_close)) {


    override fun onClick(view: View) {
        super.onClick(view)
        if (view.id == R.id.pub_success_close) {
            dismiss()
        }
    }

    override fun show() {
        super.show()
        var timer = Timer()
        var task = object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }
        timer.schedule(task, 5000L)
    }
}
