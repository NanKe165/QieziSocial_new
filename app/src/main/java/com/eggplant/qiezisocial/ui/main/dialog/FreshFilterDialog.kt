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
import com.eggplant.qiezisocial.ui.main.adapter.FreshSelectObjectAdapter
import com.eggplant.qiezisocial.ui.main.fragment.GridSpacingItemDecoration
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_fresh_filter.*
import java.util.*

/**
 * Created by Administrator on 2021/4/20.
 */

class FreshFilterDialog(context: Context, listenedItems: IntArray?) : BaseDialog(context, R.layout.dialog_fresh_filter, listenedItems) {
    var data: FilterEntry = FilterEntry()
    val selectObjectAdapter = FreshSelectObjectAdapter(null)
    var scenes = QzApplication.get().loginEntry?.scenes
    val random = Random()
    var selectPeople = "全部"
    var canceledOutside=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setGravity(Gravity.CENTER)
        window.setWindowAnimations(R.style.scale_menu_animation) // 添加动画效果
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = window!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        window!!.attributes = lp
        setCanceledOnTouchOutside(false)// 点击Dialog外部消失
//        window.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        //一定要在setContentView之后调用，否则无效
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                View.SYSTEM_UI_FLAG_IMMERSIVE or
//                View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.decorView.systemUiVisibility = uiOptions

        filter_object_ry.layoutManager = GridLayoutManager(getContext(), 2)
        filter_object_ry.addItemDecoration(GridSpacingItemDecoration(2, getContext().resources.getDimension(R.dimen.qb_px_18).toInt(), getContext().resources.getDimension(R.dimen.qb_px_25).toInt()))
        filter_object_ry.adapter = selectObjectAdapter
        var data = QzApplication.get().loginEntry!!.scenes
        selectObjectAdapter.setNewData(data)
//        filter_all.setOnClickListener {
//            peopleSelect(0)
//        }
//        filter_girl.setOnClickListener {
//            peopleSelect(1)
//        }
//        filter_boy.setOnClickListener {
//            peopleSelect(2)
//        }
        filter_close.setOnClickListener {
            dismiss()
        }
        filter_sure.setOnClickListener(this)
        filter_bg.setOnClickListener {
            if (canceledOutside)dismiss() }
    }

//    private fun peopleSelect(select: Int) {
//        filter_boy.background = ContextCompat.getDrawable(context, R.drawable.filter_boy_bg)
//        filter_girl.background = ContextCompat.getDrawable(context, R.drawable.filter_girl_bg)
//        filter_all.background = ContextCompat.getDrawable(context, R.drawable.filter_purple_bg)
//        when (select) {
//            2 -> {
//                selectPeople = "找帅哥"
//                filter_boy.background = ContextCompat.getDrawable(context, R.drawable.filter_boy_select_bg)
//            }
//            1 -> {
//                selectPeople = "找美女"
//                filter_girl.background = ContextCompat.getDrawable(context, R.drawable.filter_girl_select_bg)
//            }
//            0 -> {
//                selectPeople = "全部"
//                filter_all.background = ContextCompat.getDrawable(context, R.drawable.filter_all_select_bg)
//            }
//        }
//
//    }

    fun setFilterData(filter: FilterEntry) {
        data = filter
    }

    override fun show() {
        super.show()

//        val people = data.people
//        var select = when (people) {
//            "全部" -> 0
//            "同城美女", "美女", "找美女" -> 1
//            "同城帅哥", "帅哥", "找帅哥" -> 2
////                "全国美女" -> 3
////                "全国帅哥" -> 4
//            else -> {
//                0
//            }
//        }
//        peopleSelect(select)
//        dlg_filter_seekbar?.setProgress(select)
        val goal = data.goal
       val sid= data.sid
        if (goal.isNotEmpty()) {
            filter_close.visibility = View.VISIBLE
            val goals = goal.split(",")
            selectObjectAdapter.setSingleItem(goals[0],sid,"0")
//                goals.forEach {
//                    selectObjectAdapter.addSelectItem(it)
//                }
            setCanceledOnTouchOutside(true)
            canceledOutside=true
        } else {
            val scenesEntry = scenes!![random.nextInt(scenes!!.size)]
            selectObjectAdapter.setSingleItem(scenesEntry.title,scenesEntry.sid,"0")
            filter_close.visibility = View.GONE
            setCanceledOnTouchOutside(false)
            canceledOutside=false
        }
        selectObjectAdapter.notifyDataSetChanged()


    }

    fun getFilterData(): FilterEntry {
        data.people = "全部"
        data.goal =selectObjectAdapter.selectItem
        data.sid=selectObjectAdapter.selectSid
        return data
    }


}
