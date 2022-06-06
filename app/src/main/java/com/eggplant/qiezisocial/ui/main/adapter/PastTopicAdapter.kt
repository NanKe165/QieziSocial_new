package com.eggplant.qiezisocial.ui.main.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.PastTopicEntry
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_past_topic.view.*

/**
 * Created by Administrator on 2021/4/25.
 */

class PastTopicAdapter(data: List<PastTopicEntry>?) : BaseQuickAdapter<PastTopicEntry, BaseViewHolder>(R.layout.ap_past_topic, data) {

    override fun convert(helper: BaseViewHolder, item: PastTopicEntry) {
        helper.itemView.ap_ptopic_title.text = item.text
        helper.itemView.ap_ptopic_usercount.text = "${item.usercount}"
        helper.itemView.ap_ptopic_topiccount.text = "${item.topiccount}"
        if (item.created > 0) {
            //php时间戳为10位，应转为java13位
            var time = item.created*1000
            helper.itemView.ap_ptopic_time.text = when {
                DateUtils.isToday("$time") -> DateUtils.timeMinute("$time")
                DateUtils.isSameWeek("$time") -> DateUtils.getWeek(time)
                DateUtils.IsYesterday(time) -> "昨天 "
                else -> DateUtils.timeM("$time")
            }
        }


    }
}
