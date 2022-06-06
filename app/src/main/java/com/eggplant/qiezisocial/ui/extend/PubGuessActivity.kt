package com.eggplant.qiezisocial.ui.extend

import android.os.Bundle
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.entry.QuestionEntry
import com.eggplant.qiezisocial.model.GuessFateModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.ui.extend.fragment.SetGuessFragment
import com.eggplant.qiezisocial.ui.extend.fragment.SetGuessNumberFragment
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_pub_guess.*

/**
 * Created by Administrator on 2020/7/13.
 */

class PubGuessActivity : BaseActivity() {
    var pattern: String? = null
    var picPath: String? = null
    var eligibleQsNumb = 1
    override fun getLayoutId(): Int {
        return R.layout.activity_pub_guess
    }

    override fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.pub_guess_ft, SetGuessNumberFragment.instanceFragment(null)).commit()

    }

    override fun initData() {

    }

    override fun initEvent() {
        pub_guess_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }

    fun onNext(qsNumb: Int, eligibleQsNumb: Int, pattern: String, picPath: String?) {
        this.picPath = picPath
        this.pattern = pattern
        this.eligibleQsNumb = eligibleQsNumb
        var data = Bundle()
        data.putInt("qsNumb", qsNumb)
//        data.putInt("eligibleQsNumb", eligibleQsNumb)
//        data.putString("pattern", pattern)
//        data.putString("picPath", picPath)
        supportFragmentManager.beginTransaction().replace(R.id.pub_guess_ft, SetGuessFragment.instanceFragment(data)).commit()
    }

    fun pubGuess(myQsList: ArrayList<QuestionEntry>) {
        GuessFateModel().pubGuess(eligibleQsNumb, picPath, pattern, myQsList, object : DialogCallback<BaseEntry<GuessFateEntry>>(activity,"正在发布...") {
            override fun onSuccess(response: Response<BaseEntry<GuessFateEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (TextUtils.equals(it.stat, "ok")) {
                                TipsUtil.showToast(mContext, "发布成功")
                                activity.finish()
                            } else{
                                TipsUtil.showToast(mContext, it.msg!!)
                            }
                        }

                   }
                }
            }
        })
    }

}
