package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.text.TextUtils
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_inputbox.*

/**
 * Created by Administrator on 2020/10/27.
 */

class InputboxActivity : BaseActivity(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    override fun initView() {

    }

    override fun initData() {

        var tousername = intent.getStringExtra("tousername")
        if (tousername != null && tousername.isNotEmpty()) {
            inputbox_keyboard.edit.hint = "回复：$tousername"
        }
    }

    override fun initEvent() {
        inputbox_rootview.setOnClickListener {
            finish()
        }
        inputbox_keyboard.setOnSendclickListneer { str ->
            if (!TextUtils.isEmpty(str)) {
                intent.putExtra("txt", str)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun expressionClick(str: String?) {
        inputbox_keyboard.input(inputbox_keyboard.edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        inputbox_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        inputbox_keyboard.delete(inputbox_keyboard.edit)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_inputbox
    }

    override fun onPause() {
        super.onPause()
        inputbox_keyboard.reset()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }

}
