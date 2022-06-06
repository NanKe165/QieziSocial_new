package com.eggplant.qiezisocial.ui.main.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_friendmsg.view.*

/**
 * Created by Administrator on 2020/4/23.
 */

class FriendMsgAdapter(dataList: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_friendmsg, dataList) {


    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
        val type = item.type
        helper.addOnClickListener(R.id.ap_fmsg_head)
        val newMsgTime = item.newMsgTime
        val created = item.created
        if (newMsgTime != 0L) {
            when {
                DateUtils.isToday(newMsgTime.toString()) -> helper.itemView.ap_fmsg_time.text = DateUtils.timeMinute(newMsgTime.toString())
                DateUtils.isSameWeek(newMsgTime.toString()) -> helper.itemView.ap_fmsg_time.text = DateUtils.getWeek(newMsgTime)
                DateUtils.IsYesterday(newMsgTime) -> helper.itemView.ap_fmsg_time.text = "昨天"
                else -> helper.itemView.ap_fmsg_time.text = DateUtils.getMonth(newMsgTime.toString())
            }
        } else {
            helper.itemView.ap_fmsg_time.text = ""
        }
        if (item.msgNum > 0) {
            helper.itemView.ap_fmsg_hint.visibility = View.VISIBLE
            helper.itemView.ap_fmsg_hint.text = item.msgNum.toString()
        } else {
            helper.itemView.ap_fmsg_hint.visibility = View.GONE
        }
        helper.itemView.ap_fmsg_nick.text = item.nick
        if (type=="gfriendlist") {
            if (item.msg!=null&&item.msg.isNotEmpty()) {
                helper.itemView.ap_fmsg_reply.visibility=View.VISIBLE
                helper.itemView.ap_fmsg_reply.text = item.msg
            }else{
                helper.itemView.ap_fmsg_reply.visibility=View.GONE
            }
        }else if (type=="gapplylist"){
            helper.itemView.ap_fmsg_reply.visibility=View.VISIBLE
            helper.itemView.ap_fmsg_reply.text = "邀请您添加为好友"
            helper.itemView.ap_fmsg_hint.visibility = View.VISIBLE
            helper.itemView.ap_fmsg_hint.text = "1"
            when {
                DateUtils.isToday(created.toString()) -> helper.itemView.ap_fmsg_time.text = DateUtils.timeMinute(created.toString())
                DateUtils.isSameWeek(created.toString()) -> helper.itemView.ap_fmsg_time.text = DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> helper.itemView.ap_fmsg_time.text = "昨天"
                else -> helper.itemView.ap_fmsg_time.text = DateUtils.getMonth(created.toString())
            }
        }
        Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_fmsg_head)
    }


}