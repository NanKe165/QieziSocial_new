package com.eggplant.qiezisocial.ui.main.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.azlist.AZBaseAdapter
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity
import com.eggplant.qiezisocial.widget.image.CircleImageView
import kotlinx.android.synthetic.main.ap_friendlist.view.*

/**
 * Created by Administrator on 2020/4/15.
 */

class FriendlistAdapter(context: Context, dataList: List<AZItemEntity<MainInfoBean>>?) : AZBaseAdapter<MainInfoBean, RecyclerView.ViewHolder>(dataList) {
    var mContext: Context = context
    var headView: View? = null


    //item类型
    val ITEM_TYPE_HEADER = 0
    val ITEM_TYPE_CONTENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_TYPE_HEADER) {
            return headHolder(headView!!)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ap_friendlist, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return if (headView == null) {
            super.getItemCount()
        } else {
            super.getItemCount() + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (headView != null && position < 1) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_CONTENT
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FriendlistAdapter.ItemHolder) {
            if (headView == null) {
                onBindContentHolder(position, holder)
            } else {
                onBindContentHolder(position - 1, holder)
            }
        }

    }

    private fun onBindContentHolder(position: Int, holder: ItemHolder) {
        var item = mDataList[position].value
        var sortLetters = mDataList[position].sortLetters
        val ps = getSortLettersFirstPosition(sortLetters)
        if (position == ps) {
            holder.letterTv.visibility = View.VISIBLE
            holder.letterTv.text = sortLetters
        } else {
            holder.letterTv.visibility = View.GONE
        }

        //
        if (mDataList.size>position+1){
            var sortLetters = mDataList[position+1].sortLetters
            val ps = getSortLettersFirstPosition(sortLetters)
            if (position==ps-1){
                holder.line.visibility=View.GONE
            }else{
                holder.line.visibility=View.VISIBLE
            }

        }else{
            holder.line.visibility=View.GONE
        }

        holder.itemView.setOnClickListener {
            this.mListener?.invoke(item)
        }
        holder.itemView.head_img.setOnClickListener {
            this.headClickListener?.invoke(item.convertUserEntry())
        }
        holder.itemView.setOnLongClickListener { v ->
            this.mLongListener?.invoke(v, item)
            true
        }

        if (!TextUtils.isEmpty(item.face)) {
            Glide.with(mContext).load(API.PIC_PREFIX + item.face!!).into(holder.headImg)
        } else {
            holder.headImg.setImageResource(R.mipmap.normal_head)
        }
        var name = ""
        if (!TextUtils.isEmpty(item.remark)) {
            name = item.remark
        } else if (!TextUtils.isEmpty(item.nick)) {
            name = item.nick
        }
        holder.nameTv.text = name

//        if (!TextUtils.isEmpty(item.msg)) {
//            holder.messageTv.text = item.msg
//            holder.messageTv.visibility = View.VISIBLE
//            holder.timeTv.visibility = View.VISIBLE
//            val newMsgTime = item.newMsgTime
//            if (newMsgTime != 0L) {
//                if (DateUtils.isToday(newMsgTime.toString() + "")) {
//                    holder.timeTv.text = DateUtils.timeMinute(newMsgTime.toString())
//                } else if (DateUtils.isSameDate(newMsgTime.toString() + "")) {
//                    holder.timeTv.text = DateUtils.getWeek(newMsgTime)
//                } else if (DateUtils.IsYesterday(newMsgTime)) {
//                    holder.timeTv.text = "昨天"
//                } else {
//                    holder.timeTv.text = DateUtils.getMonth(newMsgTime.toString())
//                }
//            }
//        } else {
//            holder.messageTv.text = ""
        holder.messageTv.visibility = View.GONE
        holder.timeTv.visibility = View.GONE
//        }
//        if (item.msgNum > 0) {
//            holder.messageHintTv.visibility = View.VISIBLE
//            holder.messageHintTv.text = item.msgNum.toString()
//        } else {
//            holder.messageHintTv.visibility = View.GONE
//        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headImg: CircleImageView
        var headHint: View
        var messageHintTv: TextView
        var timeTv: TextView
        var messageTv: QzTextView
        var nameTv: TextView
        var letterTv: QzTextView
        var line:View

        init {
            headImg = itemView.head_img
            headHint = itemView.head_hint
            messageHintTv = itemView.message_hint_tv
            timeTv = itemView.time_tv
            messageTv = itemView.message_tv
            nameTv = itemView.name_tv
            letterTv = itemView.letter_tv
            line=itemView.line_bottom
        }
    }

    inner class headHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    var headClickListener: ((UserEntry) -> Unit)? = null
    var mListener: ((MainInfoBean) -> Unit)? = null
    var mLongListener: ((View, MainInfoBean) -> Unit)? = null
    fun setItemClickListen(listener: (MainInfoBean) -> Unit) {
        this.mListener = listener
    }

    fun setItemLongClickListene(listener: (View, MainInfoBean) -> Unit) {
        this.mLongListener = listener
    }

    fun setItemHeadClickListen(listener: (UserEntry) -> Unit) {
        this.headClickListener = listener
    }

    fun addHeadView(view: View) {
        this.headView = view
    }
}
