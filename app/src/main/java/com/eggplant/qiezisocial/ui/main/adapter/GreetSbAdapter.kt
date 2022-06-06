package com.eggplant.qiezisocial.ui.main.adapter

import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.event.GreetSbEvent
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_greetsb.view.*

/**
 * Created by Administrator on 2022/4/14.
 */

class GreetSbAdapter(data: List<GreetSbEvent>?) : BaseQuickAdapter<GreetSbEvent, BaseViewHolder>(R.layout.ap_greetsb, data) {

    override fun convert(helper: BaseViewHolder, item: GreetSbEvent) {
            helper.itemView.ap_greetsb_nick.text=item.from_userinfor.nick
            Glide.with(mContext).load(API.PIC_PREFIX+item.from_userinfor.face).into(helper.itemView.ap_greetsb_head)
            when(item.act){
                "ggreet"->{
                    helper.itemView.ap_greetsb_txt.text="给你打个招呼"
                    helper.itemView.ap_greetsb_img.setImageResource(R.mipmap.icon_hello)
                }
                "gbutter"->{
                    if (QzApplication.get().loginEntry?.userinfor?.sex=="男") {
                        helper.itemView.ap_greetsb_txt.text = "今天你最帅"
                    }else{
                        helper.itemView.ap_greetsb_txt.text = "今天你最美"
                    }
                    helper.itemView.ap_greetsb_img.setImageResource(R.mipmap.icon_good)
                }
                "gcomfort"->{
                    helper.itemView.ap_greetsb_txt.text="你好久都没理我啦"
                    helper.itemView.ap_greetsb_img.setImageResource(R.mipmap.icon_comfort)
                }
            }

        val created = "${item.created}"
        val time = created.toLong()
        val timeTv = helper.itemView.ap_greetsb_time
        if (!TextUtils.isEmpty(created) && !TextUtils.equals(created, "0")) {
            when {
                DateUtils.isToday(created) -> timeTv.text = DateUtils.timeMinute(created)
                DateUtils.isSameWeek(created) -> timeTv.text = DateUtils.getWeek(time) + " " + DateUtils.timeMinute(created)
                DateUtils.IsYesterday(time) -> timeTv.text = "昨天 " + DateUtils.timeMinute(created)
                else -> timeTv?.text = DateUtils.timet(created)
            }
            timeTv?.visibility = View.VISIBLE
        } else {
            timeTv.text = ""
        }
    }
}
