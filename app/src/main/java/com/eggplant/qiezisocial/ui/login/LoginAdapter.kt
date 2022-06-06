package com.eggplant.qiezisocial.ui.login

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.LoginMsgEntry
import kotlinx.android.synthetic.main.ap_login_mine.view.*
import kotlinx.android.synthetic.main.ap_login_other.view.*

/**
 * Created by Administrator on 2020/4/24.
 */

class LoginAdapter(mContext: Context, data: List<ChatMultiEntry<LoginMsgEntry>>?) : BaseMultiItemQuickAdapter<ChatMultiEntry<LoginMsgEntry>, BaseViewHolder>(data) {
    var context = mContext

    init {
        addItemType(ChatMultiEntry.CHAT_MINE, R.layout.ap_login_mine)
        addItemType(ChatMultiEntry.CHAT_OTHER, R.layout.ap_login_other)
    }

    override fun convert(helper: BaseViewHolder?, item: ChatMultiEntry<LoginMsgEntry>?) {
        var content = item?.bean?.content
        var type = item?.bean?.type
        when (helper?.itemViewType) {
            ChatMultiEntry.CHAT_OTHER -> {
                helper.itemView.ap_login_other_txt.text = content
            }
            ChatMultiEntry.CHAT_MINE -> {
                if (TextUtils.equals(type, "msg")) {
                    helper.itemView.ap_login_mine_img.visibility = View.GONE
                    helper.itemView.ap_login_mine_txt_gp.visibility = View.VISIBLE
                    helper.itemView.ap_login_mine_txt.text = content
                } else if (TextUtils.equals(type, "pic")) {
                    helper.itemView.ap_login_mine_img.visibility = View.VISIBLE
                    helper.itemView.ap_login_mine_txt_gp.visibility = View.GONE
                    Glide.with(mContext).load(content).into(helper.itemView.ap_login_mine_img)
                }

            }
        }
    }

}
