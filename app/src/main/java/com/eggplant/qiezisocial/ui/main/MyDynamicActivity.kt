package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.DynamicModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.InputboxActivity
import com.eggplant.qiezisocial.ui.main.adapter.DynamicAdapter
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_blacklist.*
import kotlinx.android.synthetic.main.dlg_dynamic_delete.view.*


/**
 * Created by Administrator on 2021/8/24.
 */


class MyDynamicActivity : BaseActivity() {
    lateinit var adapter: DynamicAdapter
    lateinit var model: DynamicModel
    var popWindow: BasePopupWindow? = null
    var popWindow2: BasePopupWindow? = null
    val REQUEST_COMMENT_CODE: Int = 122
    var deleteId = -1
    var deletePs = -1
    var deleteSid = -1
    var deleteUid = -1
    var reportTv: QzTextView? = null

    var uid=0
    override fun getLayoutId(): Int {
        return R.layout.activity_blacklist
    }

    override fun initView() {
        initPopWindow()
        uid=intent.getIntExtra("uid",0)
        model = DynamicModel()
        adapter = DynamicAdapter(activity, null)

        adapter.setEnableLoadMore(true)
        black_ry.layoutManager = LinearLayoutManager(mContext)
        black_ry.adapter = adapter
        black_bar.setTitle("我的话题")
    }

    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dlg_dynamic_delete, null, false)
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.setOnClickListener {
            deleteDynamic()
            popWindow?.dismiss()
        }


        val popView2 = LayoutInflater.from(mContext).inflate(R.layout.dlg_dynamic_delete, null, false)

        reportTv = popView.dlg_dynamic_delete

        popView2.dlg_dynamic_delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        popWindow2= BasePopupWindow(mContext)
        popWindow2?.contentView = popView2
        popWindow2?.showAnimMode = 1
        popView2.setOnClickListener {
            if (deleteUid != application.loginEntry!!.userinfor.uid) {
                reportDynamic()
            } else {
                deleteDynamic()
            }
            deleteUid = -1
            deleteId = -1
            deleteSid = 0
            if (deletePs != -1)
                adapter.remove(deletePs)
            deletePs = -1
            popWindow2?.dismiss()
        }
    }


    override fun initData() {
        if (uid==0) {
            adapter.deleteOpen = true
            model.getMyDynamic(0, object : JsonCallback<BaseEntry<BoxEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                    if (response!!.isSuccessful) {
                        adapter.setNewData(response.body().list)
                    }
                }
            })
        }else {
            if(uid==application.loginEntry?.userinfor?.uid)
                adapter.deleteOpen = true
            adapter.setEnableLoadMore(false)
            model.getOtherDynamic(uid,object :JsonCallback<BaseEntry<BoxEntry>>(){
                override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                    if (response!!.isSuccessful) {
                        adapter.setNewData(response.body().list)
                    }
                }
            })
        }
    }

    override fun initEvent() {
        black_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnLoadMoreListener({
            if (adapter.data.isEmpty()) {
                model.getMyDynamic(adapter.data.size, object : JsonCallback<BaseEntry<BoxEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                        if (response!!.isSuccessful) {
                            if (response.body().list != null) {
                                adapter.addData(response.body().list!!)
                                adapter.loadMoreComplete()
                            } else {
                                adapter.loadMoreEnd(true)
                            }
                        }
                    }
                })
            } else {
                adapter.loadMoreEnd(true)
            }

        }, black_ry)







        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.ap_dy_delete) {
                deleteId = adapter.data[position].id
                val sid = adapter.data[position].sid
                if (sid!=null&&sid.isNotEmpty())
                    deleteSid = sid.toInt()
                else
                    deleteSid=0
                deletePs = position
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                popWindow?.showAtLocation(view, Gravity.NO_GRAVITY, loca[0] - (ScreenUtil.dip2px(mContext, 103) - view.width), loca[1] + view.height)
            }else  if (view.id == R.id.ap_dy_pub_comment) {
                val view = adapter.getViewByPosition(position, R.id.ap_dy_comment)
                showInputComment(view)
                var intent = Intent(mContext, InputboxActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("id", adapter.data[position].id)
                intent.putExtra("toid", adapter.data[position].uid)
                startActivityForResult(intent, REQUEST_COMMENT_CODE)
                overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
            }else if (view.id == R.id.ap_dy_like_img) {
                val id = adapter.data[position].id
                if (!adapter.data[position].mylike) {
                    (view as ImageView).setImageResource(R.mipmap.like_select)
                    val animation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    animation.duration = 100
                    animation.fillAfter = true
                    view.startAnimation(animation)
                    like(position, id)
                }
            }else if (view.id == R.id.ap_dy_report) {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_COMMENT_CODE -> {
                    if (data != null) {
                        val postion = data.getIntExtra("position", -1)
                        val id = data.getIntExtra("id", -1)
                        val toid = data.getStringExtra("toid")
                        val content = data.getStringExtra("txt")
                        if (postion != -1 && id != -1 && toid != null) {
                            commentDynamic(postion, id, toid.toInt(), content)
                        }
                    }
                }
            }
        }
    }


    fun commentDynamic(position: Int, id: Int, toid: Int, content: String) {
        model.commentDynaimc(id, toid, content, object : JsonCallback<BaseEntry<CommentEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<CommentEntry>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        commentSuccess(response.body().record, position)
                    }
                }
            }
        })
    }

    private fun commentSuccess(record: CommentEntry?, position: Int) {
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

    private fun showInputComment(view: View?) {
        view?.let {
            val rect = IntArray(2)
            view.getLocationOnScreen(rect)
            var rvInputY = rect[1]
            val rvInputHeight = view.height

            black_ry.postDelayed({
                val y = ScreenUtil.dip2px(mContext, 480)
                black_ry.smoothScrollBy(0, rvInputY - (y - rvInputHeight))
            }, 300)
        }

    }
    private fun deleteDynamic() {
        if (deleteId != -1 && deleteSid != -1)
            model.deleteDynamic(deleteId, deleteSid, object : JsonCallback<BaseEntry<*>>() {
                override fun onSuccess(response: Response<BaseEntry<*>>?) {
                    if (response!!.isSuccessful) {
                        TipsUtil.showToast(mContext, response.body().msg!!)
                        if (response.body().stat == "ok") {
                            deleteId = -1
                            deleteSid = -1
                            if (deletePs != -1)
                                adapter.remove(deletePs)
                            deletePs = -1
                        }
                    }
                }

            })
    }
    fun reportDynamic() {
        model.reportDynaimc(activity, "moment", deleteId, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    TipsUtil.showToast(activity, response.body().msg!!)
                    if (response.body().stat == "ok") {

                    }
                }
            }
        })
    }

    fun like(position: Int, id: Int) {
        Log.i("likeDy", "id:$id")
        model.like(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        notifyLikeView(position)
                    }
                }
            }
        })
    }

    fun notifyLikeView(position: Int) {
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

}
