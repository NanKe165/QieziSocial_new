package com.eggplant.qiezisocial.ui.setting.fragment

import android.support.v7.widget.StaggeredGridLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.setting.ModifyActivity
import com.eggplant.qiezisocial.ui.setting.adapter.ModifyLabelAdapter
import kotlinx.android.synthetic.main.fragment_modify_label.*

/**
 * Created by Administrator on 2020/6/23.
 */

class ModifyLabelFragment : BaseFragment() {
    lateinit var adapter: ModifyLabelAdapter
    var data = ArrayList<String>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_modify_label
    }

    override fun initView() {
        adapter = ModifyLabelAdapter(null)
        val manager = StaggeredGridLayoutManager( 8, StaggeredGridLayoutManager.HORIZONTAL)

        modify_label_ry.layoutManager = manager
        modify_label_ry.adapter = adapter
    }


    override fun initData() {

        adapter.setNewData(application.loginEntry?.interest)
    }

    override fun initEvent() {
        adapter.setOnLabelSelectListener { list ->
            modify_label_hint.text = "(${list.size}/4)"
        }
        modify_label_sure.setOnClickListener {
            var selectLabel = adapter.selectLabel
            var insterest = StringBuffer()
            selectLabel.forEach {
                insterest.append("$it ")
            }
            (activity as ModifyActivity).modify(insterest.toString().trimEnd())
        }
    }

}
