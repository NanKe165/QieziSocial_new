package com.eggplant.qiezisocial.ui.setting.fragment

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.setting.ModifyActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import kotlinx.android.synthetic.main.fragment_modify_sign.*

/**
 * Created by Administrator on 2020/6/22.
 */

class ModifySignFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_modify_sign
    }

    override fun initView() {
        var txt = arguments?.getString("txt")
        if (!TextUtils.isEmpty(txt)) {
            modify_sign.hint = txt
        }
    }

    lateinit var lintener: TextWatcher
    override fun initEvent() {
        lintener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { modify_sign_hint.text = "(${it.length}/18)" }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
        modify_sign.addTextChangedListener(lintener)
        modify_sign_sure.setOnClickListener {
         var sign=   modify_sign.text.toString().trimEnd()
            if (!TextUtils.isEmpty(sign)) {
                (activity as ModifyActivity).modify(sign)
            }else{
                TipsUtil.showToast(mContext,"内容不能为空")
            }
        }
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        modify_sign.removeTextChangedListener(lintener)
        super.onDestroyView()
    }


}
