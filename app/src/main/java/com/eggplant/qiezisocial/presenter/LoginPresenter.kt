package com.eggplant.qiezisocial.presenter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.LoginContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.CommentUtils
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.google.gson.Gson
import com.lzy.okgo.model.Response
import java.util.*

/** Created by Administrator on 2020/4/24.
 * */

class LoginPresenter : BasePresenter<LoginContract.view>(), LoginContract.Presenter {
    private var currentStep = LoginContract.LoginStep.PHONE
    private var msgDelayTime = 1500L
    private var phone: String? = null
    private var isNewUser: Boolean = false
    fun initData(context: Context, user: UserEntry?) {
        if (user == null) {
            mView?.addItem(getServiceData("你好呀，我叫花花是这边管事的 欢迎来到问题盒子!"), 0)
            mView?.addItem(getServiceData("注册(登录)需要你的手机号，你的号码是？"), msgDelayTime)
        } else {
            val userEntry = context.getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
            val phone = userEntry.getString("phone", "")
            mView?.addItem(getServiceData("你好呀，我叫花花是这边管事的 欢迎来到问题盒子!"), 0)
            mView?.addItem(getServiceData("检测到你的手机号$phone 已经注册成功"), 0)
            mView?.hintEidt(0)
            mView?.hintFragment(0)
            onNextStep(user)
        }
    }

    private fun onNextStep(user: UserEntry) {
        when {
            TextUtils.isEmpty(user.birth) -> {
                onLoginSuccess()
            }
            TextUtils.isEmpty(user.sex) -> {
                onModifyBirthSuccess()
            }
            TextUtils.isEmpty(user.nick) -> {
                onModifySexSuccess()
            }
            TextUtils.isEmpty(user.face) -> {
                onModifyNickSuccess()
            }
            TextUtils.isEmpty(user.label) -> {
                onModifyHeadSuccess()
            }
            TextUtils.isEmpty(user.`object`) -> {
                onModifyInterestSuccess()
            }
            else -> {
                onModifyObjectSuccess()
            }
        }
    }

    private fun getServiceData(s: String): ChatMultiEntry<LoginMsgEntry> {
        return ChatMultiEntry(ChatMultiEntry.CHAT_OTHER, LoginMsgEntry("msg", s))
    }

    private fun getMineData(s: String, msgType: String): ChatMultiEntry<LoginMsgEntry> {
        return ChatMultiEntry(ChatMultiEntry.CHAT_MINE, LoginMsgEntry(msgType, s))
    }

    fun inputTxt(context: Context, txt: String, contentType: String) {
        mView?.addItem(getMineData(txt, contentType), 0)
        when (currentStep) {
            LoginContract.LoginStep.PHONE -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                val isPhone = CommentUtils.checkPhone(txt)
                if (isPhone) {
                    phone = txt
                    requestCode(context, txt, false)
                } else {
                    mView?.addItem(getServiceData("这个号码有点问题，再试一次？"), 0)
                    mView?.showEidt(0)
                    TipsUtil.showToast(context, "这个号码有点问题，再试一次？")
                }
            }
            LoginContract.LoginStep.VCODE -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                login(context, txt)
            }
            LoginContract.LoginStep.BIRTH -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                val birth = txt.replace("年", "-").replace("月", "-").replace("日", "").trim()
                Log.i("testLogin", "$birth")
                modifyInfo(context, birth, currentStep)
            }
            LoginContract.LoginStep.SEX -> {
                mView?.hintFragment(0)
                modifyInfo(context, txt, currentStep)
            }
            LoginContract.LoginStep.NICK -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                modifyInfo(context, txt, currentStep)

            }
            LoginContract.LoginStep.HEAD -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                modifyHead(context, txt)
            }
            LoginContract.LoginStep.INTEREST -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                modifyInfo(context, txt, currentStep)
            }
            LoginContract.LoginStep.OBJECT -> {
                mView?.hintEidt(0)
                mView?.hintFragment(0)
                modifyInfo(context, txt, currentStep)
            }

        }
    }

    /**
     * 登录
     */
    private fun login(context: Context, code: String) {
        phone?.let {
            val ph = it
            LoginModel.login(context, it, code, object : JsonCallback<LoginEntry>() {
                override fun onSuccess(response: Response<LoginEntry>?) {
                    response?.let {
                        if (response.isSuccessful) {
                            val body = response.body()
                            QzApplication.get().loginEntry = body
                            val map = HashMap<String, Any>()
                            val gson = Gson()
                            val careerList = gson.toJson(body.careerlist)
                            val interest = gson.toJson(body.interest)
                            val objectlist = gson.toJson(body.objectlist)
                            map.put("objectList", objectlist)
                            map.put("careerList", careerList)
                            map.put("interest", interest)
                            if (TextUtils.equals(body.stat, "ok")) {
                                QzApplication.get().infoBean = body.userinfor as UserEntry
                                QzApplication.get().isLogin = true
                                val user = body.userinfor
                                map.put("token", body.token)
                                map.put("phone", ph)
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
                                StorageUtil.SPSave(context, "userEntry", map)
                                onNextStep(body.userinfor)
                            } else {
                                TipsUtil.showToast(context, body.msg)
                                mView?.showEidt(0)
                            }
                        } else {
                            TipsUtil.showToast(context, response.code().toString())
                            mView?.showEidt(0)
                        }
                    }
                }

                override fun onError(response: Response<LoginEntry>?) {
                    super.onError(response)
                    response?.message()?.let {
                        TipsUtil.showToast(context, it)
                    }
                    mView?.showEidt(0)
                }
            })
        }
    }

    /**
     * 发送验证码
     */
    private fun requestCode(context: Context, txt: String, resend: Boolean) {
        if (TextUtils.equals(txt, "18611115113")) {
            onSendCodeSuccess()
            return
        }
        LoginModel.getCode(context, txt, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    it.body().msg?.let { TipsUtil.showToast(context, it) }
                    if (TextUtils.equals("ok", it.body().stat)) {
                        if (resend) {
                            mView?.addItem(getServiceData("收到，验证码重新发给你啦"), msgDelayTime)
                        } else {
                            onSendCodeSuccess()
                        }
                    } else {
                        mView?.showEidt(0)
                    }
                }
            }

            override fun onError(response: Response<BaseEntry<*>>?) {
                super.onError(response)
                response?.message()?.let {
                    TipsUtil.showToast(context, it)
                }
                mView?.showEidt(0)
            }
        })
    }

    /**
     * 添加信息
     */
    private fun modifyInfo(context: Context, txt: String, step: LoginContract.LoginStep) {
        val key: String = when (step) {
            LoginContract.LoginStep.BIRTH ->
                "birth"
            LoginContract.LoginStep.SEX ->
                "sex"
            LoginContract.LoginStep.NICK ->
                "nick"
            LoginContract.LoginStep.INTEREST ->
                "label"
            LoginContract.LoginStep.OBJECT ->
                "object"
            else -> {
                ""
            }
        }
        if (!TextUtils.isEmpty(key)) {
            LoginModel.modifyInfo(context, key, txt, object : JsonCallback<BaseEntry<UserEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            if (TextUtils.equals(it.body().stat, "ok")) {
                                val user = it.body()?.userinfor
                                saveInfo(context, user)
                                onModifyInfoSuccess(step)
                            } else {
                                it.body().msg?.let {
                                    TipsUtil.showToast(context, it)
                                }
                                onModifyInfoError(step)
                            }
                        } else {
                            TipsUtil.showToast(context, it.code().toString())
                            onModifyInfoError(step)
                        }
                    }
                }

                override fun onError(response: Response<BaseEntry<UserEntry>>?) {
                    super.onError(response)
                    response?.let {
                        TipsUtil.showToast(context, it.code().toString() + it.message())
                    }
                    onModifyInfoError(step)
                }
            })
        }
    }

    /**
     * 添加头像
     */
    private fun modifyHead(context: Context, txt: String) {
        LoginModel.modifyHead(context, txt, object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        if (TextUtils.equals(it.body().stat, "ok")) {
                            val user = it.body()?.userinfor
                            saveInfo(context, user)
                            onModifyHeadSuccess()
                        } else {
                            it.body().msg?.let {
                                TipsUtil.showToast(context, it)
                            }
                            mView?.showSelectHead(0)
                        }
                    } else {
                        TipsUtil.showToast(context, it.code().toString())
                        mView?.showSelectHead(0)
                    }
                }
            }

            override fun onError(response: Response<BaseEntry<UserEntry>>?) {
                super.onError(response)
                response?.let {
                    TipsUtil.showToast(context, it.code().toString() + it.message())
                }
                mView?.showSelectHead(0)
            }
        })
    }


    private fun onModifyInfoSuccess(step: LoginContract.LoginStep) {
        when (step) {
            LoginContract.LoginStep.BIRTH ->
                onModifyBirthSuccess()
            LoginContract.LoginStep.SEX ->
                onModifySexSuccess()
            LoginContract.LoginStep.NICK ->
                onModifyNickSuccess()
            LoginContract.LoginStep.INTEREST ->
                onModifyInterestSuccess()
            LoginContract.LoginStep.OBJECT -> {
                isNewUser = true
                onModifyObjectSuccess()
            }
            else -> {

            }
        }
    }

    private fun onModifyInfoError(step: LoginContract.LoginStep) {
        when (step) {
            LoginContract.LoginStep.BIRTH -> {
                mView?.showSelectBirthView(0)
            }
            LoginContract.LoginStep.SEX -> {
                mView?.showSelectSexView(0)
            }
            LoginContract.LoginStep.NICK -> {
                mView?.showEidt(0)
                mView?.setEditInputType(InputType.TYPE_CLASS_TEXT)
            }
            LoginContract.LoginStep.INTEREST -> {
                mView?.showSelectInterest(0)
            }
            LoginContract.LoginStep.OBJECT -> {
                mView?.showSelectObject(0)
            }
            else -> {
            }
        }
    }

    private fun onSendCodeSuccess() {
        mView?.addItem(getServiceData("验证码发你啦，请输入暗号"), msgDelayTime)
        mView?.showEidt(msgDelayTime)
        mView?.showResendView(msgDelayTime)
        currentStep = LoginContract.LoginStep.VCODE
    }

    private fun onLoginSuccess() {
        mView?.addItem(getServiceData("注册成功[qzxs99][qzxs99] 想知道你的生日什么时候，填完不能反悔哦"), msgDelayTime)
        mView?.showSelectBirthView(msgDelayTime)
        currentStep = LoginContract.LoginStep.BIRTH
    }

    private fun onModifyBirthSuccess() {
        mView?.addItem(getServiceData("你是男生还是女生呢？填完不能反悔哦"), msgDelayTime)
        mView?.showSelectSexView(msgDelayTime)
        currentStep = LoginContract.LoginStep.SEX
    }

    private fun onModifySexSuccess() {
        mView?.addItem(getServiceData("[qzxs108]"), msgDelayTime)
        mView?.addItem(getServiceData("给自己取个好听的昵称吧"), msgDelayTime)
        mView?.showEidt(msgDelayTime)
        mView?.setEditInputType(InputType.TYPE_CLASS_TEXT)
        currentStep = LoginContract.LoginStep.NICK
    }

    private fun onModifyNickSuccess() {
        mView?.addItem(getServiceData("上传一个好看的头像吧"), msgDelayTime)
        mView?.showSelectHead(msgDelayTime)
        currentStep = LoginContract.LoginStep.HEAD
    }

    private fun onModifyHeadSuccess() {
        mView?.addItem(getServiceData("选择一下你的兴趣标签我们 会根据你的标签推荐首页的内容"), msgDelayTime)
        mView?.showSelectInterest(msgDelayTime)
        currentStep = LoginContract.LoginStep.INTEREST
    }

    private fun onModifyInterestSuccess() {
        mView?.addItem(getServiceData("最后选择一下你的交友目的吧"), msgDelayTime)
        mView?.showSelectObject(msgDelayTime)
        currentStep = LoginContract.LoginStep.OBJECT
    }

    private fun onModifyObjectSuccess() {
        mView?.addItem(getServiceData("客气的话我就不说了，让我们一起进入问题盒子吧！"), msgDelayTime)
        if (!isNewUser) {
            mView?.goMianActivity(msgDelayTime + 2500)
        } else {
            mView?.goPubActivity(msgDelayTime + 2500)
        }
    }

    private fun saveInfo(context: Context, user: UserEntry?) {
        QzApplication.get().infoBean = user
        if (user != null) {
            val map = HashMap<String, Any>()
            map.put("nick", user.nick)
            map.put("birth", user.birth)
            map.put("sex", user.sex)
            map.put("face", user.face)
            map.put("sex", user.sex)
            map.put("label", user.label)
            map.put("object", user.`object`)
            StorageUtil.SPSave(context, "userEntry", map)
        }
    }


    fun reenterPhone() {
        mView?.addItem(getMineData("手机号错了", "msg"), 0)
        mView?.addItem(getServiceData("那么你真正的手机号是什么呢？"), msgDelayTime)
        currentStep = LoginContract.LoginStep.PHONE
    }

    fun resnedCode(context: Context) {
        mView?.addItem(getMineData("重新发送", "msg"), 0)
        phone?.let { requestCode(context, it, true) }
    }


}
