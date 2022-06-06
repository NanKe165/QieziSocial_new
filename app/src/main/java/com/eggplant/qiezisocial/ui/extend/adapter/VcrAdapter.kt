package com.eggplant.qiezisocial.ui.extend.adapter

import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.ap_vcr.view.*
import me.samlss.bloom.Bloom
import me.samlss.bloom.effector.BloomEffector
import me.samlss.bloom.listener.BloomListener

/**
 * Created by Administrator on 2020/7/14.
 */

class VcrAdapter(activity: BaseActivity, data: List<VcrEntry>?) : BaseQuickAdapter<VcrEntry, BaseViewHolder>(R.layout.ap_vcr, data) {
    var activity: BaseActivity = activity

    private var collectVisiable = true
    override fun convert(helper: BaseViewHolder, item: VcrEntry) {
        helper.addOnClickListener(R.id.ap_vcr_report)
        helper.addOnClickListener(R.id.ap_vcr_share)
        helper.addOnClickListener(R.id.ap_vcr_comment)
//        helper.addOnClickListener(R.id.ap_vcr_like)
        helper.addOnClickListener(R.id.ap_vcr_follow)
        helper.addOnClickListener(R.id.ap_vcr_head)
        helper.addOnClickListener(R.id.ap_vcr_img)

        if (item.title.isEmpty()) {
            helper.itemView.ap_vcr_content.visibility = View.GONE
        } else {
            helper.itemView.ap_vcr_content.visibility = View.VISIBLE
        }
        if (item.mylike) {
            helper.itemView.ap_vcr_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_select, 0, 0, 0)
        } else {
            helper.itemView.ap_vcr_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_unselect, 0, 0, 0)
        }
        if (item.att) {
            helper.itemView.ap_vcr_follow.text = "已关注"
        } else {
            helper.itemView.ap_vcr_follow.text = "关注"
        }
        if (collectVisiable && item.uid != QzApplication.get().infoBean!!.uid.toString()) {
            helper.itemView.ap_vcr_follow.visibility = View.VISIBLE
        } else {
            helper.itemView.ap_vcr_follow.visibility = View.GONE
        }
        helper.itemView.ap_vcr_nick.text = item.userinfor.nick
        helper.itemView.ap_vcr_content.text = item.title
        helper.itemView.ap_vcr_comment.text = "${item.comment}"
        helper.itemView.ap_vcr_like.text = "${item.like}"
        helper.itemView.ap_vcr_age.text = "${DateUtils.dataToAge(item.userinfor.birth)}岁"

        if (item.created >= 0) {
            //php时间戳为10位，应转为java13位
            var created = (item.created.toLong() * 1000)
            var time = created.toString()
            helper.itemView.ap_vcr_time.text = when {
                DateUtils.isToday(time) -> DateUtils.timeMinute(time)
                DateUtils.isSameWeek(time) -> DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> "昨天 "
                else -> DateUtils.timeM(time)
            }
        }


        Glide.with(mContext).load(API.PIC_PREFIX + item.userinfor.face).into(helper.itemView.ap_vcr_head)
        if (item.userinfor.sex.contains("女")) {
            helper.itemView.ap_vcr_age.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.girl_little, 0, 0, 0)
        } else {
            helper.itemView.ap_vcr_age.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.boy_little, 0, 0, 0)
        }
        var media = item.media
        var controller = TxVideoPlayerController(mContext)
        helper.itemView.ap_vcr_player.controller = controller
        media.forEach {
            if (it.type == "video") {
                helper.itemView.ap_vcr_player.setUp(API.PIC_PREFIX + it.org, null)
            } else if (it.type == "pic") {
//                Glide.with(mContext).load(API.PIC_PREFIX + it.org).into(controller.imageView())
                Glide.with(mContext).load(API.PIC_PREFIX + it.org).into(helper.itemView.ap_vcr_img)
            }
        }

        helper.itemView.ap_vcr_boom.setOnClickListener { v ->
            v.isClickable = false
            var loca = IntArray(2)
            v.getLocationOnScreen(loca)
            boomClickListener?.invoke(loca[0], loca[1] - ScreenUtil.getStatusBarHeight(activity), item.id)
            var view = helper.itemView
            view.postDelayed({
                BoomView(view, loca[0], loca[1], helper.adapterPosition)
            }, 500)
        }
        helper.itemView.ap_vcr_like.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    likeEvent?.actionDown(position = helper.adapterPosition, view = v, id = item.id)
                }
                MotionEvent.ACTION_CANCEL -> {
//                    Log.e("diaryAdapter", "x:${event.x}  y:${event.y}")
                    likeEvent?.actionUp(position = helper.adapterPosition, view = v)

                }
                MotionEvent.ACTION_UP -> {
                    likeEvent?.actionUp(position = helper.adapterPosition, view = v)
                }
            }
            true
        }
    }

    fun setCollectVisiable(b: Boolean) {
        collectVisiable = b
    }

    var likeEvent: DiaryAdapter.OnLikeTouchEvent? = null
    fun setOnLikeTouchEvent(event: DiaryAdapter.OnLikeTouchEvent) {
        this.likeEvent = event
    }

    var boomClickListener: ((Int, Int, Int) -> Unit)? = null
    private fun BoomView(view: View, viewX: Int, viewY: Int, position: Int) {
        Bloom.with(activity)
                .setParticleRadius(3F)
                .setEffector(BloomEffector.Builder()
                        .setDuration(500)
                        .setAnchor((view.width / 2).toFloat(), (view.height / 2).toFloat())
                        .build())
                .setBloomListener(object : BloomListener {
                    override fun onBegin() {
                        view.alpha = 0f
                    }

                    override fun onEnd() {
                        remove(position)
                    }

                })
                .boom(view)
    }
}
