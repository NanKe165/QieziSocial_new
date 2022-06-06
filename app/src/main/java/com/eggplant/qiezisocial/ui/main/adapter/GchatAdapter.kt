package com.eggplant.qiezisocial.ui.main.adapter

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.widget.AuVideoTxtView
import kotlinx.android.synthetic.main.ap_ft_gchat.view.*

/**
 * Created by Administrator on 2021/3/31.
 */

class GchatAdapter(data: List<ArrayList<ChatMultiEntry<ChatEntry>>>?) : BaseQuickAdapter<List<ChatMultiEntry<ChatEntry>>, BaseViewHolder>(R.layout.ap_ft_gchat, data) {
    internal var notifyPosition = 0
    internal var startAnim = true
    var cacheData = ArrayList<ChatMultiEntry<ChatEntry>>()
    var currentHolder: BaseViewHolder? = null
    override fun convert(helper: BaseViewHolder, item: List<ChatMultiEntry<ChatEntry>>) {
        val size = item.size
        if (size < 6) {
            (0..5).map { getGpView(it, helper) }
                    .forEach { it!!.visibility = View.INVISIBLE }
        }
        (0 until size).forEach {
            setContentViewData(it, helper, item[it])
        }
        if (helper.adapterPosition == 0 + headerLayoutCount) {
            currentHolder = helper
        }
        if (startAnim) {
            if (size > notifyPosition) {
                (0 until size)
                        .filter { it != notifyPosition }
                        .map { getGpView(it, helper) }
                        .forEach { it!!.visibility = View.VISIBLE }
            }
        } else {
            (0 until size)
                    .map { getGpView(it, helper) }
                    .forEach { it!!.visibility = View.VISIBLE }
        }
        if (size == 1) {
            Log.i("gchatAdapter", "convert---------")
            startViewAnim()
        }
    }

    private fun setContentViewData(p: Int, helper: BaseViewHolder, entry: ChatMultiEntry<ChatEntry>) {
        var contentView = getContentView(p, helper)
        contentView!!.setContentLine(getViewMaxLine(p))
        contentView.setInfo(entry)
        contentView.setOnClickListener { v ->
            itemClickListener?.invoke(p, v, entry)

        }
        contentView.setOnLongClickListener { v ->
            itemLongClickListener?.invoke(entry)
            true
        }
        contentView.head.setOnClickListener {
            val uid = entry.bean.from
            headClickListener?.invoke(uid)
        }
//        contentView.getaPlay().setOnClickListener {
//
//        }
//        contentView.getvPlay().setOnClickListener {
//
//        }
    }

    private fun getViewMaxLine(p: Int): Int {
        return when (p) {
            0, 1, 3, 4, 5 -> {
                2
            }
            2 -> {
                6
            }
            else -> {
                2
            }
        }
    }

    fun addNewData(bean: ChatMultiEntry<ChatEntry>) {
        startAnim = true
        if (doAnim) {
            cacheData.add(bean)
            return
        }
        val gSize = data.size
        if (gSize > 0) {
            val size = data[0]!!.size
            if (size < 6) {
                if (currentHolder == null) {
                    cacheData.add(bean)
                    return
                }
                (data[0]!! as ArrayList<ChatMultiEntry<ChatEntry>>).add(bean)
                notifyPosition = data[0]!!.size - 1
                setContentViewData(notifyPosition, currentHolder!!, bean)
                startViewAnim()
            } else {
                currentHolder = null
                var d = arrayListOf(bean)
                data.add(0, d)
                notifyPosition = 0
                notifyItemRangeInserted(headerLayoutCount + 0, 1)
            }
        } else {
            val d = arrayListOf(bean)
            data.add(0, d)
            notifyPosition = 0
            notifyItemRangeInserted(headerLayoutCount + 0, 1)
        }
        if (listener != null) {
            listener!!.scroll()
        }
    }


    var doAnim = false
    private fun startViewAnim() {
        if (currentHolder != null) {
            var rloca = IntArray(2)
            var aloca = IntArray(2)
            val rView = currentHolder!!.itemView.ap_gchat_rootview
            val animView = getGpView(notifyPosition, currentHolder!!)
            if (animView != null) {
                animView.visibility = View.VISIBLE
                rView.getLocationOnScreen(rloca)
                animView.getLocationOnScreen(aloca)
                var translateY = (rloca[1] - aloca[1] - animView.height).toFloat()
                Log.i("gchatAdapter", "translationY:$translateY")
                if (translateY == 0f) {
                    translateY = -1500f
                }

                val animation = ObjectAnimator.ofFloat(animView, "translationY", translateY, 0f)
                animation.duration = 1000L
                animation.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        doAnim = false
                        if (cacheData.size > 0) {
                            val bean = cacheData[0]
                            cacheData.remove(bean)
                            addNewData(bean)
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        doAnim = true
                    }

                })
                animation.start()
                notifyPosition = -1
            }
        } else {
            Log.i("gchatAdapter", "currentHolder==null")
        }

    }

    private fun getContentView(position: Int, helper: BaseViewHolder): AuVideoTxtView? {
        when (position) {
            0 -> {
                return helper.itemView.ap_gchat_avt6
            }
            1 -> {
                return helper.itemView.ap_gchat_avt5
            }
            2 -> {
                return helper.itemView.ap_gchat_avt3
            }
            3 -> {
                return helper.itemView.ap_gchat_avt4
            }
            4 -> {
                return helper.itemView.ap_gchat_avt2
            }
            5 -> {
                return helper.itemView.ap_gchat_avt1
            }
        }
        return null

    }

    private fun getGpView(i: Int, helper: BaseViewHolder): View? {
        when (i) {
            0 -> {
                return helper.itemView.ap_gchat_view6
            }
            1 -> {
                return helper.itemView.ap_gchat_view5
            }
            2 -> {
                return helper.itemView.ap_gchat_view3
            }
            3 -> {
                return helper.itemView.ap_gchat_view4
            }
            4 -> {
                return helper.itemView.ap_gchat_view2
            }
            5 -> {
                return helper.itemView.ap_gchat_view1
            }
        }
        return null
    }


    interface ScrollListener {
        fun scroll()
    }

    var listener: ScrollListener? = null
    var headClickListener: ((Long) -> Unit)? = null
    var itemClickListener: ((Int, View, ChatMultiEntry<ChatEntry>) -> Unit)? = null
    var itemLongClickListener:((ChatMultiEntry<ChatEntry>)->Unit)?=null
}
