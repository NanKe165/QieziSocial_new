package com.eggplant.qiezisocial.ui.setting.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BlackListEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_friendlist.view.*

/**
 * Created by Administrator on 2020/6/11.
 */
class BlackListAdapter(data: List<BlackListEntry>?) : BaseQuickAdapter<BlackListEntry, BaseViewHolder>(R.layout.ap_friendlist, data) {

    override fun convert(helper: BaseViewHolder, item: BlackListEntry) {
        helper.itemView.name_tv.text = item.userinfor.nick
        Glide.with(mContext).load(API.PIC_PREFIX+item.userinfor.face).into(helper.itemView.head_img)
    }
}