package com.eggplant.qiezisocial.ui.main.adapter

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.QzTextView
import kotlinx.android.synthetic.main.ap_card.view.*
import java.util.*

/**
 * Created by Administrator on 2022/3/4.
 */

class CardAdapter(data: List<BoxEntry>?) : BaseQuickAdapter<BoxEntry, BaseViewHolder>(R.layout.ap_card, data) {
    val random = Random()
    var lastP = 0
    var lastR = -1
    var repeatRotationContent = 0
    var ryWidth = 0
    var roleBgIndex = 0
    var roleBg = intArrayOf(R.drawable.role_bg1, R.drawable.role_bg2, R.drawable.role_bg3, R.drawable.role_bg4)
    override fun convert(helper: BaseViewHolder, item: BoxEntry) {
        helper.itemView.ap_card_nick.text = item.userinfor.nick
        helper.itemView.ap_card_content.text = item.text
        helper.itemView.ap_card_content.setFontFormat(item.font)
        Glide.with(mContext).load(API.PIC_PREFIX + item.userinfor.face).into(helper.itemView.ap_card_img)
        if (item.userinfor.sex == "男") {
            helper.itemView.ap_card_sex.setImageResource(R.mipmap.sex_boy)
        } else {
            helper.itemView.ap_card_sex.setImageResource(R.mipmap.sex_girl)
        }
        val careers = item.userinfor.careers
        if (careers == null || careers.isEmpty()) {
            helper.itemView.ap_card_state.visibility = View.GONE
        } else {
            helper.itemView.ap_card_state.visibility = View.VISIBLE
            helper.itemView.ap_card_state.text = careers
            setRoleBg(helper.itemView.ap_card_state)
        }


        val layoutParams = helper.itemView.ap_card_gp.layoutParams as FrameLayout.LayoutParams
        var gpWidth = ryWidth

        if (gpWidth == 0)
            gpWidth = ScreenUtil.getDisplayWidthPixels(mContext)
//        Log.i("cardAp", "gpWidth :$gpWidth")
        val x0 = gpWidth / 2
        val we1 = random.nextFloat() * (0.9f - 0.7f) + 0.7f
        val we2 = random.nextFloat() * (1 - 0.1f) + 0.1f
        var itemX = 0
        val txtLength = item.text.length
        var fw = 1.0F * txtLength / 20
        if (fw >= 1) {
            fw = 1.0f
        }
        var width = (fw * gpWidth).toInt()
//        Log.i("cardAp", "width--- :$width   fw$fw ")
        if (width < ScreenUtil.dip2px(mContext, 200)) {
//            Log.i("cardAp", "width <200dp")
            width = ScreenUtil.dip2px(mContext, 200)
        }

        var r = random.nextInt(2)

        if (r == lastR) {
            repeatRotationContent++
        }
        if (repeatRotationContent >= 2) {
            r = if (lastR == 0) {
                1
            } else {
                0
            }
            lastR = r
            repeatRotationContent = 0
        }
        if (lastP == 0) {
            lastP = 1
            itemX = when {
                width > gpWidth * 2 / 3 -> x0 - (width * 2 / 3 * we1).toInt()
                width > x0 -> x0 - (width * we1).toInt()
                width < x0 / 2 -> x0 - (width + (x0 - width) * we2).toInt()
                else -> x0 - (width + (x0 - width) * we2).toInt()
            }
        } else {
            lastP = 0
            itemX = when {
                width > gpWidth * 2 / 3 -> x0 - (width / 3 * we1).toInt()
                width > x0 -> x0 + (width * (1.0f - we1)).toInt()
                width < x0 / 2 -> x0 + ((x0 - width) * we2).toInt()
                else -> x0 + ((x0 - width) * we2).toInt()
            }
        }
        layoutParams.setMargins(itemX, 0, 0, 0)
        layoutParams.width = width
        helper.itemView.ap_card_gp.layoutParams = layoutParams
        if (r == 0) {
            lastR = 0
            helper.itemView.rotation = 10 * we2
        } else {
            lastR = 1
            helper.itemView.rotation = -10 * we2
        }
        helper.addOnClickListener(R.id.ap_card_gp)
    }

    private fun setRoleBg(tv: QzTextView) {
        tv.background = ContextCompat.getDrawable(mContext, roleBg[roleBgIndex % 4])
        roleBgIndex++
    }

}
