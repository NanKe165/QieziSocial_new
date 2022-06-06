package com.eggplant.qiezisocial.ui.login

import android.content.Context
import android.content.Intent
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.event.FinishEvent
import com.eggplant.qiezisocial.ui.login.fragment.LoginFragment
import com.eggplant.qiezisocial.ui.login.fragment.SetInfoFragment
import com.eggplant.qiezisocial.ui.main.MainActivity

/**
 * Created by Administrator on 2021/4/25.
 */

class LoginActivity2 : BaseActivity() {
    val loginFragment=LoginFragment()
    val setinfoFragment=SetInfoFragment()
    private var currentFragment: BaseFragment? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_login2
    }

    override fun initView() {
     var from=   intent.getStringExtra("from")
        if (from=="login"){
            setFragment2()
        }else {
            setFragment1()
        }
        val phone = intent.getStringExtra("phone")
        val head = intent.getStringExtra("head")
        if (phone!=null&&head!=null){
            loginFragment.setHeadAndPhone(head,phone)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    fun setFragment1() {
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().remove(currentFragment).commit()
        }
        currentFragment=loginFragment
        supportFragmentManager.beginTransaction().add(R.id.ft_login_gp,currentFragment).commit()
    }

    fun setFragment2() {
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().remove(currentFragment).commit()
        }
        currentFragment=setinfoFragment
        supportFragmentManager.beginTransaction().add(R.id.ft_login_gp, currentFragment).commit()
    }

    fun loginSuccess() {
        startActivity(Intent(mContext, MainActivity::class.java))
        finish()
    }

    fun goPubActivity() {
//        startActivity(Intent(mContext, PubTxtActivity::class.java).putExtra("from","login"))
//        overridePendingTransition(R.anim.open_enter,R.anim.open_exit)
        val notice = getSharedPreferences("permissionAgreement", Context.MODE_PRIVATE)
        val edit = notice.edit()
        edit.putBoolean("nextGoPub", true)
        edit.commit()
        startActivity(Intent(mContext, MainActivity::class.java).putExtra("fromRegister",true))
        finish()
    }

    override fun finishAllActivity(event: FinishEvent) {

    }
}
