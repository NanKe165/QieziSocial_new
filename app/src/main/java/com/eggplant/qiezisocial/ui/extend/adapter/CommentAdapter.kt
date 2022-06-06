package com.eggplant.qiezisocial.ui.extend.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.VcrCommentEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_comment.view.*

/**
 * Created by Administrator on 2020/7/16.
 */

class CommentAdapter(data: List<VcrCommentEntry>?) : BaseQuickAdapter<VcrCommentEntry, BaseViewHolder>(R.layout.ap_comment, data) {

    override fun convert(helper: BaseViewHolder, item: VcrCommentEntry) {
        helper.itemView.ap_comment_nick.text = item.userinfor.nick
        helper.itemView.ap_comment_content.text = item.title
        Glide.with(mContext).load(API.PIC_PREFIX+item.userinfor.face).into(helper.itemView.ap_comment_head)
        if (item.created >= 0) {
            //php时间戳为10位，应转为java13位
            var created = item.created* 1000
            var time = created.toString()
            helper.itemView.ap_comment_time.text = when {
                DateUtils.isToday(time) -> DateUtils.timeMinute(time)
                DateUtils.isSameWeek(time) -> DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> "昨天 "
                else -> DateUtils.timeM(time)
            }
        }
    }
}
