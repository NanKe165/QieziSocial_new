package com.eggplant.qiezisocial.ui.main.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.widget.azlist.AZBaseAdapter
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity

/**
 * Created by Administrator on 2021/12/20.
 */

class SelectAdapter(dataList: List<AZItemEntity<ScenesEntry>>?) : AZBaseAdapter<ScenesEntry, SelectAdapter.ItemHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.ap_select_college, parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (!TextUtils.isEmpty(mDataList[position].value.title)) {
            holder.mTextName.text = mDataList[position].value.title
        } else {
            holder.mTextName.text = ""
        }
        holder.itemView.setOnClickListener {
            selectClickListener?.invoke(mDataList[position].value)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextName: TextView

        init {
            mTextName = itemView.findViewById(R.id.ap_select_college_name)
        }
    }
    var selectClickListener:((ScenesEntry) ->Unit)?=null
}
