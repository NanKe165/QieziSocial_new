package com.eggplant.qiezisocial.ui.award

import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.JindouEntry
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_jindou_rule.*

/**
 * Created by Administrator on 2022/6/15.
 */

class JindouRuleActivity: BaseActivity() {
    lateinit var adapter: JindouAdapter
    var imgs = intArrayOf(R.mipmap.icon_gz1, R.mipmap.icon_gz2, R.mipmap.icon_gz3, R.mipmap.icon_gz4, R.mipmap.icon_gz5, R.mipmap.icon_gz6
            , R.mipmap.icon_gz7, R.mipmap.icon_gz8)
    var titles = arrayListOf<String>("完成注册", "完善资料", "一个提问", "一个回复", "一篇日志或动态", "获得1个点赞", "为别人点赞", "成功交友一人")
    var jdc = intArrayOf(50, 20, 5, 5, 5, 5, 1, 20)
    override fun getLayoutId(): Int {
        return R.layout.activity_jindou_rule
    }

    override fun initView() {
        jindou_rule_ry.layoutManager=LinearLayoutManager(mContext)
        adapter = JindouAdapter(null)
        jindou_rule_ry.adapter = adapter
    }

    override fun initData() {
        imgs.forEachIndexed { index, i ->
            var entry = JindouEntry()
            entry.locaImg = i
            entry.title = titles[index]
            entry.jdcount = "${jdc[index]}"
            adapter.addData(entry)
        }
    }

    override fun initEvent() {
        jindou_rule_bar.setTbListener(object :SimpBarListener(){
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }
}
