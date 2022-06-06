package com.eggplant.qiezisocial.ui.login.fragment

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import com.eggplant.qiezisocial.widget.scrollPicker.adapter.ScrollPickerAdapter
import kotlinx.android.synthetic.main.ft_select_object.*

/**
 * Created by Administrator on 2020/6/28.
 */

class SelectObjectFragment : BaseFragment() {
    var selectValue = ""


    override fun getLayoutId(): Int {
        return R.layout.ft_select_object
    }

    override fun initView() {
        var data = application.loginEntry?.objectlist
        ft_select_object_scrollPicker.layoutManager = LinearLayoutManager(mContext)
        val builder = ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mContext)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#E5E5E5")
                .setItemViewProvider(null)
                .setOnScrolledListener { v ->
                    val text = v.tag as String
                    if (text != null) {
                        selectValue = text
                    }
                }
                .setDataList(data)
        val mScrollPickerAdapter = builder.build()
        ft_select_object_scrollPicker.adapter = mScrollPickerAdapter
    }

    override fun initEvent() {
        ft_select_object_sure.setOnClickListener {
            if (!TextUtils.isEmpty(selectValue)) {
                (activity as LoginActivity).setObject(selectValue)
            }
        }
    }

    override fun initData() {


    }
}
