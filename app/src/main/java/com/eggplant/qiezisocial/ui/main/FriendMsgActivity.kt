package com.eggplant.qiezisocial.ui

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.FriendListActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.adapter.FriendMsgAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_friendmsg.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class FriendMsgActivity : BaseActivity() {
    lateinit var adapter: FriendMsgAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_friendmsg
    }

    override fun initView() {
        adapter = FriendMsgAdapter(null)
        ft_msg_ry.layoutManager = LinearLayoutManager(mContext)
        ft_msg_ry.adapter = adapter
    }

    override fun initData() {
        initSmartSwipe()
    }

    private fun refreshData() {
        val mainInfoBeans = MainDBManager.getInstance(mContext).queryMainUserList()
        var data = getFdListData(mainInfoBeans)
        adapter.setNewData(data)

    }

    private fun getFdListData(beans: List<MainInfoBean>): List<MainInfoBean> {

        var newFriendSize = 0
        val firendList = ArrayList<MainInfoBean>()
        if (beans != null && beans.isNotEmpty()) {
            beans.indices
                    .map { beans[it] }
                    .filterTo(firendList) {
                        if (TextUtils.equals(it.type, "gapplylist")) {
                            newFriendSize++
                        }
                        TextUtils.equals(it.type, "gfriendlist") && !TextUtils.isEmpty(it.msg)
                    }
//            mView?.newFriendSize(newFriendSize)
            return firendList
        }
//        mView?.newFriendSize(newFriendSize)
        return ArrayList()

    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun initEvent() {
        ft_msg_toft.setOnClickListener { startActivity(Intent(mContext, FriendListActivity::class.java)) }
        ft_msg_bar.setTbListener(object : SimpBarListener() {

            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnItemClickListener { _, view, position ->
            val mainInfoBean = adapter.data[position]
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", mainInfoBean))
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            val bean = adapter.data[position].convertUserEntry()
            bean.friend = "yes"
            startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", bean))
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMsg(event: NewMsgEvent) {
        refreshData()
    }


}
