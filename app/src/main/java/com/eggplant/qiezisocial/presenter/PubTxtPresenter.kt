package com.eggplant.qiezisocial.presenter

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.PubTxtContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.PubTxtModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2020/4/22.
 */
class PubTxtPresenter : BasePresenter<PubTxtContract.View>(), PubTxtContract.Presenter {
    private val model = PubTxtModel()
    fun pubQuestion(activity: AppCompatActivity, scenes: String, role: String, context: String, font: String, data: ArrayList<String>, destory: String, m: Int, broadCaost: Int,sid:String) {
        Log.i("pubTxtP","pubstart----${System.currentTimeMillis()}")
        model.pubQuestion(activity,scenes,role, context, font, data, destory, m, broadCaost,sid,object : DialogCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                Log.i("pubTxtP","success---${System.currentTimeMillis()}")
                response?.let {
                    if (it.isSuccessful) {
                        if (TextUtils.equals(it.body().stat, "ok")) {
                            if (it.body().feedback!=null&&it.body().feedback!!.isNotEmpty()){
                                mView?.showTost(it.body().feedback)
                            }
                            it.body().record?.let {
                                mView?.pubQuestionSuccess(it)
                            }
                        } else {
                            mView?.showTost(it.body().msg)
                        }
                    } else {
                        mView?.showTost("${it.code()} ${it.message()}")
                    }
                }
            }
        })
    }

    override fun pubPrivateQs(context: String, font: String, uid: Int, data: ArrayList<String>) {

        model.pubPrivateQs(context,font,data,uid,object : DialogCallback<BaseEntry<*>>(){
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful){
                    if (response.body().stat=="ok"){
                        mView?.pubQuestionSuccess(BoxEntry())
                    }else{
                        mView?.showTost(response.body().msg)
                    }
                }else{
                    mView?.showTost("${response.code()} ${response.message()}")
                }
            }
        })
    }
}