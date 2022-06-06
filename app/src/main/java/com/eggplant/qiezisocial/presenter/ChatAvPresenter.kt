package com.eggplant.qiezisocial.presenter

import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.ChatAvContract
import com.eggplant.qiezisocial.model.ChatModel

/**
 * Created by Administrator on 2020/6/1.
 */

class ChatAvPresenter : BasePresenter<ChatAvContract.View>(), ChatAvContract.Presenter {
    var model: ChatModel = ChatModel()
    fun getId(activity: AppCompatActivity) {
        model.getId(activity)
    }

}
