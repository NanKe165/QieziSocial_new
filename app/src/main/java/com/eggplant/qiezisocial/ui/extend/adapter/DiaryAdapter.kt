package com.eggplant.qiezisocial.ui.extend.adapter

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.widget.MultMediaView
import kotlinx.android.synthetic.main.adapter_diary.view.*
import me.samlss.bloom.Bloom
import me.samlss.bloom.effector.BloomEffector
import me.samlss.bloom.listener.BloomListener

/**
 * Created by Administrator on 2020/10/15.
 */

class DiaryAdapter(activity: Activity, data: List<DiaryEntry>?) : BaseQuickAdapter<DiaryEntry, BaseViewHolder>(R.layout.adapter_diary, data) {
    var activity: Activity = activity
    var mineDiary=false
    override fun convert(helper: BaseViewHolder, item: DiaryEntry) {

        helper.itemView.ap_diary_txt.text = item.text
        helper.itemView.ap_diary_like.text = "${item.like}"
        helper.itemView.ap_diary_comment.text = "${item.comment}"

//        helper.addOnClickListener(R.id.ap_diary_burst)
        helper.addOnClickListener(R.id.ap_diary_comment)
        helper.addOnClickListener(R.id.ap_diary_report)
        Glide.with(mContext).load(API.PIC_PREFIX+item.userinfor.face).into(helper.itemView.ap_diary_head)
        helper.itemView.ap_diary_nick.text=item.userinfor.nick
        if(item.userinfor.sex=="男")
        {
            helper.itemView.ap_diary_sex.setImageResource(R.mipmap.sex_boy)
        }else{
            helper.itemView.ap_diary_sex.setImageResource(R.mipmap.sex_girl)
        }
        if (mineDiary){
            helper.itemView.ap_diary_report.visibility=View.VISIBLE
        }
        if (item.mylike) {
            helper.itemView.ap_diary_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_select, 0, 0, 0)
        } else {
            helper.itemView.ap_diary_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_unselect, 0, 0, 0)
        }
        if (item.created >= 0) {
            //php时间戳为10位，应转为java13位
            var created = (item.created.toLong() * 1000)
            var time = created.toString()
            helper.itemView.ap_diary_time.text = when {
                DateUtils.isToday(time) -> DateUtils.timeMinute(time)
                DateUtils.isSameWeek(time) -> DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> "昨天 "
                else -> DateUtils.timeM(time)
            }
        }
        if (item.media.isEmpty()){
            helper.itemView.ap_diary_mulmedia.visibility=View.GONE
        }else{
            helper.itemView.ap_diary_mulmedia.visibility=View.VISIBLE
            var type=MultMediaView.TYPE_IMG
            item.media.forEach {
                if (it.type=="video")
                    type=MultMediaView.TYPE_VIDEO
            }
            helper.itemView.ap_diary_mulmedia.setNewData(item.media,type)
        }

        when {
            item.comment < 6 -> {
                helper.itemView.ap_diary_shade.visibility = View.GONE
                helper.itemView.ap_diary_packup_img.visibility = View.GONE
            }
            item.hasMoreComment -> {
                helper.itemView.ap_diary_shade.visibility = View.VISIBLE
                helper.itemView.ap_diary_packup_img.visibility = View.GONE
            }
            else -> {
                helper.itemView.ap_diary_shade.visibility = View.GONE
                helper.itemView.ap_diary_packup_img.visibility = View.VISIBLE
            }
        }

        helper.itemView.ap_diary_commentlist.setDiaryUid(item.uid)
        helper.itemView.ap_diary_commentlist.datas = item.commentlist
        helper.itemView.ap_diary_commentlist.setOnItemClickListener { position ->
            commentListener?.invoke(helper.adapterPosition, item.commentlist[position])
        }
        helper.itemView.ap_diary_packup_img.setOnClickListener {
            if (item.commentlist.size > 6) {
                var newlist = item.commentlist.subList(0, 6)
                item.commentlist = newlist
            } else {
                var newlist = item.commentlist.subList(0, item.commentlist.size - 1)
                item.commentlist = newlist
            }
            item.hasMoreComment = true
            notifyItemChanged(helper.adapterPosition)
        }

        helper.itemView.ap_diary_shade.setOnClickListener {
            loadMoreCommentListener?.invoke(item.id, helper.adapterPosition, item.commentlist.size)
        }


        helper.itemView.ap_diary_burst.setOnClickListener { v ->

            v.isClickable = false
            var loca = IntArray(2)
            v.getLocationOnScreen(loca)
            boomClickListener?.invoke(loca[0], loca[1], item.id)
            var view = helper.itemView
            view.postDelayed({
                BoomView(view, loca[0], loca[1], helper.adapterPosition)
            }, 500)

        }
        helper.itemView.ap_diary_like.setOnTouchListener { v, event ->
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

    var likeEvent: OnLikeTouchEvent? = null
    fun setOnLikeTouchEvent(event: OnLikeTouchEvent) {
        this.likeEvent = event
    }

    interface OnLikeTouchEvent {
        fun actionDown(position: Int, view: View, id: Int)
        fun actionUp(position: Int, view: View)
    }

    var commentListener: ((Int, CommentEntry) -> Unit)? = null
    var boomClickListener: ((Int, Int, Int) -> Unit)? = null
    var loadMoreCommentListener: ((Int, Int, Int) -> Unit)? = null
    fun addDatas(newData: MutableCollection<out DiaryEntry>): Boolean {
        var it = newData.iterator()
        while (it.hasNext()) {
            if (data.contains(it.next()))
                it.remove()
        }
        if (newData.isEmpty()) {
            return false
        }
        addData(data)
        return true
    }


}
