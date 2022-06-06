package com.eggplant.qiezisocial.ui.main.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_lchat_mine.view.*

/**
 * Created by Administrator on 2021/4/19.
 */

class LchatAdapter
/**
 * Same as QuickAdapter#QuickAdapter(Context,int) but with
 * some initialization data.
 *
 * @param data A new list is created out of this one to avoid mutable list
 */
(data: List<ChatMultiEntry<ChatEntry>>?) : BaseMultiItemQuickAdapter<ChatMultiEntry<ChatEntry>, BaseViewHolder>(data) {
    init {
        addItemType(ChatMultiEntry.CHAT_MINE, R.layout.ap_lchat_mine)
        addItemType(ChatMultiEntry.CHAT_OTHER, R.layout.ap_lchat_other)
    }

    override fun convert(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        helper.itemView.ap_lchat_nick.text = item.bean.nick
        helper.itemView.ap_lchat_txt.text = item.bean.content
        val layoutType=item.bean.layoutType
        if (item.itemType == ChatMultiEntry.CHAT_OTHER) {
            if (item.bean.content.isNotEmpty()) {
                helper.itemView.ap_lchat_hint.text = ""
            } else {
                if (layoutType=="gpic") {
                    helper.itemView.ap_lchat_hint.text = "${item.bean.nick} 看了你的照片"
                }else if (layoutType=="gvideo"){
                    helper.itemView.ap_lchat_hint.text = "${item.bean.nick} 看了你的视频"
                }
                helper.itemView.ap_lchat_txt.text = ""
                helper.itemView.ap_lchat_nick.text = ""
            }
        }
        if (item.bean.face.isNotEmpty()) {
            Glide.with(mContext).load(API.PIC_PREFIX + item.bean.face).into(helper.itemView.ap_lchat_head)
        }
    }
}
