package com.eggplant.qiezisocial.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.SpaceContract
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.SpacePresenter
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.adapter.OtherAlbumAdapter
import com.eggplant.qiezisocial.ui.main.fragment.SpaceFragment
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_other_space.*

/**
 * Created by Administrator on 2020/4/27.
 */

class OtherSpaceActivity : BaseMvpActivity<SpacePresenter>(), SpaceContract.View {

    override fun initPresenter(): SpacePresenter {
        return SpacePresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_other_space
    }

//    override fun initView(savedInstanceState: Bundle?) {
//        super.initView(savedInstanceState)
//        startBgAnim(savedInstanceState)
//    }

    lateinit var adapter: OtherAlbumAdapter
    var bean: UserEntry? = null
    override fun initView() {
        mPresenter.attachView(this)
        adapter = OtherAlbumAdapter(null)
        other_sp_ry.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
        other_sp_ry.adapter = adapter
    }

    override fun initData() {
        bean = intent.getSerializableExtra("bean") as UserEntry?
        if (bean == null) {
            finish()
        } else {
            val bundle = Bundle()
            bundle.putSerializable("bean",bean)
            supportFragmentManager.beginTransaction().add(R.id.other_sp_ft, SpaceFragment.newFragment(bundle)).commit()
//            mPresenter.setInfoData(bean!!)
            if (bean!!.uid == QzApplication.get().infoBean!!.uid) {
                setAddVisiable(View.GONE)
            }else if (bean!!.friend=="yes"){
                setIsFriend(bean!!.friend)
            }

        }

    }

    override fun initEvent() {
        other_sp_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        other_sp_close.setOnClickListener {
            finish()
        }
//        other_sp_bg.setOnClickListener {
//            finish()
//        }
        other_sp_add.setOnClickListener {


            if (other_sp_add.text.toString() == "添加好友") {
//                mPresenter.addFriend(activity,bean!!.uid)
                startActivity(Intent(mContext,VerifyFriendActivity::class.java).putExtra("from","add").putExtra("user",bean))
            } else {
                val queryMainUser = MainDBManager.getInstance(mContext).queryMainUser("$bean.uid")
                startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean",queryMainUser).putExtra("user",bean))
            }
        }
        other_sp_chat2.setOnClickListener{
            val queryMainUser = MainDBManager.getInstance(mContext).queryMainUser("$bean.uid")
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean",queryMainUser).putExtra("user",bean))
        }
        other_sp_chat.setOnClickListener {
//            test()
            startActivity(Intent(mContext,PubTxtActivity::class.java).putExtra("from","space").putExtra("nick",bean!!.nick).putExtra("uid",bean!!.uid))
            overridePendingTransition(R.anim.open_enter3, R.anim.open_exit3)
//            var mainBean = MainDBManager.getInstance(mContext).queryMainUser(bean!!.uid.toString())
//            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", mainBean).putExtra("user", bean))

        }
        adapter.setOnItemClickListener { _, view, position ->
            var data = ArrayList<ImageInfo>()
            adapter.data.forEach {
                var info = ImageInfo()
                info.bigImageUrl = API.PIC_PREFIX + it
                info.thumbnailUrl = API.PIC_PREFIX + it
                data.add(info)
            }
            PrevUtils.onImageItemClick(mContext, view, position, data)
        }
    }
    fun test(){
        val myNotificationView = RemoteViews(packageName, R.layout.custom_notification)
        myNotificationView.setTextViewText(R.id.notification_title, "Title-")
        myNotificationView.setTextViewText(R.id.notification_text, "Message-")
        Glide.with(mContext).asBitmap().load("https://img.zcool.cn/community/0127475e16ef91a801216518b4023c.jpg@1280w_1l_2o_100sh.jpg").into(object :
        SimpleTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                myNotificationView.setImageViewBitmap(R.id.notification_bg, resource)
                myNotificationView.setImageViewResource(R.id.notification_large_icon, R.mipmap.box_iauncher)
                myNotificationView.setImageViewResource(R.id.notification_small_icon,  R.mipmap.box_iauncher)
                var notification: Notification? = null
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val id = "my_channel_02"
                val name = "msg_channel2"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(mChannel)
                    val builder = Notification.Builder(mContext)
                    builder.setChannelId(id)
                    builder.setContentText(title)
                    builder.setSmallIcon(R.mipmap.box_iauncher)
                    builder.setDefaults(Notification.DEFAULT_ALL)
                    builder.setAutoCancel(true)
                    builder.setCustomBigContentView(myNotificationView)
                    builder.setContent(myNotificationView)
                    notification = builder.build()
                    //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                    notification!!.contentView = myNotificationView

                } else {
                    val notificationBuilder = NotificationCompat.Builder(mContext)
                    notificationBuilder.setCustomBigContentView(myNotificationView)
                    notificationBuilder.setContent(myNotificationView)
                    notificationBuilder.setChannelId(id)
                    notificationBuilder.setContentText(title)
                    notificationBuilder.setSmallIcon(R.mipmap.box_iauncher)
                    notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
                    notificationBuilder.setAutoCancel(true)
                    notification = notificationBuilder.build()
                    notification!!.contentView = myNotificationView
                }
                notificationManager.notify("msg_2", 2, notification)
            }
        })

    }

    override fun setAccount(card: String?) {
        other_sp_id.text = card
    }

    override fun setAdd(friend: String?) {
//        if (TextUtils.equals(friend, "no")) {
//            other_sp_add.visibility = View.VISIBLE
//        } else {

//            other_sp_add.visibility = View.GONE
//        }
    }

    override fun setSex(sex: String?) {
        sex?.let {
            if (sex.contains("男")) {
                other_sp_sex.setImageResource(R.mipmap.sex_boy_big)
            } else {
                other_sp_sex.setImageResource(R.mipmap.sex_girl_big)
            }
        }
    }

    override fun setFace(s: String) {
        Glide.with(mContext).load(s).into(other_sp_head)
    }

    override fun setLabel(birth: String?, sex: String?, wh: String, labelData: ArrayList<String>) {
        other_sp_label.setData(birth, sex, wh, labelData)
    }

    override fun setNick(nick: String?) {
        other_sp_name.text = nick

    }

    override fun setBirth(birth: String?) {
        other_sp_age.text = "${DateUtils.dataToAge(birth)}岁"
    }

    override fun setCareer(careers: String?) {
        other_sp_career.text = careers
    }

    override fun setHW(wh: String) {
        other_sp_wh.text = wh
    }

    override fun setInster(s: String?) {
        other_sp_inster.text = s
    }

    override fun setEdu(edu: String?) {
        other_sp_edu.text = edu
    }

    override fun setObj(s: String?) {
        other_sp_obj.text = s
    }

    override fun setPic(pic: List<String>) {
        if (pic.isNotEmpty()) {
            other_sp_photo_gp.visibility = View.VISIBLE
//            other_sp_ry.visibility = View.VISIBLE
            adapter.setNewData(pic)
        }
    }

    override fun setSign(sign: String?) {
        if (TextUtils.isEmpty(sign)) {
            other_sp_sign.visibility = View.GONE
        } else {
            other_sp_sign.visibility = View.VISIBLE
        }
        other_sp_sign.text = sign
    }

    override fun setMood(mood: String?) {
        if (TextUtils.isEmpty(mood)) {
            other_sp_state.visibility = View.GONE
        } else {
            other_sp_state.visibility = View.VISIBLE
            other_sp_state_tv.text=mood
            other_sp_state_tv.setTextColor(ContextCompat.getColor(mContext!!, R.color.state_select))
            setStateIcon(mood)
        }
    }

    private fun setStateIcon(mood: String?) {
        var statedraw=0
        when (mood) {
            getString(R.string.state1) -> {
                statedraw=R.mipmap.mine_state_ku
            }
            getString(R.string.state2) -> {
                statedraw=R.mipmap.mine_state_liekai
            }
            getString(R.string.state3) -> {
                statedraw=R.mipmap.mine_state_kaixin
            }
            getString(R.string.state4) -> {
                statedraw=R.mipmap.mine_state_kun
            }
            getString(R.string.state5) -> {
                statedraw=R.mipmap.mine_state_fadai
            }
            getString(R.string.state6) -> {
                statedraw=R.mipmap.mine_state_gudan
            }
            getString(R.string.state7) -> {
                statedraw=R.mipmap.mine_state_youshang
            }
            getString(R.string.state8) -> {
                statedraw=R.mipmap.mine_state_fennu
            }
            getString(R.string.state9) -> {
                statedraw=R.mipmap.mine_state_chigua
            }
        }
        other_sp_state_tv.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }

    override fun setIsFriend(friend: String?) {
//        other_sp_add.text="私人提问"
//        if (TextUtils.isEmpty(friend) || friend == "no") {
//            other_sp_add.text = "添加好友"
//        } else if (friend == "yes") {
//            other_sp_add.text = "撩一下"
//        }
        other_sp_add.visibility=View.GONE
        other_sp_chat.visibility=View.GONE
        other_sp_chat2.visibility=View.VISIBLE
    }

    override fun setAddVisiable(visiable: Int) {
        other_sp_add.visibility=visiable
        other_sp_chat.visibility=visiable
    }
    private fun startBgAnim(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) run {
            other_sp_bg?.visibility = View.INVISIBLE
            val viewTreeObserver = other_sp_bg.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealAnim()
                        other_sp_bg?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            other_sp_bg?.visibility = View.VISIBLE
        }
    }

    private fun revealAnim() {
        var anim = AlphaAnimation(0f, 1f)
        anim.duration = 600
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                other_sp_bg?.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
            }

        })
        other_sp_bg?.startAnimation(anim)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }

//    override fun finish() {
//        if (doFinishAnim) {
//            super.finish()
//            overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
//        } else {
//            finishAnim()
//        }
//    }

    var doFinishAnim = false
    private fun finishAnim() {
        var anim = AlphaAnimation(1f, 0f)
        anim.duration = 100
        other_sp_bg?.let {
            it.startAnimation(anim)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                other_sp_bg?.visibility = View.INVISIBLE
                doFinishAnim = true
                finish()
            }

        })
    }

}
