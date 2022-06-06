package com.eggplant.qiezisocial.ui

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.LoginEntry
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.main.dialog.PermissionDialog
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.google.gson.Gson
import com.lzy.okgo.model.Response
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import java.util.*

/**
 * Created by Administrator on 2020/4/9.
 */

class WelcomeActivity : BaseActivity() {
    private var isFirst: Boolean = false
    private var newVersion=false
    private var gopub: Boolean = false
    internal var dialog: PermissionDialog? = null
    var version=0
    override fun getLayoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun initView() {
        val intent = intent
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        dialog = PermissionDialog(activity, intArrayOf(R.id.cancel, R.id.sure))
        dialog!!.setOnBaseDialogItemClickListener({ dialog, view ->
            if (view.id == R.id.cancel) {
                finish()
            } else if (view.id == R.id.sure) {
                val notice = getSharedPreferences("permissionAgreement", Context.MODE_PRIVATE)
                val edit = notice.edit()
                edit.putBoolean("isFirst", false)
                edit.commit()
                dialog.cancel()
                preInitUm()
                realInitdata()
            }
        })


        val notice = getSharedPreferences("permissionAgreement", Context.MODE_PRIVATE)
        val edit = notice.edit()
//        edit.putBoolean("isFirst", false)
        isFirst = notice.getBoolean("isFirst", true)
        gopub = notice.getBoolean("nextGoPub", false)
        val versionCodel = notice.getInt("vscode", 0)
        var info = packageManager.getPackageInfo(this.packageName, 0)
        version= info.versionCode
        newVersion= (versionCodel < version)
        if (gopub) {
            edit.putBoolean("nextGoPub", false)
            edit.commit()
        }
        if (isFirst) {
            dialog?.show()
        } else {
            realInitdata()
        }
    }

    private val myTask: MyTask
        get() {
            return MyTask()
        }

    private fun realInitdata() {
        initUm()
        var task = myTask
        Timer().schedule(task, 1000)
    }

    override fun initData() {


    }

    override fun initEvent() {

    }
    private fun preInitUm(){
        UMConfigure.setLogEnabled(true)
        UMConfigure.preInit(this, UmSetting.appkey, UmSetting.channel)

        // QQ设置
        PlatformConfig.setQQZone("1108880284", "EBbAT0869ltj3wRX")
        PlatformConfig.setQQFileProvider("com.eggplant.qiezisocial.fileprovider")
        //QQ官方sdk授权
//        Tencent.setIsPermissionGranted(true)
        //微信
        PlatformConfig.setWeixin("wxa0bbcce01a1c57f3", "1c1d0137795ee7e595cc2636bd9243db")
        PlatformConfig.setWXFileProvider("com.eggplant.qiezisocial.fileprovider")
        //新浪微博(第三个参数为回调地址)
        PlatformConfig.setSinaWeibo("1975914947", "6ebbb4d78852669a8fcc2d10bf3c0bb2", "http://sns.whalecloud.com/sina2/callback")
        PlatformConfig.setSinaFileProvider("com.eggplant.qiezisocial.fileprovider")
    }
    private fun initUm() {
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(this, UmSetting.appkey, UmSetting.channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    internal inner class MyTask : TimerTask() {

        override fun run() {
            if (isFirst||newVersion) {
                startActivity(Intent(mContext, GuideActivity::class.java))
                activity.finish()
            } else {
                val userEntry = getSharedPreferences("userEntry", MODE_PRIVATE)
                var token = userEntry.getString("token", "")
                var phone = userEntry.getString("phone", "")
                autoLogin(phone, token)
            }
        }
    }

    private fun autoLogin(phone: String?, token: String?) {
//        Log.i("welcomeA","token:$token")
        LoginModel.autoLogin(mContext, phone, token, "", "", "", object : JsonCallback<LoginEntry>() {
            override fun onSuccess(response: Response<LoginEntry>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    application.loginEntry = body

                    val map = HashMap<String, Any>()
                    val gson = Gson()
                    val careerList = gson.toJson(body.careerlist)
                    val interest = gson.toJson(body.interest)
                    val objectlist = gson.toJson(body.objectlist)
                    map.put("objectList", objectlist)
                    map.put("careerList", careerList)
                    map.put("interest", interest)
                    if (TextUtils.equals(body.stat, "ok")) {
                        application.infoBean = body.userinfor
                        application.isLogin = true
                        application.filterData=body.filter
                        val user = body.userinfor
                        if (!TextUtils.isEmpty(user.stat)) {
                            map.put("stat", user.stat)
                        }
                        map.put("sign", user.sign)
                        map.put("object", user.`object`)
                        map.put("label", user.label)
                        map.put("nick", user.nick)
                        map.put("birth", user.birth)
                        map.put("sex", user.sex)
                        map.put("card", user.card)
                        map.put("careers", user.careers)
                        map.put("face", user.face)
                        map.put("uid", user.uid)
                        map.put("question", user.topic)
                        map.put("city", user.city)
                        map.put("edu", user.edu)
                        map.put("weight", user.weight)
                        map.put("height", user.height)
                        map.put("xz", user.xz)
                        map.put("spacebg", user.spaceback)
                        map.put("latitude", user.latitude)
                        map.put("longitude", user.longitude)

                        val strJson = gson.toJson(user.pic)
                        map.put("pic", strJson)
                        StorageUtil.SPSave(mContext, "userEntry", map)
//                        if (TextUtils.isEmpty(user.birth) || TextUtils.isEmpty(user.sex) || TextUtils.isEmpty(user.nick) || TextUtils.isEmpty(user.face) || TextUtils.isEmpty(user.label) || TextUtils.isEmpty(user.`object`)) {
//                            val intent = Intent(mContext, LoginActivity::class.java)
//                            intent.putExtra("from", "login")
//                            intent.putExtra("bean", body.userinfor)
//                            intent.putExtra("loginEntry", body)
//                            startActivity(intent)
//                        }

                        if (TextUtils.isEmpty(user.sex) || TextUtils.isEmpty(user.nick) || TextUtils.isEmpty(user.face)) {
                            val intent = Intent(mContext, LoginActivity2::class.java)
                            intent.putExtra("from", "login")
                            intent.putExtra("bean", body.userinfor)
                            intent.putExtra("loginEntry", body)
                            startActivity(intent)
                        } else {
//                            if (gopub) {
//                                startActivity(Intent(mContext, PubTxtActivity::class.java).putExtra("from", "login"))
//                                overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
//                            } else {
                                startActivity(Intent(mContext, MainActivity::class.java))
//                            }
                        }
                        activity.finish()
                    } else {
                        if(body.msg!=null&&body.msg=="你被封号了"){
                            TipsUtil.showToast(mContext,body.msg)
                        }

                        StorageUtil.SPSave(mContext, "userEntry", map)
                        removeCookies()
                        val intent = Intent(mContext, LoginActivity2::class.java)
                        startActivity(intent)
                        activity.finish()
                        return
                    }
                }
            }

            override fun onError(response: Response<LoginEntry>) {
                super.onError(response)
                TipsUtil.showToast(mContext, "error ${response.code()}")
//                startActivity(Intent(mContext, LoginActivity2::class.java))
//                activity.finish()
            }
        })

    }
}
