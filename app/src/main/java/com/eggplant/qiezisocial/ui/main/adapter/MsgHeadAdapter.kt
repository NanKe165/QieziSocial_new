package com.eggplant.qiezisocial.ui.main.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_msg_head.view.*

/**
 * Created by Administrator on 2022/2/9.
 */

class MsgHeadAdapter(data: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_msg_head, data) {


    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
        Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_msg_head_img)
        if (item.online=="0"){
            helper.itemView.ap_msg_head_online.visibility=View.GONE
        }else{
            helper.itemView.ap_msg_head_online.visibility=View.VISIBLE
        }

        if (item.msgNum > 0) {
            helper.itemView.ap_msg_head_hint.visibility = View.VISIBLE
            helper.itemView.ap_msg_head_hint.text = item.msgNum.toString()
        } else {
            helper.itemView.ap_msg_head_hint.visibility = View.GONE
        }
    }
}
