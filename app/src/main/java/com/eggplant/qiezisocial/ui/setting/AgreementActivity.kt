package com.eggplant.qiezisocial.ui.setting

import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_agreement.*

/**
 * Created by Administrator on 2019/4/4.
 */

class AgreementActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_agreement
    }

    override fun initView() {
        val from = intent.getStringExtra("from")
        if (TextUtils.equals(from, "privacy")) {
            agree_bar.setTitle("隐私政策")
            agree_txt.setText(R.string.privacy)
        } else if (TextUtils.equals(from, "agreement")) {
            agree_bar.setTitle("用户协议")
            agree_txt.setText(R.string.agreement)
        }
        agree_txt.setMovementMethod(ScrollingMovementMethod.getInstance())

    }

    override fun initData() {

    }

    override fun initEvent() {
        agree_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                activity.finish()
            }
        })
    }


}
