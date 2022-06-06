package com.eggplant.qiezisocial.ui.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.event.AnswQsEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.ui.main.adapter.HomeAdapter
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_home_detail.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2022/2/15.
 */

class HomeDetailActivity : BaseActivity() {
    lateinit var adaper: HomeAdapter
    var p1 = 0
    var p2 = 0
    var barHight = 0F
    var topMargin = 0F
    var recyclerHeight = 0
    var threadPause = false
    override fun getLayoutId(): Int {
        return R.layout.activity_home_detail
    }

    override fun initView() {
        adaper = HomeAdapter(null)
        home_detail_ry.layoutManager = LinearLayoutManager(mContext)
        home_detail_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 10), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        recyclerHeight = intent.getIntExtra("ry_height", 0)
        val data = intent.getSerializableExtra("data")
        p1 = intent.getIntExtra("p1", 0)
        p2 = intent.getIntExtra("p2", 0)
        if (recyclerHeight == 0 || p1 == 0 || p2 == 0 || data == null) {
            finish()
            return
        }
        barHight = resources!!.getDimension(R.dimen.qb_px_60)
        topMargin = resources!!.getDimension(R.dimen.qb_px_30)
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, recyclerHeight)
        params.setMargins(0, (barHight + topMargin).toInt(), 0, 0)
        home_detail_ry.layoutParams = params
        home_detail_ry.adapter = adaper
        adaper.setNewData(data as ArrayList<BoxEntry>)
//        home_detail_ry.postDelayed({
//
//            var params2 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//            home_detail_ry.layoutParams = params2
//        },1000)
        startAnim()
        home_detail_ry.post {
            startRyAnim()
        }

    }


    override fun initData() {

    }

    override fun initEvent() {
        home_detail_bg.setOnClickListener {
            finish()
        }
        home_detail_ry.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        threadPause = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        threadPause = true

                    }
                    MotionEvent.ACTION_UP -> {
                        home_ry?.postDelayed({
                            threadPause = false
                        }, 5000)

                    }
                }
                return false
            }
        }

        )
        adaper.setOnItemClickListener { adapter, view, position ->
            if (ClickUtil.isNotFastClick()) {
                var bean = adaper.data[position]
                if (bean.uid.toInt() == application.infoBean?.uid) {
//                    TipsUtil.showToast(mContext, "自娱自乐可就没意思了!")
                    return@setOnItemClickListener
                }
                bean.read = true
                adaper.notifyItemChanged(position)
                if (bean.type == "boxquestiongroup") {

                } else {
                    val location = IntArray(2)
                    view.getLocationOnScreen(location)
                    joinQs(activity, bean, location[1], p1, p2)
                }
            }
        }
        adaper.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.ap_home_head -> {
                    var userInfo = (adapter.data[position] as BoxEntry).userinfor
                    var intent = Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", userInfo)
                    startActivity(intent)
                    overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
            }
        }
    }

    fun joinQs(context: Context, bean: BoxEntry, startY: Int, msgX: Int, msgY: Int) {
        createQsMainInfoBean(context, bean, startY, msgX, msgY)
        visitQuestion(bean.id)
    }

    fun visitQuestion(id: Int) {
        OkGo.post<String>(API.VISIT_QUESTION)
                .params("id", id)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>?) {

                    }
                })
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
                overridePendingTransition(0, 0)
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
            overridePendingTransition(0, 0)
        }

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

    private fun startRyAnim() {
        val addHeight = home_detail_bg.height - home_detail_ry.height - barHight - topMargin
        Log.i("homeDetail", "addHeight:   $addHeight  ")
        var heightAnim = ValueAnimator.ofInt(0, addHeight.toInt())
        val params = home_detail_ry.layoutParams as FrameLayout.LayoutParams
        val oldHeight = params.height
        heightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            val newParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, oldHeight + height)
            newParams.setMargins(0, (barHight + topMargin).toInt(), 0, 0)
            home_detail_ry.layoutParams = newParams
        }
        heightAnim.duration = 450
        heightAnim.start()

    }

    private fun finishRyAnim() {
        val params = home_detail_ry.layoutParams as FrameLayout.LayoutParams
        val oldHeight = params.height
        var heightAnim = ValueAnimator.ofInt(oldHeight, 0)
        heightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            val newParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height)
            newParams.setMargins(0, (barHight + topMargin).toInt(), 0, 0)
            home_detail_ry.layoutParams = newParams
        }
        heightAnim.duration = 450
        heightAnim.start()
    }

    private fun startAnim() {
        val startAnim = ObjectAnimator.ofFloat(home_detail_bg, "alpha", 0f, 0.5f)
        startAnim.duration = 450
        startAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        startAnim.start()
    }

    var doFinishAnim = false
    override fun finish() {
        if (doFinishAnim) {
            super.finish()
//            overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
            overridePendingTransition(0, 0)
        } else {
            finishAnim()
        }
    }

    private fun finishAnim() {
        finishRyAnim()
        val finishAnim = ObjectAnimator.ofFloat(home_detail_bg, "alpha", 0.5f, 0f)
        finishAnim.duration = 450
        finishAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                doFinishAnim = true
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        finishAnim.start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeData(event: AnswQsEvent) {
        adaper.showing = !event.startAnim
        adaper.notifyDataSetChanged()

    }
}
