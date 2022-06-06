package com.eggplant.qiezisocial.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.ViewDragHelper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.base.BaseWebSocketActivity
import com.eggplant.qiezisocial.contract.MainContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.EmojiEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.event.ChangeBgEvent
import com.eggplant.qiezisocial.event.PingEvent
import com.eggplant.qiezisocial.event.RefresHomeEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.NearByModel
import com.eggplant.qiezisocial.model.boardcast.NetworkChangeEvent
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.presenter.MainPresenter
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.WebSocketService
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import com.eggplant.qiezisocial.ui.extend.GuessFateActivity
import com.eggplant.qiezisocial.ui.extend.VcrActivity
import com.eggplant.qiezisocial.ui.main.dialog.ExtendDialog
import com.eggplant.qiezisocial.ui.main.fragment.*
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.LocationUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.umeng.socialize.UMShareAPI
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Field


class MainActivity : BaseWebSocketActivity<MainPresenter>(), MainContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {

    lateinit var extendDialog: ExtendDialog
    //    private lateinit var filterDialog: FreshFilterDialog
    lateinit var mainAdapter: MainVpAdapter
    var spaceFragment: SpaceFragment? = null
    var mMemoryBoss: MemoryBoss? = null
    lateinit var hintPopWindow: BasePopupWindow
    val REQUEST_SET_SCENE = 101
    var homeHintNumb = 0
    var edit: SharedPreferences.Editor? = null
    override fun initPresenter(): MainPresenter {
        return MainPresenter()
    }

    override val webSocketClass: Class<out AbsBaseWebSocketService>
        get() = WebSocketService::class.java

    override fun onCommonResponse(response: CommonResponse) {

    }

    override fun onErrorResponse(response: WebSocketSendDataErrorEvent) {
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {

    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mPresenter.attachView(this)
        mMemoryBoss = MemoryBoss()
        registerComponentCallbacks(mMemoryBoss)
        mPresenter.registNetworkReceiver(mContext)

        val preferences = getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
        edit = preferences.edit()
        homeHintNumb = preferences.getInt("home_hint_numb", 0)
        if (homeHintNumb > 20) {
            needShow = false
        }
        mainAdapter = MainVpAdapter(supportFragmentManager, savedInstanceState)
        main_vp.adapter = mainAdapter
        main_vp.offscreenPageLimit = 4
        main_top_bar.setViewPager(main_vp)
        val bundle = Bundle()
        bundle.putSerializable("bean", application.infoBean)
        if (savedInstanceState != null && savedInstanceState.getBoolean("isHomeActDestroy", false)) {

        } else {
            spaceFragment = SpaceFragment.newFragment(bundle)
            supportFragmentManager.beginTransaction().add(R.id.main_ft, spaceFragment).commit()
        }
        initDialog()
        getChatEmoji()
        val layoutParams = main_drawer_ft.layoutParams
        layoutParams.width = ScreenUtil.getDisplayWidthPixels(mContext)
        main_drawer_ft.layoutParams = layoutParams
        main_drawer_ft.isClickable = true
        setDrawerLeftEdgeSize(main_drawer, 0.35f)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean("isHomeActDestroy", true)
        super.onSaveInstanceState(outState)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun getLocation() {
        //检查定位权限
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        //判断
        if (permissions.size === 0) {//有权限，直接获取定位
            getLocationLL()
        } else {//没有权限，获取定位权限
            requestPermissions(permissions.toArray(arrayOfNulls<String>(permissions.size)), 2)
        }
    }

    private fun getLocationLL() {
        LocationUtils.getInstance(this).addressCallback = object : LocationUtils.AddressCallback {
            override fun onGetAddress(address: Address?) {

            }

            override fun onGetLocation(lat: Double, lng: Double) {
                application.infoBean!!.latitude = "$lat"
                application.infoBean!!.longitude = "$lng"
                NearByModel().setLocaltion("$lng", "$lat")
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationLL()
                } else {
//                     Toast.makeText(this, "未同意获取定位权限", Toast.LENGTH_SHORT).show()
                    Log.i("get_location", "getlocation error")
                }
            }
        }
    }

    private fun getChatEmoji() {
        ChatModel().getChatEmoji(object : JsonCallback<BaseEntry<EmojiEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<EmojiEntry>>?) {
                if (response!!.isSuccessful) {
                    QzApplication.get().emojiList = response.body().att
                }
            }
        })
    }

//    private fun createTimer() {
//        main_top_bar.postDelayed({
//            var canShowClickme = main_top_bar.changeClickHint()
//            if (canShowClickme) {
//                createTimer()
//            }
//        }, 15 * 1000)
//    }

    private fun initDialog() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.pop_select_hint, null, false)
        hintPopWindow = BasePopupWindow(mContext)
        hintPopWindow.showAnimMode = 1
        hintPopWindow.contentView = popView
//        hintPopWindow.setOnDismissListener {
//            needShow = false
//        }
        popView.setOnClickListener {
            edit?.putInt("home_hint_numb", homeHintNumb + 1)
            edit?.commit()
            hintPopWindow.dismiss()
        }
        extendDialog = ExtendDialog(mContext, kotlin.intArrayOf(R.id.extend_caicaicai, R.id.extend_vcr, R.id.extend_near_qs))
        extendDialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.extend_caicaicai -> {
                    startActivity(Intent(mContext, GuessFateActivity::class.java))
                }
                R.id.extend_vcr -> {
                    startActivity(Intent(mContext, VcrActivity::class.java))
                }
                R.id.extend_near_qs -> {

                }
            }
            dialog.dismiss()
        }

//        filterDialog = FreshFilterDialog(mContext, null)
//        filterDialog.setOnDismissListener {
//            (mainAdapter.fragments[0] as HomeFragment2).pauseFlag = false
//        }
//
//        filterDialog.setOnShowListener {
//            (mainAdapter.fragments[0] as HomeFragment2).pauseFlag = true
//        }
//        filterDialog.setOnKeyListener { dialog, keyCode, event ->
//            if (filterDialog.data.goal == null || filterDialog.data.goal.isEmpty()) {
//                var data = filterDialog.getFilterData()
//                if (data.goal != null && data.goal.isNotEmpty()) {
//                    application.loginEntry!!.filter.goal = data.goal
//                    application.loginEntry!!.filter.people = data.people
//                    mPresenter.setFilter(data)
//                    main_top_bar.setFuncText(data.goal)
//                    setBoxFilter(data.goal, data.sid)
//                } else {
//                    finish()
//                }
//                true
//            } else {
//                false
//            }
//
//        }
//        filterDialog.setOnBaseDialogItemClickListener { _, view ->
//            if (view.id == R.id.filter_sure) {
//                var data = filterDialog.getFilterData()
//                if (data.goal != null && data.goal.isNotEmpty()) {
//                    application.loginEntry!!.filter.goal = data.goal
//                    application.loginEntry!!.filter.people = data.people
//                    mPresenter.setFilter(data)
//                    main_top_bar.setFuncText(data.goal)
//                    setBoxFilter(data.goal, data.sid)
//                } else {
//                    TipsUtil.showToast(mContext, "请选择交友目的")
//
//                    return@setOnBaseDialogItemClickListener
//                }
//            }
//            filterDialog.dismiss()
//            if (needShow)
//                showHintView()
//        }

    }

    var needShow = true
    var lastTime = 0L
    var drawerClosed = true
    fun showHintView() {
        if (main_vp.currentItem != 0 || !drawerClosed)
            return
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastTime < 10 * 60 * 1000) {
            return
        }
        val loca = main_top_bar.funcViewLoca
//        if (mainAdapter.fragments[0] is HomeFragment2) {
//            (mainAdapter.fragments[0] as HomeFragment2)
//        }
        hintPopWindow.showAtLocation(main_preview, Gravity.NO_GRAVITY, loca[0], loca[1] + ScreenUtil.dip2px(mContext, 35))
        main_preview.postDelayed({
            if (hintPopWindow.isShowing)
                hintPopWindow.dismiss()
        }, 3000)
        lastTime = currentTimeMillis
    }

    override fun initData() {
        if (application.isLogin) {
            mPresenter.startWebService(mContext)
            main_vp.setNoScroll(false)
        } else {
            main_vp.setNoScroll(true)
        }
        mPresenter.getFilterData()
    }


    override fun initEvent() {

        main_top_bar.setOnPageSelectListener { p ->
            if (p == 0) {
                ////打开手势滑动
                main_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                //禁止手势滑动
                main_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            if (p != 2) {
//                if (mainAdapter.fragments[2] is GroupChatFragment2) {
//                    (mainAdapter.fragments[2] as GroupChatFragment2).resetKeyboard()
//                }
                if (mainAdapter.fragments[2] is FriendParkFramgnet) {
                    (mainAdapter.fragments[2] as FriendParkFramgnet).stopShowReply()
                }
            } else {
                if (mainAdapter.fragments[2] is FriendParkFramgnet) {
                    (mainAdapter.fragments[2] as FriendParkFramgnet).prepareShowReply()
                }
//                if (mainAdapter.fragments[2] is GroupChatFragment2) {
//                    (mainAdapter.fragments[2] as GroupChatFragment2).requestData()
//                }
//                if (mainAdapter.fragments[2] is FriendMsgFragment) {
//                    (mainAdapter.fragments[2] as FriendMsgFragment).getLocaOnec()
//                }
            }
//            if (p == 3) {
//                if (mainAdapter.fragments[3] is FriendMsgFragment) {
//                    (mainAdapter.fragments[3] as FriendMsgFragment).getLocaOnec()
//                }
//            }
        }
        main_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                main_top_bar.setMineHead()
                drawerClosed = true
            }

            override fun onDrawerOpened(drawerView: View) {
                spaceFragment?.refreshVisit()
                drawerClosed = false
            }
        })
        main_top_bar.setOnMainBarClickListener {
            //            extendDialog.show()
            if (main_vp.currentItem == 0) {
                val pubViewHeight = (mainAdapter.fragments[0] as HomeFragment2).isPubViewHeight
                if (pubViewHeight) {
                    (mainAdapter.fragments[0] as HomeFragment2).stopPubAnim()
                    return@setOnMainBarClickListener
                }
            }
            if (hintPopWindow.isShowing)
                hintPopWindow.dismiss()
//            filterDialog.show()

            showSetScene(getBoxFilter()!!)
        }
        main_top_bar.setMineClickListener {
            main_drawer.openDrawer(Gravity.START)
        }
        main_drawer_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                main_drawer.closeDrawer(Gravity.START)
            }
        })

    }

    private fun showSetScene(filter: FilterEntry?) {
        if (ClickUtil.isNotFastClick()) {
            Log.i("showSetScene", "goal ${filter?.goal}  ")
            startActivityForResult(Intent(mContext, SetSceneActivity::class.java).putExtra("sid", filter?.sid).putExtra("goal", filter?.goal).putExtra("type", filter?.type), REQUEST_SET_SCENE)
            overridePendingTransition(0, 0)
        }
    }

    var firstSetScene = true
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SET_SCENE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val sid = data.getStringExtra("sid")
                val goal = data.getStringExtra("goal")
                var type = data.getStringExtra("type")
                val moment = data.getStringExtra("moment")
                if (sid != null && goal != null) {
                    val entry = FilterEntry()
                    entry.sid = sid
                    entry.goal = goal
                    application.loginEntry!!.filter.goal = goal
                    application.loginEntry!!.filter.sid = sid
                    application.loginEntry!!.filter.type = type
                    application.loginEntry!!.filter.moment = moment
                    mPresenter.setFilter(entry)
                    main_top_bar.setFuncText(goal)
                    if (type == null)
                        type = "0"
                    setBoxFilter(goal, sid, type, moment)
                }
                firstSetScene = false
                if (needShow) {
                    main_preview.postDelayed({
                        showHintView()
                    }, 3000)
                }
            }
        } else {
            //QQ与新浪QQ与新浪不需要添加Activity，但需要在使用QQ分享或者授权的Activity中
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        }
    }

    fun refreshHome(event: RefresHomeEvent) {
        val entry = event.entry
        val sid = entry.sid
        val goal = entry.goal
        var type = entry.type
        val moment = entry.moment


        if (sid != null && goal != null) {
            val entry = FilterEntry()
            entry.sid = sid
            entry.goal = goal
            application.loginEntry!!.filter.goal = goal
            application.loginEntry!!.filter.sid = sid
            application.loginEntry!!.filter.type = type
            application.loginEntry!!.filter.moment = moment
            mPresenter.setFilter(entry)
            main_top_bar.setFuncText(goal)
            if (type == null)
                type = "0"
            setBoxFilter(goal, sid, type, moment)
        }
        firstSetScene = false
        main_vp.setCurrentItem(0)

    }

    inner class MainVpAdapter(manager: FragmentManager, savedInstanceState: Bundle?) : FragmentPagerAdapter(manager) {

        val fragments = java.util.ArrayList<Fragment>(3)
        var array = arrayOfNulls<Fragment>(3)

        init {
            if (savedInstanceState != null && savedInstanceState.getBoolean("isHomeActDestroy", false)) {
                for (fragment in manager.fragments) {
                    Log.e("MainActivity", "ftmanager--------------:${fragment is BaseFragment}  ${fragment::class.java.simpleName}")
                    when {
                        fragment::class.java.simpleName == "HomeFragment2" -> array[0] = fragment
                        fragment::class.java.simpleName == "MsgFragment" -> array[1] = fragment
                        fragment::class.java.simpleName == "FriendParkFramgnet" -> array[2] = fragment
                    }
                }

                array.forEach {
                    fragments.add(it!!)
                }
            } else {
                fragments.add(HomeFragment2.instanceFragment(null))
                fragments.add(MsgFragment.instanceFragment(null))
                fragments.add(FriendParkFramgnet.instanceFragment(null))
            }
//            fragments.add(HomeFragment2.instanceFragment(null))
//            fragments.add(MsgFragment.instanceFragment(null))
//            fragments.add(FriendMsgFragment.instanceFragment(null))
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }

    private fun setBoxFilter(goal: String, sid: String, type: String, moment: String) {
        if (mainAdapter.fragments[0] is HomeFragment2) {
            if (firstSetScene) {
                (mainAdapter.fragments[0] as HomeFragment2).needToshowGuide = true
            }
            (mainAdapter.fragments[0] as HomeFragment2).setFilter(goal, sid, type, moment)
        }
    }

    private fun getBoxFilter(): FilterEntry? {
        if (mainAdapter.fragments[0] is HomeFragment2) {
            return (mainAdapter.fragments[0] as HomeFragment2).getFilter()
        }
        return null
    }

    override fun setFilterIsCollect(collect: Boolean) {
        if (mainAdapter.fragments[0] is HomeFragment2) {
            (mainAdapter.fragments[0] as HomeFragment2).setFilterCollect(collect)
        }
    }

    override fun setFilterData(filter: FilterEntry) {
//        Log.i("mainActiity","setFilterData ${application.filterData==null}")
//        application.filterData = filter
        if (filter.goal == null || filter.goal!!.isEmpty()) {

        } else {
            main_top_bar.setFuncText(filter.goal)
        }

        showSetScene(application.filterData)
        setFilterIsCollect(filter.fav)
//        filterDialog.setFilterData(filter)
//        if (filter.goal == null || filter.goal!!.isEmpty()) {
//            filterDialog.show()
//        } else {
//            main_top_bar.setFuncText(filter.goal)
//            filterDialog.show()
//        }
    }

    override fun setMsgNum(msgSum: Int) {
//        main_top_bar.setMsgHint(msgSum)
        main_top_bar.setMsgDotVisiable(msgSum > 0)
    }

    override fun setApplyListNum(applyNum: Int) {
        main_top_bar.setApplyDotVisiable(applyNum > 0)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ping(event: PingEvent) {
        ping()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: NetworkChangeEvent) {
        hasNetWork(event.isConnected)
    }

    internal var isFirst = true

    private fun hasNetWork(has: Boolean) {
        //        TipsUtil.showToast(mContext,has+"");
        if (has) {
            if (!application.isLogin) {
//                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(phone)) {
//                    autoLogin(phone, token)
//                }
            } else {
                mPresenter.initChatMsgUUID(activity)
            }
            if (!isFirst) {
                reconnected()
            } else {
                isFirst = false
            }
        } else {

        }
    }

    fun hasNewMsg(setShortcut: Boolean) {
        var hasmsg = mPresenter.hasNewMsg(mContext, setShortcut)
    }


    override fun onResume() {
        super.onResume()
        hasNewMsg(false)
        mMemoryBoss?.let {
            if (it.isAppWentToBg) {
                it.isAppWentToBg = false
                main_vp?.let {
                    //当前显示HomeFragment2
                    if (it.currentItem == 0) {
                        (mainAdapter.fragments[0] as HomeFragment2).setNextTopic()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        mPresenter.unRegistNetworkReciver(mContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && mMemoryBoss != null) {
            unregisterComponentCallbacks(mMemoryBoss)
        }
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0)
    }

    // 用来计算返回键的点击间隔时间
    private var exitTime: Long = 0


    override fun onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return
        if (main_drawer.isDrawerOpen(main_drawer_ft)) {
            main_drawer.closeDrawer(Gravity.START)
            return
        }
        if (System.currentTimeMillis() - exitTime > 2000) {
            TipsUtil.showToast(mContext, "再次点击退出")
            exitTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeBg(event: ChangeBgEvent) {
        if (main_drawer.isDrawerOpen(main_drawer_ft)) {
            main_drawer.closeDrawer(Gravity.START)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun socketMsg(event: SocketMsgEvent) {
        if (event.msg != null) {
//            Log.i("socketMsg","${event.msg}")
            sendSocketMessage(event.msg)
        }
    }


    override fun expressionClick(str: String?) {
        if (mainAdapter.fragments[2] is GroupChatFragment2) {
            (mainAdapter.fragments[2] as GroupChatFragment2).expressionClick(str)
        }
    }

    override fun expressionaddRecent(str: String?) {
        if (mainAdapter.fragments[2] is GroupChatFragment2) {
            (mainAdapter.fragments[2] as GroupChatFragment2).expressionaddRecent(str)
        }
    }

    override fun expressionDeleteClick(v: View?) {
        if (mainAdapter.fragments[2] is GroupChatFragment2) {
            (mainAdapter.fragments[2] as GroupChatFragment2).expressionDeleteClick(v)
        }
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (application.infoBean != null && application.isLogin) {
                main_vp.setNoScroll(false)
            } else {
                main_vp.setNoScroll(true)
            }
        }
        return super.dispatchTouchEvent(event)
    }

//    fun setQsNumb(size: Int) {

    //    }
//        main_top_bar.setQuestionNumb(size)
    private fun setDrawerLeftEdgeSize(drawerLayout: DrawerLayout, displayWidthPercentage: Float) {
        var mDragger: Field? = null
        try {
            mDragger = drawerLayout.javaClass.getDeclaredField(
                    "mLeftDragger") //mRightDragger for right obviously
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        mDragger!!.isAccessible = true
        var draggerObj: ViewDragHelper? = null
        try {
            draggerObj = mDragger
                    .get(drawerLayout) as ViewDragHelper
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        var mEdgeSize: Field? = null
        try {
            mEdgeSize = draggerObj!!.javaClass.getDeclaredField(
                    "mEdgeSize")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        mEdgeSize!!.isAccessible = true
        var edge = 0
        try {
            edge = mEdgeSize.getInt(draggerObj)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        try {
            mEdgeSize.setInt(draggerObj, Math.max(edge, (ScreenUtil.getDisplayWidthPixels(mContext) * displayWidthPercentage).toInt()))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun getCurrentFragment(): Fragment {
        return mainAdapter.fragments[main_vp.currentItem]
    }

    fun msgImgFlicker() {
        main_top_bar.msgImgAnim()
    }

    fun onThreadBeat() {
        if (mainAdapter.fragments[2] is FriendParkFramgnet) {
            (mainAdapter.fragments[2] as FriendParkFramgnet).onThreadBeat()
        }
    }




}
