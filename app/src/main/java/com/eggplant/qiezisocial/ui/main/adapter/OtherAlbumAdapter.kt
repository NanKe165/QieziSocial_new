package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_other_album.view.*

/**
 * Created by Administrator on 2020/6/5.
 */

class OtherAlbumAdapter(data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.ap_other_album, data) {
    override fun convert(helper: BaseViewHolder, item: String) {

        Glide.with(mContext).load(API.PIC_PREFIX + item).into(helper.itemView.ap_other_album)
    }

}
