package com.eggplant.qiezisocial.ui.login.fragment

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import kotlinx.android.synthetic.main.ft_select_sex.*

/**
 * Created by Administrator on 2020/4/26.
 */

class SelectSexFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.ft_select_sex
    }

    override fun initView() {

    }

    override fun initEvent() {
        ft_select_sex_boy.setOnClickListener {
            (activity as LoginActivity).setSex("男")
        }
        ft_select_sex_girl.setOnClickListener {
            (activity as LoginActivity).setSex("女")
        }
    }

    override fun initData() {

    }
}
