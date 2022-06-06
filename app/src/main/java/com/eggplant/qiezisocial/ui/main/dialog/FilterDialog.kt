package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.View
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.ui.main.adapter.SelectObjectAdapter
import com.eggplant.qiezisocial.ui.main.fragment.GridSpacingItemDecoration
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_filter.*
import java.util.*

/**
 * Created by Administrator on 2020/10/22.
 */

class FilterDialog(context: Context, listenedItems: IntArray?) : BaseDialog(context, R.layout.dialog_filter, listenedItems) {
    var data: FilterEntry = FilterEntry()
    var careerList = QzApplication.get().loginEntry?.careerlist
    var objectList = QzApplication.get().loginEntry?.objectlist
    val random=Random()
    val selectObjectAdapter = SelectObjectAdapter(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER)
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        setCanceledOnTouchOutside(false)// 点击Dialog外部消失

        var volumes = ArrayList<String>()
        volumes.add("全部")
        volumes.add("同城美女")
        volumes.add("同城帅哥")
//        volumes.add("全国美女")
//        volumes.add("全国帅哥")

        dlg_filter_seekbar.initData(volumes)

//        selectObjectAdapter.selectItem.put(0, objectList!![0])
//        selectObjectAdapter.selectItem.put(2, objectList!![2])
        dlg_filter_object_ry.layoutManager = GridLayoutManager(getContext(), 3)
        dlg_filter_object_ry.addItemDecoration(GridSpacingItemDecoration(3, getContext().resources.getDimension(R.dimen.qb_px_10).toInt(), getContext().resources.getDimension(R.dimen.qb_px_10).toInt()))
        dlg_filter_object_ry.adapter = selectObjectAdapter
        selectObjectAdapter.setNewData(objectList)
        dlg_filter_close.setOnClickListener {
            dismiss()
        }
        dlg_filter_sure.setOnClickListener(this)
    }

    fun setFilterData(filter: FilterEntry) {
        data = filter

    }

    override fun show() {
        super.show()

        val people = data.people
        var select = when (people) {
            "全部" -> 0
            "同城美女" -> 1
            "同城帅哥" -> 2
//                "全国美女" -> 3
//                "全国帅哥" -> 4
            else -> {
                0
            }
        }
        dlg_filter_seekbar?.setProgress(select)
        val goal = data.goal
        if (goal.isNotEmpty()) {
            dlg_filter_close.visibility=View.VISIBLE
            val goals = goal.split(",")
            selectObjectAdapter.setSingleItem(goals[0])
//                goals.forEach {
//                    selectObjectAdapter.addSelectItem(it)
//                }
            setCanceledOnTouchOutside(true)
        }else{

            selectObjectAdapter.setSingleItem(objectList!![random.nextInt(objectList!!.size)])
            dlg_filter_close.visibility=View.GONE
            setCanceledOnTouchOutside(false)
        }
        selectObjectAdapter.notifyDataSetChanged()


    }

    fun getFilterData(): FilterEntry {
        var goals = StringBuffer()
        selectObjectAdapter.selectItem.filterValues {
            goals.append("$it,")
            true
        }
        data.people = dlg_filter_seekbar?.selectData
        data.goal = goals.toString().removeSuffix(",")
        return data
    }



}
