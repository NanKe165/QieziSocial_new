package com.eggplant.qiezisocial.ui.main.adapter

import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.StateEntry
import kotlinx.android.synthetic.main.ap_select_state.view.*

/**
 * Created by Administrator on 2021/2/2.
 */

class SelectStateAdapter(data: List<StateEntry>?) : BaseQuickAdapter<StateEntry, BaseViewHolder>(R.layout.ap_select_state, data) {
    var selectItem = -1

    override fun convert(helper: BaseViewHolder, item: StateEntry) {
        if (helper.adapterPosition == selectItem) {
            helper.itemView.ap_select_state_bg.background = ContextCompat.getDrawable(mContext, R.drawable.ap_state_select_bg)
        } else {
            helper.itemView.ap_select_state_bg.background = ContextCompat.getDrawable(mContext, R.drawable.ap_state_unselect_bg)
        }
        helper.itemView.ap_select_state_tv.text = item.des
        Glide.with(mContext).load(item.img).into(helper.itemView.ap_select_state_img)
        helper.itemView.ap_select_state_bg.setOnClickListener {
            if (selectItem==helper.adapterPosition){
                selectItem=-1
                notifyItemChanged(helper.adapterPosition)
            }else{
                var lastSelectItem=selectItem
                selectItem=helper.adapterPosition
                notifyItemChanged(selectItem)
                notifyItemChanged(lastSelectItem)
            }
        }

    }
}
