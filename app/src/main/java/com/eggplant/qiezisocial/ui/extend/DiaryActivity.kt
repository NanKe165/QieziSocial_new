package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.DiaryContract
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.event.DiaryEvent
import com.eggplant.qiezisocial.presenter.DiaryPresenter
import com.eggplant.qiezisocial.ui.extend.adapter.DiaryAdapter
import com.eggplant.qiezisocial.ui.extend.dialog.PubVcrDialog
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import com.luck.picture.lib.PictureSelector
import kotlinx.android.synthetic.main.activity_diary.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Administrator on 2020/10/15.
 */

class DiaryActivity : BaseMvpActivity<DiaryPresenter>(), DiaryContract.View {
    val REQUEST_PUB_CODE = 121
    val REQUEST_COMMENT_CODE: Int = 122
    private val REQUEST_CODE_SELECT = 101
    private val REQUEST_CODE_RECORD = 102
    lateinit var adapter: DiaryAdapter
    var user: UserEntry? = null
    var popWindow: BasePopupWindow? = null
    private lateinit var pubDialog: PubVcrDialog
    var deleteId = -1
    var deletePs = -1
    override fun initPresenter(): DiaryPresenter {
        return DiaryPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_diary
    }

    override fun initView() {
        mPresenter.attachView(this)
        val bean = intent.getSerializableExtra("bean") ?: finish()
        user = bean as UserEntry
        adapter = DiaryAdapter(this, null)
        if (user!!.uid == application.infoBean!!.uid) {
            diary_bar.setRightTxt("发布")
            diary_bar.setTitle("我的日志")
            adapter.mineDiary = true
        } else {
            diary_bar.setTitle("${user!!.nick}的日志")
        }

        initPopWindow()
        diary_ry.layoutManager = LinearLayoutManager(mContext)
//        diary_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 13), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        diary_ry.adapter = adapter
    }

    override fun initData() {
        mPresenter.setNewData(user!!.uid)
    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dlg_dynamic_delete, null, false)
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.setOnClickListener {
            mPresenter.boomDiary(deleteId)
            deleteId = -1
            if (deletePs != -1)
                adapter.remove(deletePs)
            deletePs = -1
            popWindow?.dismiss()
        }
        pubDialog = PubVcrDialog(mContext, intArrayOf(R.id.dlg_pub_vcr_record, R.id.dlg_pub_vcr_select))
        pubDialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_pub_vcr_record -> {
                    mPresenter.recordVideo(activity, REQUEST_CODE_RECORD)
                }
                R.id.dlg_pub_vcr_select -> {
                    mPresenter.selectVideo(activity, REQUEST_CODE_SELECT)
                }
            }
            dialog.dismiss()
        }
    }

    override fun initEvent() {
        adapter.boomClickListener = { x, y, id ->
            diary_animview.boom(x, y)
            mPresenter.boomDiary(id)
        }
        adapter.commentListener = { position, commententry ->
            val view = adapter.getViewByPosition(position, R.id.ap_diary_commentlist)
            showInputComment(view, position)
            var intent = Intent(mContext, InputboxActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("id", adapter.data[position].id)
            intent.putExtra("toid", commententry.uid)
            if (commententry.uid.toInt() != application.infoBean!!.uid) {
                intent.putExtra("tousername", commententry.userinfor.nick)
            }
            startActivityForResult(intent, REQUEST_COMMENT_CODE)
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.ap_diary_comment) {
                val view = adapter.getViewByPosition(position, R.id.ap_diary_commentlist)
                showInputComment(view, position)
                var intent = Intent(mContext, InputboxActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("id", adapter.data[position].id)
                intent.putExtra("toid", adapter.data[position].uid)
                startActivityForResult(intent, REQUEST_COMMENT_CODE)
                overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            } else if (view.id == R.id.ap_diary_report) {
                deleteId = adapter.data[position].id
                deletePs = position
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                popWindow?.showAtLocation(view, Gravity.NO_GRAVITY, loca[0] - (ScreenUtil.dip2px(mContext, 103) - view.width), loca[1] + view.height)
            }
        }
        adapter.loadMoreCommentListener = { id, aPosition, cPosition ->
            mPresenter.loadMoreComment(id, aPosition, cPosition)
        }
        adapter.setOnLoadMoreListener({
            var size = adapter.data.size
            mPresenter.loadMoreData(size, user!!.uid)
        }, diary_ry)
        adapter.setOnLikeTouchEvent(object : DiaryAdapter.OnLikeTouchEvent {
            override fun actionDown(position: Int, view: View, id: Int) {
                if (!adapter.data[position].mylike) {
                    adapter.data[position].mylike = true
                    adapter.data[position].like += 1
                    adapter.notifyItemChanged(position)
                    mPresenter.likeDiary(mContext, id, position)
                }
//                var loca = IntArray(2)
//                view.getLocationOnScreen(loca)
//                diary_animview.startAnim(loca[0], loca[1])
            }

            override fun actionUp(position: Int, view: View) {
//                diary_animview.stopAnim()
            }

        })

        diary_bar.setTbListener(object : TopBarListener {
            override fun onTxtClick() {
                pubDialog.show()
            }

            override fun onReturn() {
                finish()
            }

        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun newDiary(entry: DiaryEvent) {
        if (!entry.deleteDiary)
            mPresenter.setNewData(user!!.uid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_COMMENT_CODE) {
                if (data != null) {
                    val postion = data.getIntExtra("position", -1)
                    val id = data.getIntExtra("id", -1)
                    val toid = data.getStringExtra("toid")
                    val content = data.getStringExtra("txt")
                    if (postion != -1 && id != -1 && toid != null) {
                        mPresenter.commentDiary(postion, id, toid.toInt(), content)
                    }
                }
            } else if (requestCode == REQUEST_PUB_CODE) {
                mPresenter.setNewData(user!!.uid)
            } else if (requestCode == REQUEST_CODE_SELECT) {
                var imgs = PictureSelector.obtainMultipleResult(data)
                if (imgs == null || imgs.size <= 0) {
                    return
                }
                mPresenter.selectVideoSuccess(mContext, ArrayList(imgs))
                activity.overridePendingTransition(R.anim.open_enter3, R.anim.open_exit3)
            }
        }
    }

    private fun showInputComment(view: View?, position: Int) {
        view?.let {
            val rect = IntArray(2)
            view.getLocationOnScreen(rect)
            var rvInputY = rect[1]
            val rvInputHeight = view.height
            diary_ry.postDelayed({
                val y = ScreenUtil.dip2px(mContext, 300)
//                diary_ry.scrollToPosition(position)
                diary_ry.smoothScrollBy(0, rvInputY - (y - rvInputHeight))
            }, 300)
        }

    }


    override fun loadMoreDataComplete() {
        adapter.loadMoreComplete()
    }

    override fun loadMoreDataFinish() {
        if (adapter.data.size > 4) {
            adapter.loadMoreEnd()
        } else {
            adapter.loadMoreEnd(true)
        }
    }


    override fun loadMoreCommentSuccess(aPosition: Int, list: List<CommentEntry>) {
        if (adapter.data.size <= aPosition) {
            return
        }
        adapter.data[aPosition].commentlist.addAll(list)
        if (list.size < 6) {
            adapter.data[aPosition].hasMoreComment = false
        }
        adapter.notifyItemChanged(aPosition)

    }

    override fun loadmoreCommentError(aPosition: Int) {

        if (adapter.data.size <= aPosition) {
            return
        }
        adapter.data[aPosition].hasMoreComment = false
        adapter.notifyItemChanged(aPosition)

    }

    override fun setNewData(data: List<DiaryEntry>?) {
        adapter.setNewData(data)
    }

    override fun addData(data: List<DiaryEntry>): Boolean {
        return adapter.addDatas(data as ArrayList<DiaryEntry>)
    }

    override fun likDiarySuccess(msg: String?, position: Int) {

    }

    override fun commentSuccess(record: CommentEntry?, position: Int) {
        adapter.data[position].commentlist.add(0, record)
        adapter.data[position].comment += 1
        adapter.notifyItemChanged(position)
    }

}
