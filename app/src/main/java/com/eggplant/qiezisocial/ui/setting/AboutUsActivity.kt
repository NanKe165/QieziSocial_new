package com.eggplant.qiezisocial.ui.setting

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_aboutus.*

/**
 * Created by Administrator on 2020/6/23.
 */

class AboutUsActivity : BaseActivity() {
    override fun getLayoutId(): Int {
      return R.layout.activity_aboutus
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initEvent() {
        about_us_bar.setTbListener(object :SimpBarListener(){
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }
}
