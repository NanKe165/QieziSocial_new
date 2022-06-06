package com.eggplant.qiezisocial.ui.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.ui.main.fragment.*
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import kotlinx.android.synthetic.main.activity_setscene.*

/**
 * Created by Administrator on 2021/11/23.
 */

class SetSceneActivity : BaseActivity(), ViewTreeObserver.OnPreDrawListener {
    private val ANIMATE_DURATION = 400L
    override fun onPreDraw(): Boolean {
        scene_vp.viewTreeObserver.removeOnPreDrawListener(this)
        val anim = AnimatorSet()
        val scalAnim = ValueAnimator.ofFloat(0.1f, 1.0f)
        scene_bg.pivotX = ScreenUtil.getDisplayWidthPixels(mContext) * 0.25F
        scene_bg.pivotY = ScreenUtil.getDisplayHeightPixels(mContext) * 0.05F
        scene_bg.pivotX
        scalAnim.addUpdateListener { animation ->
            var value = (animation.animatedValue as Float)
            scene_bg.scaleX = value
            scene_bg.scaleY = value
        }


        val valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        valueAnimator.addUpdateListener { animation ->
            var value = (animation.animatedValue as Float)
            scene_blackbg.alpha = value
            scene_bg.alpha = value
        }

//        addIntoListener(anim)
        anim.duration = ANIMATE_DURATION
        anim.playTogether(scalAnim, valueAnimator)
        anim.start()
        return true
    }

    private var mFragments: ArrayList<BaseFragment>? = null
    private val mTitlesArrays = arrayOf("推荐", "痕迹", "自建")
    private var canceledOutside = false
    private val REQUEST_CHECK_SCENES = 111
    private var pageChangeListener:ViewPager.OnPageChangeListener?=null;
    private var selectPosition=0
    override fun getLayoutId(): Int {
        return R.layout.activity_setscene
    }

    override fun initView() {
        scene_vp.viewTreeObserver.addOnPreDrawListener(this)
        val sid = intent.getStringExtra("sid")
        val goal = intent.getStringExtra("goal")
        val type = intent.getStringExtra("type")
        mFragments = ArrayList()
        val bundle1 = Bundle()
        bundle1.putString("sid", sid)
        bundle1.putString("goal", goal)
        bundle1.putString("type", type)
        mFragments!!.add(SceneFragment.newFragment(bundle1))
        val bundle = Bundle()
        bundle.putString("scene_model", "browse")
        bundle.putString("sid", sid)
        bundle.putString("goal", goal)
        bundle.putString("type", type)
        mFragments!!.add(SceneFragment.newFragment(bundle))
        val bundle2 = Bundle()
        bundle2.putString("scene_model", "my_scene")
        bundle2.putString("sid", sid)
        bundle2.putString("goal", goal)
        bundle2.putString("type", type)
        mFragments!!.add(SceneSelfBuildFragment.newFragment(bundle2))
        scene_vp.adapter = ScenePagerAdapter(supportFragmentManager)
        scene_vp.offscreenPageLimit = 3
        scene_tab.setViewPager(scene_vp, mTitlesArrays)
        selectPosition=application.scenePosition
        scene_vp.setCurrentItem(selectPosition)

    }

    override fun initData() {

    }

    override fun initEvent() {
        scene_bg.setOnClickListener {
            getSelectScene()
            finishActivityAnim()
        }
        scene_sure.setOnClickListener {
            selectScene()
        }
        pageChangeListener=object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                selectPosition=position
            }
        }
        scene_vp.addOnPageChangeListener(pageChangeListener!!)
    }

    fun selectScene() {
        val selectScene = getSelectScene()
        if (selectScene.goal != null && selectScene.goal.isNotEmpty()) {
            val scenes = selectScene.scenes
            if (scenes?.code!=null&&scenes.code.isNotEmpty()&&scenes.userinfor?.uid!=application.loginEntry?.userinfor?.uid){
                setVerifyScene(scenes)
                return
            }
            Log.i("getfilterdara","result---${selectScene.goal}   ${selectScene.sid}   ${selectScene.type}   ${selectScene.moment}" )
            val intnet = Intent()
            intnet.putExtra("goal", selectScene.goal)
            intnet.putExtra("sid", selectScene.sid)
            intnet.putExtra("type", selectScene.type)
            intnet.putExtra("moment", selectScene.moment)
            setResult(Activity.RESULT_OK, intnet)
            finishActivityAnim()
        } else {
            Log.i("setScene", "${selectScene.goal == null}  ${selectScene.goal.isEmpty()}")
            TipsUtil.showToast(mContext, "请先选择一个场景")
//                finishActivityAnim()
        }
    }

    private fun getSelectScene(): FilterEntry {
        val currentItem = scene_vp.currentItem
        val sceneFragment = mFragments!![currentItem]
        if (currentItem == 2) {
            val data = (sceneFragment as SceneSelfBuildFragment).getFilterData()
            canceledOutside = sceneFragment.canceledOutside
            return data
        } else {
            val data = (sceneFragment as SceneFragment).getFilterData()
            canceledOutside = sceneFragment.canceledOutside
            return data
        }

    }

    inner class ScenePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return mFragments!![position]
        }

        override fun getCount(): Int {
            return mFragments!!.size
        }
    }

    override fun onBackPressed() {
        getSelectScene()
        finishActivityAnim()
    }

    fun finishActivityAnim() {
        if (!canceledOutside) {
            TipsUtil.showToast(mContext, "请先选择一个场景")
            return
        }
        val anim = AnimatorSet()
        val scalAnim = ValueAnimator.ofFloat(1.0f, 0.1f)
        scene_bg.pivotX = ScreenUtil.getDisplayWidthPixels(mContext) * 0.25F
        scene_bg.pivotY = ScreenUtil.getDisplayHeightPixels(mContext) * 0.05F
        scene_bg.pivotX
        scalAnim.addUpdateListener { animation ->
            var value = (animation.animatedValue as Float)
            scene_bg.scaleX = value
            scene_bg.scaleY = value
        }


        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.addUpdateListener { animation ->
            var value = (animation.animatedValue as Float)
            scene_blackbg.alpha = value
            scene_bg.alpha = value
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                scene_bg.visibility = View.GONE
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        anim.duration = ANIMATE_DURATION
        anim.playTogether(scalAnim, valueAnimator)
        anim.start()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
    }

    fun setMyScene() {
        scene_set_mys.visibility = View.VISIBLE
        val fragment = SetMySceneFragment.newFragment(null)
        if (!fragment.isAdded) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.scene_set_mys, fragment)
            transaction.commit()
        } else {
            fragment.resetData()
        }

    }

    fun setMysceneSuccess() {
        setMysceneClose()
    }

    fun setMysceneClose() {
        scene_set_mys.visibility = View.GONE
    }

    var hasAddFt = false
    private fun setSelfScene(scenesEntry: ScenesEntry?) {
        scene_set_mys.visibility = View.VISIBLE
        val bundle = Bundle()
        bundle.putSerializable("scene", scenesEntry)
        val fragment = SetSelfSceneFragment.newFragment(bundle)
        if (!fragment.isAdded) {
            val transaction = supportFragmentManager.beginTransaction()
            if (hasAddFt) {
                transaction.replace(R.id.scene_set_mys, fragment)
            }else {
                transaction.add(R.id.scene_set_mys,fragment)
            }
            transaction.commit()
            hasAddFt = true
        } else {
            if (hasAddFt) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.scene_set_mys, fragment)
                transaction.commit()
            }
            fragment.resetData(scenesEntry)
        }
    }

    private fun setVerifyScene(scenesEntry: ScenesEntry) {
        scene_set_mys.visibility = View.VISIBLE
        val bundle = Bundle()
        bundle.putSerializable("scene", scenesEntry)
        val fragment = SceneVerifyFragment.newFragment(bundle)
        if (!fragment.isAdded) {
            val transaction = supportFragmentManager.beginTransaction()
            if (hasAddFt) {
                transaction.replace(R.id.scene_set_mys, fragment)
            }else {
                transaction.add(R.id.scene_set_mys,fragment)
            }
            transaction.commit()
            hasAddFt = true
        } else {
            if (hasAddFt) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.scene_set_mys, fragment)
                transaction.commit()
            }
            fragment.resetData(scenesEntry)
        }
    }

    fun setSelfScene() {
        setSelfScene(null)
    }

//    fun setSelfsceneSucess() {
//        setMysceneClose()
//        (mFragments!![2] as SceneSelfBuildFragment).setSelfScenes()
//    }

    fun checkScenes(data: List<ScenesEntry>) {
        val intent = Intent(mContext, CheckSceneActivity::class.java)
        intent.putExtra("data", ArrayList(data))
        startActivityForResult(intent, REQUEST_CHECK_SCENES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SCENES) {
            if (resultCode == Activity.RESULT_OK) {
                val data = data?.getSerializableExtra("scene")
                if (data != null) {
                    val scenesEntry = data as ScenesEntry
                    showModifyScenes(scenesEntry)
                }
            }
        }
    }

    private fun showModifyScenes(scenesEntry: ScenesEntry) {
        setSelfScene(scenesEntry)
    }

    fun setModifysceneSucess() {
        setMysceneClose()
        (mFragments!![2] as SceneSelfBuildFragment).setSelfScenes()
    }

    fun verifySceneSucess(sceneEntry: ScenesEntry) {
        setMysceneClose()
        val intnet = Intent()
        intnet.putExtra("goal", sceneEntry.title)
        intnet.putExtra("sid", sceneEntry.sid)
        intnet.putExtra("type", sceneEntry.type)
        intnet.putExtra("moment", sceneEntry.moment)
        setResult(Activity.RESULT_OK, intnet)
        finishActivityAnim()
    }

    override fun onDestroy() {
        scene_vp.removeOnPageChangeListener(pageChangeListener!!)
        application.scenePosition=selectPosition
        super.onDestroy()
    }

}
