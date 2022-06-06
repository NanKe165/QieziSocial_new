package com.eggplant.qiezisocial.ui.login.fragment

import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import kotlinx.android.synthetic.main.ft_select_interest.*

/**
 * Created by Administrator on 2020/4/26.
 */

class SelectInterestFragment : BaseFragment() {
    var selectViews = ArrayList<QzTextView>()
    var selectInsterest: StringBuffer = StringBuffer()
    override fun getLayoutId(): Int {
        return R.layout.ft_select_interest
    }

    override fun initView() {

    }

    override fun initEvent() {
        ft_select_interest_sure.setOnClickListener {
            setInterest()
        }
        ft_select_interest_wordview.setOnLabelClickListener { v ->
            selectView(v)
        }
    }


    override fun initData() {
        var data = application.loginEntry?.interest
        if (data != null) {
            ft_select_interest_wordview.setData(data)
        }
    }

    private fun setInterest() {
        if (selectViews.size == 0) {
            TipsUtil.showToast(mContext, "还没有选择标签呢！")
            return
        } else {
            selectViews.forEach {
                selectInsterest.append("${it.text} ")
            }
            (activity as LoginActivity).setInterest(selectInsterest.toString().trimEnd())
        }
    }

    private fun selectView(v: View) {
        when {
            selectViews.contains(v) -> {
                v.setBackgroundResource(R.drawable.login_label_unselect)
                selectViews.remove(v)
            }
            selectViews.size < 4 -> {
                v.setBackgroundResource(R.drawable.login_label_select)
                selectViews.add(v as QzTextView)
            }
            else -> {
                TipsUtil.showToast(mContext, "最多只能选择四条哦！")
                ft_select_interest_hint.text = "(${selectViews.size}/4)"
            }

        }
        ft_select_interest_hint.text = "(${selectViews.size}/4)"
    }

}
