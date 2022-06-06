package com.eggplant.qiezisocial.ui

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.LoginEntry
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.google.gson.Gson
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_guide.*
import java.util.*


/**
 * Created by Administrator on 2021/11/8.
 */

class GuideActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_guide
    }

    override fun initView() {
        val guidePager = GuidePager()
        guide_vp.adapter = guidePager
    }

    override fun initData() {
        guide_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 3) {
                    guide_btn.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun initEvent() {
        guide_btn.setOnClickListener {
            var info = packageManager.getPackageInfo(this.packageName, 0)
            var version= info.versionCode
            val notice = getSharedPreferences("permissionAgreement", Context.MODE_PRIVATE)
            val edit = notice.edit()
            edit.putInt("vscode", version)
            edit.commit()
            val userEntry = getSharedPreferences("userEntry", MODE_PRIVATE)
            var token = userEntry.getString("token", "")
            var phone = userEntry.getString("phone", "")
            autoLogin(phone, token)
        }
    }

    inner class GuidePager : PagerAdapter() {
        var imgs = intArrayOf(R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3, R.mipmap.guide4)
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(mContext)
            imageView.setImageResource(imgs[position])
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            val lps = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.layoutParams = lps
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return imgs.size
        }
    }


    private fun autoLogin(phone: String?, token: String?) {
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
                    Log.i("WelcomeActivity", "stat:${body.stat}  msg:${body.msg}  chance:${body.chance}  uid is null:${body.userinfor.uid == null} ")
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
                            startActivity(Intent(mContext, MainActivity::class.java))
                        }
                        activity.finish()
                    } else {
                        StorageUtil.SPSave(mContext, "userEntry", map)
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
