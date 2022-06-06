package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.ui.main.fragment.SceneVerifyFragment
import kotlinx.android.synthetic.main.activity_verifyscene.*

class VerifySceneActivity: BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_verifyscene
    }

    override fun initView() {
       val scenesEntry= intent.getSerializableExtra("bean") as ScenesEntry
        val bundle = Bundle()
        bundle.putSerializable("scene", scenesEntry)
        supportFragmentManager.beginTransaction()
                .add(R.id.verify_scene_ft,SceneVerifyFragment.newFragment(bundle))
                .commit()
    }

    override fun initData() {

    }

    override fun initEvent() {
        verify_scene_bg.setOnClickListener {
            finish()
        }

    }

    fun verifySceneSucess(sceneEntry: ScenesEntry) {
        setResult(Activity.RESULT_OK,Intent())
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
    }

}
