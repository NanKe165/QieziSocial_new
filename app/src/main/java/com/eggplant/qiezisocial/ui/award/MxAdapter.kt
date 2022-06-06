package com.eggplant.qiezisocial.ui.award

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.MxEntry
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_mx.view.*

/**
 * Created by Administrator on 2022/1/27.
 */

class MxAdapter(data: List<MxEntry>?) : BaseQuickAdapter<MxEntry, BaseViewHolder>(R.layout.ap_mx, data) {

    override fun convert(helper: BaseViewHolder, item: MxEntry) {
        helper.itemView.ap_mx_title.text=item.text
        helper.itemView.ap_mx_count.text="+${item.num}金豆"
        val time = item.time*1000
        if (time != 0L) {
            when {
                DateUtils.isToday(time.toString()) -> helper.itemView.ap_mx_time.text = DateUtils.timeMinute(time.toString())
                DateUtils.isSameWeek(time.toString()) -> helper.itemView.ap_mx_time.text = DateUtils.getWeek(time)
                DateUtils.IsYesterday(time) -> helper.itemView.ap_mx_time.text = "昨天"
                else -> helper.itemView.ap_mx_time.text = DateUtils.getMonth(time.toString())
            }
        } else {
            helper.itemView.ap_mx_time.text = ""
        }

    }
}
