package com.eggplant.qiezisocial.ui

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.ui.main.adapter.FriendlistAdapter
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.utils.PinyinUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity
import com.eggplant.qiezisocial.widget.azlist.LettersComparator
import kotlinx.android.synthetic.main.activity_select_friend.*
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by Administrator on 2020/8/3.
 */

class SelectFriendActivity : BaseActivity() {
    private lateinit var adapter: FriendlistAdapter
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    private var mComparator: LettersComparator? = null
        get() {
            if (field == null)
                field = LettersComparator()
            return field
        }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_friend
    }

    override fun initView() {
        adapter = FriendlistAdapter(mContext, null)
        select_friend_ry.layoutManager = LinearLayoutManager(mContext)
        select_friend_ry.adapter = adapter
        select_friend_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))


    }

    override fun initData() {
        val data = MainDBManager.getInstance(mContext).queryMainUserList()
        setNewData(data)
    }

    override fun initEvent() {
        select_friend_azview.setOnLetterChangeListener { letter ->
            val position = adapter?.getSortLettersFirstPosition(letter)
            if (position != -1) {
                if (select_friend_ry.layoutManager is LinearLayoutManager) {
                    val manager = select_friend_ry.layoutManager as LinearLayoutManager
                    manager.scrollToPositionWithOffset(position, 0)
                } else {
                    select_friend_ry.layoutManager.scrollToPosition(position)
                }
            }
        }
        adapter.setItemClickListen { bean ->
            finish()
        }

    }

    /**
     * 数据排序 -并设置数据
     */
    fun setNewData(date: List<MainInfoBean>) {
        val runnable = Runnable {
            val fdListData = getFdListData(beans = date)
            Collections.sort(fdListData, mComparator)
            select_friend_ry.post {
                if (fdListData.isEmpty()) {
                    TipsUtil.showToast(mContext, "你还没有添加好友哦")
                    return@post
                }
                adapter.dataList = fdListData
            }

        }
        singleThreadExecutor.execute(runnable)
    }

    /**
     * 获取好友列表数据并类型转换
     */
    fun getFdListData(beans: List<MainInfoBean>?): List<AZItemEntity<MainInfoBean>> {

        val firendList = ArrayList<MainInfoBean>()

        if (beans != null && beans.isNotEmpty()) {
            beans.indices
                    .map { beans[it] }
                    .filterTo(firendList) {
                        TextUtils.equals(it.type, "gfriendlist")
                    }
            var friendData = fillData(firendList)

            return friendData
        }
        return ArrayList()
    }

    /**
     * 添加排序表示
     */
    fun fillData(date: List<MainInfoBean>): List<AZItemEntity<MainInfoBean>> {
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


}
