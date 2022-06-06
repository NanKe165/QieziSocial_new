package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.text.TextUtils
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.FriendListContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.FriendListModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.PinyinUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity
import com.eggplant.qiezisocial.widget.azlist.LettersComparator
import com.lzy.okgo.model.Response
import java.util.*
import java.util.concurrent.Executors


/**
 * Created by Administrator on 2020/4/14.
 */

class FriendListPresneter : BasePresenter<FriendListContract.View>(), FriendListContract.Presenter {

    private var mComparator: LettersComparator? = null
        get() {
            if (field == null)
                field = LettersComparator()
            return field
        }
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()


    /**
     * 数据排序 -并设置数据
     */
    fun setNewData(date: List<MainInfoBean>) {
        val runnable = Runnable {
            val fdListData = getFdListData(beans = date)
            Collections.sort(fdListData, mComparator)
            mView?.setNewData(fdListData)

        }
        singleThreadExecutor.execute(runnable)
    }


    /**
     * 获取好友列表数据并类型转换
     */
    override fun getFdListData(beans: List<MainInfoBean>?): List<AZItemEntity<MainInfoBean>> {

        val firendList = ArrayList<MainInfoBean>()
        var newFriendSize = 0
        if (beans != null && beans.isNotEmpty()) {
            beans.indices
                    .map { beans[it] }
                    .filterTo(firendList) {
                        if (TextUtils.equals(it.type, "gapplylist")) {
                            newFriendSize++
                        }
                        TextUtils.equals(it.type, "gfriendlist")
                    }
            var friendData = fillData(firendList)
            mView?.newFriendSize(newFriendSize)
            return friendData
        }
        mView?.newFriendSize(newFriendSize)
        return ArrayList()
    }

    /**
     * 添加排序表示
     */
    override fun fillData(date: List<MainInfoBean>): List<AZItemEntity<MainInfoBean>> {
        val sortList = ArrayList<AZItemEntity<MainInfoBean>>()
        for (aDate in date) {
            val item = AZItemEntity<MainInfoBean>()
            item.value = aDate
            //汉字转换成拼音
            val pinyin = PinyinUtils.getPingYin(aDate.nick)
            //取第一个首字母
            var letters = ""
            if (!TextUtils.isEmpty(pinyin))
                letters = pinyin.substring(0, 1).toUpperCase()
            // 正则表达式，判断首字母是否是英文字母
            if (letters.matches("[A-Z]".toRegex())) {
                item.setSortLetters(letters.toUpperCase())
            } else {
                item.setSortLetters("#")
            }
            sortList.add(item)
        }
        return sortList
    }

    fun addBlackList(activity: Activity, u: Long) {
        FriendListModel.addBlack(activity, u.toInt(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful){
                        TipsUtil.showToast(activity,response.body().msg!!)
                    }
                }
            }
        })
    }
}
