package com.eggplant.qiezisocial.ui.login.fragment

import android.os.CountDownTimer
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import kotlinx.android.synthetic.main.ft_resend.*

/**
 * Created by Administrator on 2020/4/26.
 */

class ResendFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.ft_resend
    }

    var countTimer: CountDownTimer? = null
    override fun initView() {
        ft_resend_code.isClickable = false
        ft_resend_code.isFocusable = false
        startCountDown()
    }


    override fun initEvent() {
        ft_resend_phone_error.setOnClickListener {
            (activity as LoginActivity).reenterPhone()
        }
        ft_resend_code.setOnClickListener {
            (activity as LoginActivity).resendCode()
            ft_resend_code.isClickable = false
            ft_resend_code.isFocusable = false
            startCountDown()
        }
    }

    override fun initData() {

    }

    private fun startCountDown() {
        if (countTimer != null) {
            countTimer?.cancel()
        }
        countTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ft_resend_code?.isClickable = false
                ft_resend_code?.isFocusable = false
                ft_resend_code?.text = "重新发送(${Math.round(millisUntilFinished.toDouble() / 1000) - 1}s)"
            }

            override fun onFinish() {
                ft_resend_code?.isClickable = true
                ft_resend_code?.isFocusable = true
                ft_resend_code?.text = "重新发送"
            }
        }
        countTimer?.start()
    }

}
