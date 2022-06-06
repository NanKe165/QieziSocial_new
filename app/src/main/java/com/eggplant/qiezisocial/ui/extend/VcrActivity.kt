package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.VcrContract
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.VcrPresenter
import com.eggplant.qiezisocial.ui.extend.adapter.DiaryAdapter
import com.eggplant.qiezisocial.ui.extend.adapter.VcrAdapter
import com.eggplant.qiezisocial.ui.extend.dialog.PubVcrDialog
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import com.luck.picture.lib.PictureSelector
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import kotlinx.android.synthetic.main.activity_vcr.*
import kotlinx.android.synthetic.main.pop_vcr.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2020/7/14.
 */

class VcrActivity : BaseMvpActivity<VcrPresenter>(), VcrContract.View {
    private lateinit var adapter: VcrAdapter
    private lateinit var pubDialog: PubVcrDialog
//    private lateinit var shareDialog: ShareDialog
    private val REQUEST_CODE_VIDEO = 101
    private val REQUEST_CODE_RECORD = 102
    private var model = 0//0  我的vcr   1 我关注的vcr   2 我发布的vcr
    private lateinit var popWindow: BasePopupWindow
    private var clickPosition = -1
    override fun getLayoutId(): Int {
        return R.layout.activity_vcr
    }

    override fun initPresenter(): VcrPresenter {
        return VcrPresenter()
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = VcrAdapter(activity = this, data = null)
        vcr_ry.layoutManager = LinearLayoutManager(mContext)
        vcr_ry.adapter = adapter
        vcr_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))
        model = intent.getIntExtra("model", 0)
        if (model != 0) {
            if (model == 1) {
                vcr_bar.setTitle("关注的")
            }
            vcr_bar.setRightTxt("")
            adapter.setCollectVisiable(false)
        }
        initDialog()
        initPopWindow()

    }

    private fun initPopWindow() {
        var view = LayoutInflater.from(mContext).inflate(R.layout.pop_vcr, null, false)
        when (model) {
            1 -> {
                view.pop_vcr_report2.setOnClickListener {
                    if (clickPosition != -1)
                        mPresenter.reportVcr(clickPosition, adapter.data[clickPosition].id)
                    clickPosition = -1
                    popWindow.dismiss()
                }
                view.pop_vcr_guanzhu.setOnClickListener {
                    if (clickPosition != -1)
                        mPresenter.cancelCollect(clickPosition, adapter.data[clickPosition].id)
                    clickPosition = -1
                    popWindow.dismiss()
                }
            }
            0 -> {
                view.pop_vcr_gp2.visibility = View.GONE
                view.pop_vcr_gp1.setOnClickListener {
                    if (clickPosition != -1)
                        mPresenter.reportVcr(clickPosition, adapter.data[clickPosition].id)
                    clickPosition = -1
                    popWindow.dismiss()
                }

            }
            2 -> {
                view.pop_vcr_gp2.visibility = View.GONE
                view.pop_vcr_report1.text = "删除"
                view.pop_vcr_gp1.setOnClickListener {
                    if (clickPosition != -1)
                        mPresenter.deleteVcr(clickPosition, adapter.data[clickPosition].id)
                    clickPosition = -1
                    popWindow.dismiss()
                }
            }
        }
        popWindow = BasePopupWindow(mContext)
        popWindow.contentView = view
        popWindow.showAnimMode = 1

    }

    private fun initDialog() {
        pubDialog = PubVcrDialog(mContext, intArrayOf(R.id.dlg_pub_vcr_record, R.id.dlg_pub_vcr_select))
        pubDialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_pub_vcr_record -> {
                    mPresenter.recordVideo(activity, REQUEST_CODE_RECORD)
                }
                R.id.dlg_pub_vcr_select -> {
                    mPresenter.selectVideo(activity, REQUEST_CODE_VIDEO)
                }
            }
            dialog.dismiss()
        }

//        shareDialog = ShareDialog(mContext, intArrayOf(R.id.dlg_share_friend, R.id.dlg_share_pyq, R.id.dlg_share_wechat, R.id.dlg_share_weibo))
//        shareDialog.setOnBaseDialogItemClickListener { dialog, view ->
//            when (view.id) {
//                R.id.dlg_share_friend -> {
//                    startActivity(Intent(mContext, SelectFriendActivity::class.java))
//                }
//                R.id.dlg_share_pyq -> {
//
//                }
//                R.id.dlg_share_wechat -> {
//
//                }
//                R.id.dlg_share_weibo -> {
//
//                }
//            }
//            dialog.dismiss()
//        }

    }

    override fun initData() {

        mPresenter.setFollow(model)
        mPresenter.initData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initEvent() {
        vcr_bar.setTbListener(object : TopBarListener {
            override fun onReturn() {
                finish()

            }

            override fun onTxtClick() {
                pubDialog.show()
            }
        })
        adapter.setOnLoadMoreListener({
            mPresenter.loadMoreData(adapter.data.size)
        }, vcr_ry)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ap_vcr_report -> {
                    clickPosition = position
                    var loca = IntArray(2)
                    view.getLocationOnScreen(loca)
                    popWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, loca[1] + view.height)
                }
                R.id.ap_vcr_follow -> {
                    mPresenter.collectVcr(position, adapter.data[position].id)
                }
                R.id.ap_vcr_share -> {
//                    shareDialog.show()
                }
                R.id.ap_vcr_comment -> {
                    startActivity(Intent(mContext, CommentActivity::class.java).putExtra("id", adapter.data[position].id))
                    overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
                R.id.ap_vcr_like -> {
                    mPresenter.likeVcr(position, adapter.data[position].id)
                }
                R.id.ap_vcr_head -> {
                    startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", adapter.data[position].userinfor))
                    overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
                R.id.ap_vcr_img -> {
                    var url = API.Companion.PIC_PREFIX + adapter.data[position].media[0].org
                    PrevUtils.onImageItemClick(mContext, view, url, url)
                }
            }
        }
        adapter.setOnLikeTouchEvent(object : DiaryAdapter.OnLikeTouchEvent {
            override fun actionUp(position: Int, view: View) {
                vcr_animview.stopAnim()
            }
            override fun actionDown(position: Int, view: View, id: Int) {
                if (!adapter.data[position].mylike) {
                    adapter.data[position].mylike = true
                    adapter.data[position].like += 1
                    adapter.notifyItemChanged(position)
                    mPresenter.likeVcr(position, id)
                }
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                vcr_animview.startAnim(loca[0], loca[1] - ScreenUtil.getStatusBarHeight(activity))
            }
        })
        adapter.boomClickListener = { x, y, id ->
            vcr_animview.boom(x, y)
            mPresenter.boom(id)
        }
        vcr_ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_VIDEO -> {
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    val file = imgs[0]
                    var path = file.androidQToPath
                    if (path == null || path.isEmpty()) {
                        path = file.path
                    }
                    mPresenter.selectVideoSuccess(mContext, path)
                }
            }
        }
    }

    override fun setNewData(data: List<VcrEntry>?) {
        adapter.setNewData(data)
    }

    override fun addData(data: List<VcrEntry>?) {
        if (data == null || data.isEmpty()) {
            if (adapter.data.size > 2) {
                adapter.loadMoreEnd()
            } else {
                adapter.loadMoreEnd(true)
            }
        } else {
            adapter.addData(data)
            adapter.loadMoreComplete()
        }
    }

    override fun deleteVcrWithPosition(position: Int) {
        if (position >= 0 && position < adapter.data.size) {
            adapter.remove(position)
            TipsUtil.showToast(mContext, "删除成功")
        }
    }

    override fun cancelCollectWithPosition(position: Int) {
        if (position >= 0 && position < adapter.data.size) {
            adapter.remove(position)
            TipsUtil.showToast(mContext, "已取消")
        }
    }

    override fun refreshCollectWithPosition(position: Int) {
        if (position >= 0 && position < adapter.data.size) {
            adapter.data[position].att = true
            adapter.notifyItemChanged(position)
            TipsUtil.showToast(mContext, "关注成功")
        }
    }

    override fun refreshLikeWithPosition(position: Int, like: Int?) {
        if (position >= 0 && position < adapter.data.size && like != null) {
            adapter.data[position].like = like
            adapter.data[position].mylike = true
            adapter.notifyItemChanged(position)
            TipsUtil.showToast(mContext, "赞+1")
        }
    }

    override fun removeReportVcrWithPosition(position: Int) {
        if (position >= 0 && position < adapter.data.size) {
            adapter.remove(position)
            TipsUtil.showToast(mContext, "举报成功")
        }
    }

    override fun onStop() {
        super.onStop()
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
    }

    override fun onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return
        super.onBackPressed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVcrPubSuccess(record: VcrEntry) {
        var visiblePosition = (vcr_ry.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (visiblePosition == adapter.data.size - 1) {
            adapter.addData(record)
        } else {
            adapter.addData(visiblePosition, record)
            vcr_ry.scrollToPosition(visiblePosition)
        }
    }

}
