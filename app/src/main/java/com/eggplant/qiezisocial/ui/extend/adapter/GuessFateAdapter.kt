package com.eggplant.qiezisocial.ui.extend.adapter

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_guess_fate.view.*

/**
 * Created by Administrator on 2020/7/7.
 */

class GuessFateAdapter(data: List<GuessFateEntry>?) : BaseQuickAdapter<GuessFateEntry, BaseViewHolder>(R.layout.ap_guess_fate, data) {

    override fun convert(helper: BaseViewHolder, item: GuessFateEntry) {
        if (!TextUtils.isEmpty(item.pic)) {
            Glide.with(mContext).load(API.PIC_PREFIX + item.pic).into(helper.itemView.ap_guessfate_head)
        } else {
            Glide.with(mContext).load(API.PIC_PREFIX + item.userinfor.face).into(helper.itemView.ap_guessfate_head)
        }
        helper.itemView.ap_guessfate_nick.text = item.userinfor.nick
        helper.itemView.ap_guessfate_size.text = "(${item.question.size})"
        helper.itemView.ap_guessfate_content.text="我有${item.question.size}个小心思猜对${item.passsum}个自动加好友哦"
        helper.itemView.ap_guessfate_title.text=item.question[0].title

    }
}
