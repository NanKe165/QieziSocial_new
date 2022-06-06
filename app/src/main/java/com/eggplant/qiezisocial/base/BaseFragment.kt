package com.eggplant.qiezisocial.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2020/4/9.
 */
abstract class BaseFragment : Fragment() {
    private val TAG = "BaseF"
    protected var activity: Activity? = null
    protected var mContext: Context? = null
    lateinit var application: QzApplication

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        application = QzApplication.get()
        activity = getActivity()
        mContext = context
        val v = inflater.inflate(getLayoutId(), container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
//        if (application.loginEntry==null) {
//            application.loginEntry = savedInstanceState?.getSerializable("loginEntry") as LoginEntry?
//            application.filterData = savedInstanceState?.getSerializable("filterData") as FilterEntry?
//            application.infoBean = savedInstanceState?.getSerializable("infoBean") as UserEntry?
//            savedInstanceState?.let {
//                application.msgUUID = it.getString("msgUUID")
//                application.chatGsId = it.getLong("chatGsId")
//                application.chatUid = it.getLong("chatUid")
//                application.isLogin = true
//            }
//        }
        initView()
        initEvent()
        initData()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putSerializable("loginEntry", application.loginEntry)
//        outState.putSerializable("filterData", application.filterData)
//        outState.putSerializable("infoBean", application.infoBean)
//        outState.putString("msgUUID", application.msgUUID)
//        outState.putLong("chatGsId", application.chatGsId)
//        outState.putLong("chatUid", application.chatUid)
//        outState.putBoolean("isLogin", application.isLogin)
//        outState.putBoolean("isWebLogin", application.isWebLogin)
//        super.onSaveInstanceState(outState)
//    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initEvent()

    protected abstract fun initData()

    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun WebSocketSendDataError(event: WebSocketSendDataErrorEvent) {
        Log.e(TAG, "WebSocketSendDataError: " + event.errorMsg)
    }

}