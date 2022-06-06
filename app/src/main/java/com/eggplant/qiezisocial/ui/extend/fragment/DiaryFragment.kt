package com.eggplant.qiezisocial.ui.extend.fragment

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.DiaryContract
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.presenter.DiaryPresenter
import com.eggplant.qiezisocial.ui.extend.InputboxActivity
import com.eggplant.qiezisocial.ui.extend.PubDiaryActivity
import com.eggplant.qiezisocial.ui.extend.adapter.DiaryAdapter
import com.eggplant.qiezisocial.utils.ScreenUtil
import kotlinx.android.synthetic.main.fragment_diary.*

/**
 * Created by Administrator on 2021/1/22.
 */

class DiaryFragment : BaseMvpFragment<DiaryPresenter>(), DiaryContract.View {

    val REQUEST_PUB_CODE = 121
    val REQUEST_COMMENT_CODE: Int = 122

    lateinit var adapter: DiaryAdapter


    override fun initPresenter(): DiaryPresenter {
        return DiaryPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_diary
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = DiaryAdapter(activity!!, null)
        ft_diary_ry.layoutManager = LinearLayoutManager(mContext)
        ft_diary_ry.adapter = adapter

    }

    override fun initEvent() {
        adapter.boomClickListener = { x, y, id ->
            ft_diary_animview.boom(x, y - resources.getDimension(R.dimen.qb_px_90).toInt())
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
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
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
                activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            }
        }
        adapter.loadMoreCommentListener = { id, aPosition, cPosition ->
            mPresenter.loadMoreComment(id, aPosition, cPosition)
        }
        adapter.setOnLoadMoreListener({
            var size = adapter.data.size
//            mPresenter.loadMoreData(size, user!!.uid)
        }, ft_diary_ry)
        adapter.setOnLikeTouchEvent(object : DiaryAdapter.OnLikeTouchEvent {
            override fun actionDown(position: Int, view: View, id: Int) {
                if (!adapter.data[position].mylike) {
                    adapter.data[position].mylike = true
                    adapter.data[position].like += 1
                    adapter.notifyItemChanged(position)
                    mPresenter.likeDiary(mContext!!, id, position)
                }
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                ft_diary_animview.startAnim(loca[0], loca[1])
            }

            override fun actionUp(position: Int, view: View) {
                ft_diary_animview.stopAnim()
            }

        })
        ft_diary_pub.setOnClickListener {
            startActivityForResult(Intent(mContext, PubDiaryActivity::class.java), REQUEST_PUB_CODE)
//            activity?.overridePendingTransition(R.anim.open_enter3, R.anim.open_exit3)
        }

    }

    override fun initData() {
//        mPresenter.setNewData(user!!.uid)
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

//                mPresenter.setNewData(user!!.uid)
            }
        }
    }

    private fun showInputComment(view: View?, position: Int) {
        view?.let {
            val rect = IntArray(2)
            view.getLocationOnScreen(rect)
            var rvInputY = rect[1]
            val rvInputHeight = view.height
            ft_diary_ry.postDelayed({
                val y = ScreenUtil.dip2px(mContext, 300)
//                diary_ry.scrollToPosition(position)
                ft_diary_ry.smoothScrollBy(0, rvInputY - (y - rvInputHeight))
            }, 300)
        }

    }

    override fun setNewData(data: List<DiaryEntry>?) {
        adapter.setNewData(data)
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

}
