package com.eggplant.qiezisocial.ui.setting.adapter

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import kotlinx.android.synthetic.main.ap_modify_label.view.*

/**
 * Created by Administrator on 2020/6/23.
 */

class ModifyLabelAdapter(data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.ap_modify_label, data) {
    var colors = intArrayOf(R.color.label_color1, R.color.label_color2, R.color.label_color3, R.color.label_color4, R.color.label_color5,
            R.color.label_color6, R.color.label_color7)
    var selectLabel = ArrayList<String>()
    override fun convert(helper: BaseViewHolder, item: String) {
//        helper.itemView.ap_modify_label_tv.setOpenRegionUrl(false)
//        helper.itemView.ap_modify_label_tv.setDontConsumeNonUrlClicks(false)
        helper.itemView.ap_modify_label_tv.text = item
        if (selectLabel.contains(item)) {
//            helper.itemView.ap_modify_label_tv.setTextColor(ContextCompat.getColor(mContext,R.color.tv_black))
            helper.itemView.ap_modify_label_tv.background= ContextCompat.getDrawable(mContext, R.drawable.lab_select)
        } else {
//            helper.itemView.ap_modify_label_tv.setTextColor(ContextCompat.getColor(mContext,colors[helper.adapterPosition % colors.size]))
            helper.itemView.ap_modify_label_tv.background= ContextCompat.getDrawable(mContext, R.drawable.lab_unselect)
        }
        helper.itemView.ap_modify_label_tv.setOnClickListener { v ->
            if (selectLabel.contains(item)) {
                selectLabel.remove(item)
                v.background = ContextCompat.getDrawable(mContext, R.drawable.lab_unselect)
//                helper.itemView.ap_modify_label_tv.setTextColor(ContextCompat.getColor(mContext,colors[helper.adapterPosition % colors.size]))
                mListener?.invoke(selectLabel)
            } else if (selectLabel.size < 4) {
                selectLabel.add(item)
                v.background = ContextCompat.getDrawable(mContext, R.drawable.lab_select)
//                helper.itemView.ap_modify_label_tv.setTextColor(ContextCompat.getColor(mContext,R.color.tv_black))
                mListener?.invoke(selectLabel)
            }
        }
    }

    var mListener: ((ArrayList<String>) -> Unit)? = null
    fun setOnLabelSelectListener(listener: ((ArrayList<String>) -> Unit)) {
        this.mListener = listener
    }
}
