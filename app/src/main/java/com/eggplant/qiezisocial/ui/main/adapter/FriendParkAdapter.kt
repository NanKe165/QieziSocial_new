package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_fd_park.view.*

/**
 * Created by Administrator on 2022/4/6.
 */

class FriendParkAdapter(data: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_fd_park, data) {

    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
        if (item.face.isNotEmpty())
            Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_fd_park_head)
        helper.itemView.ap_fd_park_nick.text=item.nick


    }
}
