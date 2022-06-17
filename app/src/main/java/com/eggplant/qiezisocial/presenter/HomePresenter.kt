package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.HomeContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.HomeModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.AnswQsActivity
import com.eggplant.qiezisocial.ui.main.fragment.GetHomeDataController
import com.lzy.okgo.model.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


/**
 * Created by Administrator on 2020/4/14.
 */

class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter {


    private var model: HomeContract.Model = HomeModel()
    var homeDataController = GetHomeDataController()
    var topic: List<String>? = null
    var newTopic: List<String>? = null
    var topicIndex = 0
    var timer: Timer? = null
    var timer2: Timer? = null
    override fun setFilter(goal: String, sid: String) {
        homeDataController.currentScenes = goal
        homeDataController.currentSid = sid
    }

    override fun getCurrentGoal(): String {
        return homeDataController.currentScenes
    }

    override fun getCurrentSid(): String {
        return homeDataController.currentSid
    }

    override fun getCurrentType(): String {
        return homeDataController.currentType
    }

    override fun getRedPacket(sid: String) {
        model.getRedPacket(sid, object : JsonCallback<BaseEntry<RedPacketEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<RedPacketEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        mView?.setRedPacket(list[0])
                    }
                }
            }
        })
    }

    override fun initData(itemSize: Int, scenes: String, sid: String, type: String) {
        homeDataController.getData(itemSize, scenes, sid, type) { data -> mView?.setNewData(data) }
    }

    override fun addNewData() {
        Log.i("continueToAddData", "presenter- addNewData")
        var data = homeDataController.getData(1)
        Log.i("continueToAddData", "presenter- addNewDataSize:${data.size}")
        mView?.addData(data)
    }

    override fun collectScene(currentSid: String, i: Boolean) {
        model.collectScene(currentSid, i, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    mView?.showTost(response.body().msg)
                    if (response.body().stat == "ok") {
                        mView?.collectSceneSuccess()
                    }

                }
            }
        })
    }

    override fun setTopic(activity: Activity, scenes: String, sid: String) {
        model.getTopic(activity, scenes, sid, object : JsonCallback<TopicEntry>() {
            override fun onSuccess(response: Response<TopicEntry>?) {
                if (response!!.isSuccessful) {
                    if (topic == null) {
                        topic = response.body().topic
                        if (topic != null && topic!!.isNotEmpty()) {
                            mView?.setTopic(topic!![0])
                        }
                    } else {
                        newTopic = response.body().topic
                        if (newTopic != null && newTopic!!.isNotEmpty()) {
                            mView?.setTopic(newTopic!![0])
                        }
                    }
                }
            }
        })
        //网络重新获取topic  每30分钟网络获取新topic
        if (timer == null) {
            timer = Timer()
            val timerTask = timerTask {
                val goal = QzApplication.get().loginEntry?.filter?.goal
                val s = QzApplication.get().loginEntry?.filter?.sid
                if (goal != null && s != null)
                    setTopic(activity, goal, s)
                Thread.sleep(30 * 60 * 1000)
            }
            timer!!.schedule(timerTask, 30 * 60 * 1000, 1 * 1000)
        }
        //设置下一个topic 每3分钟设置下一个topic[index]
        if (timer2 == null) {
            timer2 = Timer()
            val timerTask2 = timerTask {
                setNextTopic(activity)
                Thread.sleep(3 * 60 * 1000)
                mView?.showMainHintView()
            }
            timer2!!.schedule(timerTask2, 3 * 60 * 1000, 1 * 1000)
        }

    }

    override fun setNextTopic(activity: Activity) {
        topicIndex++
        if (topic != null && topic!!.isNotEmpty()) {
            if (topicIndex >= topic!!.size) {
                topicIndex = 0
            }
            mView?.setTopic(topic!![topicIndex])
            if (newTopic != null && newTopic!!.isNotEmpty()) {
                topic = ArrayList<String>(newTopic)
                newTopic = null
            }
        } else {
            topicIndex = 0
            var goal = QzApplication.get().loginEntry?.filter?.goal
            var sid = QzApplication.get().loginEntry?.filter?.sid
            if (goal != null && sid != null)
                setTopic(activity, goal, sid)
        }
    }

    override fun refreshData(tag: Any, sid: String) {
        model.getBoxInfo(tag, 0, sid, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body().list.let {
                            mView?.setNewData(it)
                        }
                    }
                }
            }
        })
        model.getQuestion(tag, object : JsonCallback<BaseEntry<SysQuestionEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<SysQuestionEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        var qsList = it.body().list
                        QzApplication.get().qsList = qsList
                        mView?.setSysQsList(qsList)
                    }
                }
            }
        })
    }

    override fun addData(tag: Any, begin: Int) {
//        model.getBoxInfo(tag, begin, object : JsonCallback<BaseEntry<BoxEntry>>() {
//            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
//                response?.let {
//                    if (it.isSuccessful) {
//                        it.body().list.let {
//                            mView?.addData(it)
//                        }
//                    }
//                }
//            }
//
//        })
    }

    override fun pubQuestion(activity: Activity, context: String, font: String, sid: String) {
        model.pubQuestion(activity, context, font, sid, object : DialogCallback<BaseEntry<BoxEntry>>(activity, "正在提交审核...") {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {

                        if (TextUtils.equals(it.body().stat, "ok")) {
                            it.body().record?.let {

                            }
                            mView?.showTost("发送成功，系统正在为您匹配推送")
                        } else {
                            mView?.showTost(it.body().msg)
                        }

                    } else {
                        mView?.showTost("${it.code()} ${it.message()}")
                    }
                }

            }
        })
    }

    override fun continueToAddData(i: Int) {
        taktTime = i
        startResuestQsTimer()
    }

    private var currentThreadName: String? = null
    private var lastTime: Long = 0
    var msgThread: MsgThread? = null
    private var taktTime = 5 * 1000
    var threadPause = false

    inner class MsgThread : Thread() {
        init {
            currentThreadName = System.currentTimeMillis().toString()
            name = currentThreadName
        }

        override fun run() {
            super.run()
            while (TextUtils.equals(currentThreadName, this.name)) {
                try {
                    Thread.sleep(taktTime.toLong())
                    mView?.onThreadBeat()
                    if (!threadPause)
                        mView?.continueToAddData()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun startResuestQsTimer() {
        if (msgThread == null) {
            msgThread = MsgThread()
        }
        if (msgThread != null)
            msgThread?.start()
    }


    private fun stopTimer() {
        if (msgThread != null) {
            msgThread?.interrupt()
            msgThread = null
        }
    }


    override fun joinGroup(context: Context, bean: BoxEntry) {
        model.joinGroup(bean.id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {

                        createGsMainInfoBean(context, bean)
                    }
                }
            }
        })
    }

    override fun joinQs(context: Context, bean: BoxEntry, startY: Int, msgX: Int, msgY: Int) {
        createQsMainInfoBean(context, bean, startY, msgX, msgY)
        visitQuestion(bean.id)
    }

    private fun visitQuestion(id: Int) {
        model.visitQuestion(id)
    }

    /**
     * model 1--qsid  2--gsid
     */
    private fun createChatInfo(context: Context, qsBean: BoxEntry, model: Int): Boolean {
        val sp = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
        val userId = sp.getInt("uid", 0)
        var qsEntry = ChatEntry()
        when (model) {
            1 -> {
                qsEntry.qsid = qsBean.id.toLong()
                qsEntry.to = userId.toLong()
            }
            2 -> {
                qsEntry.gsid = qsBean.id.toLong()
                qsEntry.to = 0
            }
            else -> return false
        }
        qsEntry.msgId = (System.currentTimeMillis() - 100).toString()
        qsEntry.type = "gtxt"
        qsEntry.chatId = qsBean.uid.toLong()
        qsEntry.from = qsBean.uid.toLong()
        qsEntry.userId = userId.toLong()
        qsEntry.face = qsBean.userinfor.face
        qsEntry.id = System.currentTimeMillis() - 10
        qsEntry.created = (System.currentTimeMillis() - 100).toString()
        qsEntry.content = qsBean.text
        qsBean.media?.forEachIndexed { index, mediaEntry ->
            when (index) {
                0 -> {
                    qsEntry.question1 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                }
                1 -> {
                    qsEntry.question2 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                }
                2 -> {
                    qsEntry.question3 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                }
            }
        }
        return ChatDBManager.getInstance(context).insertUser(qsEntry)
    }

    private fun createQsMainInfoBean(context: Context, bean: BoxEntry, startY: Int, msgX: Int, msgY: Int) {
        val sp = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
        val userId = sp.getInt("uid", 0)
        var mianBean = MainDBManager.getInstance(context).queryMainUserWithQsid(bean.uid, bean.id.toLong())
        if (mianBean == null) {
            createChatInfo(context, bean, 1)
            var newBean = MainInfoBean()
            newBean.qsid = bean.id.toLong()
            newBean.qsuid = bean.userinfor!!.uid
            newBean.qsTxt = bean.text
            newBean.qsUserFace = API.PIC_PREFIX + bean.userinfor.face
            newBean.qsNick = bean.userinfor.nick
            newBean.uid = bean.userinfor.uid.toLong()
            newBean.sex = bean.userinfor.sex
            newBean.nick = bean.userinfor.nick
            newBean.birth = bean.userinfor.birth
            newBean.card = bean.userinfor.card
            newBean.careers = bean.userinfor.careers
            newBean.city = bean.userinfor.city
            newBean.height = bean.userinfor.height
            newBean.weight = bean.userinfor.weight
            newBean.edu = bean.userinfor.edu
            newBean.topic = bean.userinfor.topic
            newBean.xz = bean.userinfor.xz
            newBean.account = bean.userinfor.card
            newBean.pic = bean.userinfor.pic
            newBean.face = bean.userinfor.face
            newBean.type = "temporal"
            newBean.created = System.currentTimeMillis()
            newBean.userId = userId.toLong()
            newBean.mood = bean.userinfor.mood
            bean.media?.forEachIndexed { index, mediaEntry ->
                when (index) {
                    0 -> {
                        newBean.media1 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                    }
                    1 -> {
                        newBean.media2 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                    }
                    2 -> {
                        newBean.media3 = "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
                    }
                }
            }
            var success = MainDBManager.getInstance(context).insertUser(newBean)
            if (success) {
//                context.startActivity(Intent(context, ChatActivity::class.java).putExtra("bean", newBean).putExtra("from", "home").putExtra("qs",bean.text))
                context.startActivity(Intent(context, AnswQsActivity::class.java)
                        .putExtra("bean", bean)
                        .putExtra("mainBean", newBean)
                        .putExtra("startY", startY)
                        .putExtra("msgX", msgX)
                        .putExtra("msgY", msgY))
//                (context as Activity).overridePendingTransition(R.anim.open_enter2,R.anim.open_exit3)
                (context as Activity).overridePendingTransition(0, 0)
            }

        } else {
            mianBean.mood = bean.userinfor.mood
            MainDBManager.getInstance(context).updateUser(mianBean)
//            context.startActivity(Intent(context, ChatActivity::class.java).putExtra("bean", mianBean).putExtra("from", "home").putExtra("qs",bean.text))
            context.startActivity(Intent(context, AnswQsActivity::class.java)
                    .putExtra("bean", bean)
                    .putExtra("mainBean", mianBean)
                    .putExtra("startY", startY)
                    .putExtra("msgX", msgX)
                    .putExtra("msgY", msgY))
//            (context as Activity).overridePendingTransition(R.anim.open_enter2,R.anim.open_exit3)
            (context as Activity).overridePendingTransition(0, 0)
        }

    }

    private fun createGsMainInfoBean(context: Context, bean: BoxEntry) {
        val sp = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
        val userId = sp.getInt("uid", 0)
        var mianBean = MainDBManager.getInstance(context).queryMainUserWithGsId(bean.id.toLong())
        if (mianBean == null) {
            var qsUserface = StringBuffer()
            bean.personinfor.forEachIndexed { index, userEntry ->
                if (index < 4) {
                    qsUserface.append(API.PIC_PREFIX + userEntry.face).append(",")
                }
            }
            qsUserface.deleteCharAt(qsUserface.length - 1)
            createChatInfo(context, bean, 2)
            var newBean = MainInfoBean()
            newBean.gsid = bean.id.toLong()
            newBean.qsTxt = bean.text
            newBean.qsUserFace = qsUserface.toString()
            newBean.uid = bean.userinfor.uid.toLong()
            newBean.sex = bean.userinfor.sex
            newBean.nick = bean.userinfor.nick
            newBean.birth = bean.userinfor.birth
            newBean.card = bean.userinfor.card
            newBean.careers = bean.userinfor.careers
            newBean.city = bean.userinfor.city
            newBean.height = bean.userinfor.height
            newBean.weight = bean.userinfor.weight
            newBean.edu = bean.userinfor.edu
            newBean.topic = bean.userinfor.topic
            newBean.xz = bean.userinfor.xz
            newBean.account = bean.userinfor.card
            newBean.pic = bean.userinfor.pic
            newBean.face = bean.userinfor.face
            newBean.type = "temporal"
            newBean.created = System.currentTimeMillis()
            newBean.userId = userId.toLong()
            newBean.mood = bean.userinfor.mood
            var success = MainDBManager.getInstance(context).insertUser(newBean)
            if (success) {
                context.startActivity(Intent(context, ChatActivity::class.java).putExtra("bean", newBean).putExtra("from", "home"))
            }
        } else {
            mianBean.mood = bean.userinfor.mood
            MainDBManager.getInstance(context).updateUser(mianBean)
            context.startActivity(Intent(context, ChatActivity::class.java).putExtra("bean", mianBean).putExtra("from", "home"))
        }

    }


    override fun detachView() {
        super.detachView()
        stopTimer()
        homeDataController.desroyController()
    }

    fun getFilterType(activity: Activity, sid: String) {
        mView?.setSceneUser(null)
        model.getFilterType(activity, sid, object : JsonCallback<BaseEntry<ScenesEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<ScenesEntry>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().infor?.userinfor != null) {
                        val user = response.body().infor!!.userinfor
                        if (user.uid!=0&&user.uid != QzApplication.get().infoBean?.uid) {
                            mView?.setSceneUser(user)
                            return
                        }
                    }
                }
            }
        })
    }
}
