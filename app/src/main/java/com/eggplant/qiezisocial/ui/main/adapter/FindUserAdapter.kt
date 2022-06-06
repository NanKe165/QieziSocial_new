package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.FindUserEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_find_user.view.*

class FindUserAdapter(data: List<FindUserEntry>?) : BaseQuickAdapter<FindUserEntry, BaseViewHolder>(R.layout.ap_find_user, data) {

    override fun convert(helper: BaseViewHolder, entry: FindUserEntry) {
       val item= entry.userinfor

        helper.itemView.ap_find_user_nick.text=item!!.nick
        helper.itemView.ap_find_user_card.text=item.card
        Glide.with(mContext).load(API.PIC_PREFIX+item.face).into(helper.itemView.ap_find_user_head)
    }
}
