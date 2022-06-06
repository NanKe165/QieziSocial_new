package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean

/**
 * Created by Administrator on 2022/4/6.
 */

interface FriendParkContract{
    interface Model {
    }

    interface View: BaseView {
        fun setNewData(data: ArrayList<MainInfoBean>)
    }

    interface Presenter {
        fun setData(queryMainUserList: List<MainInfoBean>)
    }
}
