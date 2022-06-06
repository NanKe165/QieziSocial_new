package com.eggplant.qiezisocial.ui.main.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import kotlinx.android.synthetic.main.ap_single_img.view.*

/**
 * Created by Administrator on 2022/4/27.
 */

class SingleImageAdapter(data: List<Int>?) : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.ap_single_img, data) {

    override fun convert(helper: BaseViewHolder, item: Int) {
            helper.itemView.ap_single_img.setImageResource(item)
    }
}
