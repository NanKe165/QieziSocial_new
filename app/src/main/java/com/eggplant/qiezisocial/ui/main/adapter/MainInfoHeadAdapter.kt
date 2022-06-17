package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_maininfo_head.view.*

/**
 * Created by Administrator on 2022/6/17.
 */

class MainInfoHeadAdapter(data: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_maininfo_head, data) {

    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
        Glide.with(mContext).load(API.Companion.PIC_PREFIX+item.face).into(helper.itemView.ap_mianinfo_head)

    }
}
