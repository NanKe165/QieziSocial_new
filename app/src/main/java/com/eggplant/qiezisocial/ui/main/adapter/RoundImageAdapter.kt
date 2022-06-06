package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import kotlinx.android.synthetic.main.ap_roundimg.view.*

/**
 * Created by Administrator on 2021/10/14.
 */

class RoundImageAdapter(data: List<MediaEntry>?) : BaseQuickAdapter<MediaEntry, BaseViewHolder>(R.layout.ap_roundimg, data) {

    override fun convert(helper: BaseViewHolder, item: MediaEntry) {
        Glide.with(mContext).load(API.PIC_PREFIX+item.org).into(helper.itemView.ap_roundimg)
        helper.itemView.ap_roundimg.setOnClickListener {
            v->
            var imginfo = ArrayList<ImageInfo>()
            data.forEach {
                var info = ImageInfo()
                info.bigImageUrl = API.PIC_PREFIX + it.org
                info.thumbnailUrl = API.PIC_PREFIX + it.extra
                imginfo.add(info)
            }
            PrevUtils.onImageItemClick(mContext, v, helper.adapterPosition, imginfo)
//            PrevUtils.onImageItemClick(mContext,v,API.PIC_PREFIX+item.org,API.PIC_PREFIX+item.extra)
        }
    }
}
