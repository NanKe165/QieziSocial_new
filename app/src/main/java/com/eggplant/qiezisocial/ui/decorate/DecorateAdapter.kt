package com.eggplant.qiezisocial.ui.decorate

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import kotlinx.android.synthetic.main.ap_dector.view.*

class DecorateAdapter(data: List<Int>?) : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.ap_dector, data) {

    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.itemView.ap_dector_img.setImageResource(item)

    }
}