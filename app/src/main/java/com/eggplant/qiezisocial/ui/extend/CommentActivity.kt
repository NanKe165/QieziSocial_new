package com.eggplant.qiezisocial.ui.extend

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.VcrCommentEntry
import com.eggplant.qiezisocial.model.VcrModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.extend.adapter.CommentAdapter
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.lzy.okgo.model.Response
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_comment.*
import sj.keyboard.utils.EmoticonsKeyboardUtils

/**
 * Created by Administrator on 2020/7/16.
 */

class CommentActivity : BaseActivity(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    private lateinit var adapter: CommentAdapter
    private lateinit var popWindow: BasePopupWindow
    private var id = -1
    private val model = VcrModel()
    private var clickPositionBean: VcrCommentEntry? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_comment
    }

    override fun initView() {
        adapter = CommentAdapter(null)
        comment_ry.layoutManager = LinearLayoutManager(mContext)
        comment_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))
        comment_ry.adapter = adapter
        initPop()
    }

    private fun initPop() {
        popWindow = BasePopupWindow(mContext)
        var view = LayoutInflater.from(mContext).inflate(R.layout.pop_comment, null, false)
        popWindow.contentView = view
        popWindow.showAnimMode = 1
        view.findViewById<View>(R.id.pop_comment_chat).setOnClickListener {
            if (clickPositionBean != null) {
                startActivity(Intent(mContext, ChatActivity::class.java).putExtra("user", clickPositionBean!!.userinfor))
            }
        }
        view.findViewById<View>(R.id.pop_comment_report).setOnClickListener {
            if (clickPositionBean != null) {
                model.reoprtVcrComment(clickPositionBean!!.id, object : JsonCallback<BaseEntry<*>>() {
                    override fun onSuccess(response: Response<BaseEntry<*>>?) {
                        if (response!!.isSuccessful) {
                                TipsUtil.showToast(mContext, response.body().msg!!)
                        }
                    }
                })
            }
        }
    }

    override fun initData() {
        id = intent.getIntExtra("id", -1)
        if (id == -1)
            finish()
        setNewData()
    }


    override fun initEvent() {
        adapter.setOnItemLongClickListener { _, view, position ->
            clickPositionBean = adapter.data[position]
            var loca = IntArray(2)
            view.getLocationOnScreen(loca)
            popWindow.showAtLocation(view, Gravity.NO_GRAVITY, loca[0] + view.width / 2, loca[1] + view.height / 3)
            true
        }
        adapter.setOnLoadMoreListener({
            loadMoreData()
        }, comment_ry)
        adapter.setOnItemClickListener { _, _, _ ->
            comment_keyboard.reset()
        }
        comment_ry.setOnTouchListener { _, _ ->
            comment_keyboard.reset()
            false
        }
        comment_close.setOnClickListener {
            finish()
        }
        comment_rootview.setOnClickListener {
            finish()
        }
        comment_keyboard.setOnSendclickListneer { str ->
            if (!TextUtils.isEmpty(str)) {
                comment_keyboard.reset()
                model.pubVcrComment(id, str, object : JsonCallback<BaseEntry<VcrCommentEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<VcrCommentEntry>>?) {
                        response?.let {
                            if (response.isSuccessful && response.body()!!.stat == "ok") {
                                adapter.addData(0, response.body().record!!)
                            }
                        }
                    }
                })
            }
        }

    }

    private fun setNewData() {
        model.getVcrComment(id, 0, object : JsonCallback<BaseEntry<VcrCommentEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<VcrCommentEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        adapter.setNewData(response.body()!!.list)
                    }
                }
            }
        })

    }

    private fun loadMoreData() {
        model.getVcrComment(id, adapter.data.size, object : JsonCallback<BaseEntry<VcrCommentEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<VcrCommentEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        var data = response.body()!!.list
                        if (data == null || data.isEmpty()) {
                            adapter.loadMoreEnd(true)
                        } else {
                            adapter.addData(data)
                            adapter.loadMoreComplete()
                        }
                    }
                }
            }
        })
    }

    override fun expressionClick(str: String?) {
        comment_keyboard.input(comment_keyboard.edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        comment_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        comment_keyboard.delete(comment_keyboard.edit)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            val isConsum = comment_keyboard.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onPause() {
        super.onPause()
        comment_keyboard.reset()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}
