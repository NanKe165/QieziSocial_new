package com.eggplant.qiezisocial.ui.main.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.HomeContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.event.*
import com.eggplant.qiezisocial.presenter.HomePresenter
import com.eggplant.qiezisocial.ui.decorate.DecorateActivity
import com.eggplant.qiezisocial.ui.extend.dialog.ShareDialog
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.main.*
import com.eggplant.qiezisocial.ui.main.adapter.HomeAdapter
import com.eggplant.qiezisocial.ui.main.dialog.JoinGroupDialog
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.GuideUtil
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.guideview.Component
import com.eggplant.qiezisocial.widget.guideview.GuideBean
import com.eggplant.qiezisocial.widget.guideview.GuideBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Administrator on 2020/4/14.
 * 首页---信息盒
 */
class HomeFragment : BaseMvpFragment<HomePresenter>(), HomeContract.View {
    override fun setRedPacket(redPacketEntry: RedPacketEntry) {

    }

    private val REQUEST_ANSWQS = 100
    private val REQUEST_PUB_BOX = 101
    private var replyPosition = -1
    private var itemSize = 4
    private lateinit var joinDialog: JoinGroupDialog
    var bgs = arrayListOf<Int>(R.drawable.homebg1, R.drawable.homebg2, R.drawable.homebg3, R.drawable.homebg4, R.drawable.homebg5,
            R.drawable.homebg6, R.drawable.homebg7, R.drawable.homebg8, R.drawable.homebg9, R.drawable.homebg10, R.drawable.homebg11, R.drawable.homebg12)

    var goalBgs = arrayListOf<Int>(R.color.homebg1, R.color.homebg2, R.color.homebg3, R.color.homebg4, R.color.homebg5, R.color.homebg6, R.color.homebg7)
    var goalDector = arrayListOf<Int>(R.drawable.icon_home_label_dector1, R.drawable.icon_home_label_dector2, R.drawable.icon_home_label_dector3,
            R.drawable.icon_home_label_dector4, R.drawable.icon_home_label_dector5, R.drawable.icon_home_label_dector6, R.drawable.icon_home_label_dector7)
    var dynamicDraw = arrayListOf<Int>(R.mipmap.icon_home_dynamic1, R.mipmap.icon_home_dynamic2, R.mipmap.icon_home_dynamic3, R.mipmap.icon_home_dynamic4,
            R.mipmap.icon_home_dynamic5, R.mipmap.icon_home_dynamic6, R.mipmap.icon_home_dynamic7)
    var pauseFlag = true
    var spaceback: String = ""    //    private lateinit var pubSuccessDialog: PubSuccessDialog
    var sceneCollect = false
    lateinit var shareDialog: ShareDialog
    private val REQUEST_INVITE = 112
    var edit: SharedPreferences.Editor? = null
    var guideHasShow = false
    var ry_offsetY = 0F
    var ry_lastY = 0F
    //    private var qsIndex = 0
//    private var qsList: List<SysQuestionEntry>? = null
    override fun initPresenter(): HomePresenter {
        return HomePresenter()
    }

    companion object {
        private var fragment: HomeFragment? = null
            get() {
                if (field == null)
                    field = HomeFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): HomeFragment {
            if (bundle != null) {
                fragment?.arguments = bundle
            }
            return fragment!!
        }
    }

    lateinit var adaper: HomeAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }


    override fun initView() {
        mPresenter.attachView(this)
//        val fromRegister = activity!!.intent.getBooleanExtra("fromRegister", false)
//        if (!fromRegister)
//            startPubAnim()
        changeHomeBg()
        initShareDialog()
        initJoinDialog()

//        pubSuccessDialog = PubSuccessDialog(mContext!!)
//        setRecyclerViewHeight()
        adaper = HomeAdapter(null)
        home_ry.layoutManager = LinearLayoutManager(mContext)
        home_ry.itemAnimator = HomeRyAnimator()
        home_ry.adapter = adaper
//        home_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))
        home_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 10), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        home_refresh.setEnableAutoLoadMore(true)
        //列表不满一页时候开启上拉加载功能
        home_refresh.setEnableLoadMoreWhenContentNotFull(true)
        val preferences = activity!!.getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
        edit = preferences.edit()
        guideHasShow = preferences.getBoolean("guide_has_show", false)
        var newPub = activity!!.intent.getBooleanExtra("newPub", false)
        if (newPub) {
            home_mine_pub.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home_reply_hint, 0, R.mipmap.icon_mine_pub, 0)
        }

        home_text_swicher.setFactory {
            val textView = QzTextView(mContext!!)
            textView.setTextColor(ContextCompat.getColor(mContext!!, R.color.tv_gray4))
            textView.textSize = 11f
            textView
        }
//        home_ry.postDelayed({
//            startPubAnim()
//        }, 1000)
    }

    private fun initShareDialog() {
        val image = UMImage(activity, R.mipmap.share_img)

        //UMLog_Social
        val thumb = UMImage(activity, R.mipmap.share_img)
        val shareAction = ShareAction(activity).withText("交个朋友").withMedia(image)
        shareDialog = ShareDialog(mContext, intArrayOf(R.id.dlg_share_qq, R.id.dlg_share_pyq, R.id.dlg_share_wechat, R.id.dlg_share_sina))
        shareDialog.setOnBaseDialogItemClickListener { dialog, view ->
            var type = ""
            when (view.id) {
                R.id.dlg_share_qq -> {
                    type = "qq"
                    shareAction.platform = SHARE_MEDIA.QQ
                    shareAction.share()
//                    Log.i("GpShare", "share_qq")
                }
                R.id.dlg_share_pyq -> {
                    type = "pyq"
                    shareAction.platform = SHARE_MEDIA.WEIXIN_CIRCLE
                    shareAction.share()
//                    Log.i("GpShare", "share_wx")
                }
                R.id.dlg_share_wechat -> {
                    type = "wechat"
                    shareAction.platform = SHARE_MEDIA.WEIXIN
                    shareAction.share()
//                    Log.i("GpShare", "share_wx")
                }
                R.id.dlg_share_sina -> {
                    type = "sina"
                    shareAction.platform = SHARE_MEDIA.SINA
                    shareAction.share()
                }

            }
//            val data = adaper.data
//            if (data.isNotEmpty()) {
//                startActivity(Intent(mContext, InviteActivity::class.java).putExtra("data", ArrayList(data)).putExtra("type", type))
//                activity?.overridePendingTransition(R.anim.open_enter2, R.anim.empty_anim)
//            }
            dialog.dismiss()
        }
    }

    private fun setBackGround(p: Int) {
        home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg)
        when (p) {
            0 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector1)

            }
            1 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector2)

            }
            2 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector3)
            }
            4 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector5)
            }
            5 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector6)

            }
            6 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector7)
            }
            7 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector8)
                home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg1)

            }
            8 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector9)
                home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg2)
            }
            9 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector10)
//                home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg3)
            }
            10 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector11)
//                home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg3)
            }
            11 -> {
                home_decorate.setImageResource(R.drawable.icon_home_dector12)
//                home_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg3)
            }
            else -> {
                home_decorate.setImageDrawable(null)
            }
        }
        home_root_view.background = ContextCompat.getDrawable(mContext!!, bgs[p])
        setDecorateVisiable()
        setRecyclerViewHeight()

    }


    override fun initEvent() {
        home_scene_collect.setOnClickListener {
            mPresenter.collectScene(mPresenter.getCurrentSid(), sceneCollect)
        }
        home_share.setOnClickListener {
            shareDialog.show()
        }
        home_root_view.setOnLongClickListener { view ->
            startActivity(Intent(mContext, DecorateActivity::class.java))
//            startActivity(Intent(mContext, TestActivity::class.java))
            true
        }
        home_dt.setOnClickListener {
            if (!ClickUtil.isNotFastClick()) {
                return@setOnClickListener
            }
            Log.i("alip", "startActivity  ${mPresenter.getCurrentSid()}")
//            mPresenter.getcurrentType()0表示图文，2表示视频，3 表示音频，4表示地图
            when {
                mPresenter.getCurrentType() == "2" -> startActivity(Intent(mContext, VideoPlayerActivity::class.java).putExtra("gid", mPresenter.getCurrentSid().toInt()))
                mPresenter.getCurrentType() == "3" -> startActivity(Intent(mContext, AudioPlayerActivity::class.java).putExtra("gid", mPresenter.getCurrentSid().toInt()))
                mPresenter.getCurrentType() == "4" -> RxPermissions(fragment!!)
                        .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe { b ->
                            if (b) {
                                startActivity(Intent(mContext, MapDynamicActivity::class.java)
                                        .putExtra("goal", mPresenter.getCurrentGoal())
                                        .putExtra("sid", mPresenter.getCurrentSid()))
                            } else {
                                TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                            }
                        }
                else -> startActivity(Intent(mContext, DynamicActivity::class.java).putExtra("goal", mPresenter.getCurrentGoal())
                        .putExtra("sid", mPresenter.getCurrentSid())
                        .putExtra("name", home_dt_tv.text.toString()))
            }
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            home_dt_hint.visibility = View.GONE
        }
//        home_refresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
//            override fun onLoadMore(refreshLayout: RefreshLayout) {
//                mPresenter.addData(activity!!, adaper?.data.size)
//            }
//
//            override fun onRefresh(refreshLayout: RefreshLayout) {
////                hintRefreshHint()
//                home_ry.postDelayed({
//                    mPresenter.refreshData(activity!!)
//                }, 1000)
//            }
//        })
        home_pub.setOnClickListener {
            if (isPubViewHeight) {
                stopPubAnim()
            }
            if (ClickUtil.isNotFastClick()) {
                var intent = Intent(mContext, PubTxtActivity::class.java)
                intent.putExtra("question", home_topictxt.text.toString())
                startActivityForResult(intent, REQUEST_PUB_BOX)
                activity?.overridePendingTransition(R.anim.open_enter3, R.anim.open_exit3)
            }
        }
        home_mine_pub.setOnClickListener {
            startActivity(Intent(mContext, MinePubBoxActivity::class.java))
            home_mine_pub.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_mine_pub, 0)
        }
        home_ry.setOnTouchListener(object : View.OnTouchListener {
            var showDetail = false
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        mPresenter.threadPause = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (ry_lastY == 0F) {
                            ry_lastY = event.y
                        }
                        ry_offsetY += (event.y - ry_lastY)
                        ry_lastY = event.y
                        if (Math.abs(ry_offsetY) > ScreenUtil.dip2px(mContext, 50)) {
                            if (!showDetail)
                                showDataDetail()
                            showDetail = true
                        }
                        mPresenter.threadPause = true

                    }
                    MotionEvent.ACTION_UP -> {
                        showDetail=false
                        ry_lastY = 0F
                        ry_offsetY = 0F
                        home_ry?.postDelayed({
                            mPresenter.threadPause = false
                        }, 5000)

                    }
                }
                return false
            }
        })

        adaper.setOnItemLongClickListener { _, view, position ->
            //            mPresenter.threadPause = true
//            home_pub?.postDelayed({
//                mPresenter.threadPause = false
//            }, 5000)
            showDataDetail()
            true
        }
        adaper.setOnItemClickListener { _, view, position ->
            if (ClickUtil.isNotFastClick()) {
                var bean = adaper.data[position]
                if (bean.uid.toInt() == application.infoBean?.uid) {
//                    TipsUtil.showToast(mContext, "自娱自乐可就没意思了!")
                    return@setOnItemClickListener
                }
                bean.read = true
                replyPosition = position
                adaper.notifyItemChanged(position)
                if (bean.type == "boxquestiongroup") {
                    joinDialog.setJoinData(bean)
                    joinDialog.show()
                } else {
                    val location = IntArray(2)
                    view.getLocationOnScreen(location)
                    val msgLoca = (activity as MainActivity).main_top_bar.msgImgLoca
                    mPresenter.joinQs(activity!!, bean, location[1], msgLoca[0], msgLoca[1])
                }
//                var intent = Intent(mContext, AnswQsActivity::class.java)
//                intent.putExtra("bean", bean)
//                startActivityForResult(intent, REQUEST_ANSWQS)
//                activity?.overridePendingTransition(R.anim.open_enter2, R.anim.open_exit2)
//                setQsNumb()
            }
        }
        adaper.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.ap_home_head -> {
                    var userInfo = (adapter.data[position] as BoxEntry).userinfor
                    var intent = Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", userInfo)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
            }
        }
        home_shadow.setOnClickListener {
            stopPubAnim()
        }


    }

    private fun showDataDetail() {
        if (removeData.isEmpty()) {
            return
        }
        val msgLoca = (activity as MainActivity).main_top_bar.msgImgLoca
        val data = arrayListOf<BoxEntry>()
        data.addAll(adaper.data)
        data.addAll(removeData)
        startActivity(Intent(mContext, HomeDetailActivity::class.java)
                .putExtra("ry_height", home_ry.height)
                .putExtra("data", data)
                .putExtra("p1", msgLoca[0])
                .putExtra("p2", msgLoca[1]))
        activity!!.overridePendingTransition(0, 0)

    }

    override fun initData() {
//        mPresenter.refreshData(activity!!)
        var scenes = ""
        var sid = ""
        var type = "0"
        var moment = ""
        if (application.loginEntry != null && application.loginEntry!!.filter != null) {
            scenes = application.loginEntry!!.filter.goal
            sid = application.loginEntry!!.filter.sid
            if (application.loginEntry!!.filter.type != null)
                type = application.loginEntry!!.filter.type
        }
        mPresenter.initData(itemSize, scenes, sid, type)
        mPresenter.continueToAddData(5000)
        mPresenter.setTopic(activity!!, scenes, sid)
        setBgWithGoal(scenes, moment)
    }

    override fun onResume() {
        super.onResume()
        pauseFlag = false
    }

    override fun onPause() {
        super.onPause()
        pauseFlag = true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        pauseFlag = !isVisibleToUser
    }

    private fun initJoinDialog() {
        joinDialog = JoinGroupDialog(mContext!!, intArrayOf(R.id.dlg_joingp_join))
        joinDialog.setOnDismissListener {
            pauseFlag = false
        }
        joinDialog.setOnShowListener {
            pauseFlag = true
        }
        joinDialog.setOnBaseDialogItemClickListener { dialog, view ->
            if (view.id == R.id.dlg_joingp_join) {
                val bean = joinDialog.bean
                mPresenter.joinGroup(mContext!!, bean!!)
            }
            joinDialog.dismiss()
        }

    }

    fun setFilterCollect(collect: Boolean) {
        sceneCollect = collect
        changeCollectBg()
    }

    private fun changeCollectBg() {
        if (sceneCollect) {
            home_scene_collect.setImageResource(R.mipmap.icon_scene_collected)
        } else {
            home_scene_collect.setImageResource(R.mipmap.icon_scene_collect)
        }
    }

    fun setFilter(goal: String, sid: String, type: String, moment: String) {
        if (mPresenter.getCurrentGoal() == goal) {
            prepareShowGuide()
            return
        }

        adaper.data.clear()
        adaper.notifyDataSetChanged()
        removeData.clear()
        mPresenter.initData(itemSize, goal, sid, type)
        mPresenter.setTopic(activity!!, goal, sid)
        setBgWithGoal(goal, moment)
    }

    fun getFilter(): FilterEntry {
        val entry = FilterEntry()
        entry.sid = mPresenter.getCurrentSid()
        entry.goal = mPresenter.getCurrentGoal()
        entry.type = mPresenter.getCurrentType()
        return entry
    }

    private fun setRecyclerViewHeight() {
        var displayHeight = ScreenUtil.getDisplayHeightPixels(activity)
        var barHight = resources.getDimension(R.dimen.qb_px_60)
//        var pubLRMargin = resources.getDimension(R.dimen.qb_px_11)
        //resources.getDimension(R.dimen.qb_px_45)
//        var pubHeight = home_pub.height
        var dectorHeight = home_decorate.measuredHeight
        dectorHeight = if (home_decorate.drawable == null) {
            0
        } else {
            resources.getDimension(R.dimen.qb_px_40).toInt()
        }
        Log.i("homeft", " dectorHeight:$dectorHeight")
        //resources.getDimension(R.dimen.qb_px_43)
//        (home_pub.layoutParams as FrameLayout.LayoutParams).bottomMargin
        var bottomMargin = (home_decorate.layoutParams as FrameLayout.LayoutParams).bottomMargin
        var topMargin = resources.getDimension(R.dimen.qb_px_30)
        var itemHeight = resources.getDimension(R.dimen.qb_px_93) + ScreenUtil.dip2px(mContext, 10)
        var controlHeight = resources.getDimension(R.dimen.qb_px_30)
        var recyclerSpace = displayHeight - barHight - dectorHeight - topMargin - bottomMargin - controlHeight
        var itemContent = (recyclerSpace / itemHeight).toInt()
        Log.i("homeft", " itemheight:$itemHeight size:${recyclerSpace % itemHeight}")
        itemSize = itemContent
        var recyclerHeight = (itemContent * itemHeight).toInt() - ScreenUtil.dip2px(mContext, 1)
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, recyclerHeight)
        params.setMargins(0, (barHight + topMargin).toInt(), 0, 0)
        home_ry.layoutParams = params
    }

    override fun collectSceneSuccess() {
        sceneCollect = !sceneCollect
        changeCollectBg()
    }

    override fun showMainHintView() {
        if (activity is MainActivity && !pauseFlag) {
            home_ry?.post {
                //                (activity as MainActivity).showHintView()
            }
        }
    }

    override fun continueToAddData() {
        home_ry?.post {
            if (!pauseFlag)
                mPresenter.addNewData()
        }
    }


    override fun setNewData(data: List<BoxEntry>?) {
        home_refresh.finishRefresh()
        if (data != null && data.isNotEmpty()) {
//            showRefreshHint(dataNum = data.size)
            Log.i("homeAdapter", "setNewData   size:${data.size}")
            home_ry.post {
                adaper.setNewData(data)
                runLayoutAnimation(home_ry)
            }
            prepareShowGuide()
//            setQsNumb()
        } else {
            TipsUtil.showToast(mContext, "暂时没有新的数据喽！")
        }
    }

    var needToshowGuide = false
    private fun prepareShowGuide() {
//        && !guideHasShow
        if (needToshowGuide && !guideHasShow) {
            showGuide()
        }
        needToshowGuide = false
    }

    var showGuide = false
    private fun showGuide() {
        val guide1 = GuideBean(home_scene_collect, R.mipmap.home_guide1, -ScreenUtil.dip2px(mContext, 10), -ScreenUtil.dip2px(mContext, 15), Component.ANCHOR_RIGHT, Component.FIT_END)
        val guide2 = GuideBean(home_pub, R.mipmap.home_guide2, ScreenUtil.dip2px(mContext, 80), -ScreenUtil.dip2px(mContext, 25), Component.ANCHOR_LEFT, Component.FIT_END)
        val guide3 = GuideBean(home_dt, R.mipmap.home_guide3, ScreenUtil.dip2px(mContext, 30), -ScreenUtil.dip2px(mContext, 25), Component.ANCHOR_LEFT, Component.FIT_END)
        val guide4 = GuideBean(home_guide_tag, R.mipmap.home_guide4, -(ScreenUtil.dip2px(mContext, 110)), ScreenUtil.dip2px(mContext, 35), Component.ANCHOR_RIGHT, Component.FIT_START)
        val guideList = arrayListOf(guide1, guide2, guide3, guide4)
        val corners = arrayListOf(ScreenUtil.dip2px(mContext, 10), ScreenUtil.dip2px(mContext, 25), ScreenUtil.dip2px(mContext, 20), ScreenUtil.dip2px(mContext, 10))
        GuideUtil.showGuide(activity, guideList, corners, object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {

            }

            override fun onDismiss() {
                showGuide = false
                guideHasShow = true
                edit?.putBoolean("guide_has_show", true)
                edit?.commit()
            }
        })
        showGuide = true
    }

    override fun setTopic(s: String) {
        home_topictxt.post {
            home_topictxt.text = s
            home_text_swicher.setText(s)
        }
    }

    //    private fun setQsNumb() {
//        var size = 0
//        adaper.data.forEach {
//            if (!it.read) {
//                size++
//            }
//        }
//        (activity as MainActivity).setQsNumb(size)
//    }
    private fun msgImgFlicker() {
        (activity as MainActivity).msgImgFlicker()
    }

    val removeData = ArrayList<BoxEntry>()
    override fun addData(data: List<BoxEntry>?) {

        if (data != null && data.isNotEmpty() && stopPubAnimFinish) {
            var it = (data as ArrayList).iterator()
            while (it.hasNext()) {
                if (adaper.data.contains(it.next()))
                    it.remove()
            }
            if (data.isEmpty()) {
                return
            }
            home_refresh.finishLoadMore(true)
            home_ry.post {
                if (adaper.data.size > itemSize - 1) {
                    removeData.add(0,adaper.data[itemSize - 1])
                    adaper.remove(itemSize - 1)
                }
                if (adaper.data.size == 0) {
                    adaper.setNewData(data)
                    runLayoutAnimation(home_ry)
                } else {
                    adaper.addData(0, data)
//                    val layoutManager = home_ry.layoutManager as LinearLayoutManager
//                    val firstCvPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
//
//                    adaper.addData(firstCvPosition, data)
//                    if (firstCvPosition == 0) {
//                        layoutManager.scrollToPosition(0)
//                    }
                }
            }
//            setQsNumb()
        } else {
            home_refresh.finishLoadMoreWithNoMoreData()
        }
    }

    override fun setSysQsList(list: List<SysQuestionEntry>?) {
//        qsList = list
//        nextQs()
    }

    override fun showTost(s: String?) {
        s?.let { TipsUtil.showToast(mContext, it) }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }
//    private fun nextQs() {
//        if (qsList != null && qsList!!.isNotEmpty()) {
//            if (qsIndex >= qsList!!.size) {
//                qsIndex = 0
//            }
//
//            qsIndex++
//        }
//    }


//    private fun showRefreshHint(dataNum: Int) {
//        if (dataNum > 0) {
//            var task = timerTask {
//                home_refresh_hint.post {
//                    home_refresh_hint.visibility = VISIBLE
//                    home_refresh_hint.text = "新收到$dataNum 条"
//                    startAinm(home_refresh_hint)
//                }
//                Thread.sleep(2000)
////                hintRefreshHint()
//            }
//            Timer().schedule(task, 1300)
//        }
//    }

//    private fun startAinm(view: View?) {
//        var anim = TranslateAnimation(0f, 0f, -30f, 0f)
//        anim.duration = 400
//        view?.startAnimation(anim)
//    }
//
//    private fun hintRefreshHint() {
//        home_refresh_hint?.post {
//            home_refresh_hint.clearAnimation()
//            home_refresh_hint.visibility = GONE
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ANSWQS) {
                if (replyPosition >= 0 && replyPosition < adaper.data.size) {
                    var bean = data?.getSerializableExtra("answBean") as BoxEntry?
                    bean?.let {
                        adaper.data[replyPosition] = it
                        adaper.notifyItemChanged(replyPosition)
                    }
                }
            } else if (requestCode == REQUEST_PUB_BOX) {
//                home_mine_pub.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home_reply_hint, 0, R.mipmap.icon_mine_pub, 0)
//                pubSuccessDialog.show()
                var bean = data?.getSerializableExtra("pubBox") as BoxEntry?
                bean?.let {
                    addData(arrayListOf(it))
                }
                mPresenter.setNextTopic(activity!!)
            }
        }
    }


//    private fun addDataInFirstView(bean: BoxEntry) {
//        var manager = home_ry.layoutManager as LinearLayoutManager
//        var position = manager.findFirstCompletelyVisibleItemPosition()
//        if (position >= 0) {
//            if (position < adaper.data.size) {
//                adaper.setData(position, bean)
//            } else {
//                adaper.addData(bean)
//            }
//        }
//    }

    private fun startPubAnim() {
        var heightPx = ScreenUtil.getDisplayHeightPixels(mContext)
        home_shadow.visibility = VISIBLE
        home_pub.translationY = -(heightPx) / 3.toFloat()
        home_decorate.translationY = -(heightPx) / 3.toFloat()
//        var anim = ObjectAnimator.ofFloat(home_pub, "translationY", 0.0f, -(heightPx) / 3.toFloat())
//        anim.duration = 1000L
//        anim.start()
        isPubViewHeight = true
    }

    var stopPubAnimFinish = true
    //发布按钮是否是高亮状态
    var isPubViewHeight = false

    fun stopPubAnim() {
        var heightPx = ScreenUtil.getDisplayHeightPixels(mContext)
        home_shadow.visibility = VISIBLE
        var animset = AnimatorSet()
        var anim = ObjectAnimator.ofFloat(home_pub, "translationY", -(heightPx) / 3.toFloat(), 0.0f)
        var anim2 = ObjectAnimator.ofFloat(home_decorate, "translationY", -(heightPx) / 3.toFloat(), 0.0f)
        animset.playTogether(anim, anim2)
        animset.duration = 1000L
//        anim.duration = 1000L
        animset.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                home_shadow.visibility = GONE
                stopPubAnimFinish = true
                setDecorateVisiable()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                stopPubAnimFinish = false
                isPubViewHeight = false
            }
        })
        if (stopPubAnimFinish) {
            animset.start()
        }
    }

    fun setNextTopic() {
        if (showGuide) {
            return
        }
        mPresenter.setNextTopic(activity!!)
        startPubAnim()
        setDecorateVisiable()
    }

    fun setDecorateVisiable() {
//        if (isPubViewHeight) {
//            home_decorate.visibility = View.INVISIBLE
//        } else {
//            home_decorate.visibility = View.VISIBLE
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeBg(event: ChangeBgEvent) {
        changeHomeBg()
        var scenes = ""
        var sid = ""
        var type = "0"
        if (application.loginEntry!!.filter != null) {
            scenes = application.loginEntry!!.filter.goal
            sid = application.loginEntry!!.filter.sid
            type = application.loginEntry!!.filter.type
        }
        mPresenter.initData(itemSize, scenes, sid, type)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeData(event: AnswQsEvent) {
        adaper.showing = !event.startAnim
        adaper.notifyDataSetChanged()
        if (event.replySuccess)
            msgImgFlicker()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun smallTxt(event: SmallTxtEvent) {
//       event.txt
        setTopic(event.txt)
    }


    //有新动态
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun momentCount(event: MomentCountEvent) {
        if (event.count > 0 && mPresenter.getCurrentType() != "2" && mPresenter.getCurrentType() != "3") {
            home_dt_hint.visibility = View.VISIBLE
        } else {
            home_dt_hint.visibility = View.GONE
        }
    }

    //封号
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun seal(event: SealEvent) {
        TipsUtil.showToast(mContext, "你被封号了")
        EventBus.getDefault().post(LogoutEvent())
        EventBus.getDefault().post(FinishEvent())
        startActivity(Intent(mContext, LoginActivity2::class.java))
    }


    private fun changeHomeBg() {
        if (application.infoBean != null) {
            spaceback = application.infoBean!!.spaceback
        }
        if (spaceback.isNotEmpty() && spaceback.toInt() < bgs.size) {
            setBackGround(spaceback.toInt())
        } else {
            setBackGround(0)
        }
    }


    private fun setBgWithGoal(goal: String, moment: String) {
        var setSuccess = false
        if (application.loginEntry != null) {
            val scenes = application.loginEntry!!.scenes
            scenes?.forEachIndexed { index, scenesEntry ->
                if (scenesEntry.title == goal) {
//                setBgColorAndDector(index)
                    if (scenesEntry.pic != null && scenesEntry.pic.isNotEmpty())
                        setDynamicNick(scenesEntry.pic.toInt() - 1, scenesEntry.moment)
                    else
                        setDynamicNick(6, scenesEntry.moment)
                    setSuccess = true
                }
            }
        }
        if (!setSuccess) {
            if (moment.isNotEmpty()) {
                setDynamicNick(6, moment)
            } else {
                setDynamicNick(6, "动态")
            }
        }
    }

    private fun setDynamicNick(index: Int, moment: String) {

        home_dt_tv.text = moment
        if (index < 6) {
            home_dt_tv.setCompoundDrawablesWithIntrinsicBounds(dynamicDraw[index], 0, 0, 0)
        } else {
            home_dt_tv.setCompoundDrawablesWithIntrinsicBounds(dynamicDraw[6], 0, 0, 0)
        }
    }

    private fun setBgColorAndDector(index: Int) {
        if (index < 6) {
            home_decorate.setImageResource(goalDector[index])
            home_root_view.setBackgroundColor(ContextCompat.getColor(mContext!!, goalBgs[index]))
        } else {
            home_decorate.setImageResource(goalDector[6])
            home_root_view.setBackgroundColor(ContextCompat.getColor(mContext!!, goalBgs[6]))
        }
        setRecyclerViewHeight()
    }

    var lastTitle: String = ""
    var currentGid: Int = 0
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTitle(data: List<GchatEntry>) {
        if (data.isNotEmpty()) {
            val data = data[0]
            if (data.title != lastTitle) {
                currentGid = data.id
                lastTitle = data.title
            }
        }
    }

    override fun onThreadBeat() {
        (activity as MainActivity).onThreadBeat()
    }
}
