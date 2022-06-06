package com.eggplant.qiezisocial.ui.main.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_visitor.view.*

/**
 * Created by Administrator on 2021/11/3.
 */

class VisitorAdapter(data: List<UserEntry>?) : BaseQuickAdapter<UserEntry, BaseViewHolder>(R.layout.ap_visitor, data) {
    var newVisitorNumb=0
    override fun convert(helper: BaseViewHolder, item: UserEntry) {
        helper.itemView.ap_visitor_nick.text=item.nick
        if (item.face.isNotEmpty())
            Glide.with(mContext).load(API.PIC_PREFIX+item.face).into(helper.itemView.ap_visitor_head)
        if(newVisitorNumb>0&&helper.adapterPosition<newVisitorNumb){
            helper.itemView.ap_visitor_hint.visibility=View.VISIBLE
        }else{
            helper.itemView.ap_visitor_hint.visibility=View.GONE
        }
    }
}
