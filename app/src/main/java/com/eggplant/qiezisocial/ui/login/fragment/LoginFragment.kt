package com.eggplant.qiezisocial.ui.login.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.LoginEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.setting.AgreementActivity
import com.eggplant.qiezisocial.utils.CommentUtils
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.google.gson.Gson
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

/**
 * Created by Administrator on 2021/4/25.
 */

class LoginFragment : BaseFragment() {
    var countTimer: CountDownTimer? = null
    var head:String=""
    var phone:String=""
    private var isChecked = false
    val textWatche = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null || s.isEmpty()) {
//                ft_login_head.visibility = View.GONE
                ft_login_rule.visibility = View.VISIBLE
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun initView() {
        setPermissionTxt()
        if (head.isNotEmpty()&&phone.isNotEmpty()){
//            Glide.with(mContext!!).load(API.Companion.PIC_PREFIX + head).into(ft_login_head)
            ft_login_phone.setText(phone)
//            ft_login_head.visibility = View.VISIBLE
            ft_login_rule.visibility = View.GONE
            ft_login_sure.background=ContextCompat.getDrawable(mContext!!,R.drawable.tv_yellow_bg3)
            isChecked=true
        }
    }

    override fun initEvent() {

        ft_login_phone.addTextChangedListener(textWatche)
        ft_login_rule.setOnClickListener {
            if (isChecked){
                ft_login_rule.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.login_rule_unread,0,0,0)
                ft_login_sure.background=ContextCompat.getDrawable(mContext!!,R.drawable.tv_gray_bg4)
            }else{
                ft_login_rule.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.login_rule_read,0,0,0)
                ft_login_sure.background=ContextCompat.getDrawable(mContext!!,R.drawable.tv_yellow_bg3)
            }
            isChecked=!isChecked
        }
        ft_login_get_vcode.setOnClickListener {
            val phone = ft_login_phone.text.toString().trim()
            if (phone.isEmpty()) {
                TipsUtil.showToast(mContext, "请输入手机号")
            } else if (!CommentUtils.checkPhone(phone)) {
                TipsUtil.showToast(mContext, "请输入正确的手机号")
            } else {
                ft_login_get_vcode.isClickable = false
                ft_login_get_vcode.isFocusable = false
                ft_login_get_vcode.background=ContextCompat.getDrawable(mContext!!,R.drawable.tv_gray_bg3)
                ft_login_get_vcode.setTextColor(ContextCompat.getColor(mContext!!,R.color.tv_e2))
                requestCode(mContext!!, phone)
                startCountDown()
            }
        }
        ft_login_sure.setOnClickListener {
            if (!isChecked) {
                TipsUtil.showToast(mContext, "请您阅读《用户协议》与《隐私政策》后并勾选同意按钮才能登录")
                return@setOnClickListener
            }
            val phone = ft_login_phone.text.toString().trim()
            val code = ft_login_vcode.text.toString().trim()
            if (phone.isEmpty()) {
                TipsUtil.showToast(mContext, "请输入手机号")
            } else if (!CommentUtils.checkPhone(phone)) {
                TipsUtil.showToast(mContext, "请输入正确的手机号")
            } else if (code.isEmpty()) {
                TipsUtil.showToast(mContext, "请填写验证码")
            } else {
                ft_login_sure.isClickable=false
                login(mContext!!, phone, code)
            }
        }
    }

    override fun initData() {

    }


    /**
     * 登录
     */
    private fun login(context: Context, phone: String, code: String) {
        LoginModel.login(context, phone, code, object : DialogCallback<LoginEntry>(activity,"正在登陆...") {
            override fun onSuccess(response: Response<LoginEntry>?) {
                ft_login_sure.isClickable=true
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
                            application.filterData=body.filter
                            val user = body.userinfor
                            map.put("token", body.token)
                            map.put("phone", phone)
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
                            if (user.sex.isEmpty() || user.face.isEmpty() || user.nick.isEmpty()) {
                                (activity as LoginActivity2).setFragment2()
                            } else {
                                (activity as LoginActivity2).loginSuccess()
                            }
                        } else {
                            TipsUtil.showToast(context, body.msg)
                        }
                    } else {
                        TipsUtil.showToast(context, response.code().toString())
                    }
                }
            }
        })
    }


    /**
     * 发送验证码
     */
    private fun requestCode(context: Context, txt: String) {
        if (TextUtils.equals(txt, "18611115113")) {
            return
        }
        LoginModel.getCode(context, txt, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    it.body().msg?.let { TipsUtil.showToast(context, it) }
                    if (TextUtils.equals("ok", it.body().stat)) {
                        TipsUtil.showToast(mContext, "验证码已发送")
                    }
                }
            }

        })
    }

    fun setHeadAndPhone(head: String, phone: String) {
        this.head=head
        this.phone=phone

    }

    private fun setPermissionTxt() {

        val style = SpannableStringBuilder()

        //设置文字  已表阅读并同意用户协议和隐私政策
        style.append(getString(R.string.login_rule))

        //设置部分文字点击事件
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                context!!.startActivity(Intent(context, AgreementActivity::class.java).putExtra("from", "agreement"))
            }
        }
        style.setSpan(clickableSpan, 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ft_login_rule.text = style

        //设置部分文字颜色
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#F5C657"))
        style.setSpan(foregroundColorSpan, 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        //设置部分文字点击事件
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                context!!.startActivity(Intent(context, AgreementActivity::class.java).putExtra("from", "privacy"))
            }
        }
        style.setSpan(clickableSpan2, 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ft_login_rule.text = style

        //设置部分文字颜色
        val foregroundColorSpan2 = ForegroundColorSpan(Color.parseColor("#F5C657"))
        style.setSpan(foregroundColorSpan2, 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        //配置给TextView
        ft_login_rule.movementMethod = LinkMovementMethod.getInstance()
        ft_login_rule.text = style
    }

    private fun startCountDown() {
        if (countTimer != null) {
            countTimer?.cancel()
        }
        countTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ft_login_get_vcode?.isClickable = false
                ft_login_get_vcode?.isFocusable = false
                ft_login_get_vcode?.text = "(${Math.round(millisUntilFinished.toDouble() / 1000) - 1}s)"
            }

            override fun onFinish() {
                ft_login_get_vcode?.isClickable = true
                ft_login_get_vcode?.isFocusable = true
                ft_login_get_vcode?.text = "重新获取"
                ft_login_get_vcode.background=ContextCompat.getDrawable(mContext!!,R.drawable.tv_yellow_bg)
                ft_login_get_vcode.setTextColor(ContextCompat.getColor(mContext!!,R.color.tv_black))
            }
        }
        countTimer?.start()
    }

    override fun onDestroyView() {
        ft_login_phone.removeTextChangedListener(textWatche)
        super.onDestroyView()
    }

}


