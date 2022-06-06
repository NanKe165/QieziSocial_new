package com.eggplant.qiezisocial.ui.main.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.utils.DateUtils
import kotlinx.android.synthetic.main.ap_check_scene.view.*
import java.util.*

/**
 * Created by Administrator on 2022/4/28.
 */

class CheckSceneAdapter(data: List<ScenesEntry>?) : BaseQuickAdapter<ScenesEntry, BaseViewHolder>(R.layout.ap_check_scene, data) {
    private var imgs = ArrayList<Int>()
    init {
        imgs.add(R.drawable.icon_scene_img1)
        imgs.add(R.drawable.icon_scene_img2)
        imgs.add(R.drawable.icon_scene_img3)
        imgs.add(R.drawable.icon_scene_img4)
        imgs.add(R.drawable.icon_scene_img5)
        imgs.add(R.drawable.icon_scene_img6)
        imgs.add(R.drawable.icon_scene_img7)
        imgs.add(R.drawable.icon_scene_img8)
        imgs.add(R.drawable.icon_scene_img9)
        imgs.add(R.drawable.icon_scene_img10)
        imgs.add(R.drawable.icon_scene_img11)
        imgs.add(R.drawable.icon_scene_img12)
        imgs.add(R.drawable.icon_scene_img13)
        imgs.add(R.drawable.icon_scene_img14)
        imgs.add(R.drawable.icon_scene_img15)
        imgs.add(R.drawable.icon_scene_img16)
        imgs.add(R.drawable.icon_scene_img17)
        imgs.add(R.drawable.icon_scene_img18)
        imgs.add(R.drawable.icon_scene_img19)
        imgs.add(R.drawable.icon_scene_img20)
        imgs.add(R.drawable.icon_scene_img21)
        imgs.add(R.drawable.icon_scene_img22)
        imgs.add(R.drawable.icon_scene_img23)
        imgs.add(R.drawable.icon_scene_img24)
        imgs.add(R.drawable.icon_scene_img25)
        imgs.add(R.drawable.icon_scene_img26)
        imgs.add(R.drawable.icon_scene_img27)
        imgs.add(R.drawable.icon_scene_img28)
    }
    override fun convert(helper: BaseViewHolder, item: ScenesEntry) {
        helper.addOnClickListener(R.id.ap_check_scene_modify)
        helper.itemView.ap_check_scene_cause_gp.visibility=View.GONE
        helper.itemView.ap_check_scene_title.text=item.title
        helper.itemView.ap_check_scene_des.text=item.des
        if (item.stat=="正在审核"){
//            helper.itemView.ap_check_scene_cause_gp.visibility=View.VISIBLE
            helper.itemView.ap_check_scene_stat.setImageResource(R.mipmap.icon_check_stat1)
        }else if (item.stat=="审核不通过"||item.stat=="审核未通过"){
            helper.itemView.ap_check_scene_stat.setImageResource(R.mipmap.icon_check_stat3)
            helper.itemView.ap_check_scene_cause_gp.visibility=View.VISIBLE
            if (item.note!=null&&item.note.isNotEmpty()){
                helper.itemView.ap_check_scene_cause.text="未通过原因：${item.note}"
            }
        }else{
            helper.itemView.ap_check_scene_stat.setImageResource(R.mipmap.icon_check_stat2)
        }
        if (item.created!=null) {
            var created = (item.created.toLong() * 1000)
            var time = created.toString()
            helper.itemView.ap_check_scene_time.text = when {
                DateUtils.isToday(time) -> DateUtils.timeMinute(time)
                DateUtils.isSameWeek(time) -> DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> "昨天"
                else -> DateUtils.timeM(time)
            }
        }
        if (item.pic != null) {
            val pic = item.pic.toInt()
            if (pic < imgs.size) {
                helper.itemView.ap_check_scene_img.setImageResource(imgs[pic])
            } else {
                helper.itemView.ap_check_scene_img.setImageDrawable(null)
            }
        }

    }
}
