package com.eggplant.qiezisocial.ui.main.adapter

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import kotlinx.android.synthetic.main.ap_select_object.view.*

/**
 * Created by Administrator on 2021/2/2.
 */

class SelectObjectAdapter(data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.ap_select_object, data) {
    var selectItem: HashMap<Int, String> = HashMap()
    private var addSelectItem = ArrayList<String>()

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.itemView.ap_select_object_tv.text = item
        val iterator = addSelectItem.iterator()
        while (iterator.hasNext()){
            val it = iterator.next()
            if (it==item){
                selectItem.put(helper.adapterPosition, item)
                iterator.remove()
                continue
            }
        }
//
        if (selectItem.containsKey(helper.adapterPosition)) {
            helper.itemView.ap_select_object_gp.background = ContextCompat.getDrawable(mContext, R.drawable.tv_yellow_bg)
        } else {
            helper.itemView.ap_select_object_gp.background = ContextCompat.getDrawable(mContext, R.drawable.tv_gray_bg3)
        }
        helper.itemView.ap_select_object_gp.setOnClickListener {
            if (selectItem.containsKey(helper.adapterPosition)) {
//                selectItem.remove(helper.adapterPosition)
            } else {
                selectItem.clear()
                selectItem.put(helper.adapterPosition, item)
                notifyDataSetChanged()
            }
        }
    }


    fun  setSingleItem(select: String){
        selectItem.clear()
        addSelectItem.add(select)
    }
//    fun addSelectItem(select: String) {
//        addSelectItem.add(select)
//    }
}
