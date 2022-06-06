package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.MsgContract
import com.eggplant.qiezisocial.entry.MsgMultiltem
import com.eggplant.qiezisocial.event.ChangeBgEvent
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.presenter.MsgPresenter
import com.eggplant.qiezisocial.socket.event.FriendListEvent
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.FriendActivity
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.adapter.MsgHeadAdapter
import com.eggplant.qiezisocial.ui.main.adapter.MsgListAdapter2
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import kotlinx.android.synthetic.main.dialog_addblack.view.*
import kotlinx.android.synthetic.main.fragment_msg.*
import kotlinx.android.synthetic.main.layout_msgry_head.view.*
import kotlinx.android.synthetic.main.view_msg_head.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2020/4/14.
 */

class MsgFragment : BaseMvpFragment<MsgPresenter>(), MsgContract.View {
    companion object {
        private var fragment: MsgFragment? = null
            get() {
                if (field == null) {
                    field = MsgFragment()
                }
                return field
            }

        fun instanceFragment(bundle: Bundle?): MsgFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    lateinit var adaper: MsgListAdapter2
    lateinit var headAdapter: MsgHeadAdapter
    var popWindow: BasePopupWindow? = null
    var deleteUser: MainInfoBean? = null

    lateinit var headview: View
    override fun initPresenter(): MsgPresenter {
        return MsgPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_msg
    }

    override fun initView() {
        mPresenter.attachView(this)

        initPopWindow()
        adaper = MsgListAdapter2(null)
        adaper.setHeaderAndEmpty(true)
        ft_msg_ry.layoutManager = LinearLayoutManager(mContext)
        ft_msg_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 13), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        ft_msg_ry.adapter = adaper
        adaper.bindToRecyclerView(ft_msg_ry)
        adaper.setEmptyView(R.layout.layout_empty_msg)
        initHeadView()
        headAdapter = MsgHeadAdapter(null)
        msg_head_ry.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
        msg_head_ry.adapter = headAdapter


    }


    private fun initHeadView() {
        headview = LayoutInflater.from(mContext).inflate(R.layout.layout_msgry_head, null)
        adaper.setHeaderView(headview)
        headview.msg_head_apply_gp.setOnClickListener {
            startActivity(Intent(mContext,FriendActivity::class.java).putExtra("from","apply"))
        }
        headview.msg_head_system_gp.setOnClickListener {
            val sysBean=MainDBManager.getInstance(mContext).queryMainUser("2048")
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", sysBean))
        }
    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dialog_addblack, null, false)
        popView.dialog_addblack_tv.text = "删除该聊天"
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.setOnClickListener {
            deleteUser?.let {
                mPresenter.deleteChat(mContext!!, it)
                mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
            }
            deleteUser = null
            popWindow?.dismiss()
        }

    }

    override fun initEvent() {
        msg_head_gp.setOnClickListener {
            startActivity(Intent(mContext,FriendActivity::class.java).putExtra("from","friend"))
        }
        adaper.setOnItemClickListener { adapter, _, position ->
            var item = adapter.data[position] as MsgMultiltem<MainInfoBean>
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("bean", item.bean)
            intent.putExtra("qs", item.bean.qsTxt)
            startActivity(intent)
        }
        headAdapter.setOnItemClickListener { _, view, position ->
            val mainInfoBean = headAdapter.data[position]
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", mainInfoBean))
        }
        adaper.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.ap_msg_head) {
                val itemViewType = adapter.getItemViewType(position)
                var item = adapter.data[position] as MsgMultiltem<MainInfoBean>
                if (itemViewType == MsgMultiltem.QUES_MSG) {

                    if (item.bean.qsid == 0L) {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("bean", item.bean)
                        intent.putExtra("qs", item.bean.qsTxt)
                        startActivity(intent)
                    } else {
                        mPresenter.toOtherActivity(activity!!, item.bean.uid)
                    }
                    return@setOnItemChildClickListener
                }
                if (item.bean.gsid != 0L) {
                    return@setOnItemChildClickListener
                }
                var bean = item.bean.convertUserEntry()
                val mainuser = MainDBManager.getInstance(mContext).queryMainUser("${bean.uid}")
                if (mainuser != null && mainuser.type == "gfriendlist") {
                    bean.friend = "yes"
                }
                var intent = Intent(mContext, OtherSpaceActivity::class.java)
                intent.putExtra("bean", bean)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            }
        }
        adaper.setOnItemLongClickListener { _, view, position ->
            var data = adaper.data[position]
            var loca = IntArray(2)
            view.getLocationOnScreen(loca)
            deleteUser = data.bean
            popWindow?.showAtLocation(view, Gravity.NO_GRAVITY, view.width / 4, loca[1] + view.height / 2)
            true
        }

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
        mPresenter.setHeadData(MainDBManager.getInstance(context).queryMainUserListWithMsg())
    }

    override fun setApplyData(applyList: ArrayList<MainInfoBean>) {
        if (applyList.isNotEmpty()){
            headview.msg_head_apply_numb.text="${applyList.size}"
            headview.msg_head_apply_numb.visibility=View.VISIBLE
        }else{
            headview.msg_head_apply_numb.visibility=View.GONE
        }
    }

    override fun setFriendData(friendList: ArrayList<MainInfoBean>) {
        if (friendList.isNotEmpty()) {
            msg_head_gp.visibility = View.VISIBLE
            headAdapter.setNewData(friendList)
            ft_msg_bg.setBackgroundColor(ContextCompat.getColor(mContext!!,R.color.msg_head_bg))
        } else {
            msg_head_gp.visibility = View.GONE
            ft_msg_bg.setBackgroundColor(ContextCompat.getColor(mContext!!,R.color.home_bg))
        }
    }

    override fun setSystemData(hasSystemBean: Boolean, systemNumb: Int) {
        if (hasSystemBean){
            headview.msg_head_system_gp.visibility=View.VISIBLE
        }else{
            headview.msg_head_system_gp.visibility=View.GONE
        }
        if (systemNumb>0){
            headview.msg_head_system_numb.text="$systemNumb"
            headview.msg_head_system_numb.visibility=View.VISIBLE
        }else{
            headview.msg_head_system_numb.visibility=View.GONE
        }
    }

    override fun setAdapterData(data: ArrayList<MainInfoBean>) {

    }

    override fun setAdapterData2(data: ArrayList<MsgMultiltem<MainInfoBean>>) {
        adaper.setNewData(data)
    }

    override fun setTitle(title: String) {

    }

    override fun setTitleSize(friendmsgSize: Int) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMsg(event: NewMsgEvent) {
        (activity as MainActivity).hasNewMsg(true)
        mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
        mPresenter.setHeadData(MainDBManager.getInstance(context).queryMainUserListWithMsg())
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFriendList(event: FriendListEvent) {
        (activity as MainActivity).hasNewMsg(true)
        mPresenter.setHeadData(MainDBManager.getInstance(context).queryMainUserListWithMsg())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeBg(event: ChangeBgEvent) {

    }


}
