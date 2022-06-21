package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.event.RefresHomeEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.adapter.MainInfoHeadAdapter
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_selfscene_success.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
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
        selfscene_success_share_wechat.setOnClickListener {
            shareImg("wechat")
        }
        selfscene_success_share_friendcircle.setOnClickListener {
            shareImg("pyq")
        }
        selfscene_success_share_qq.setOnClickListener {
            shareImg("qq")
        }
        selfscene_success_share_sina.setOnClickListener {
            shareImg("sina")
        }
    }
    private fun shareImg(type:String){
        val image = UMImage(activity, R.mipmap.share_img)

        //UMLog_Social
        val thumb = UMImage(activity, R.mipmap.share_img)
        val shareAction = ShareAction(activity).withText("交个朋友").withMedia(image)
        when(type){
            "wechat"->{
                shareAction.platform = SHARE_MEDIA.WEIXIN
                shareAction.share()
            }
            "pyq"->{
                shareAction.platform = SHARE_MEDIA.WEIXIN_CIRCLE
                shareAction.share()
            }
            "sina"->{
                shareAction.platform = SHARE_MEDIA.SINA
                shareAction.share()
            }
            "qq" -> {
                shareAction.platform = SHARE_MEDIA.QQ
                shareAction.share()
            }
        }

    }

    private fun shareScene(mainInfoBean: MainInfoBean) {
        createShareSceneEntry(mainInfoBean)
        sendSceneData(mainInfoBean)
    }

    private fun getMsgId(activity: AppCompatActivity) {
        OkGo.post<String>(API.GET_ID)
                .tag(activity)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        if (response.isSuccessful) {
                            try {
                                val `object` = JSONObject(response.body())
                                val stat = `object`.getString("stat")
                                if (TextUtils.equals(stat, "ok")) {
                                    QzApplication.get().msgUUID = `object`.getString("id")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                    }
                })
    }

    private fun sendSceneData(mainInfoBean: MainInfoBean) {
        val id = application.msgUUID
        application.msgUUID = "0"
        getMsgId(activity)
        val obj = JSONObject()
        obj.put("act", "gsharescenes")
        obj.put("type", "message")
        obj.put("created", "${System.currentTimeMillis()}")
        obj.put("from", "${application.infoBean!!.uid.toLong()}")
        obj.put("to", "${mainInfoBean.uid}")
        obj.put("range", "private")
        obj.put("id", id)
        val data = JSONObject()
        data.put("scene_bg", sceneEntry!!.background)
        data.put("scene_pic", sceneEntry!!.pic)
        data.put("scene_code", sceneEntry!!.code)
        data.put("scene_des", sceneEntry!!.des)
        data.put("scene_title", sceneEntry!!.title)
        data.put("scene_type", sceneEntry!!.type)
        data.put("scene_sid", sceneEntry!!.sid)
        data.put("scene_uid", sceneEntry!!.user)
        data.put("scene_moment",sceneEntry!!.moment)
        obj.put("data",data)
        EventBus.getDefault().post(SocketMsgEvent(obj.toString()))

    }


    private fun createShareSceneEntry(mainInfoBean: MainInfoBean) {


        val entry = ChatEntry()
        entry.from = application.infoBean!!.uid.toLong()
        entry.to = mainInfoBean.uid
        entry.created = System.currentTimeMillis().toString()
        entry.msgId = System.currentTimeMillis().toString()
        entry.face = application.infoBean!!.face
        entry.type = "gsharescenes"
        entry.chatId = mainInfoBean.uid
        entry.userId = application.infoBean!!.uid.toLong()
        entry.scene_bg = sceneEntry!!.background
        entry.scene_pic = sceneEntry!!.pic
        entry.scene_code = sceneEntry!!.code
        entry.scene_des = sceneEntry!!.des
        entry.scene_title = sceneEntry!!.title
        entry.scene_type = sceneEntry!!.type
        entry.scene_sid = sceneEntry!!.sid
        entry.scene_uid = sceneEntry!!.user
        entry.scene_moment=sceneEntry!!.moment
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
