package com.eggplant.qiezisocial.base

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.billy.android.swipe.SmartSwipe
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.LoginEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.event.FinishEvent
import com.eggplant.qiezisocial.event.LogoutEvent
import com.eggplant.qiezisocial.event.RefresHomeEvent
import com.eggplant.qiezisocial.socket.event.OtherLoginEvnet
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.utils.NotifycationUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.OkGo
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2020/4/9.
 */
abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var mContext: Context
    protected lateinit var activity: AppCompatActivity
    protected lateinit var application: QzApplication
    var infoBean: UserEntry? = null
    var loginEntry: LoginEntry? = null
    protected var isUseBaseUi = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        activity = this
        application = QzApplication.get()
        ScreenUtil.hideStatusBar(this)
        if (isUseBaseUi) {
            initSystemUi()
        }
        if (application.loginEntry==null) {
            application.loginEntry = savedInstanceState?.getSerializable("loginEntry") as LoginEntry?
            application.filterData = savedInstanceState?.getSerializable("filterData") as FilterEntry?
            application.infoBean = savedInstanceState?.getSerializable("infoBean") as UserEntry?
            savedInstanceState?.let {
                application.msgUUID = it.getString("msgUUID")
                application.chatGsId = it.getLong("chatGsId")
                application.chatUid = it.getLong("chatUid")
                application.isLogin = true
            }
        }
        setContentView(getLayoutId())
        if (!EventBus.getDefault().isRegistered(activity)) {
            EventBus.getDefault().register(activity)
        }
        initView()
        initView(savedInstanceState)
        initEvent()
        initData()
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initView()
    open protected fun initView(savedInstanceState: Bundle?) {

    }
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable("loginEntry", application.loginEntry)
        outState?.putSerializable("filterData", application.filterData)
        outState?.putSerializable("infoBean", application.infoBean)
        outState?.putString("msgUUID", application.msgUUID)
        outState?.putLong("chatGsId", application.chatGsId)
        outState?.putLong("chatUid", application.chatUid)
        outState?.putBoolean("isLogin", application.isLogin)
        outState?.putBoolean("isWebLogin", application.isWebLogin)
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (application.loginEntry==null) {
            application.loginEntry = savedInstanceState?.getSerializable("loginEntry") as LoginEntry?
            application.filterData = savedInstanceState?.getSerializable("filterData") as FilterEntry?
            application.infoBean = savedInstanceState?.getSerializable("infoBean") as UserEntry?
            savedInstanceState?.let {
                application.msgUUID = it.getString("msgUUID")
                application.chatGsId = it.getLong("chatGsId")
                application.chatUid = it.getLong("chatUid")
                application.isLogin = true
            }
        }
    }

    protected abstract fun initData()

    protected abstract fun initEvent()
    protected fun initSmartSwipe() {
        SmartSwipe.wrap(this).addConsumer(ActivitySlidingBackConsumer(this))
                .setRelativeMoveFactor(1.0f)
                .enableLeft()
    }

    private fun initSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            //      ScreenUtil.setStatusTextColor(true, this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val array = theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground, android.R.attr.textColorPrimary))
                val backgroundColor = array.getColor(0, 0xD8D8D8)
                array.recycle()
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                //设置状态栏颜色
                window.statusBarColor = backgroundColor
            }
        }
    }

    fun removeCookies() {
        // HttpUrl httpUrl = HttpUrl.parse(QZAPI.BASE_URL);
        val cookieStore = OkGo.getInstance().cookieJar.cookieStore
        //cookieStore.removeCookie(httpUrl);
        cookieStore.removeAllCookie()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun logout(event: LogoutEvent) {
        application.isLogin = false
        application.isWebLogin = false
        application.infoBean = null
        application.loginEntry = null
        val sp = mContext.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove("nick")
        editor.remove("birth")
        editor.remove("sex")
        editor.remove("card")
        editor.remove("careers")
        editor.remove("face")
        editor.remove("uid")
        editor.remove("question")
        editor.remove("city")
        editor.remove("edu")
        editor.remove("weight")
        editor.remove("height")
        editor.remove("xz")
        editor.remove("pic")
        editor.remove("account")
        editor.remove("token")
        editor.remove("phone")
        editor.remove("spacebg")
        editor.remove("longitude")
        editor.remove("latitude")
        editor.remove("newMsgNumb")
        editor.commit()
        removeCookies()
        NotifycationUtils.getInstance(mContext).cancelNotify(1)
        ShortcutBadger.removeCount(mContext)
        if (event.msg!=null){
            TipsUtil.showToast(mContext,event.msg)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showOtherLogin(evnet: OtherLoginEvnet) {
        //        EventBus.getDefault().post(new LogoutEvent());
        //        if (dialog == null) {
        //            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //            builder.setMessage("此帐号在其他设备登录！")
        //                    .setCancelable(false)
        //                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
        //
        //                        @Override
        //                        public void onClick(DialogInterface dialog, int which) {
        //                            // 点击“确认”后的操作
        ////                            activity.finish();
        //                            EventBus.getDefault().post(new FinishEvent());
        //                            startActivity(new Intent(mContext, LoginActivity.class));
        //                        }
        //                    });
        //            dialog = builder.create();
        //        }
        //        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open  fun finishAllActivity(event: FinishEvent) {
        activity.finish()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    open  fun RefreshHomey(event: RefresHomeEvent) {
        if(activity is MainActivity) {
            (activity as MainActivity).refreshHome(event)
        }else{
            activity.finish()
        }
    }
    override fun onDestroy() {
        OkGo.getInstance().cancelTag(activity)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(activity)
        super.onDestroy()
    }

    /**
     * 设置 app 字体不随系统字体设置改变
     */
    override fun getResources(): Resources? {
        val res = super.getResources()
        if (res != null) {
            val config = res.configuration
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f
                res.updateConfiguration(config, res.displayMetrics)
            }
        }
        return res
    }

}