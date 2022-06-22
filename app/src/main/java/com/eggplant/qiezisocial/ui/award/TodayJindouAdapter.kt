package com.eggplant.qiezisocial.ui.award

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.TodayJindouEntry
import kotlinx.android.synthetic.main.ap_todayjindou.view.*

/**
 * Created by Administrator on 2022/6/22.
 */

class TodayJindouAdapter(data: List<TodayJindouEntry>?) : BaseQuickAdapter<TodayJindouEntry, BaseViewHolder>(R.layout.ap_todayjindou, data) {

    override fun convert(helper: BaseViewHolder, item: TodayJindouEntry) {
        helper.itemView.ap_td_jd_title.text=item.text
        if (item.stat=="miss"){
            helper.itemView.ap_td_jd_numb.text="(0/1)"
        }else{
            helper.itemView.ap_td_jd_numb.text="(1/1)"
        }
    }
}
