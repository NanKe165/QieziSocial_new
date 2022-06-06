package com.eggplant.qiezisocial.ui.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import com.autonavi.ae.gmap.utils.GLMD5Util
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.RedPacketEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.flow.BeansFlowView
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_rob_beans.*

/**
 * Created by Administrator on 2022/3/24.
 */

class RobBeansActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_rob_beans
    }

    lateinit var redPacketEntry: RedPacketEntry
    private lateinit var popWindow: BasePopupWindow
    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        redPacketEntry = intent.getSerializableExtra("bean") as RedPacketEntry
        rob_flow.setData(redPacketEntry.jids)
        initPopWindow()
        startAnim()
    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dialog_robbeans_add, null, false)
        popWindow = BasePopupWindow(mContext)
        popWindow.contentView = popView
        popWindow.showAnimMode = 1
    }

    override fun initData() {

    }
    private var lastViewClickTime=0L
    override fun initEvent() {
        rob_flow.setViewAnimListener(object :BeansFlowView.ViewAnimListener{
            override fun onAnimFinish() {
                val ownedData = rob_flow.ownedData
                rob_flow?.post {
                    TipsUtil.showToast(mContext, "总获得${ownedData.size}金豆")
                    verifyRobBeans(ownedData)
                }
            }

            override fun onViewClick(v: IntArray) {
//               Log.i("robBeansAc","x :${v[0]}  y:${v[1]} ")
//                popWindow.showAtLocation(rob_bg,Gravity.NO_GRAVITY,v[0],v[1])
                lastViewClickTime=System.currentTimeMillis()
                rob_numb.visibility=View.VISIBLE
                rob_numb.x=v[0].toFloat()
                rob_numb.y=v[1].toFloat()
                rob_numb?.postDelayed({
                    val time = System.currentTimeMillis() - lastViewClickTime
                    if (time>=300){
                        rob_numb.visibility=View.INVISIBLE
                    }
                },300)
            }
        })
    }

    private fun verifyRobBeans(ownedData: List<String>) {
        if (ownedData.isEmpty()){
            TipsUtil.showToast(mContext,"真可惜，一个都没抢到呢")
            finish()
            return
        }
        var ids = StringBuffer()
        var signStr = StringBuffer()
        ownedData.forEachIndexed { index, s ->
            ids.append(s)
            signStr.append("${s}")
            if (index != ownedData.size - 1) {
                ids.append(",")
                signStr.append(",")
            }else{
                signStr.append("jindou")
            }
        }
//        Log.i("RobBeans","ids: $ids  \n\t signStr:$signStr   \n\t  ${GLMD5Util.getStringMD5(signStr.toString())}")
        OkGo.post<BaseEntry<*>>(API.REPORT_RED_PACKET)
                .params("hid", redPacketEntry.hid)
                .params("ids", ids.toString())
                .params("sign", GLMD5Util.getStringMD5(signStr.toString()))
                .execute(object : JsonCallback<BaseEntry<*>>() {
                    override fun onSuccess(response: Response<BaseEntry<*>>?) {
                        if (response!!.isSuccessful) {
//                            TipsUtil.showToast(mContext, response.body().msg!!)
                            if (response.body().stat == "ok") {
                                TipsUtil.showToast(mContext, "获得${ownedData.size}金豆")
                            } else {
                                TipsUtil.showToast(mContext, response.body().msg!!)
                            }
                            rob_flow?.postDelayed({ finish() }, 1000)
                        } else {
                            finish()
                        }
                    }
                })
    }

    var repeatContent = 0
    var imags = intArrayOf(R.mipmap.icon_numb_10, R.mipmap.icon_numb_9, R.mipmap.icon_numb_8, R.mipmap.icon_numb_7, R.mipmap.icon_numb_6, R.mipmap.icon_numb_5, R.mipmap.icon_numb_4
            , R.mipmap.icon_numb_3, R.mipmap.icon_numb_2, R.mipmap.icon_numb_1, R.mipmap.icon_numb_0)

    private fun startNumbAnim() {
        val numbAnim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        numbAnim.interpolator = OvershootInterpolator()
        numbAnim.duration = 1000
        numbAnim.repeatCount = 9
        numbAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                repeatContent++
                if (repeatContent < imags.size)
                    rob_img?.setImageResource(imags[repeatContent])
            }

            override fun onAnimationEnd(animation: Animation?) {
                rob_img?.visibility = View.GONE
                rob_flow?.startAnim()
            }

            override fun onAnimationStart(animation: Animation?) {
                rob_img?.setImageResource(imags[0])
            }
        })
        rob_img.startAnimation(numbAnim)
    }

    private fun startAnim() {
        val startAnim = ObjectAnimator.ofFloat(rob_bg, "alpha", 0f, 0.5f)
        startAnim.duration = 450
        startAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                rob_img?.setImageResource(R.mipmap.icon_rob_beans_hint)
                rob_img?.postDelayed({
                    startNumbAnim()
                }, 1000)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        startAnim.start()
    }


    var doFinishAnim = false
    override fun finish() {
        if (doFinishAnim) {
            super.finish()
//            overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
            overridePendingTransition(0, 0)
        } else {
            finishAnim()
        }
    }

    private fun finishAnim() {
        val finishAnim = ObjectAnimator.ofFloat(rob_bg, "alpha", 0.5f, 0f)
        finishAnim.duration = 450
        finishAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                doFinishAnim = true
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                rob_flow.stopAnim()
            }
        })
        finishAnim.start()
    }

    override fun onPause() {
        super.onPause()
        rob_flow.pauseAnim()
    }

    override fun onRestart() {
        super.onRestart()
        rob_flow.startAnim()
    }

    override fun onDestroy() {
        rob_flow.stopAnim()
        super.onDestroy()
    }

}
