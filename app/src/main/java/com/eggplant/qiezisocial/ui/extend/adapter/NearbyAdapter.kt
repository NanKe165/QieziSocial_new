package com.eggplant.qiezisocial.ui.extend.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.LocationUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.image.RoundRectImageView
import kotlinx.android.synthetic.main.ap_nearby.view.*

/**
 * Created by Administrator on 2020/12/16.
 */

class NearbyAdapter(data: List<UserEntry>?) : BaseQuickAdapter<UserEntry, BaseViewHolder>(R.layout.ap_nearby, data) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: UserEntry) {
        Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_nearby_head)
        helper.itemView.ap_nearby_nick.text = item.nick
        helper.itemView.ap_nearby_age.text = "${DateUtils.dataToAge(item.birth)}岁"
        if (item.sex == "男") {
            helper.itemView.ap_nearby_sex.setImageResource(R.mipmap.sex_boy)
        } else {
            helper.itemView.ap_nearby_sex.setImageResource(R.mipmap.sex_girl)
        }
        helper.itemView.ap_nearby_careers.text = item.careers
        if (item.sign.isNotEmpty()) {
            helper.itemView.ap_nearby_sign.visibility = View.VISIBLE
            helper.itemView.ap_nearby_sign.text = item.sign
        } else {
            helper.itemView.ap_nearby_sign.visibility = View.GONE
        }
        if (item.pic.isEmpty()) {
            helper.itemView.ap_nearby_imgs.visibility = View.GONE
        } else {
            helper.itemView.ap_nearby_imgs.visibility = View.VISIBLE
            helper.itemView.ap_nearby_imgs.removeAllViews()
            item.pic.forEachIndexed { index, s ->
                if (index < 3) {
                    val img = RoundRectImageView(mContext)
                    img.scaleType = ImageView.ScaleType.CENTER_CROP
                    val params = LinearLayout.LayoutParams(ScreenUtil.dip2px(mContext, 70), LinearLayout.LayoutParams.MATCH_PARENT)
                    params.setMargins(0, 0, 10, 0)
                    img.layoutParams = params
                    helper.itemView.ap_nearby_imgs.addView(img)
                    Glide.with(mContext).load(API.PIC_PREFIX + s).into(img)
                }
            }
        }
        val latitude = item.latitude
        val longitude = item.longitude
        val myLatitude = QzApplication.get().infoBean!!.latitude
        val myLongitude = QzApplication.get().infoBean!!.longitude
        if (latitude.isNotEmpty()&&longitude.isNotEmpty()&&myLatitude.isNotEmpty()&&myLongitude.isNotEmpty()){
            helper.itemView.ap_nearby_distance.text="${LocationUtils.getDistance(latitude.toDouble(),longitude.toDouble(),myLatitude.toDouble(),myLongitude.toDouble()).toInt()}米以内"
        }else{
            helper.itemView.ap_nearby_distance.text=""
        }

    }
}
