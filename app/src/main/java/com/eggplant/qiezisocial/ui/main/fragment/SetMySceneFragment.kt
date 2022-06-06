package com.eggplant.qiezisocial.ui.main.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.MysceneEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.SetMySceneActivity
import com.eggplant.qiezisocial.ui.main.SetSceneActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.fragment_setmy_scene.*

/**
 * Created by Administrator on 2021/12/20.
 */

class SetMySceneFragment : BaseFragment() {
    companion object {
        private var fragment: SetMySceneFragment? = null
            get() {
                if (field == null)
                    field = SetMySceneFragment()
                return field
            }

        fun newFragment(bundle: Bundle?): SetMySceneFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    val REQUEST_SELECT_COLLEGE = 101
    val REQUEST_SELECT_STAR = 102
    val REQUEST_SELECT_FACTIRT = 103


    private var currentCollege: ScenesEntry? = null
    private var currentStar: ScenesEntry? = null
    private var currentFactory: ScenesEntry? = null
    private var setCollegeSize = 3
    override fun getLayoutId(): Int {
        return R.layout.fragment_setmy_scene
    }

    override fun initView() {

    }

    override fun initEvent() {
        myscene_sure.setOnClickListener {
            setMyScene()
        }
        myscene_close.setOnClickListener {

            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).setMysceneClose()
            }
        }
        myscene_set_college_gp.setOnClickListener {
//            if (setCollegeSize >= 3) {
//                TipsUtil.showToast(mContext, "你已经不能再修改了哦")
//                return@setOnClickListener
//            }
            startActivityForResult(Intent(mContext, SetMySceneActivity::class.java).putExtra("from", "college"), REQUEST_SELECT_COLLEGE)
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        myscene_set_star_gp.setOnClickListener {
            startActivityForResult(Intent(mContext, SetMySceneActivity::class.java).putExtra("from", "star"), REQUEST_SELECT_STAR)
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        myscene_set_factory_gp.setOnClickListener {
            startActivityForResult(Intent(mContext, SetMySceneActivity::class.java).putExtra("from", "factory"), REQUEST_SELECT_FACTIRT)
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        myscene_college_del.setOnClickListener {
//            if (setCollegeSize >= 3) {
//                TipsUtil.showToast(mContext, "你已经不能再修改了哦")
//                return@setOnClickListener
//            }
//            TipsUtil.showToast(mContext, "你还剩${3 - setCollegeSize}次修改机会哦")
            resetCollegeData()
        }
        myscene_star_del.setOnClickListener {
            resetStarData()
        }
        myscene_factory_del.setOnClickListener {
            resetFactoryData()
        }
    }


    override fun initData() {
        resetData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_COLLEGE) {
                val entry = data?.getSerializableExtra("data")
                if (entry != null) {
                    setCollegeData(entry as ScenesEntry)
                }

            } else if (requestCode == REQUEST_SELECT_STAR) {
                val entry = data?.getSerializableExtra("data")
                if (entry != null) {
                    setStarData(entry as ScenesEntry)
                }

            } else if (requestCode == REQUEST_SELECT_FACTIRT) {
                val entry = data?.getSerializableExtra("data")
                if (entry != null) {
                    setFactoryData(entry as ScenesEntry)
                }

            }
        }
    }

    private fun setFactoryData(scenesEntry: ScenesEntry) {
        myscene_set_factory_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.filter_objitem_bg3)
        myscene_set_factory_txt.text = scenesEntry.title
        myscene_set_factory_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.white))
        myscene_set_factory_txt.typeface = Typeface.DEFAULT_BOLD
        myscene_factory_del.visibility = View.VISIBLE
        currentFactory = scenesEntry
    }

    private fun resetFactoryData() {
        myscene_set_factory_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.icon_select_myscene)
        myscene_set_factory_txt.text = "设置我喜欢的大厂"
        myscene_set_factory_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.bottom_hint_color))
        myscene_set_factory_txt.typeface = Typeface.DEFAULT
        myscene_set_factory_img.setImageResource(R.mipmap.icon_myscene_factory)
        myscene_factory_del.visibility = View.GONE
        currentFactory = null
    }

    private fun setStarData(scenesEntry: ScenesEntry) {
        myscene_set_star_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.filter_objitem_bg2)
        myscene_set_star_txt.text = scenesEntry.title
        myscene_set_star_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.white))
        myscene_set_star_txt.typeface = Typeface.DEFAULT_BOLD
        myscene_star_del.visibility = View.VISIBLE
        currentStar = scenesEntry
    }

    private fun resetStarData() {
        myscene_set_star_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.icon_select_myscene)
        myscene_set_star_txt.text = "设置我喜欢的明星"
        myscene_set_star_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.bottom_hint_color))
        myscene_set_star_txt.typeface = Typeface.DEFAULT
        myscene_set_star_img.setImageResource(R.mipmap.icon_myscene_star)
        myscene_star_del.visibility = View.GONE
        currentStar = null
    }

    private fun setCollegeData(scenesEntry: ScenesEntry) {
        myscene_set_college_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.filter_objitem_bg1)
        myscene_set_college_txt.text = scenesEntry.title
        myscene_set_college_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.white))
        myscene_set_college_txt.typeface = Typeface.DEFAULT_BOLD
//        if (setCollegeSize < 3) {
            myscene_college_del.visibility = View.VISIBLE
//        } else {
//            myscene_college_del.visibility = View.GONE
//        }
        currentCollege = scenesEntry
    }

    private fun resetCollegeData() {
        myscene_set_college_gp.background = ContextCompat.getDrawable(mContext!!, R.drawable.icon_select_myscene)
        myscene_set_college_txt.text = "设置我的学校"
        myscene_set_college_txt.setTextColor(ContextCompat.getColor(mContext!!, R.color.bottom_hint_color))
        myscene_set_college_txt.typeface = Typeface.DEFAULT
        myscene_set_college_img.setImageResource(R.mipmap.icon_myscene_college)
        myscene_college_del.visibility = View.GONE
        currentCollege = null
    }

    fun resetData() {
        resetCollegeData()
        resetFactoryData()
        resetStarData()
        OkGo.post<MysceneEntry>(API.GET_MYSCENE)
                .execute(object : JsonCallback<MysceneEntry>() {
                    override fun onSuccess(response: Response<MysceneEntry>?) {
                        if (response!!.isSuccessful) {
                            setCollegeSize = response.body().number
                            if (response.body().college != null) {
                                setCollegeData(response.body().college)
                            }
                            if (response.body().star != null) {
                                setStarData(response.body().star)
                            }
                            if (response.body().factory != null) {
                                setFactoryData(response.body().factory)
                            }
                        }
                    }
                })
    }

    private fun setMyScene() {
        val post = OkGo.post<MysceneEntry>(API.SET_MYSCENE)
        if (currentCollege != null) {
            post.params("collegeid", currentCollege!!.sid)
        } else {
            post.params("collegeid", "")
        }
        if (currentStar != null) {
            post.params("starid", currentStar!!.sid)
        } else {
            post.params("starid", "")
        }
        if (currentFactory != null) {
            post.params("factoryid", currentFactory!!.sid)
        } else {
            post.params("factoryid", "")
        }
        post.execute(object : JsonCallback<MysceneEntry>() {
            override fun onSuccess(response: Response<MysceneEntry>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        if (activity is SetSceneActivity) {
                            (activity as SetSceneActivity).setMysceneSuccess()
                        }
                    } else {
                        TipsUtil.showToast(mContext, response.body().msg)
                    }
                }
            }
        })
    }

}
