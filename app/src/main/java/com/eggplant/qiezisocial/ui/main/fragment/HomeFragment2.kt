package com.eggplant.qiezisocial.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout.VERTICAL
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.HomeContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.event.*
import com.eggplant.qiezisocial.presenter.HomePresenter
import com.eggplant.qiezisocial.ui.extend.dialog.ShareDialog
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.main.*
import com.eggplant.qiezisocial.ui.main.adapter.CardAdapter
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity2
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.CommentUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.progress.WaveProgressView
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ft_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2022/3/7.
 */

class HomeFragment2 : BaseMvpFragment<HomePresenter>(), HomeContract.View {
    override fun onThreadBeat() {
        (activity as MainActivity).onThreadBeat()
    }

    companion object {
        private var fragment: HomeFragment2? = null
            get() {
                if (field == null)
                    field = HomeFragment2()
                return field
            }

        fun instanceFragment(bundle: Bundle?): HomeFragment2 {
            if (bundle != null) {
                fragment?.arguments = bundle
            }
            return fragment!!
        }
    }

    override fun initPresenter(): HomePresenter {
        return HomePresenter()
    }

    private var currentRedPacket: RedPacketEntry? = null
    override fun setRedPacket(redPacketEntry: RedPacketEntry) {
        currentRedPacket = redPacketEntry
        val startTime = redPacketEntry.start * 1000
        val time = startTime - System.currentTimeMillis()
        if (time > 10 * 60 * 1000) {
            home2_jd.postDelayed({
                home2_jd.visibility=View.VISIBLE
                home2_progress.setProgressNum(110.0f, 10*60*1000)
            },time-10*60*1000)

        }else if (time>0){
            home2_jd.visibility=View.VISIBLE
            home2_progress.setProgressNum(110.0f, time)
        }

    }

    override fun setNewData(data: List<BoxEntry>?) {
        if (data != null && data.isNotEmpty()) {
            home2_ry.post {
                adapter.setNewData(data)
                runLayoutAnimation(home2_ry)
            }
        } else {
            TipsUtil.showToast(mContext, "暂时没有新的数据喽！")
        }
    }

    override fun addData(data: List<BoxEntry>?) {
        if (data != null && data.isNotEmpty()) {
//            var it = (data as ArrayList).iterator()
//            while (it.hasNext()) {
//                if (adapter.data.contains(it.next()))
//                    it.remove()
//            }
            if (data.isEmpty()) {
                return
            }
            home2_ry.post {
                if (adapter.data.size == 0) {
                    adapter.setNewData(data)
                    runLayoutAnimation(home2_ry)
                } else {
                    val layoutManager = home2_ry.layoutManager as LinearLayoutManager
//                    val firstCvPosition = layoutManager.findFirstVisibleItemPosition()
                    var lastCvPosition = layoutManager.findLastVisibleItemPosition() - adapter.headerLayoutCount
                    if (lastCvPosition>=adapter.data.size){
                        adapter.addData( data)
                    }else{
                        adapter.addData(lastCvPosition, data)
                    }
//                    Log.i("homeFt2", "firstCvP---$firstCvPosition")
//                    layoutManager.scrollToPosition(lastCvPosition)
//                    if (firstCvPosition == 0) {
//                        layoutManager.scrollToPosition(0)
//                    }
                }
            }
        }
    }

    override fun setSysQsList(qsList: List<SysQuestionEntry>?) {

    }

    override fun showTost(s: String?) {
        s?.let { TipsUtil.showToast(mContext, it) }
    }

    override fun continueToAddData() {
        home2_ry?.post {
            if (!pauseFlag)
                mPresenter.addNewData()
        }
    }

    override fun setTopic(s: String) {
        home2_topictxt.post {
            home2_topictxt.text = s
            home2_text_swicher.setText(s)
        }

    }

    override fun collectSceneSuccess() {
        sceneCollect = !sceneCollect
        changeCollectBg()
    }

    override fun showMainHintView() {

    }

    lateinit var adapter: CardAdapter
    var ry_offsetY = 0F
    var ry_lastY = 0F
    private var itemSize = 10
    var pauseFlag = true
    var sceneCollect = false
    var spaceback: String = ""
    var dynamicDraw = arrayListOf<Int>(R.mipmap.icon_home_dynamic1, R.mipmap.icon_home_dynamic2, R.mipmap.icon_home_dynamic3, R.mipmap.icon_home_dynamic4,
            R.mipmap.icon_home_dynamic5, R.mipmap.icon_home_dynamic6, R.mipmap.icon_home_dynamic7)
    var bgs = arrayListOf<Int>(R.drawable.homebg1, R.drawable.homebg2, R.drawable.homebg3, R.drawable.homebg4, R.drawable.homebg5,
            R.drawable.homebg6, R.drawable.homebg7, R.drawable.homebg8, R.drawable.homebg9, R.drawable.homebg10, R.drawable.homebg11, R.drawable.homebg12)
    //发布按钮是否是高亮状态
    var isPubViewHeight = false
    var needToshowGuide = false
    lateinit var shareDialog: ShareDialog
    private val REQUEST_PUB_BOX = 101
    override fun getLayoutId(): Int {
        return R.layout.ft_home
    }

    override fun initView() {
        mPresenter.attachView(this)
        changeHomeBg()
        initShareDialog()
        adapter = CardAdapter(null)
        adapter.bindToRecyclerView(home2_ry)
        adapter.ryWidth = home2_ry.width
        val headView = LayoutInflater.from(mContext).inflate(R.layout.layout_test_head_foot, null, false)
        val footView = LayoutInflater.from(mContext).inflate(R.layout.layout_test_head_foot, null, false)
        adapter.setHeaderView(headView)
        adapter.setFooterView(footView)
    val   layoutManager=  LinearLayoutManager(mContext,VERTICAL,true)
         layoutManager.stackFromEnd = true
        home2_ry.layoutManager = layoutManager

        home2_ry.itemAnimator = HomeRyAnimator()
        home2_ry.adapter = adapter
        home2_progress.setTextView(home2_jd_time)
        home2_text_swicher.setFactory {
            val textView = QzTextView(mContext!!)
            textView.setTextColor(ContextCompat.getColor(mContext!!, R.color.tv_gray4))
            textView.textSize = 11f
            textView
        }

    }

    override fun initEvent() {
        home2_progress.setOnAnimationListener(object : WaveProgressView.OnAnimationListener {
            override fun howToChangeText(interpolatedTime: Float, updateNum: Float, maxNum: Float, time: Long): String {
//                val time = (time/1000 * (1.0f - interpolatedTime)).toLong()
                var showTime = "00:00"
                currentRedPacket?.let {
                    val nextStartTime = it.start * 1000 - System.currentTimeMillis()
                    if (nextStartTime <= 0) {
                        return showTime
                    }
                    showTime = when {
                        nextStartTime > 60 * 60 * 1000 -> {
                            val hour = nextStartTime / (60 * 60 * 1000)
                            val mTime = nextStartTime % (60 * 60 * 1000)
                            val min = mTime / (60 * 1000)
                            var sTim = mTime % (60 * 1000)
                            val s = sTim / 1000
                            "${hour}:${min}:${s}"
                        }
                        nextStartTime > 60 * 1000 -> {
                            val min = nextStartTime / (60 * 1000)
                            var sTim = nextStartTime % (60 * 1000)
                            val s = sTim / 1000
                            "${min}:${s}"
                        }
                        else -> "00:${time / 1000}"
                    }

                }

                return showTime
            }

            override fun howToChangeWaveHeight(percent: Float, waveHeight: Float): Float {

                return 0f
            }
        })
        home2_progress.setOnClickListener {

            currentRedPacket?.let {
                val nextStartTime = it.start * 1000 - System.currentTimeMillis()
                if (nextStartTime <= 0)
                    return@setOnClickListener
                when {
                    nextStartTime > 60 * 60 * 1000 -> {
                        val hour = nextStartTime / (60 * 60 * 1000)
                        val mTime = nextStartTime % (60 * 60 * 1000)
                        val min = mTime / (60 * 1000)
                        var sTim = mTime % (60 * 1000)
                        val s = sTim / 1000
                        TipsUtil.showToast(mContext, "距下次金豆雨:${hour}时${min}分${s}秒")
                    }
                    nextStartTime > 60 * 1000 -> {
                        val min = nextStartTime / (60 * 1000)
                        var sTim = nextStartTime % (60 * 1000)
                        val s = sTim / 1000
                        TipsUtil.showToast(mContext, "距下次金豆雨:${min}分${s}秒")
                    }
                    else -> TipsUtil.showToast(mContext, "距下次金豆雨:${nextStartTime / 1000}秒")
                }

            }

        }
        home2_progress.setListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                val activityName = CommentUtils.getRunningActivityName(mContext)
//                val pm = mContext!!.getSystemService(Context.POWER_SERVICE) as PowerManager
//                val isScreenOn = pm.isInteractive//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
//                Log.i("home2Ac","isScreenOn $isScreenOn")
                if (currentRedPacket != null
                        && activityName != "com.eggplant.qiezisocial.ui.main.SetSceneActivity"
                        && activityName != "com.eggplant.qiezisocial.ui.main.RobBeansActivity") {
                    startActivity(Intent(mContext, RobBeansActivity::class.java).putExtra("bean", currentRedPacket))
                    activity?.overridePendingTransition(0, 0)
                }
                restartRedPackage()


            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id==R.id.ap_card_gp){
                if (ClickUtil.isNotFastClick()) {
                    var bean = adapter.data[position]
                    if (bean.uid.toInt() == application.infoBean?.uid) {
                        return@setOnItemChildClickListener
                    }
                    if (bean.type == "boxquestiongroup") {
//                    joinDialog.setJoinData(bean)
//                    joinDialog.show()
                    } else {
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)
                        val msgLoca = (activity as MainActivity).main_top_bar.msgImgLoca
                        mPresenter.joinQs(activity!!, bean, location[1], msgLoca[0], msgLoca[1])
                    }
                }
            }
        }
//        adapter.setOnItemClickListener { _, view, position ->
//            if (ClickUtil.isNotFastClick()) {
//                var bean = adapter.data[position]
//                if (bean.uid.toInt() == application.infoBean?.uid) {
//                    return@setOnItemClickListener
//                }
//                if (bean.type == "boxquestiongroup") {
////                    joinDialog.setJoinData(bean)
////                    joinDialog.show()
//                } else {
//                    val location = IntArray(2)
//                    view.getLocationOnScreen(location)
//                    val msgLoca = (activity as MainActivity).main_top_bar.msgImgLoca
//                    mPresenter.joinQs(activity!!, bean, location[1], msgLoca[0], msgLoca[1])
//                }
//            }
//        }
        home2_dt.setOnClickListener {
            if (!ClickUtil.isNotFastClick()) {
                return@setOnClickListener
            }
            Log.i("alip", "startActivity  ${mPresenter.getCurrentSid()}")
//            mPresenter.getcurrentType()0表示图文，2表示视频，3 表示音频，4表示地图
            when {
                mPresenter.getCurrentType() == "2" -> startActivity(Intent(mContext, VideoPlayerActivity::class.java).putExtra("gid", mPresenter.getCurrentSid().toInt()))
                mPresenter.getCurrentType() == "3" -> startActivity(Intent(mContext, AudioPlayerActivity::class.java).putExtra("gid", mPresenter.getCurrentSid().toInt()))
                mPresenter.getCurrentType() == "4" -> RxPermissions(HomeFragment2.fragment!!)
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
                        .putExtra("name", home2_dt_tv.text.toString()))
            }
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            home2_dt_hint.visibility = View.GONE
        }
        home2_pub.setOnClickListener {
            val intent = Intent(mContext, PubTxtActivity2::class.java)
            intent.putExtra("question", home2_topictxt.text.toString())
            if (spaceback.isNotEmpty() && spaceback.toInt() < bgs.size) {
                intent.putExtra("index", spaceback.toInt())
            } else {
                intent.putExtra("index", 0)
            }
            startActivityForResult(intent, REQUEST_PUB_BOX)
            activity?.overridePendingTransition(0, 0)
        }
        home2_collect.setOnClickListener {
            mPresenter.collectScene(mPresenter.getCurrentSid(), sceneCollect)
        }
        home2_share.setOnClickListener {
            shareDialog.show()
        }

        home2_ry.setOnTouchListener(object : View.OnTouchListener {
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
                            showDetail = true
                        }
                        mPresenter.threadPause = true

                    }
                    MotionEvent.ACTION_UP -> {
                        showDetail = false
                        ry_lastY = 0F
                        ry_offsetY = 0F
                        home2_ry?.postDelayed({
                            mPresenter.threadPause = false
                        }, 5000)

                    }
                }
                return false
            }
        })

    }

    private fun restartRedPackage() {
        home2_jd.visibility = View.GONE
        currentRedPacket = null
//        home2_progress?.postDelayed({ home2_progress.restProgress() }, 1000)
        var sid = ""
        if (application.loginEntry != null && application.loginEntry!!.filter != null) {
            sid = application.loginEntry!!.filter.sid
        }
        mPresenter.getRedPacket(sid)
    }

    override fun initData() {
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
        mPresenter.getRedPacket(sid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PUB_BOX) {
                var bean = data?.getSerializableExtra("pubBox") as BoxEntry?
                bean?.let {
                    addData(arrayListOf(it))
                }
                mPresenter.setNextTopic(activity!!)
            }
        }
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
            dialog.dismiss()
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun setFilterCollect(collect: Boolean) {
        sceneCollect = collect
        changeCollectBg()
    }

    private fun changeCollectBg() {
        if (sceneCollect) {
            home2_collect_img.setImageResource(R.mipmap.icon_home_collect_yellow)
        } else {
            home2_collect_img.setImageResource(R.mipmap.icon_home_collect_white)
        }
    }

    fun setFilter(goal: String, sid: String, type: String, moment: String) {
        if (mPresenter.getCurrentGoal() == goal) {
            return
        }

        adapter.data.clear()
        adapter.notifyDataSetChanged()
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

        home2_dt_tv.text = moment
        if (index < 6) {
            home2_dt_tv.setCompoundDrawablesWithIntrinsicBounds(dynamicDraw[index], 0, 0, 0)
        } else {
            home2_dt_tv.setCompoundDrawablesWithIntrinsicBounds(dynamicDraw[6], 0, 0, 0)
        }
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
//        adapter.showing = !event.startAnim
//        adapter.notifyDataSetChanged()
        if (event.startAnim) {
            home2_ry.alpha = 0.5f
        } else {
            home2_ry.alpha = 1.0f
        }
        if (event.replySuccess)
            msgImgFlicker()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changePub(event: PubTxtEvent) {
//        adapter.showing = !event.startAnim
//        adapter.notifyDataSetChanged()
        if (event.startAnim) {
            home2_pub.visibility = View.INVISIBLE
            home2_decorate.visibility = View.INVISIBLE
            home2_ry.alpha = 0.5f
        } else {
            home2_pub.visibility = View.VISIBLE
            home2_decorate.visibility = View.VISIBLE
            home2_ry.alpha = 1.0f
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun smallTxt(event: SmallTxtEvent) {
        setTopic(event.txt)
    }


    //有新动态
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun momentCount(event: MomentCountEvent) {
        if (event.count > 0 && mPresenter.getCurrentType() != "2" && mPresenter.getCurrentType() != "3") {
            home2_dt_hint.visibility = View.VISIBLE
        } else {
            home2_dt_hint.visibility = View.GONE
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

    private fun msgImgFlicker() {
        (activity as MainActivity).msgImgFlicker()
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

    private fun setBackGround(p: Int) {
        home2_pub.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg)
        val params = home2_decorate.layoutParams as FrameLayout.LayoutParams
        val bottomMargin = params.bottomMargin

        home2_decorate2.setImageDrawable(null)
        var imageSource = -1
        when (p) {
            0 -> {
                imageSource = R.drawable.icon_home_dector1
            }
            1 -> {
                imageSource = R.drawable.icon_home_dector2
            }
            2 -> {
                imageSource = R.drawable.icon_home_dector3
            }
            3 -> {
                imageSource = R.drawable.icon_home_dector4

            }
            4 -> {
                imageSource = R.drawable.icon_home_dector5

            }
            5 -> {
                imageSource = R.drawable.icon_home_dector6
            }
            6 -> {
                imageSource = R.drawable.icon_home_dector7
            }
            7 -> {
                imageSource = R.drawable.icon_home_dector8
            }
            8 -> {
                imageSource = R.drawable.icon_home_dector9
            }
            else -> {
                home2_decorate.setImageDrawable(null)
            }
        }
        if (imageSource == -1) {
            home2_decorate.setImageDrawable(null)
        } else {
            home2_decorate2.setImageResource(imageSource)
            home2_decorate.setImageResource(imageSource)
//            home2_decorate2.postDelayed({
//                //                Log.i("homeFt2", "dector-----width:${home2_decorate2.width}    height;${home2_decorate2.height}")
//                var scale: Float = 1.3f / 2.0f
//                if (imageSource == R.drawable.icon_home_dector7) {
//                    scale = 0.9f / 2.0f
//                }
//                scale=1.0f
//                val params = FrameLayout.LayoutParams((scale * home2_decorate2.width).toInt(), (scale * home2_decorate2.height).toInt())
//                params.setMargins(0, 0, 0, ScreenUtil.dip2px(mContext, 55))
//                params.gravity = Gravity.CENTER_HORIZONTAL
//                home2_decorate.layoutParams = params
//                home2_decorate.setImageResource(imageSource)
//            }, 500)
        }



        home2_root_view.background = ContextCompat.getDrawable(mContext!!, bgs[p])
//        setDecorateVisiable()
    }

    fun setNextTopic() {
        mPresenter.setNextTopic(activity!!)
//        setDecorateVisiable()
    }

    fun stopPubAnim() {

    }

}
