package com.eggplant.qiezisocial.ui.setting.fragment

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.setting.ModifyActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import kotlinx.android.synthetic.main.fragment_modify_nick.*

/**
 * Created by Administrator on 2020/6/22.
 */

class ModifyNickFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_modify_nick
    }

    override fun initView() {
        var txt = arguments?.getString("txt")
        if (!TextUtils.isEmpty(txt)) {
            modify_nick.hint = txt
        }
    }

    lateinit var lintener: TextWatcher
    override fun initEvent() {
        lintener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { modify_nick_hint.text = "(${it.length}/8)" }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
        modify_nick.addTextChangedListener(lintener)
        modify_nick_sure.setOnClickListener {
            var nick = modify_nick.text.toString().trimEnd()
            if (!TextUtils.isEmpty(nick)) {
                (activity as ModifyActivity).modify(nick)
            } else {
                TipsUtil.showToast(mContext, "内容不能为空")
            }
        }
        modify_nick.setOnEditorActionListener { v, actionId, event ->
            event.keyCode == KeyEvent.KEYCODE_ENTER
        }
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        modify_nick_sure.removeTextChangedListener(lintener)
        super.onDestroyView()
    }
}
