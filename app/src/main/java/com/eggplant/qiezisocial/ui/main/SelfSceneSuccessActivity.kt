package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.event.RefresHomeEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.adapter.MainInfoHeadAdapter
import kotlinx.android.synthetic.main.activity_selfscene_success.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Administrator on 2022/6/17.
 */

class SelfSceneSuccessActivity : BaseActivity() {
    var sceneEntry: ScenesEntry? = null
    lateinit var adapter: MainInfoHeadAdapter
    private var imgs = ArrayList<Int>()
    override fun getLayoutId(): Int {
        return R.layout.activity_selfscene_success
    }

    override fun initView() {
        val user = intent.getSerializableExtra("user")
        if (user != null)
            sceneEntry = user as ScenesEntry
        else
            finish()
        adapter = MainInfoHeadAdapter(null)
        selfscene_success_ry.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
        selfscene_success_ry.adapter = adapter

    }

    override fun initData() {
        imgs.add(R.drawable.icon_scene_img1)
        imgs.add(R.drawable.icon_scene_img2)
        imgs.add(R.drawable.icon_scene_img3)
        imgs.add(R.drawable.icon_scene_img4)
        imgs.add(R.drawable.icon_scene_img5)
        imgs.add(R.drawable.icon_scene_img6)
        imgs.add(R.drawable.icon_scene_img7)
        imgs.add(R.drawable.icon_scene_img8)
        imgs.add(R.drawable.icon_scene_img9)
        imgs.add(R.drawable.icon_scene_img10)
        imgs.add(R.drawable.icon_scene_img11)
        imgs.add(R.drawable.icon_scene_img12)
        imgs.add(R.drawable.icon_scene_img13)
        imgs.add(R.drawable.icon_scene_img14)
        imgs.add(R.drawable.icon_scene_img15)
        imgs.add(R.drawable.icon_scene_img16)
        imgs.add(R.drawable.icon_scene_img17)
        imgs.add(R.drawable.icon_scene_img18)
        imgs.add(R.drawable.icon_scene_img19)
        imgs.add(R.drawable.icon_scene_img20)
        imgs.add(R.drawable.icon_scene_img21)
        imgs.add(R.drawable.icon_scene_img22)
        imgs.add(R.drawable.icon_scene_img23)
        imgs.add(R.drawable.icon_scene_img24)
        imgs.add(R.drawable.icon_scene_img25)
        imgs.add(R.drawable.icon_scene_img26)
        imgs.add(R.drawable.icon_scene_img27)
        imgs.add(R.drawable.icon_scene_img28)
        sceneEntry?.let {
            selfscene_success_txt.text = "${it.title}"
            selfscene_success_hint.text = "${it.des}"
            if (it.background != null && it.background.isNotEmpty()) {

                Glide.with(mContext).load(it.background).into(selfscene_success_img)
            } else if (it.pic != null) {
                val pic = it.pic.toInt()
                if (pic < imgs.size) {
                    selfscene_success_img.setImageResource(imgs[pic])
                } else {
                    selfscene_success_img.setImageDrawable(null)
                }
            } else {

            }
        }
        val mainInfoBeans = MainDBManager.getInstance(mContext).queryMainUserList()
        adapter.setNewData(getFdListData(mainInfoBeans))
    }

    /**
     * 获取好友列表数据并类型转换
     */
    fun getFdListData(beans: List<MainInfoBean>?): List<MainInfoBean> {

        val firendList = ArrayList<MainInfoBean>()
        if (beans != null && beans.isNotEmpty()) {
            beans.indices
                    .map { beans[it] }
                    .filterTo(firendList) {
                        TextUtils.equals(it.type, "gfriendlist") && it.uid != 2048L
                    }

            return firendList
        }

        return firendList
    }

    override fun initEvent() {
        selfscene_success_close.setOnClickListener {
            finish()
        }
        selfscene_success_bg.setOnClickListener {
            finish()
        }
        selfscene_success_join.setOnClickListener {
            setScenes()
        }
        adapter.setOnItemClickListener { _, view, position ->
            shareScene(adapter.data[position])

            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", adapter.data[position]))
        }
    }

    private fun shareScene(mainInfoBean: MainInfoBean) {
        createShareSceneEntry(mainInfoBean)
    }

    private fun createShareSceneEntry(mainInfoBean: MainInfoBean) {


//        chatEntry.content = filePath
//        chatEntry.extra = dura.toString()
//        chatEntry.created = System.currentTimeMillis().toString()
//        chatEntry.from = myid
//        chatEntry.to = uid
//        chatEntry.face = myface
//        chatEntry.type = "gaudio"
//        chatEntry.msgId = System.currentTimeMillis().toString()
//        chatEntry.chatId = uid
//        chatEntry.msgStat = 2
//        chatEntry.userId = myid
        val entry = ChatEntry()
        entry.from = application.infoBean!!.uid.toLong()
        entry.to = mainInfoBean.uid
        entry.created = System.currentTimeMillis().toString()
        entry.msgId = System.currentTimeMillis().toString()
        entry.face = application.infoBean!!.face
        entry.type = "gsharescenes"
        entry.userId = application.infoBean!!.uid.toLong()
        entry.scene_bg = sceneEntry!!.background
        entry.scene_pic = sceneEntry!!.pic
        entry.scene_code = sceneEntry!!.code
        entry.scene_des = sceneEntry!!.des
        entry.scene_title = sceneEntry!!.title
        entry.scene_type = sceneEntry!!.type
        entry.scene_sid = sceneEntry!!.sid
        entry.scene_uid = sceneEntry!!.user
        ChatDBManager.getInstance(mContext).insertUser(entry)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit2)
    }

    private fun setScenes() {
        var data = FilterEntry()

        data.people = "全部"
        data.goal = sceneEntry!!.title
        data.sid = sceneEntry!!.sid
        data.type = sceneEntry!!.type
        data.moment = sceneEntry!!.moment
        data.scenes = sceneEntry

        if (data.goal != null && data.goal.isNotEmpty()) {

            EventBus.getDefault().post(RefresHomeEvent(data))

        }

    }


}
