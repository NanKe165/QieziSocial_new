package com.eggplant.qiezisocial.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.LoginContract
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.LoginMsgEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.presenter.LoginPresenter
import com.eggplant.qiezisocial.ui.login.fragment.*
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import com.luck.picture.lib.tools.PictureFileUtils
import kotlinx.android.synthetic.main.activity_login.*
import sj.keyboard.utils.EmoticonsKeyboardUtils
import java.lang.ref.WeakReference


/**
 * Created by Administrator on 2020/4/9.
 * 登录
 */

class LoginActivity : BaseMvpActivity<LoginPresenter>(), LoginContract.view {

    private var handler: Handler = WithoutLeakHandler(this)

    companion object {
        const val SEND_DATA: Int = 100

        /**
         * desc: 解决handler内存泄漏的问题，消息的处理需要放在内部类的{@link #Handler.handleMessage}
         */
        private class WithoutLeakHandler(activity: LoginActivity) : Handler() {
            private var activity: WeakReference<LoginActivity> = WeakReference(activity)
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    SEND_DATA -> {
                        var serviceData = msg.data.getSerializable("bean") as ChatMultiEntry<LoginMsgEntry>
                        val activity = activity.get()
                        activity?.adapter?.addData(serviceData)
                        activity?.scrollToBottom()
                    }
                }
            }
        }
    }


    lateinit var adapter: LoginAdapter
    private var currentFragment: BaseFragment? = null
    override fun initPresenter(): LoginPresenter {
        return LoginPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = LoginAdapter(mContext, null)
        login_ry.layoutManager = LinearLayoutManager(mContext)
        login_ry.adapter = adapter
        login_ft.visibility = View.GONE
    }

    override fun initData() {
        var user = intent.getSerializableExtra("bean")
        mPresenter.initData(mContext, user?.let { it as UserEntry })
    }

    override fun initEvent() {
        login_ry.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                scrollToBottom()
            }
        }
        login_ry.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP)
                EmoticonsKeyboardUtils.closeSoftKeyboard(login_edit)
            false
        }
        login_send.setOnClickListener {
            var txt = login_edit.text.toString()
            if (!TextUtils.isEmpty(txt)) {
                mPresenter.inputTxt(mContext, txt, "msg")
                login_edit.setText("")
            }
        }

    }

    override fun addItem(serviceData: ChatMultiEntry<LoginMsgEntry>, delay: Long) {
        var data = Bundle()
        data.putSerializable("bean", serviceData)
        var msg = Message()
        msg.data = data
        msg.what = SEND_DATA
        handler.sendMessageDelayed(msg, delay)
    }

    private fun addFragment() {
        supportFragmentManager.beginTransaction().add(R.id.login_ft, currentFragment).commit()
        login_ft.visibility = View.VISIBLE
    }

    /**
     * 手机号错了 重新发送验证码
     */
    override fun showResendView(delay: Long) {
        login_ft.postDelayed({
            currentFragment = ResendFragment()
            addFragment()
        }, delay)

    }


    /**
     * 选择生日
     */
    override fun showSelectBirthView(delay: Long) {
        login_ft.postDelayed({
            currentFragment = SelectBirthFragment()
            addFragment()
        }, delay)

    }

    /**
     * 选择性别
     */
    override fun showSelectSexView(delay: Long) {
        login_ft.postDelayed({
            currentFragment = SelectSexFragment()
            addFragment()
        }, delay)

    }

    /**
     * 选择头像
     */
    override fun showSelectHead(delay: Long) {
        login_ft.postDelayed({
            currentFragment = SelectHeadFragment()
            addFragment()
        }, delay)

    }

    /**
     * 选择兴趣
     */
    override fun showSelectInterest(delay: Long) {
        login_ft.postDelayed({
            currentFragment = SelectInterestFragment()
            addFragment()
        }, delay)

    }

    /**
     * 选择交友目的
     */
    override fun showSelectObject(delay: Long) {
        login_ft.postDelayed({
            currentFragment = SelectObjectFragment()
            addFragment()
        }, delay)
    }


    override fun hintFragment(delay: Long) {
        login_ft.postDelayed({
            if (currentFragment != null) {
                supportFragmentManager.beginTransaction().remove(currentFragment).commit()
            }
            login_ft.visibility = View.GONE
        }, delay)

    }

    override fun hintEidt(delay: Long) {
        login_edit_gp.postDelayed({
            EmoticonsKeyboardUtils.closeSoftKeyboard(login_edit)
            login_edit_gp.visibility = View.GONE
        }, delay)

    }

    override fun showEidt(delay: Long) {
        login_edit_gp.postDelayed({
            EmoticonsKeyboardUtils.openSoftKeyboard(login_edit)
            login_edit_gp.visibility = View.VISIBLE
        }, delay)

    }

    override fun setEditInputType(inputType: Int) {
        login_edit.inputType = inputType
    }

    fun scrollToBottom() {
        login_ry.post({
            login_ry.smoothScrollToPosition(adapter.data.size)
            (login_ry.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(adapter.data.size, 0)//先要滚动到这个位置
        })
    }


    fun reenterPhone() {
        mPresenter.reenterPhone()
        hintFragment(0)
    }

    fun resendCode() {
        mPresenter.resnedCode(context = mContext)
    }

    fun setMyBirth(sYear: Int, sMonth: Int, sDay: Int) {
        mPresenter.inputTxt(mContext, "$sYear 年$sMonth 月$sDay 日", "msg")
    }

    fun setSex(s: String) {
        mPresenter.inputTxt(mContext, "${s}生", "msg")
    }

    fun setMyHead(compressPath: String) {
        mPresenter.inputTxt(mContext, compressPath, "pic")
    }

    fun setInterest(interest: String) {
        mPresenter.inputTxt(mContext, interest, "msg")
    }

    fun setObject(selectValue: String) {
        mPresenter.inputTxt(mContext, selectValue, "msg")
    }

    override fun goMianActivity(delay: Long) {
        login_ft.postDelayed({
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }, delay)
    }

    override fun goPubActivity(delay: Long) {
        login_ft.postDelayed({
            startActivity(Intent(mContext, PubTxtActivity::class.java).putExtra("from","login"))
            overridePendingTransition(R.anim.open_enter,R.anim.open_exit)
            finish()
        }, delay)
    }

    override fun onResume() {
        super.onResume()
        login_flowview.startAnim()
    }

    override fun onPause() {
        super.onPause()
        login_flowview.pauseAnim()
    }


    override fun onDestroy() {
        PictureFileUtils.deleteAllCacheDirFile(mContext)
        login_flowview.stopAnim()
        super.onDestroy()
    }


}
