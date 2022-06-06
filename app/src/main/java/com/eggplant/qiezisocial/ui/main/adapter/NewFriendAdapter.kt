package com.eggplant.qiezisocial.ui.main.adapter

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_new_friend.view.*

/**
 * Created by Administrator on 2020/6/5.
 */

class NewFriendAdapter(data: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_new_friend,data) {

    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
        Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_new_friend_head)
        if (TextUtils.equals(item.sex, "男")) {
            helper.itemView.ap_new_friend_sex.setImageResource(R.mipmap.sex_boy)
        } else {
            helper.itemView.ap_new_friend_sex.setImageResource(R.mipmap.sex_girl)
        }
        helper.itemView.ap_new_friend_nick.text = item.nick
        helper.addOnClickListener(R.id.ap_new_friend_agree)
    }
}
