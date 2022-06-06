package com.eggplant.qiezisocial.ui.decorate

import android.util.Log
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.event.ChangeBgEvent
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_decorate_deatil.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2021/4/22.
 */

class DecorateDeatilActivity : BaseActivity() {
    var position = 0
    var imgs = arrayListOf<Int>(R.drawable.decorate_detail1, R.drawable.decorate_detail2, R.drawable.decorate_detail3, R.drawable.decorate_detail4, R.drawable.decorate_detail5,
            R.drawable.decorate_detail6,R.drawable.decorate_detail7,R.drawable.decorate_detail8,R.drawable.decorate_detail9)
    override fun getLayoutId(): Int {
        return R.layout.activity_decorate_deatil
    }

    override fun initView() {
        position = intent.getIntExtra("position", 0)
        dector_detail_img.setImageResource(imgs[position])

    }

    override fun initData() {

    }

    override fun initEvent() {
        dector_detail_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        dector_detail_sure.setOnClickListener {
            Log.i("decoratedDetail", "spaceback:$position")
            LoginModel.modifyInfo(activity, "spaceback", position.toString(), object : JsonCallback<BaseEntry<UserEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                    if (response!!.isSuccessful) {
                        if (response.body().stat == "ok") {
                            application.infoBean = response.body().userinfor
                            application.loginEntry!!.userinfor = response.body().userinfor
                            TipsUtil.showToast(mContext, "装扮成功")
                            EventBus.getDefault().post(ChangeBgEvent())
                            finish()
                        }
                    }
                }
            })
        }
    }
}
