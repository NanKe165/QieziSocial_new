package com.eggplant.qiezisocial.ui.main.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.FriendListContract
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.presenter.FriendListPresneter
import com.eggplant.qiezisocial.socket.event.FriendListEvent
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.NewFriendListActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.adapter.FriendlistAdapter
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import kotlinx.android.synthetic.main.fragment_friendlist.*
import kotlinx.android.synthetic.main.view_friendlist_head.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2020/4/14.
 * 首页--好友列表
 */

class FriendListFragment : BaseMvpFragment<FriendListPresneter>(), FriendListContract.View {
    val REQUEST_AGREE_ADD = 122
    var popWindow: BasePopupWindow? = null
    var addblackUser: MainInfoBean? = null
    lateinit var apHeadView: View

    companion object {
        private var fragment: FriendListFragment? = null
            get() {
                if (field == null)
                    field = FriendListFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): FriendListFragment {
            if (bundle != null) {
                fragment?.arguments = bundle
            }
            return fragment!!
        }
    }

    private lateinit var adapter: FriendlistAdapter
    override fun initPresenter(): FriendListPresneter {
        return FriendListPresneter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_friendlist
    }

    override fun initView() {
        mPresenter.attachView(this)
        var from = arguments?.getString("from")
        if (from != null && from == "home") {
            ft_fdlist_gp.setPadding(0, resources.getDimension(R.dimen.qb_px_90).toInt(), 0, 0)
        }
        initPopWindow()
        adapter = FriendlistAdapter(mContext!!, null)
        getHeadView()
        ft_fdlist_ry.layoutManager = LinearLayoutManager(mContext)
        ft_fdlist_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))
        ft_fdlist_ry.adapter = adapter
    }

    private fun getHeadView(): View? {
        var view = View.inflate(mContext, R.layout.view_friendlist_head, null)
        apHeadView = view
        return view
    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dialog_addblack, null, false)
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.setOnClickListener {
            addblackUser?.let {
                mPresenter.addBlackList(activity!!, it.uid)
            }
            addblackUser = null
            popWindow?.dismiss()
        }

    }

    override fun initEvent() {

        ft_fdlist_azview.setOnLetterChangeListener { letter ->
            val position = adapter?.getSortLettersFirstPosition(letter)
            if (position != -1) {
                if (ft_fdlist_ry.layoutManager is LinearLayoutManager) {
                    val manager = ft_fdlist_ry.layoutManager as LinearLayoutManager
                    manager.scrollToPositionWithOffset(position, 0)
                } else {
                    ft_fdlist_ry.layoutManager.scrollToPosition(position)
                }
            }
        }
        ft_fdlist_pub.setOnClickListener {
            startActivity(Intent(mContext, PubTxtActivity::class.java))
        }
        adapter.setItemClickListen { bean ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("bean", bean)
            startActivity(intent)
        }
        adapter.setItemHeadClickListen { bean ->
            bean.friend = "yes"
            val intent = Intent(context, OtherSpaceActivity::class.java)
            intent.putExtra("bean", bean)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        adapter.setItemLongClickListene { v, item ->
            var loca = IntArray(2)
            v.getLocationOnScreen(loca)
            addblackUser = item
            popWindow?.showAtLocation(v, Gravity.NO_GRAVITY, v.width / 4, loca[1] + v.height / 2)
        }

        apHeadView.ft_fdlist_newfriend.setOnClickListener {
            startActivityForResult(Intent(mContext, NewFriendListActivity::class.java), REQUEST_AGREE_ADD)
        }

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        val mainInfoBeans = MainDBManager.getInstance(context).queryMainUserList()
        mPresenter.setNewData(mainInfoBeans)
    }

    var lastRefreshTime = 0L
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFriendList(event: FriendListEvent) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastRefreshTime > 1000) {
            val newData = MainDBManager.getInstance(mContext).queryMainUserList()
            mPresenter.setNewData(newData)

        }
        if (TextUtils.equals(event.type, "gfriendlist") || TextUtils.equals(event.type, "gapplylist")) {
            lastRefreshTime = currentTimeMillis
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onNewMsg(event: NewMsgEvent) {
//        lastRefreshTime= System.currentTimeMillis()
//        mPresenter.setNewData(MainDBManager.newInstance(mContext).queryMainUserList())
//    }

    override fun setNewData(fdListData: List<AZItemEntity<MainInfoBean>>) {
        ft_fdlist_ry.post {
            if (fdListData.isNotEmpty()) {
                ft_fdlist_emptyview.visibility = View.GONE
            } else {
                ft_fdlist_emptyview.visibility = View.VISIBLE
            }
            if (adapter.dataList==null||adapter.dataList.size != fdListData.size)
                adapter.dataList = fdListData
        }
    }

    override fun newFriendSize(newFriendSize: Int) {
        activity?.runOnUiThread {
            if (newFriendSize > 0) {
                adapter.headView = apHeadView
                apHeadView.ft_fdlist_hint_msg.visibility = View.VISIBLE
                apHeadView.ft_fdlist_hint_msg.text = newFriendSize.toString()
            } else {
                adapter.headView = null
                apHeadView.ft_fdlist_hint_msg.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AGREE_ADD && resultCode == RESULT_OK) {
//            (activity as MainActivity).ping()
        }
    }
}
