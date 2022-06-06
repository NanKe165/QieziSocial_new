package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.ui.main.SetSceneActivity
import com.eggplant.qiezisocial.ui.main.VerifySceneActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import kotlinx.android.synthetic.main.ap_fresh_select_object.*
import kotlinx.android.synthetic.main.fragment_scene_verify.*
import java.util.*

/**
 * Created by Administrator on 2022/5/19.
 */

class SceneVerifyFragment : BaseFragment() {
    lateinit var sceneEntry: ScenesEntry
    private var imgs = ArrayList<Int>()

    companion object {
        private var fragment: SceneVerifyFragment? = null
            get() {
                if (field == null)
                    field = SceneVerifyFragment()
                return field
            }

        fun newFragment(bundle: Bundle?): SceneVerifyFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_scene_verify
    }

    override fun initView() {
        val scene = arguments?.getSerializable("scene")
        if (scene != null) {
            sceneEntry = scene as ScenesEntry
        }
        ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext!!, R.drawable.filter_objitem_bg7)

    }

    override fun initEvent() {
        ft_scene_verify_sure.setOnClickListener {
            hideInput(it)
            val psd = ft_scene_verify_code.text.toString()
            if (psd.isEmpty() || psd != sceneEntry.code) {
                TipsUtil.showToast(mContext, "邀请码错误")
                ft_scene_verify_error.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).verifySceneSucess(sceneEntry)
            }else if (activity is VerifySceneActivity){
                (activity as VerifySceneActivity).verifySceneSucess(sceneEntry)
            }

        }
        ft_scene_verify_bg.setOnClickListener {
            hideInput(it)
        }
    }

    override fun initData() {
        addImags()
        setData()

    }

    private fun setData() {
        val item = sceneEntry.title
        val hint = sceneEntry.des
        val stat = sceneEntry.stat
        ap_f_selectobj_txt.text = item
        ap_f_selectobj_hint.text = hint
        if (sceneEntry.background != null && sceneEntry.background.isNotEmpty()) {
            ap_f_selectobj_img.visibility = View.VISIBLE
            Glide.with(mContext!!).load(sceneEntry.background).into(ap_f_selectobj_img)
        } else if (sceneEntry.pic != null) {
//            helper.itemView.ap_f_selectobj_img.visibility = View.GONE
            val pic = sceneEntry.pic.toInt()
            if (pic < imgs.size) {
                ap_f_selectobj_img.setImageResource(imgs[pic])
            } else {
                ap_f_selectobj_img.setImageDrawable(null)
            }
        }
    }

    private fun addImags() {
        imgs.add(R.drawable.icon_scene_img1)
        imgs.add(R.drawable.icon_scene_img2)
        imgs.add(R.drawable.icon_scene_img3)
        imgs.add(R.drawable.icon_scene_img4)
        imgs.add(R.drawable.icon_scene_img5)
        imgs.add(R.drawable.icon_scene_img6)
        imgs.add(R.drawable.icon_scene_img7)
        imgs.add(R.drawable.icon_scene_img8)
        imgs.add(R.drawable.icon_scene_img9)
        imgs.add(R.drawable.icon_scene_img10)
        imgs.add(R.drawable.icon_scene_img11)
        imgs.add(R.drawable.icon_scene_img12)
        imgs.add(R.drawable.icon_scene_img13)
        imgs.add(R.drawable.icon_scene_img14)
        imgs.add(R.drawable.icon_scene_img15)
        imgs.add(R.drawable.icon_scene_img16)
        imgs.add(R.drawable.icon_scene_img17)
        imgs.add(R.drawable.icon_scene_img18)
        imgs.add(R.drawable.icon_scene_img19)
        imgs.add(R.drawable.icon_scene_img20)
        imgs.add(R.drawable.icon_scene_img21)
        imgs.add(R.drawable.icon_scene_img22)
        imgs.add(R.drawable.icon_scene_img23)
        imgs.add(R.drawable.icon_scene_img24)
        imgs.add(R.drawable.icon_scene_img25)
        imgs.add(R.drawable.icon_scene_img26)
        imgs.add(R.drawable.icon_scene_img27)
        imgs.add(R.drawable.icon_scene_img28)
    }

    fun resetData(scenesEntry: ScenesEntry) {

        ft_scene_verify_error.visibility = View.GONE
        ft_scene_verify_code.setText("")
        sceneEntry = scenesEntry
        setData()
    }

    private fun hideInput(view: View) {
        //如果不是edittext，则隐藏键盘
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
    }
}
