package com.eggplant.qiezisocial.ui.pub

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import kotlinx.android.synthetic.main.ap_pub_label.view.*

/**
 * Created by Administrator on 2021/6/8.
 */

class PubLabelAdapter(data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.ap_pub_label, data) {
    var labelbg = intArrayOf(R.drawable.label_bg1, R.drawable.label_bg2, R.drawable.label_bg3, R.drawable.label_bg4)
    var selectPosition=-1
    var selectLabel=""
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.itemView.ap_pub_label_txt.text = item
        helper.itemView.ap_pub_label_bg.background=ContextCompat.getDrawable(mContext,labelbg[helper.adapterPosition%4])
        if (selectPosition==helper.adapterPosition){
            helper.itemView.ap_pub_label_txt.setTextColor(ContextCompat.getColor(mContext,R.color.white))
            helper.itemView.ap_pub_label_bg.alpha=1.0f
        }else{
            helper.itemView.ap_pub_label_txt.setTextColor(ContextCompat.getColor(mContext,R.color.tv_black))
            helper.itemView.ap_pub_label_bg.alpha=0.5f
        }
        helper.itemView.setOnClickListener {
            selectPosition=helper.adapterPosition
            notifyDataSetChanged()
            selectLabel=item
        }
    }
}
