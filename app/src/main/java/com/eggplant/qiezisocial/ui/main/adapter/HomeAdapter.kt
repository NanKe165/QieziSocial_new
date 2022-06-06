package com.eggplant.qiezisocial.ui.main.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.image.CircleImageView
import com.othershe.combinebitmap.CombineBitmap
import com.othershe.combinebitmap.layout.DingLayoutManager
import kotlinx.android.synthetic.main.ap_home.view.*


/**
 * Created by Administrator on 2020/4/16.
 */

class HomeAdapter(data: List<BoxEntry>?) : BaseQuickAdapter<BoxEntry, BaseViewHolder>(R.layout.ap_home, data) {
    var mineType = false
    var showing = true
    var roleBgIndex = 0
    var roleBg = intArrayOf(R.drawable.role_bg1, R.drawable.role_bg2, R.drawable.role_bg3, R.drawable.role_bg4)
    override fun convert(helper: BaseViewHolder, item: BoxEntry?) {
        helper.addOnClickListener(R.id.ap_home_head)
//        helper.itemView.ap_home_name.setOpenRegionUrl(false)
//        helper.itemView.ap_home_content.setOpenRegionUrl(false)
//        helper.addOnClickListener(R.id.ap_home_play)
//        helper.addOnClickListener(R.id.ap_home_img)
        if (showing) {

            var anim = ObjectAnimator.ofFloat(helper.itemView.ap_home_rootview, "alpha", 0.2f, 1f)
            anim.duration = 200
            anim.start()
//            }
        } else {
            var anim = ObjectAnimator.ofFloat(helper.itemView.ap_home_rootview, "alpha", 1f,0.2f)
            anim.duration = 200
            anim.start()
//            }
        }

        if (item == null) {
            return
        }
        if (item.uid.toInt() == QzApplication.get().infoBean?.uid) {
            helper.itemView.ap_home_rootview.background = ContextCompat.getDrawable(mContext, R.drawable.tv_yellow_bg3)
        } else {
            helper.itemView.ap_home_rootview.background = ContextCompat.getDrawable(mContext, R.drawable.home_item_bg)
        }
        if (item.type == "boxquestiongroup" && item.personinfor.isNotEmpty()) {
            helper.itemView.ap_home_sex.visibility = View.GONE
            var imgs = ArrayList<String>()
            item.personinfor.forEachIndexed { index, userEntry ->
                if (index < 4) {
                    imgs.add(API.PIC_PREFIX + userEntry.face)
                }
            }
            createBitmap(mContext, helper.itemView.ap_home_head, *imgs.toTypedArray())
            helper.itemView.ap_home_name.text = "话题群聊(${item.person.size}人参与)"
        } else {
            helper.itemView.ap_home_name.text = item.userinfor.nick
            helper.itemView.ap_home_sex.visibility = View.VISIBLE
            Glide.with(mContext).load(API.PIC_PREFIX + item.userinfor.face).into(helper.itemView.ap_home_head)
        }

        if (TextUtils.equals(item.userinfor.sex, "男")) {
            helper.itemView.ap_home_sex.setImageResource(R.mipmap.sex_boy)
        } else {
            helper.itemView.ap_home_sex.setImageResource(R.mipmap.sex_girl)
        }
//        val mood = item.userinfor.mood
//        if (mood == null || mood.isEmpty()) {
//            helper.itemView.ap_home_state.visibility = View.GONE
//        } else {
//            helper.itemView.ap_home_state.visibility = View.VISIBLE
//            helper.itemView.ap_home_state.text = mood
//            setState(mood, helper.itemView.ap_home_state)
//        }
//        var role = item.role
//        if (role == null || role.isEmpty()) {
//            helper.itemView.ap_home_role.visibility = View.GONE
//        } else {
//            helper.itemView.ap_home_role.visibility = View.VISIBLE
//            helper.itemView.ap_home_role.text = role
//            setRoleBg(helper.itemView.ap_home_role)
//        }
       val careers= item.userinfor.careers
        if (careers==null||careers.isEmpty()){
            helper.itemView.ap_home_role.visibility = View.GONE
        }else{
            helper.itemView.ap_home_role.visibility = View.VISIBLE
            helper.itemView.ap_home_role.text = careers
            setRoleBg(helper.itemView.ap_home_role)
        }

        helper.itemView.ap_home_content.setFontFormat(item.font)
        helper.itemView.ap_home_content.text = item.text

        if (item.media != null && item.media.size > 0) {
            helper.itemView.ap_home_media_tag.visibility = View.VISIBLE
            var finalType = "pic"
            item.media.forEach {
                 if (TextUtils.equals(it.type, "video")) {
                    finalType = it.type
                }
            }
            if (TextUtils.equals(finalType, "pic")) {
                helper.itemView.ap_home_media_tag.setImageResource(R.mipmap.icon_pic_tag)
            } else if (TextUtils.equals(finalType, "video")) {
                helper.itemView.ap_home_media_tag.setImageResource(R.mipmap.icon_video_tag)
            }
        } else {
            helper.itemView.ap_home_media_tag.visibility = View.GONE
        }
//        if (item.answertime == 0 && !item.read && !mineType) {
//            helper.itemView.ap_home_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home_reply_hint, 0, 0, 0)
////            helper.itemView.ap_home_reply_hint.visibility = View.VISIBLE
//        } else {
//            helper.itemView.ap_home_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
////            helper.itemView.ap_home_reply_hint.visibility = View.GONE
//        }

//        if (!TextUtils.isEmpty(item.created) && !TextUtils.equals(item.created, "0")) {
//            //php时间戳为10位，应转为java13位
//            var created = item.created + "000"
//            var time = created.toLong()
//            helper.itemView.ap_home_time.text = when {
//                DateUtils.isToday(created) -> DateUtils.timeMinute(created)
//                DateUtils.isSameDate(created) -> DateUtils.getWeek(time)
//                DateUtils.IsYesterday(time) -> "昨天 "
//                else -> DateUtils.timeM(created)
//            }
//        }
    }

    private fun setRoleBg(tv: QzTextView) {
        tv.background = ContextCompat.getDrawable(mContext, roleBg[roleBgIndex % 4])
        roleBgIndex++
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


    override fun addData(newData: MutableCollection<out BoxEntry>) {
        var it = newData.iterator()
        while (it.hasNext()) {
            if (data.contains(it.next()))
                it.remove()
        }
        if (newData.isEmpty()) {
            return
        }
        super.addData(newData)
    }


//    override fun addData(position:Int,newData: MutableCollection<out BoxEntry>) {
//        var it = newData.iterator()
//        while (it.hasNext()) {
//            if (data.contains(it.next()))
//                it.remove()
//        }
//        if (newData.isEmpty()) {
//            return
//        }
//        Log.i("homeAdapter","newData size:${newData.size} ")
//        super.addData(position,newData)
//    }

    private fun createBitmap(context: Context, view: CircleImageView, vararg imgs: String) {
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
//
//                })
                .build()

    }


}
