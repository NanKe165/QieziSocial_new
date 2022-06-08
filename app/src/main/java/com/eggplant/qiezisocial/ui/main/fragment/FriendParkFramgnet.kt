package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.*
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.FriendParkContract
import com.eggplant.qiezisocial.event.GreetSbEvent
import com.eggplant.qiezisocial.event.RemoveGreetSbEvent
import com.eggplant.qiezisocial.event.ReplyGreetSbEvent
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.FriendParkPresenter
import com.eggplant.qiezisocial.socket.event.FriendListEvent
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.ui.main.DynamicActivity
import com.eggplant.qiezisocial.ui.main.GreetSbActivity
import com.eggplant.qiezisocial.ui.main.GreetSbDetailActivity
import com.eggplant.qiezisocial.ui.main.GreetSbListActivity
import com.eggplant.qiezisocial.ui.main.adapter.FriendParkAdapter
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.CommentUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.luck.picture.lib.tools.VoiceUtils
import kotlinx.android.synthetic.main.dialog_reply.view.*
import kotlinx.android.synthetic.main.ft_friendpark.*
import kotlinx.android.synthetic.main.layout_fdpark_head.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Administrator on 2022/4/6.
 */

class FriendParkFramgnet : BaseMvpFragment<FriendParkPresenter>(), FriendParkContract.View {
    override fun initPresenter(): FriendParkPresenter {
        return FriendParkPresenter()
    }

    companion object {

        private var fragment: FriendParkFramgnet? = null
            get() {
                if (field == null)
                    field = FriendParkFramgnet()
                return field
            }

        fun instanceFragment(bundle: Bundle?): FriendParkFramgnet {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    lateinit var adapter: FriendParkAdapter
    private lateinit var emptyView: View
    lateinit var headview: View
    private lateinit var greetSbList: ArrayList<GreetSbEvent>
    private lateinit var replyGreetSbList: CopyOnWriteArrayList<ReplyGreetSbEvent>
    private lateinit var replyWindow: BasePopupWindow
    private lateinit var popView: View
    private var layoutScrolling = false
    private var enableVoice = true
    override fun getLayoutId(): Int {
        return R.layout.ft_friendpark
    }

    override fun initView() {
        mPresenter.attachView(this)
        greetSbList = ArrayList()
        replyGreetSbList = CopyOnWriteArrayList()
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_fdpark, null, false)
        adapter = FriendParkAdapter(null)
        initHeadView()
        initPopWindow()
        ft_fdpark_ry.layoutManager = GridLayoutManager(mContext, 3)
//        ft_fdpark_ry.addItemDecoration(GridSpacingItemDecoration(4, resources!!.getDimension(R.dimen.qb_px_2).toInt(), resources!!.getDimension(R.dimen.qb_px_15).toInt()))
        ft_fdpark_ry.adapter = adapter
    }

    private fun initHeadView() {
        headview = LayoutInflater.from(mContext).inflate(R.layout.layout_fdpark_head, null)
        adapter.setHeaderView(headview)
        headview.fdpark_head_newmsg_gp.setOnClickListener {
            if (greetSbList.size > 0) {
                startActivity(Intent(mContext, GreetSbListActivity::class.java).putExtra("data", greetSbList))
            }
        }
        headview.fdpark_head_voice.setOnClickListener {
            enableVoice=!enableVoice
            setVoiceState()
        }

    }

    private fun setVoiceState() {
        if (enableVoice){
            headview.fdpark_head_voice.setImageResource(R.mipmap.icon_voice_open)
        }else{
            headview.fdpark_head_voice.setImageResource(R.mipmap.icon_voice_close)
        }
    }

    override fun initEvent() {
        adapter.setOnItemClickListener { _, view, position ->
            val bean = adapter.data[position]
            startActivity(Intent(mContext, GreetSbActivity::class.java).putExtra("bean", bean))
            activity?.overridePendingTransition(R.anim.open_enter2, R.anim.open_exit2)
        }
        ft_fdpark_world.setOnClickListener {
            if (!ClickUtil.isNotFastClick()) {
                return@setOnClickListener
            }
            startActivity(Intent(mContext, DynamicActivity::class.java))
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        ft_fdpark_ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    layoutScrolling = false
                } else if (newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING) {
                    layoutScrolling = true
                }
            }
        })
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
    }

    override fun setNewData(data: ArrayList<MainInfoBean>) {
        adapter.setNewData(data)
        if (data.isEmpty())
            adapter.emptyView = emptyView
    }

    fun initPopWindow() {
        popView = LayoutInflater.from(mContext).inflate(R.layout.dialog_reply, null, false)
        replyWindow = BasePopupWindow(mContext)
        replyWindow.contentView = popView
        replyWindow.showAnimMode = 1
    }

    fun showReplyWindow() {
        if (adapter.data.size == 0) {
            return
        }
        if (replyGreetSbList.size == 0) {
            return
        }
        val manager = ft_fdpark_ry.layoutManager as GridLayoutManager
        val firstVisibleItemPosition = manager.findFirstCompletelyVisibleItemPosition()
        val lastVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition()
        val data = adapter.data
        var showPosition = -1
        var showData: ReplyGreetSbEvent? = null
        var playVoice = false
        if (adapter.data.size <= replyGreetSbList.size) {
            label@ for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                val uid = data[i].uid
                for (index in 0 until replyGreetSbList.size) {
                    val uid1 = replyGreetSbList[index].from_userinfor.uid.toLong()
                    if (uid == uid1) {
                        showPosition = i
                        showData = replyGreetSbList[index]
                        return@label
                    }
                }
            }
        } else {
            replyGreetSbList.forEachIndexed { index, replyGreetSbEvent ->
                val uid = replyGreetSbEvent.from_userinfor.uid.toLong()
                for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                    val uid1 = data[i].uid
                    if (uid == uid1) {
                        showPosition = i
                        showData = replyGreetSbEvent
                        return@forEachIndexed
                    }
                }
            }
        }

        if (showPosition != -1) {
            var offest = 0
            var view = manager.getChildAt(showPosition + adapter.headerLayoutCount)
            if (view==null){
                view=manager.findViewByPosition(showPosition+adapter.headerLayoutCount)
                if (view==null) {
                    return
                }
            }
            var loca = IntArray(2)
            view.getLocationOnScreen(loca)
            val showMode = (showPosition + 1) % 3
            offest = when (showMode) {
                1 -> {
                    popView.dlg_reply_bg.setImageResource(R.mipmap.icon_dlg_reply_left)
                    -(ScreenUtil.dip2px(mContext, 136) - view.width) / 2 + ScreenUtil.dip2px(mContext, 22)

                }
                0 -> {
                    popView.dlg_reply_bg.setImageResource(R.mipmap.icon_dlg_reply_right)
                    -(ScreenUtil.dip2px(mContext, 136) - view.width) / 2 - ScreenUtil.dip2px(mContext, 22)
                }
                else -> {
                    popView.dlg_reply_bg.setImageResource(R.mipmap.icon_dlg_reply)
                    -(ScreenUtil.dip2px(mContext, 136) - view.width) / 2
                }
            }

            when (showData?.data?.replyType) {
                1 -> {
                    popView.dlg_reply_txt.text = "哼哼"
                    popView.dlg_reply_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_reply_right_vioce, 0)
                    playVoice = true
                }
                2 -> {
                    popView.dlg_reply_txt.text = "多谢夸奖"
                    popView.dlg_reply_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_reply_right_like, 0)
                }
                3 -> {
                    popView.dlg_reply_txt.text = "安慰安慰你"
                    popView.dlg_reply_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_reply_right_comfort, 0)
                }
            }
            replyWindow.showAtLocation(view, Gravity.NO_GRAVITY, loca[0] + offest, loca[1] - view.height / 3)
            if (playVoice)
                VoiceUtils.playVoice(mContext, enableVoice, R.raw.pig_music)
            replyGreetSbList.remove(showData)
            ft_fdpark_ry.postDelayed({
                replyWindow.dismiss()
            }, 4000)
        }

    }

    var lastRefreshTime = 0L
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFriendList(event: FriendListEvent) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastRefreshTime > 1000) {
            val newData = MainDBManager.getInstance(mContext).queryMainUserList()
            mPresenter.setData(newData)
        }
        if (TextUtils.equals(event.type, "gfriendlist") || TextUtils.equals(event.type, "gapplylist")) {
            lastRefreshTime = currentTimeMillis
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMsg(event: NewMsgEvent) {
        lastRefreshTime = System.currentTimeMillis()
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastRefreshTime > 1000) {
            mPresenter.setData(MainDBManager.getInstance(mContext).queryMainUserList())
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGreetSb(event: GreetSbEvent) {
        val activityName = CommentUtils.getRunningActivityName(mContext)
        val time = System.currentTimeMillis() - event.created
        Log.i("FriendPark", "-------onGreetSb  $activityName")
        if (activityName != "com.eggplant.qiezisocial.ui.main.SetSceneActivity"
                && activityName != "com.eggplant.qiezisocial.ui.main.GreetSbDetailActivity"
                &&activityName!="com.eggplant.qiezisocial.ui.main.RobBeansActivity"
                && (time < 60 * 1000L)) {
            startActivity(Intent(mContext, GreetSbDetailActivity::class.java).putExtra("data", event))
            activity?.overridePendingTransition(R.anim.open_enter4,R.anim.open_exit3)
            return
        }
        if (!greetSbList.contains(event)) {
            greetSbList.add(event)
            resetHeadInfo()
        }
    }


    private fun resetHeadInfo() {
        if (greetSbList.size > 0) {
            headview.fdpark_head_newmsg_gp.visibility = View.VISIBLE
            headview.fdpark_head_newmsg.text = "${greetSbList.size}条新消息"
            val data = greetSbList[greetSbList.size - 1]
            Glide.with(mContext!!).load(API.PIC_PREFIX + data.from_userinfor.face).into(headview.fdpark_head_img)
        } else {
            headview.fdpark_head_newmsg_gp.visibility = View.INVISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemoveGreetSb(event: RemoveGreetSbEvent) {
        if (greetSbList.contains(event.data)) {
            greetSbList.remove(event.data)
            resetHeadInfo()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReplyGreetSb(event: ReplyGreetSbEvent) {
        if (!replyGreetSbList.contains(event)) {
            replyGreetSbList.add(event)
            Log.i("FriendPark", "-------onReplyGreetSb  ${replyGreetSbList.size}")
        }
    }

    var needShowReply = false
    fun prepareShowReply() {
        needShowReply = true
    }

    fun stopShowReply() {
        needShowReply = false
    }

    fun onThreadBeat() {
        if (needShowReply && !layoutScrolling) {
            ft_fdpark_ry.post {
                showReplyWindow()
            }
        }
    }
}
