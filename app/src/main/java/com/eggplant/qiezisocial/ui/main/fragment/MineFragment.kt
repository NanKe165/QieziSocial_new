package com.eggplant.qiezisocial.ui.main.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.MineContract
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.MinePresenter
import com.eggplant.qiezisocial.ui.decorate.DecorateActivity
import com.eggplant.qiezisocial.ui.extend.VcrActivity
import com.eggplant.qiezisocial.ui.main.MyDynamicActivity
import com.eggplant.qiezisocial.ui.main.SelectStateActivity
import com.eggplant.qiezisocial.ui.setting.SetInfoActivity
import com.eggplant.qiezisocial.ui.setting.SettingActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Created by Administrator on 2020/4/14.
 */

class MineFragment : BaseMvpFragment<MinePresenter>(), MineContract.View {
    private val REQUEST_SETINFO = 110
    private val REQUEST_SETSTATE = 111

    companion object {
        private var fragment: MineFragment? = null
            get() {
                if (field == null)
                    field = MineFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): MineFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    override fun initPresenter(): MinePresenter {
        return MinePresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mine
    }

    override fun initView() {
        mPresenter.attachView(this)
    }

    override fun initEvent() {

        ft_mine_setinfo.setOnClickListener {
            startActivityForResult(Intent(mContext, SetInfoActivity::class.java), REQUEST_SETINFO)
        }
        ft_mine_setting.setOnClickListener {
            startActivity(Intent(mContext, SettingActivity::class.java))
        }
        ft_mine_vcr.setOnClickListener {
            startActivity(Intent(mContext, VcrActivity::class.java).putExtra("model", 2))
        }
        ft_mine_follow_vcr.setOnClickListener {
            //            startActivity(Intent(mContext, VcrActivity::class.java).putExtra("model", 1))
            startActivity(Intent(mContext, MyDynamicActivity::class.java))
        }
        ft_mine_state.setOnClickListener {
            startActivityForResult(Intent(mContext, SelectStateActivity::class.java), REQUEST_SETSTATE)
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        ft_mine_decorate.setOnClickListener {
            startActivity(Intent(mContext, DecorateActivity::class.java))
        }
//        ft_mine_age_gp.setOnClickListener {
//            val split = ft_mine_age.text.toString().split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
//            val calendar = Calendar.getInstance()
//            if (split.size == 3) {
//                val year = Integer.parseInt(split[0])
//                val month = Integer.parseInt(split[1])
//                val day = Integer.parseInt(split[2])
//                calendar.set(year, month - 1, day)
//            } else {
//                calendar.set(2010, 2 - 1, 1)
//            }
//            showDatePickerDialog(activity!!, THEME_HOLO_LIGHT, calendar)
//        }
    }

    override fun initData() {
        mPresenter.setData()
    }

//    var created = false
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        created = true
//    }
//
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (userVisibleHint && created) {
//            mPresenter.setData()
//        }
//    }


    override fun setSex(sex: String?) {
        if (sex!!.contains("男")) {
            ft_mine_sex.setImageResource(R.mipmap.sex_boy_big)
        } else {
            ft_mine_sex.setImageResource(R.mipmap.sex_girl_big)
        }
    }


    override fun setSign(sign: String?) {
        ft_mine_sign1.text = sign
    }

    override fun setNick(nick: String?) {
        ft_mine_nick.text = nick

    }

    override fun setLabel(birth: String?, sex: String?, wh: String, labelData: ArrayList<String>) {

        ft_mine_labelview.setData(birth, sex, wh, labelData)
    }


    override fun setFace(face: String?) {
        activity?.let { Glide.with(it).load(API.PIC_PREFIX + face).into(ft_mine_head) }
    }

    override fun setMood(mood: String?) {
        if (mood == null || mood.isEmpty()) {
//            ft_mine_state.background = ContextCompat.getDrawable(mContext!!, R.drawable.state_bg_unselect)
            ft_mine_state_tv.text = "心情"
//            ft_mine_state_tv.setTextColor(ContextCompat.getColor(mContext!!, R.color.state_unselect))
            ft_mine_state_tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.state_select_icon_white, 0, 0, 0)
        } else {
            ft_mine_state_tv.text = mood
//            ft_mine_state.background = ContextCompat.getDrawable(mContext!!, R.drawable.state_bg_select)
//            ft_mine_state_tv.setTextColor(ContextCompat.getColor(mContext!!, R.color.state_select))
            setMoodIcon(mood)
        }
    }

    private fun setMoodIcon(mood: String) {
        var statedraw = 0
        when (mood) {
            getString(R.string.state1) -> {
                statedraw = R.mipmap.mine_state_ku_white
            }
            getString(R.string.state2) -> {
                statedraw = R.mipmap.mine_state_liekai_white
            }
            getString(R.string.state3) -> {
                statedraw = R.mipmap.mine_state_kaixin_white
            }
            getString(R.string.state4) -> {
                statedraw = R.mipmap.mine_state_kun_white
            }
            getString(R.string.state5) -> {
                statedraw = R.mipmap.mine_state_fadai_white
            }
            getString(R.string.state6) -> {
                statedraw = R.mipmap.mine_state_gudan_white
            }
            getString(R.string.state7) -> {
                statedraw = R.mipmap.mine_state_youshang_white
            }
            getString(R.string.state8) -> {
                statedraw = R.mipmap.mine_state_fennu_white
            }
            getString(R.string.state9) -> {
                statedraw = R.mipmap.mine_state_chigua_white
            }
        }
        ft_mine_state_tv.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }

    override fun showTost(it: String) {
        TipsUtil.showToast(mContext, it)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

        }
        if (requestCode == REQUEST_SETINFO || requestCode == REQUEST_SETSTATE) {
            mPresenter.setData()
        }
    }


}
