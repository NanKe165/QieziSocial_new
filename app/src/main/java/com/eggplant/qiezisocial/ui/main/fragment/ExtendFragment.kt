package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.extend.DiaryActivity
import com.eggplant.qiezisocial.ui.extend.NearbyActivity
import com.eggplant.qiezisocial.ui.extend.VcrActivity
import kotlinx.android.synthetic.main.fragment_extend.*

/**
 * Created by Administrator on 2020/9/14.
 */

class ExtendFragment : BaseFragment() {
    companion object {
        private var fragment: ExtendFragment? = null
            get() {
                if (field == null)
                    field = ExtendFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): ExtendFragment {
            if (bundle != null) {
                fragment?.arguments = bundle
            }
            return fragment!!
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_extend
    }

    override fun initView() {

    }

    override fun initEvent() {
        ft_extend_caicaicai.setOnClickListener {
            startActivity(Intent(mContext, NearbyActivity::class.java))
        }
        ft_extend_vcr.setOnClickListener {
            startActivity(Intent(mContext, VcrActivity::class.java))
        }
        ft_extend_diary.setOnClickListener {
            startActivity(Intent(mContext,DiaryActivity::class.java))
        }
    }

    override fun initData() {

    }
}
