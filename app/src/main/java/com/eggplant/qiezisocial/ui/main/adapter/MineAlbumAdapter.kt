package com.eggplant.qiezisocial.ui.main.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.AlbumMultiItem
import kotlinx.android.synthetic.main.ap_mine_album.view.*
import kotlinx.android.synthetic.main.ap_takephoto.view.*

/**
 * Created by Administrator on 2020/4/20.
 */

class MineAlbumAdapter(mContext: Context?, data: MutableList<AlbumMultiItem<String>>?) : BaseMultiItemQuickAdapter<AlbumMultiItem<String>, BaseViewHolder>(data) {
    var context: Context? = mContext

    init {
        addItemType(AlbumMultiItem.TYPE_ALBUM, R.layout.ap_mine_album)
        addItemType(AlbumMultiItem.TYPE_TAKEPHOTO, R.layout.ap_takephoto)
    }

    override fun convert(helper: BaseViewHolder?, item: AlbumMultiItem<String>?) {

        when (helper?.itemViewType) {
            AlbumMultiItem.TYPE_ALBUM -> {
                item?.let {
                    Glide.with(mContext).load( it.bean).into(helper.itemView.ap_album)
                }
            }
            AlbumMultiItem.TYPE_TAKEPHOTO
                    ->{
                    helper.itemView.ap_takephoto.setImageResource(R.mipmap.sp_takephoto)
            }
        }
    }


}
