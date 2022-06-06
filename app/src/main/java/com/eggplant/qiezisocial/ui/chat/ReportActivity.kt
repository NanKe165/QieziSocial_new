package com.eggplant.qiezisocial.ui.chat

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.model.DynamicModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_report.*

/**
 * Created by Administrator on 2021/12/16.
 */

class ReportActivity : BaseActivity() {
    val model: DynamicModel = DynamicModel()
    var id = 0L
    override fun getLayoutId(): Int {
        return R.layout.activity_report
    }

    override fun initView() {
        report_edit.isFocusable = true
        report_edit.isFocusableInTouchMode = true
        report_edit.requestFocus()
        id = intent.getLongExtra("id", 0L)
        if (id==0L)
            finish()
    }

    override fun initData() {

    }

    override fun initEvent() {
        report_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        report_sure.setOnClickListener {
            val reportTxt = report_edit.text.toString()
            if (reportTxt.isEmpty()) {
                TipsUtil.showToast(mContext, "请填写投诉内容")
                return@setOnClickListener
            }
            report(reportTxt)
        }
    }

    fun report(txt: String) {
        model.reportDynaimc(activity, "im", id.toInt(),txt,object :JsonCallback<BaseEntry<*>>(){
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful){
                    TipsUtil.showToast(mContext,response.body().msg!!)
                    if (response.body().stat=="ok"){
                        finish()
                    }
                }
            }
        })
    }

}
