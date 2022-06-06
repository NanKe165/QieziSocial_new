package com.eggplant.qiezisocial.ui.pub.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import kotlinx.android.synthetic.main.fragment_pubtxt2.*

/**
 * Created by Administrator on 2020/4/23.
 */

class PubTxtFragment2 : BaseFragment() {
    var from:String?=""
    companion object {
        fun getInstance(bundle: Bundle?): PubTxtFragment2 {
            var fragment = PubTxtFragment2()
            bundle?.let { fragment.arguments = it }
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_pubtxt2
    }

    override fun initView() {
        from=arguments?.getString("from")
        if(from == "space"){
            ft_pub_hintv.text="正在发送..."
        }
        var vam = ValueAnimator.ofInt(0, 100)
        vam.addUpdateListener { va ->
            var value = va.animatedValue as Int
            ft_pub_progressbar.progress=value
        }
        vam.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                (activity as PubTxtActivity).endPub()
            }

        })
        vam.duration = 1200
        vam.start()

//        ft_pub_success_load.startAnimation(anim)
    }

    override fun initEvent() {

    }

    override fun initData() {

    }

}
