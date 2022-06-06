package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.ui.main.adapter.CheckSceneAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_check_scenes.*

/**
 * Created by Administrator on 2022/4/28.
 */

class CheckSceneActivity : BaseActivity() {
    lateinit var adapter: CheckSceneAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_check_scenes
    }

    override fun initView() {
        adapter = CheckSceneAdapter(null)
        check_scene_ry.layoutManager = LinearLayoutManager(mContext)
        check_scene_ry.adapter = adapter
    }

    override fun initData() {
        val data = intent.getSerializableExtra("data") as ArrayList<ScenesEntry>?
        adapter.setNewData(data)
    }

    override fun initEvent() {
        check_scene_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnItemChildClickListener { _, view, position ->
            val scenesEntry = adapter.data[position]
            val intent = Intent()
            intent.putExtra("scene", scenesEntry)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
