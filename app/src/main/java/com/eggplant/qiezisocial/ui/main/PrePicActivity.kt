package com.eggplant.qiezisocial.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.Scroller
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.SetInfoModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.google.gson.Gson
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_prepic.*
import java.util.*


/**
 * Created by Administrator on 2019/1/28.
 */

class PrePicActivity : BaseActivity() {


    internal var imgs: ArrayList<String>? = ArrayList()
    private var preVpadapter: PreVpadapter? = null
    internal var selectPositon = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_prepic
    }

    override fun initView() {
        imgs = intent.getStringArrayListExtra("imgs")
        val current = intent.getIntExtra("current", 1)
        if (imgs == null || imgs!!.size == 0) {
            activity.finish()
        }
        prep_bar.setTitle((current + 1).toString() + "/" + imgs?.size)
        val scroller = ViewPagerScroller(this)
        scroller.initViewPagerScroll(prep_vp)
        preVpadapter = PreVpadapter()
        prep_vp.adapter = preVpadapter
        prep_vp.currentItem = current


    }

    override fun initData() {


    }

    override fun initEvent() {
        prep_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                activity.finish()
            }
        })
        prep_vp.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {


            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                selectPositon = position
                prep_bar.setTitle((position + 1).toString() + "/" + preVpadapter?.count)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        prep_del.setOnClickListener {
            val currentItem = prep_vp.currentItem
            val delete = imgs?.get(currentItem)?.replace(API.PIC_PREFIX.toRegex(), "")
            if (delete != null) {
                SetInfoModel().delPic(activity, delete, object : JsonCallback<BaseEntry<UserEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                        if (response != null && response.isSuccessful) {
                            val body = response.body()
                            TipsUtil.showToast(mContext, body?.msg ?: "")
                            if (TextUtils.equals(body.stat, "ok")) {
                                val user = body.userinfor
                                resetData(user, currentItem)
                            }
                        }
                    }
                })
            }
        }
        prep_sure.setOnClickListener {

            activity.finish()
        }
    }

    private fun resetData(user: UserEntry?, currentItem: Int) {
        application.infoBean = user
        val map = HashMap<String, Any>()
        user?.nick?.let { map.put("nick", it) }
        user?.birth?.let { map.put("birth", it) }
        user?.sex?.let { map.put("sex", it) }
        user?.card?.let { map.put("card", it) }
        user?.careers?.let { map.put("careers", it) }
        user?.face?.let { map.put("face", it) }
        user?.uid?.let { map.put("uid", it) }
        user?.topic?.let { map.put("question", it) }
        user?.city?.let { map.put("city", it) }
        user?.edu?.let { map.put("edu", it) }
        user?.weight?.let { map.put("weight", it) }
        user?.height?.let { map.put("height", it) }
        user?.xz?.let { map.put("xz", it) }
        val gson = Gson()
        val strJson = gson.toJson(user?.pic)
        map.put("pic", strJson)
        StorageUtil.SPSave(mContext, "userEntry", map)
        preVpadapter?.let {
            when {
                currentItem + 1 < it.count -> {
                    prep_vp.currentItem = currentItem + 1
                    prep_vp.postDelayed(Runnable {
                        prep_vp.currentItem = currentItem
                        if (imgs?.size!! > currentItem)
                            imgs?.removeAt(currentItem)
                        it.notifyDataSetChanged()
                        prep_bar.setTitle((if (it.count == 0) 0 else selectPositon + 1).toString() + "/" + it.count)
                    }, 600)
                }
                currentItem - 1 >= 0 -> {
                    prep_vp.currentItem = currentItem - 1
                    prep_vp.postDelayed(Runnable {
                        if (imgs!!.size > currentItem)
                            imgs?.removeAt(currentItem)
                        it.notifyDataSetChanged()
                        prep_bar.setTitle((if (it.count == 0) 0 else selectPositon + 1).toString() + "/" + it.count)
                    }, 600)
                }
                else -> {
                    if (imgs?.size!! > currentItem)
                        imgs?.removeAt(currentItem)
                    it.notifyDataSetChanged()
                    prep_bar.setTitle((if (it.count == 0) 0 else selectPositon + 1).toString() + "/" + it.count)
                    activity.finish()
                }
            }
        }
    }


    internal inner class PreVpadapter : PagerAdapter() {
        override fun getCount(): Int {
            return imgs!!.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val s = imgs!![position]
            val imageView = ImageView(mContext)
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(mContext).load(API.PIC_PREFIX + s).into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    /**
     * ViewPager 滚动速度设置
     */
    inner class ViewPagerScroller : Scroller {
        private var mScrollDuration = 600 // 滑动速度

        /**
         * 设置速度速度
         *
         * @param duration
         */
        fun setScrollDuration(duration: Int) {
            this.mScrollDuration = duration
        }

        constructor(context: Context) : super(context) {}

        constructor(context: Context, interpolator: Interpolator) : super(context, interpolator) {}

        @SuppressLint("NewApi")
        constructor(context: Context, interpolator: Interpolator,
                    flywheel: Boolean) : super(context, interpolator, flywheel) {
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        fun initViewPagerScroll(viewPager: ViewPager) {
            try {
                val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
                mScroller.isAccessible = true
                mScroller.set(viewPager, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun finish() {
        val intent = Intent()
        intent.putStringArrayListExtra("imgs", imgs)
        setResult(RESULT_OK, intent)
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }


}
