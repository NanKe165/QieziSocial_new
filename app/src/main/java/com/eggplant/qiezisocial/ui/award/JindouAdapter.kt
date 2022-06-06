package com.eggplant.qiezisocial.ui.award

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.JindouEntry
import kotlinx.android.synthetic.main.ap_jindou.view.*

/**
 * Created by Administrator on 2022/1/27.
 */

class JindouAdapter(data: List<JindouEntry>?) : BaseQuickAdapter<JindouEntry, BaseViewHolder>(R.layout.ap_jindou, data) {

    override fun convert(helper: BaseViewHolder, item: JindouEntry) {
        helper.itemView.ap_jd_title.text=item.title
        helper.itemView.ap_jd_jdcount.text="+${item.jdcount}金豆"
        helper.itemView.ap_jd_img.setImageResource(item.locaImg)
    }

}
