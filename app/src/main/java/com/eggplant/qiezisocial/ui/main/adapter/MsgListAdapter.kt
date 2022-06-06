package com.eggplant.qiezisocial.ui.main.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.image.CircleImageView
import com.othershe.combinebitmap.CombineBitmap
import com.othershe.combinebitmap.layout.DingLayoutManager
import kotlinx.android.synthetic.main.ap_msg.view.*

/**
 * Created by Administrator on 2020/4/23.
 */

class MsgListAdapter(dataList: List<MainInfoBean>?) : BaseQuickAdapter<MainInfoBean, BaseViewHolder>(R.layout.ap_msg, dataList) {


    override fun convert(helper: BaseViewHolder, item: MainInfoBean) {
//        if (item.gsid != 0L) {
//            helper.itemView.ap_msg_sex.visibility = View.GONE
//        } else {
//            helper.itemView.ap_msg_sex.visibility = View.VISIBLE
//        }
//
//        if (TextUtils.equals(item.sex, "男")) {
//            helper.itemView.ap_msg_sex.setImageResource(R.mipmap.sex_boy)
//        } else {
//            helper.itemView.ap_msg_sex.setImageResource(R.mipmap.sex_girl)
//        }
        helper.addOnClickListener(R.id.ap_msg_head)
        val newMsgTime = item.newMsgTime
        if (newMsgTime != 0L) {
            when {
                DateUtils.isToday(newMsgTime.toString()) -> helper.itemView.ap_msg_time.text = DateUtils.timeMinute(newMsgTime.toString())
                DateUtils.isSameWeek(newMsgTime.toString()) -> helper.itemView.ap_msg_time.text = DateUtils.getWeek(newMsgTime)
                DateUtils.IsYesterday(newMsgTime) -> helper.itemView.ap_msg_time.text = "昨天"
                else -> helper.itemView.ap_msg_time.text = DateUtils.getMonth(newMsgTime.toString())
            }
        } else {
            helper.itemView.ap_msg_time.text = ""
        }
        if (item.msgNum > 0) {
            helper.itemView.ap_msg_hint.visibility = View.VISIBLE
            helper.itemView.ap_msg_hint.text = item.msgNum.toString()
        } else {
            helper.itemView.ap_msg_hint.visibility = View.GONE
        }
        helper.itemView.ap_msg_qstxt.text=""
        if (item.gsid != 0L || item.qsid != 0L) {
            helper.itemView.ap_msg_nick.text = item.nick
            helper.itemView.ap_msg_reply.text = "回复:${item.msg}"
            helper.itemView.ap_msg_reply.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            var list = item.qsUserFace?.split(",")
            var faceList = list?.filter {
                it.isNotEmpty()
            }
            if (item.gsid!=0L&&faceList != null && faceList.isNotEmpty()) {
                if (faceList.size>4){
                    faceList=faceList.subList(0,4)
                }
                createGropBitmap(mContext, helper.itemView.ap_msg_head, *faceList.toTypedArray())
            } else {
                Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_msg_head)
            }

            if (item.qsTxt.isNotEmpty()){
                var qsTxt=item.qsTxt
                helper.itemView.ap_msg_qstxt.text = qsTxt
            }
            val mood = item.mood
            if (mood == null || mood.isEmpty()) {
                helper.itemView.ap_msg_mood.visibility = View.GONE
            } else {
                helper.itemView.ap_msg_mood.visibility = View.VISIBLE
                helper.itemView.ap_msg_mood.text = mood
                setState(mood, helper.itemView.ap_msg_mood)
            }


        } else if (!TextUtils.isEmpty(item.`object`)) {
            //回答
            helper.itemView.ap_msg_nick.text = item.msg
            //问题
            helper.itemView.ap_msg_reply.text = item.`object`
            helper.itemView.ap_msg_reply.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_msg_widget, 0, 0, 0)
            Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_msg_head)
        } else {
            helper.itemView.ap_msg_nick.text = item.nick
            helper.itemView.ap_msg_reply.text = item.msg
            helper.itemView.ap_msg_reply.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            Glide.with(mContext).load(API.PIC_PREFIX + item.face).into(helper.itemView.ap_msg_head)
        }

    }

    private fun createGropBitmap(context: Context, view: CircleImageView, vararg imgs: String) {

        CombineBitmap.init(context)
                .setLayoutManager(DingLayoutManager()) // 必选， 设置图片的组合形式，支持WechatLayoutManager、DingLayoutManager
                .setSize(56) // 必选，组合后Bitmap的尺寸，单位dp
                .setGap(1) // 单个图片之间的距离，单位dp，默认0dp
//                .setGapColor() // 单个图片间距的颜色，默认白色
                .setPlaceholder(R.mipmap.normal_head) // 单个图片加载失败的默认显示图片
                .setUrls(*imgs) // 要加载的图片url数组
//                .setBitmaps() // 要加载的图片bitmap数组
//                .setResourceIds() // 要加载的图片资源id数组
                .setImageView(view) // 直接设置要显示图片的ImageView
                // 设置“子图片”的点击事件，需使用setImageView()，index和图片资源数组的索引对应
//                .setOnSubItemClickListener(OnSubItemClickListener { })
                // 加载进度的回调函数，如果不使用setImageView()方法，可在onComplete()完成最终图片的显示
//                .setOnProgressListener(object : OnProgressListener {
//                    override fun onComplete(bitmap: Bitmap?) {
//                        Log.e("homeadapter"," on complete")
//                        bitmap?.let {
//                            view.setImageBitmap(it)
//                        }
//                    }
//
//                    override fun onStart() {
//                        Log.e("homeadapter"," on start")
//                    }
//                })
                .build()

    }
    private fun setState(mood: String, stateTv: QzTextView) {
        var statedraw = 0
        var textColor = ContextCompat.getColor(mContext, R.color.white)
        var textbg: Drawable? = null
        when (mood) {
            mContext.getString(R.string.state1) -> {
                statedraw = R.mipmap.home_state_ku
                textColor = ContextCompat.getColor(mContext, R.color.tv_pink)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_pink_bg)
            }
            mContext.getString(R.string.state2) -> {
                statedraw = R.mipmap.home_state_liekai
                textColor = ContextCompat.getColor(mContext, R.color.color_orange)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_oragne_bg)
            }
            mContext.getString(R.string.state3) -> {
                statedraw = R.mipmap.home_state_kaixin
                textColor = ContextCompat.getColor(mContext, R.color.color_green)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_green_bg)
            }
            mContext.getString(R.string.state4) -> {
                statedraw = R.mipmap.home_state_kun
                textColor = ContextCompat.getColor(mContext, R.color.color_blue)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_blue_bg)
            }
            mContext.getString(R.string.state5) -> {
                statedraw = R.mipmap.home_state_fadai
                textColor = ContextCompat.getColor(mContext, R.color.color_pruple)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_prule_bg)
            }
            mContext.getString(R.string.state6) -> {
                statedraw = R.mipmap.home_state_gudan
                textColor = ContextCompat.getColor(mContext, R.color.color_orange)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_oragne_bg)
            }
            mContext.getString(R.string.state7) -> {
                statedraw = R.mipmap.home_state_youshang
                textColor = ContextCompat.getColor(mContext, R.color.color_blue)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_blue_bg)
            }
            mContext.getString(R.string.state8) -> {
                statedraw = R.mipmap.home_state_fennu
                textColor = ContextCompat.getColor(mContext, R.color.tv_pink)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_pink_bg)
            }
            mContext.getString(R.string.state9) -> {
                statedraw = R.mipmap.home_state_chigua
                textColor = ContextCompat.getColor(mContext, R.color.color_pruple)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_prule_bg)
            }
        }
        stateTv.setTextColor(textColor)
        stateTv.background = textbg
        stateTv.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }
}
