package com.eggplant.qiezisocial.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.event.DiaryEvent
import com.eggplant.qiezisocial.event.RefresHomeEvent
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.DiaryModel
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.award.JindouActivity
import com.eggplant.qiezisocial.ui.decorate.DecorateActivity
import com.eggplant.qiezisocial.ui.extend.PubDiaryActivity
import com.eggplant.qiezisocial.ui.extend.RecordVideoActivity
import com.eggplant.qiezisocial.ui.extend.dialog.PubVcrDialog
import com.eggplant.qiezisocial.ui.main.MyDynamicActivity
import com.eggplant.qiezisocial.ui.main.VerifySceneActivity
import com.eggplant.qiezisocial.ui.main.VisitorActivity
import com.eggplant.qiezisocial.ui.main.adapter.FreshSelectObjectAdapter
import com.eggplant.qiezisocial.ui.main.dialog.GzSuccessDialog
import com.eggplant.qiezisocial.ui.setting.ModifyActivity
import com.eggplant.qiezisocial.ui.setting.SetInfoActivity
import com.eggplant.qiezisocial.ui.setting.SettingActivity
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.MultMediaView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_space.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2021/11/2.
 */

class SpaceFragment : BaseFragment() {
    var model = DiaryModel()
    var user: UserEntry? = null
    private val REQUEST_SETINFO = 110
    private val REQUEST_MODIFY_INSTEREST = 108
    private val REQUEST_VERIFY_SCENES: Int=100
    private lateinit var pubDialog: PubVcrDialog
    private val REQUEST_CODE_SELECT = 101
    private var emtpyDiaryView: Drawable? = null
    private var neverHint = false
    private var hasDiary = false
    lateinit var sceneAdapter: FreshSelectObjectAdapter
    lateinit var gzDialog: GzSuccessDialog
    private var jdCount = 0

    companion object {
        fun newFragment(bundle: Bundle?): SpaceFragment {
            val fragment = SpaceFragment()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_space
    }

    override fun initView() {
        val bean = arguments?.getSerializable("bean") ?: return
        user = bean as UserEntry
        sceneAdapter = FreshSelectObjectAdapter(null)
        ft_space_scene_ry.layoutManager = GridLayoutManager(mContext, 2)
        ft_space_scene_ry.addItemDecoration(GridSpacingItemDecoration(2, resources.getDimension(R.dimen.qb_px_18).toInt(), resources.getDimension(R.dimen.qb_px_25).toInt()))
        ft_space_scene_ry.adapter = sceneAdapter
        initDialog()
        setInfo(bean)
//        refreshVisit()

    }


    private fun setInfo(bean: UserEntry) {

        if (bean.uid == application.infoBean!!.uid) {
            emtpyDiaryView = ContextCompat.getDrawable(mContext!!, R.mipmap.icon_space_dailyempty)
            ft_space_info.visibility = View.GONE
            ft_space_label_gp.visibility = View.GONE
            ft_line_label.visibility = View.GONE
            ft_space_scene_gp.visibility = View.GONE
            ft_space_appbar.post {
                if (ViewCompat.isLaidOut(ft_space_appbar)) {
                    val params = ft_space_appbar.layoutParams as CoordinatorLayout.LayoutParams
                    val behavior = params.behavior as AppBarLayout.Behavior
                    behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                        override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                            return false
                        }
                    })
                }
            }


        } else {
            emtpyDiaryView = ContextCompat.getDrawable(mContext!!, R.mipmap.icon_space_dailyempty)
            ft_space_setting.visibility = View.GONE
            ft_space_decorate.visibility = View.GONE
            ft_space_jindou.visibility = View.GONE
            ft_space_statis_gp.visibility = View.GONE
            ft_space_diary_pub.visibility = View.GONE
            ft_space_visitor_gp.visibility = View.GONE
            ft_line_visitor.visibility = View.GONE
            ft_space_gz_gp.visibility = View.GONE
            ft_line_gz.visibility = View.GONE
            ft_space_dt_gp.visibility = View.GONE
            ft_line_dt.visibility = View.GONE
            ft_space_gz.visibility = View.VISIBLE
            ft_line_gz.visibility = View.GONE
//            ft_space_diary_right.visibility = View.GONE
            ft_space_info_right.visibility = View.GONE
            ft_space_label_right.visibility = View.GONE
            ft_space_gz_right.visibility = View.GONE
            ft_space_pubdiarycount.visibility = View.GONE
            ft_space_setinfo.text = ""

            ft_space_label_gp.visibility = View.VISIBLE
            ft_line_label.visibility = View.VISIBLE
            ft_space_info.visibility = View.VISIBLE
        }
        if (bean.card.isNotEmpty())
            ft_space_card.text="朋友号：${bean.card}"
        if (bean.face.isNotEmpty())
            Glide.with(mContext!!).load(API.PIC_PREFIX + bean.face).into(ft_space_head)
        if (bean.nick.isNotEmpty())
            ft_space_nick.text = bean.nick
        var uinfo = ArrayList<String>()
        if (bean.sex.isNotEmpty())
            uinfo.add(bean.sex)
//        if (bean.birth != null && bean.birth.isNotEmpty())
//            uinfo.add("${DateUtils.dataToAge(bean.birth)}岁")
        if (bean.edu != null && bean.edu.isNotEmpty())
            uinfo.add(bean.edu)
        if (bean.height != null && bean.height.isNotEmpty())
            uinfo.add("身高 ${bean.height}cm")
        if (bean.weight != null && bean.weight.isNotEmpty())
            uinfo.add("体重 ${bean.weight}kg")
        if (bean.careers != null && bean.careers.isNotEmpty())
            uinfo.add(bean.careers)
        if (bean.`object` != null && bean.`object`.isNotEmpty())
            uinfo.add(bean.`object`)
        ft_space_info.setData(uinfo, mContext, 13, ContextCompat.getColor(mContext!!, R.color.tv_gray4), 0, 0, 20, 0, 0, 0, 0, 10)
        if (bean.label != null && bean.label.isNotEmpty()) {
            val labels = bean.label.split(" ")
            if (labels.isNotEmpty())
                labels.filter { it.isNotEmpty() }.map { "#$it" }.let {
                    ft_space_label.setData(it, mContext, 14, ContextCompat.getColor(mContext!!, R.color.tv_black2), 0, 0, 20, 0, 0, 0, 0, 10)
                }
        } else {
            ft_space_label.removeAllViews()
        }

    }

    fun refreshVisit() {
        if (user == null)
            return

//        Log.i("resfrshVisit","uid${user!!.uid}")
        model.getSpace(this,"${user!!.uid}", object : JsonCallback<VisitEntry>() {
            override fun onSuccess(response: Response<VisitEntry>?) {
                if (response!!.isSuccessful) {
                    setVisitInfo(response.body())
                }
            }

            override fun onError(response: Response<VisitEntry>?) {
                super.onError(response)
                Log.i("setVisitInfo", "${response?.exception}  ${response?.code()}")
                ft_space_diary_media.setEmtpyView(emtpyDiaryView, null)
            }
        }, SpaceFragment@ this)

    }

    private fun setVisitInfo(body: VisitEntry) {
        val allvisitcount = body.allvisitcount
        body.newvisit?.forEachIndexed { index, userEntry ->
            when (index) {
                0 -> Glide.with(mContext!!).load(API.PIC_PREFIX + userEntry.face).into(ft_space_visitor_img1)
                1 -> Glide.with(mContext!!).load(API.PIC_PREFIX + userEntry.face).into(ft_space_visitor_img2)
                2 -> Glide.with(mContext!!).load(API.PIC_PREFIX + userEntry.face).into(ft_space_visitor_img3)
            }
        }
        if (allvisitcount > 0) {
            ft_space_diary_visitor.text = "${allvisitcount}人"
        } else {
            ft_space_diary_visitor.text = ""
        }
        if (body.currentmoment != null && body.currentmoment!!.isNotEmpty()) {
            hasDiary = true
//            if (user != null && user!!.uid != application.infoBean!!.uid) {
                setDiaryInfo(body.currentmoment!![0])
//            }
        } else {
            hasDiary = false
//            if (user != null && user!!.uid != application.infoBean!!.uid) {
                ft_space_diary_media.setEmtpyView(emtpyDiaryView, null)
                ft_space_diary_media.visibility = View.VISIBLE
//            }
//
            ft_space_diary_time.visibility = View.GONE
            ft_space_diary_title.visibility = View.GONE
        }
        if (user!!.uid != application.infoBean!!.uid&&body.selfscenes!=null&&body.selfscenes!!.isNotEmpty()) {
            sceneAdapter.setNewData(body.selfscenes!!)
        }
        val newdiarycount = body.newdiarycount
        val newvisitcount = body.newvisitcount
        val secretcount = body.secretcount
        val jindoucount = body.jindoucount
        val questioncount = body.questioncount
        val answercount = body.answercount
        val momentcount = body.momentcount
        val diarycount = body.diarycount
        val likecount = body.likecount
        jdCount = jindoucount
        ft_space_jindou.text = "金豆:$jindoucount"
        ft_space_pubboxcount.text = "$questioncount"
        ft_space_answboxcount.text = "$answercount"
        ft_space_likecount.text = "$likecount"
        if (momentcount > 0) {
            ft_space_pubdtcount.text = "${momentcount}条"
        } else {
            ft_space_pubdtcount.text = ""
        }
        if (diarycount > 0) {
            ft_space_pubdiarycount.text = "${diarycount}条"
        } else {
            ft_space_pubdiarycount.text = ""
        }
        if (newdiarycount > 0) {
            ft_space_diary_numb.visibility = View.VISIBLE
            ft_space_diary_numb.text = "$newdiarycount"
        }
        if (newvisitcount > 0) {
            ft_space_visitor_numb.visibility = View.VISIBLE
            ft_space_visitor_numb.text = "$newvisitcount"
        }
        if (secretcount > 0) {
            ft_space_gz_size.text = "${secretcount}人"
        } else {
            ft_space_gz_size.text = ""
        }
//        Log.i("spaceFt", "setVisitInfo ------secreflag:${body.secretflag}")
        if (body.secretflag) {
            ft_space_gz.text = "已暗恋"
            ft_space_gz.background = ContextCompat.getDrawable(mContext!!, R.drawable.hidden_loved)
        }
    }

    private fun setDiaryInfo(diaryEntry: DiaryEntry) {
        var emtpyInfo = true
        var hasMedia = false
        if (diaryEntry.text.isNotEmpty()) {
            emtpyInfo = false
            ft_space_diary_title.visibility = View.VISIBLE
            ft_space_diary_title.text = diaryEntry.text
        } else {
            ft_space_diary_title.visibility = View.GONE
        }
        if (diaryEntry.media.isNotEmpty()) {
            emtpyInfo = false
            hasMedia = true
            var type = MultMediaView.TYPE_IMG
            diaryEntry.media.forEach {
                if (it.type == "video") {
                    type = MultMediaView.TYPE_VIDEO
                }
            }
            ft_space_diary_media.setNewData(diaryEntry.media, type)
        }
        if (diaryEntry.created > 0 && !emtpyInfo) {
            val created = diaryEntry.created.toLong() * 1000
            var time = created.toString()
            ft_space_diary_time.visibility = View.VISIBLE
            ft_space_diary_time.text = when {
                DateUtils.isToday(time) -> DateUtils.timeMinute(time)
                DateUtils.isSameWeek(time) -> DateUtils.getWeek(created)
                DateUtils.IsYesterday(created) -> "昨天 "
                else -> DateUtils.timeM(time)
            }
        } else {
            ft_space_diary_time.visibility = View.GONE
        }
        if (!emtpyInfo) {
            if (!hasMedia)
                ft_space_diary_media.setEmtpyView(null, null)
        } else {
            ft_space_diary_media.setEmtpyView(emtpyDiaryView, null)
        }

    }

    override fun initEvent() {
        sceneAdapter.sceneClickListener = { v, p ->
            setScenes()
        }
        sceneAdapter.primaryScenesListener={v ->
            setScenes()
        }
        ft_space_dt_gp.setOnClickListener {
            //            openAppMarket("com.zhaorenwan.social")
            startActivity(Intent(mContext, MyDynamicActivity::class.java))
        }
        ft_space_diary_gp.setOnClickListener {
            if (hasDiary) {
                startActivity(Intent(mContext, MyDynamicActivity::class.java).putExtra("uid",user!!.uid))
//                startActivity(Intent(mContext!!, DiaryActivity::class.java).putExtra("bean", user))
            } else {
                if (user!!.uid == application.infoBean!!.uid) {
                    pubDialog.show()
                }
            }
//            ft_space_diary_numb.visibility = View.GONE
//            ft_space_diary_numb.text = "0"
        }
        ft_space_head.setOnClickListener {
            if (user!!.uid == application.infoBean!!.uid) {
                startActivityForResult(Intent(mContext!!, SetInfoActivity::class.java), REQUEST_SETINFO)
            }
        }
        ft_space_info_gp.setOnClickListener {
            if (user!!.uid == application.infoBean!!.uid) {
                startActivityForResult(Intent(mContext!!, SetInfoActivity::class.java), REQUEST_SETINFO)
            }
        }
        ft_space_visitor_gp.setOnClickListener {
            var numb = ft_space_visitor_numb.text.toString().toInt()
            startActivity(Intent(mContext!!, VisitorActivity::class.java).putExtra("bean", user).putExtra("numb", numb))
            ft_space_visitor_numb.visibility = View.GONE
            ft_space_visitor_numb.text = "0"
        }
        ft_space_diary_pub.setOnClickListener {
            pubDialog.show()
        }
        ft_space_label_gp.setOnClickListener {
            if (user!!.uid == application.infoBean!!.uid) {
                startActivityForResult(Intent(mContext, ModifyActivity::class.java).putExtra("type", "label"), REQUEST_MODIFY_INSTEREST)
            }
        }
        ft_space_setting.setOnClickListener {
            startActivity(Intent(mContext!!, SettingActivity::class.java))
        }
        ft_space_decorate.setOnClickListener {
            startActivity(Intent(mContext, DecorateActivity::class.java))
        }
        ft_space_jindou.setOnClickListener {

            startActivity(Intent(mContext, JindouActivity::class.java).putExtra("count", jdCount))
        }
        ft_space_diary_media.emptyViewClickListener = {
            if (user!!.uid == application.infoBean!!.uid) {
                pubDialog.show()
            }
        }
        ft_space_gz.setOnClickListener {
            gzUser(user!!.uid)
        }
    }
    var readyEntry:FilterEntry?=null
    private fun setScenes() {
        var data: FilterEntry = FilterEntry()
        val selectPosition= sceneAdapter.selectPosition
        data.people = "全部"
        data.goal = sceneAdapter.selectItem
        data.sid = sceneAdapter.selectSid
        data.type = sceneAdapter.selecdtType
        data.moment = sceneAdapter.selectMoment
        if (selectPosition!=-1){
            data.scenes = sceneAdapter.data[selectPosition]
        }
        if (data.goal != null && data.goal.isNotEmpty()) {
            val scenes = data.scenes
            if (scenes?.code!=null&&scenes.code.isNotEmpty()&&scenes.userinfor?.uid!=application.loginEntry?.userinfor?.uid){
                readyEntry=data
                setVerifyScene(scenes)
                return
            }
            EventBus.getDefault().post(RefresHomeEvent(data))
//            val intnet = Intent()
//            intnet.putExtra("goal", selectScene.goal)
//            intnet.putExtra("sid", selectScene.sid)
//            intnet.putExtra("type", selectScene.type)
//            intnet.putExtra("moment", selectScene.moment)
//            setResult(Activity.RESULT_OK, intnet)
//            finishActivityAnim()
        }

    }



    private fun setVerifyScene(scenes: ScenesEntry) {
        val intent=Intent(mContext,VerifySceneActivity::class.java)
        intent.putExtra("bean",scenes)
        startActivityForResult(intent,REQUEST_VERIFY_SCENES)
        activity?.overridePendingTransition(R.anim.open_enter2,R.anim.open_exit)
    }

    private fun gzUser(uid: Int) {
        val gz = ft_space_gz.text.toString()
        var gzmodel = 0
        gzmodel = if (gz == "暗恋") {
            0
        } else {
            1
        }
        model.secretLove(uid, gzmodel, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful && response.body().stat == "ok") {

                    if (gzmodel == 0) {
                        if (!neverHint) {
                            gzDialog.show()
                        } else {
                            TipsUtil.showToast(mContext, response.body().msg!!)
                        }
                        ft_space_gz.text = "已暗恋"
                        ft_space_gz.background = ContextCompat.getDrawable(mContext!!, R.drawable.hidden_loved)
                    } else {
                        TipsUtil.showToast(mContext, response.body().msg!!)
                        ft_space_gz.text = "暗恋"
                        ft_space_gz.background = ContextCompat.getDrawable(mContext!!, R.drawable.hidden_love)
                    }
                }
            }

        }, SpaceFragment@ this)
    }

    override fun initData() {

    }

    private fun initDialog() {
        val userEntry = activity!!.getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
        val edit = userEntry.edit()
        neverHint = userEntry.getBoolean("never_hint_gz2", false)
        gzDialog = GzSuccessDialog(mContext!!, intArrayOf(R.id.dlg_gz_sure))
        gzDialog.setOnBaseDialogItemClickListener { dialog, view ->
            if (view.id == R.id.dlg_gz_sure) {
                if (gzDialog.neverHint) {
                    edit.putBoolean("never_hint_gz2", true)
                    edit.commit()
                    neverHint = true
                }
                dialog.dismiss()
            }
        }


        pubDialog = PubVcrDialog(mContext, intArrayOf(R.id.dlg_pub_vcr_record, R.id.dlg_pub_vcr_select))
        pubDialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_pub_vcr_record -> {
                    RxPermissions(this@SpaceFragment)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            .subscribe { b ->
                                run {
                                    if (b) {
                                        RecordVideoActivity.startActivityToActivity(mContext!!, PubDiaryActivity::class.java)
                                    } else {
                                        TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                                    }
                                }
                            }
                }
                R.id.dlg_pub_vcr_select -> {
                    var gallerType = PictureMimeType.ofAll()
                    PictureSelector.create(this@SpaceFragment)
                            .openGallery(gallerType)
                            .loadImageEngine(GlideEngine.createGlideEngine())
                            .isCamera(false)
                            .maxSelectNum(9)
                            .maxVideoSelectNum(1)
                            .videoMaxSecond(2 * 60)
                            .forResult(REQUEST_CODE_SELECT)
                }
            }
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SETINFO) {
            if (user!!.uid == application.infoBean!!.uid)
                setInfo(application.infoBean!!)
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT) {
                var imgs = PictureSelector.obtainMultipleResult(data)
                if (imgs == null || imgs.size <= 0) {
                    return
                }
                var type = "pic"
                imgs.forEach {
                    if (it.mimeType.contains("video/"))
                        type = "video"
                }
                startActivity(Intent(mContext, PubDiaryActivity::class.java).putExtra("imgs", ArrayList(imgs)).putExtra("mediaType", type))
//                activity!!.overridePendingTransition(R.anim.open_enter3, R.anim.open_exit3)
            } else if (requestCode == REQUEST_MODIFY_INSTEREST) {
                var insterest = data?.getStringExtra("txt")
                if (insterest != null) {
                    LoginModel.modifyInfo(this@SpaceFragment, "label", insterest, object : JsonCallback<BaseEntry<UserEntry>>() {
                        override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                            response?.let {
                                if (it.isSuccessful) {
                                    if (TextUtils.equals(response.body().stat, "ok")) {
                                        QzApplication.get().infoBean = it.body().userinfor
                                        setInfo(it.body().userinfor!!)
                                    }
                                }
                            }
                        }
                    })
                }
            }else if (requestCode==REQUEST_VERIFY_SCENES){
                if (readyEntry!=null){
                    EventBus.getDefault().post(RefresHomeEvent(readyEntry!!))
                    readyEntry=null
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun newDiary(entry: DiaryEvent) {
        refreshVisit()
    }

    override fun onResume() {
        super.onResume()
        refreshVisit()
    }
}
