package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity

/**
 * Created by Administrator on 2020/4/14.
 */

interface FriendListContract {
    interface Model {

    }

    interface View : BaseView {
        /**
         * 设置新数据
         */
        fun setNewData(fdListData: List<AZItemEntity<MainInfoBean>>)

        fun newFriendSize(newFriendSize: Int)

    }

    interface Presenter {
        /**
         *  获取好友列表数据并转换
         */

        fun getFdListData(beans: List<MainInfoBean>?): List<AZItemEntity<MainInfoBean>>

        /**
         * 数据A-Z排序
         * */

        fun fillData(date: List<MainInfoBean>): List<AZItemEntity<MainInfoBean>>
    }
}
