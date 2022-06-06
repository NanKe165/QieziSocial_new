package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_map_img.view.*

/**
 * Created by Administrator on 2021/10/21.
 */

class MapImgAdapter(data: List<MediaEntry>?) : BaseQuickAdapter<MediaEntry, BaseViewHolder>(R.layout.ap_map_img, data) {

    override fun convert(helper: BaseViewHolder, item: MediaEntry) {
            Glide.with(mContext).load(API.PIC_PREFIX+item.org).into(helper.itemView.ap_map_img)

    }
}
