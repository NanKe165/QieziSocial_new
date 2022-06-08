package com.eggplant.qiezisocial.ui.main

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.DynamicContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.presenter.DynamicPresenter
import com.eggplant.qiezisocial.ui.extend.InputboxActivity
import com.eggplant.qiezisocial.ui.main.adapter.DynamicAdapter
import com.eggplant.qiezisocial.ui.main.dialog.GchatFuncDialog
import com.eggplant.qiezisocial.utils.GlideUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.luck.picture.lib.PictureSelector
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_dynamic.*
import kotlinx.android.synthetic.main.dlg_dynamic_delete.view.*

/**
 * Created by Administrator on 2021/8/19.
 */

class DynamicActivity : BaseMvpActivity<DynamicPresenter>(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener, DynamicContract.View {
    override fun expressionClick(str: String?) {
        dy_keyboard.input(dy_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        dy_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        dy_keyboard.delete(dy_edit)
    }

    override fun initPresenter(): DynamicPresenter {
        return DynamicPresenter()
    }

    private val REQUEST_PHOTO_ALBUM = 110
    val REQUEST_COMMENT_CODE: Int = 122
    private var pubMediaPath = ArrayList<String>()
    private var pubMediaType = ""
    private var textWatch: TextWatcher? = null
    private var goal: String? = ""
    private var sid: String? = ""
    private lateinit var progressDialog: QzProgressDialog
    private lateinit var dowloadDialog: GchatFuncDialog<BoxEntry>
    var popWindow: BasePopupWindow? = null
    lateinit var adapter: DynamicAdapter
    private lateinit var emptyView: View
    var deleteId = -1
    var deletePs = -1
    var deleteUid = -1
    var deleteSid = 0
    var reportTv: QzTextView? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_dynamic
    }

    override fun initView() {
        mPresenter.attachView(this)
        goal = intent.getStringExtra("goal")
        sid = intent.getStringExtra("sid")
        val name = intent.getStringExtra("name")
        if (name != null && name.isNotEmpty()) {
            dy_bar.setTitle(name)
            dy_title.text = name
        }
        adapter = DynamicAdapter(activity, null)
        if (goal == null || sid == null) {
            goal=""
            sid=""
//            finish()
//            return
        }
        dy_keyboard.setEmojiContent(dy_edit)
        dy_media_img.isClickable = false
        initDialog()
        initPopWindow()

//        adapter.deleteOpen = true
        adapter.setEnableLoadMore(true)
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_dynamic, null, false)
        dy_ry.layoutManager = LinearLayoutManager(mContext)
        dy_ry.adapter = adapter
        textWatch = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reSetMediaView()
            }
        }

    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dlg_dynamic_delete, null, false)

        reportTv = popView.dlg_dynamic_delete

        popView.dlg_dynamic_delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.setOnClickListener {
            if (deleteUid != application.loginEntry!!.userinfor.uid) {
                mPresenter.reportDynamic(activity, deleteId, deletePs)
            } else {
                mPresenter.deleteDynamic(activity, deleteId, deletePs, deleteSid)
            }
            deleteUid = -1
            deleteId = -1
            deleteSid = 0
            if (deletePs != -1)
                adapter.remove(deletePs)
            deletePs = -1
            popWindow?.dismiss()
        }
    }


    private fun initDialog() {
        progressDialog = QzProgressDialog(mContext)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("正在压缩...")
        dowloadDialog = GchatFuncDialog(mContext, intArrayOf(R.id.dlg_gchat_block, R.id.dlg_gchat_report, R.id.dlg_gchat_save_img))
        dowloadDialog.setOnBaseDialogItemClickListener { _, view ->
            when (view.id) {
                R.id.dlg_gchat_save_img -> {
                    RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe { b ->
                                if (!b) {
                                    TipsUtil.showToast(mContext, "权限申请失败")
                                } else {
                                    val downloadPath = dowloadDialog.downloadFile
                                    if (downloadPath.isNotEmpty()) {
                                        mPresenter.saveFile(mContext, downloadPath)
                                        dowloadDialog.downloadFile = ""
                                        dowloadDialog.postion = -1
                                        dowloadDialog.bean = null
                                    }
                                }
                            }
                }
                R.id.dlg_gchat_report -> {
                    TipsUtil.showToast(mContext, "举报成功，正在审核...")
                }
                R.id.dlg_gchat_block -> {
//                    dialog.mode = 4
//                    dialog.show()
                }
            }
            dowloadDialog.dismiss()
        }
    }

    override fun initData() {
        mPresenter.getDynamic(0, goal!!, sid!!)
    }

    override fun initEvent() {

        dy_edit.addTextChangedListener(textWatch)
        rootView.setOnClickListener {
            dy_keyboard.reset()
            finish()
        }
        dy_close.setOnClickListener {
            dy_keyboard.reset()
            finish()
        }
        dy_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                dy_keyboard.reset()
                finish()
            }
        })
        dy_sel_meida.setOnClickListener { v ->
            if (pubMediaPath.size == 1) {
                PrevUtils.onImageItemClick(mContext, v, pubMediaPath[0], pubMediaPath[0])
                return@setOnClickListener
            }
            mPresenter.openGallery(activity, REQUEST_PHOTO_ALBUM)
        }
//        dy_sel_meida.setOnLongClickListener {
//            mPresenter.openGalleryVideo(activity, REQUEST_PHOTO_ALBUM)
//            true
//        }
        dy_media_delete.setOnClickListener {
            pubMediaPath.clear()
            pubMediaType = ""
            reSetMediaView()
        }
        dy_pub.setOnClickListener {
            val txt = dy_edit.text?.toString()?.trimEnd()
            if ((txt != null && txt.isNotEmpty()) || pubMediaPath.isNotEmpty()) {
                mPresenter.readyTopub(activity, goal!!, sid!!, txt, pubMediaPath, pubMediaType)
                dy_edit.setText("")
                pubMediaPath.clear()
                pubMediaType = ""
                reSetMediaView()
                dy_keyboard.reset()
            }
        }

        dy_ry.setOnTouchListener { v, event ->
            dy_keyboard.reset()
            false
        }
        adapter.commentListener = { position, entry ->
            val view = adapter.getViewByPosition(position, R.id.ap_dy_comment)
            showInputComment(view)
            var intent = Intent(mContext, InputboxActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("id", adapter.data[position].id)
            intent.putExtra("toid", entry.uid)
            if (entry.uid.toInt() != application.infoBean!!.uid) {
                intent.putExtra("tousername", entry.userinfor.nick)
            }


            startActivityForResult(intent, REQUEST_COMMENT_CODE)
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }

        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.ap_dy_pub_comment) {
                val view = adapter.getViewByPosition(position, R.id.ap_dy_comment)
                showInputComment(view)
                var intent = Intent(mContext, InputboxActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("id", adapter.data[position].id)
                intent.putExtra("toid", adapter.data[position].uid)
                startActivityForResult(intent, REQUEST_COMMENT_CODE)
                overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            } else if (view.id == R.id.ap_dy_like_img) {
                val id = adapter.data[position].id
                if (!adapter.data[position].mylike) {
                    (view as ImageView).setImageResource(R.mipmap.like_select)
                    val animation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    animation.duration = 100
                    animation.fillAfter = true
                    view.startAnimation(animation)
                    mPresenter.like(position, id)
                }
            } else if (view.id == R.id.ap_dy_head) {
                startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", adapter.data[position].userinfor))
                overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            } else if (view.id == R.id.ap_dy_report) {
                deleteUid = adapter.data[position].uid.toInt()
                deleteId = adapter.data[position].id
                val s = adapter.data[position].sid
                if (s!=null&&s.isNotEmpty())
                    deleteSid = s.toInt()
                deletePs = position
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                if (deleteUid != application.loginEntry!!.userinfor.uid) {
                    reportTv?.text = "举报"
                } else {
                    reportTv?.text = "删除"
                }
                popWindow?.showAtLocation(view, Gravity.NO_GRAVITY, loca[0] - (ScreenUtil.dip2px(mContext, 103) - view.width), loca[1] + view.height)
            }
        }
        adapter.imgLongClickListener = { positon, view, path, entry ->
            dowloadDialog.downloadFile = path
            dowloadDialog.postion = positon
            dowloadDialog.bean = entry
            dowloadDialog.show()
        }
        adapter.setOnLoadMoreListener({
            var start = 0
            if (adapter.data.isNotEmpty()) {
                start = adapter.data.size
            }
            mPresenter.getDynamic(start, goal!!, sid!!)
        }, dy_ry)
    }

    private fun showInputComment(view: View?) {
        view?.let {
            val rect = IntArray(2)
            view.getLocationOnScreen(rect)
            var rvInputY = rect[1]
            val rvInputHeight = view.height

            dy_ry.postDelayed({
                val y = ScreenUtil.dip2px(mContext, 480)
                dy_ry.smoothScrollBy(0, rvInputY - (y - rvInputHeight))
            }, 300)
        }

    }

    override fun setNewData(list: List<BoxEntry>) {
        adapter.setNewData(list)
    }

    override fun addData(list: List<BoxEntry>) {
        adapter.addData(list)
    }

    override fun addPubData(record: BoxEntry) {
        adapter.addData(0, record)
        dy_ry.scrollToPosition(0)
    }

    override fun notifyLikeView(position: Int) {
        val entry = adapter.data[position]
        val likeUser = entry.likeuser
        if (!entry.mylike) {
            entry.mylike = true
            entry.like += 1
            if (likeUser != null) {
                likeUser.add(0, application.infoBean!!)
            } else {
                val user = ArrayList<UserEntry>()
                user.add(application.infoBean!!)
                entry.likeuser = user
            }
            adapter.notifyItemChanged(position)
        }
    }

    override fun commentSuccess(record: CommentEntry?, position: Int) {
        if (record == null) {
            return
        }
        val comment = adapter.data[position].comment
        if (comment == null) {
            val data = ArrayList<CommentEntry>()
            data.add(record)
            adapter.data[position].comment = data
        } else {
            adapter.data[position].comment.add(record)
        }
        adapter.notifyItemChanged(position)
    }

    override fun loadMoreEnd(b: Boolean) {
        if (b) {
            adapter.loadMoreEnd(true)
            if (adapter.data.isEmpty())
                adapter.emptyView = emptyView
        } else {
            adapter.loadMoreComplete()
        }
    }

    override fun showUploadView(p: String) {
        progressDialog.setMessage(p)
        progressDialog.show()
    }

    override fun showCompressView(p: String) {
        progressDialog.setMessage(p)
        progressDialog.show()
    }

    override fun hideCompressView() {
        progressDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        dy_keyboard.postDelayed({
            dy_keyboard.visibility = View.VISIBLE
        }, 200)
    }

    override fun onPause() {
        super.onPause()
        dy_keyboard.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PHOTO_ALBUM -> {
                    // 图片、视频、音频选择结果回调
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    imgs.forEach {
                        var path = it.androidQToPath
                        if (path == null || path.isEmpty()) {
                            path = it.path
                        }
                        when {
                            it.mimeType.contains("video/") -> {
                                Log.i("selectFile", " file is video")
                                pubMediaType = "video"
                                pubMediaPath.add(path)
                            }
                            it.mimeType.contains("image/") -> {
                                pubMediaType = "image"
                                pubMediaPath.add(if (it.isCompressed) {
                                    it.compressPath
                                } else {
                                    path
                                })
                                Log.i("selectFile", "img   ${it.compressPath}   $path")
                            }
                            else -> {
                                Log.i("selectFile", " file is other  ${it.mimeType}")
                                pubMediaType = ""
                                pubMediaPath.clear()
                            }
                        }
                    }
                    reSetMediaView()

                }
                REQUEST_COMMENT_CODE -> {
                    if (data != null) {
                        val postion = data.getIntExtra("position", -1)
                        val id = data.getIntExtra("id", -1)
                        val toid = data.getStringExtra("toid")
                        val content = data.getStringExtra("txt")
                        if (postion != -1 && id != -1 && toid != null) {
                            mPresenter.commentDynamic(postion, id, toid.toInt(), content)
                        }
                    }
                }
            }
        }
    }

    private fun reSetMediaView() {
        dy_media_play.visibility = View.GONE
        dy_media_delete.visibility = View.GONE
//        dy_media_img.background=null
        if (pubMediaType == "") {
            Glide.with(mContext).load(R.mipmap.icon_dynamic_upload).into(dy_media_img)
//            dy_media_img.setImageResource(R.mipmap.icon_dynamic_upload)
        } else if (pubMediaType == "video") {
            dy_media_delete.visibility = View.VISIBLE
            dy_media_play.visibility = View.VISIBLE
            GlideUtils.loadVideoScreenshot(mContext, pubMediaPath[0], dy_media_img, 1)
        } else if (pubMediaType == "image") {
            dy_media_delete.visibility = View.VISIBLE
            Glide.with(mContext).load(pubMediaPath[0]).into(dy_media_img)

        }
        checkPubState()
    }

    private fun checkPubState() {
        val content = dy_edit.text?.toString()?.trimEnd()
        if ((content != null && content.isNotEmpty()) || pubMediaPath.isNotEmpty()) {
            dy_pub.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black5))
            dy_pub.background = ContextCompat.getDrawable(mContext, R.drawable.dy_pub_selc_bg)
            dy_pub.isClickable = true
        } else {
            dy_pub.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray2))
            dy_pub.background = ContextCompat.getDrawable(mContext, R.drawable.dy_pub_uselc_bg)
            dy_pub.isClickable = false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }

    override fun onDestroy() {
        dy_edit.removeTextChangedListener(textWatch)
        super.onDestroy()
    }
}
