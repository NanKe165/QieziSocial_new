package com.eggplant.qiezisocial.ui.test

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.HomeModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.CardAdapter
import com.eggplant.qiezisocial.ui.main.fragment.HomeRyAnimator
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by Administrator on 2022/2/23.
 */

class TestActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_test
    }

    lateinit var adapter: CardAdapter
    lateinit var model: HomeModel
    override fun initView() {
//        val path = Path()
//        path.moveTo(150f, 0f)
//        path.lineTo(650f, 1000f)
//        path.lineTo(50f, 3000f)
        model = HomeModel()
        test_ry.layoutManager = LinearLayoutManager(mContext)
        test_ry.itemAnimator = HomeRyAnimator()
//         CardLayoutManager(RecyclerView.VERTICAL)

        adapter = CardAdapter(null)
        adapter.bindToRecyclerView(test_ry)
        adapter.ryWidth=test_ry.width
        val headView = LayoutInflater.from(mContext).inflate(R.layout.layout_test_head_foot, null, false)
        val footView = LayoutInflater.from(mContext).inflate(R.layout.layout_test_head_foot, null, false)
        adapter.setHeaderView(headView)
        adapter.setFooterView(footView)


        test_ry.adapter = adapter
    }

    override fun initData() {
        model.getBoxInfo(0, "", "222", object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        data.addAll(list)
                        val new = ArrayList<BoxEntry>()
                        new.add(data[0])
                        new.add(data[1])
                        new.add(data[2])
                        new.add(data[3])
                        new.add(data[4])
                        new.add(data[5])
                        adapter.addData(new)
                        runLayoutAnimation(test_ry)
                        i = 6
                        startThread()
                    } else {

                    }
                }
            }


        })
    }

    private fun startThread() {
        if (msgThread == null) {
            msgThread = MsgThread()
        }
        msgThread!!.start()
    }


    private var currentThreadName: String? = null
    private var lastTime: Long = 0
    var msgThread: MsgThread? = null
    private var taktTime = 5 * 1000
    var threadPause = false
    var threadDestory = false
    var data = ArrayList<BoxEntry>()
    var i = 0

    inner class MsgThread : Thread() {
        init {
            currentThreadName = System.currentTimeMillis().toString()
            name = currentThreadName
        }

        override fun run() {
            super.run()
            while (TextUtils.equals(currentThreadName, this.name) && !threadDestory) {
                try {
                    Thread.sleep(taktTime.toLong())
                    if (!threadPause) {
                        if (i >= data.size - 1) {
                            return
                        }
                        test_ry?.post {

                            val layoutManager = test_ry.layoutManager as LinearLayoutManager
                            val firstCvPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                            adapter.addData(firstCvPosition, data[i])
                            if (firstCvPosition == 0) {
                                layoutManager.scrollToPosition(0)
                            }

                            i++
                        }

                    }

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun initEvent() {
        adapter.setOnItemClickListener { adapter, view, position ->
            //            adapter.addData(position, "萨日朗，萨日朗")
            val manager = test_ry.layoutManager as LinearLayoutManager
            val fc = manager.findFirstCompletelyVisibleItemPosition()
            val fv = manager.findFirstVisibleItemPosition()
            val lc = manager.findLastCompletelyVisibleItemPosition()
            val lv = manager.findLastVisibleItemPosition()
            Log.i("TestAct", "firstCv:$fc  firstV:$fv   lastCv:$lc  lastV:$lv")

        }
        adapter.setOnItemLongClickListener { adapter, view, position ->

            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadDestory = true
        if (msgThread != null) {
            msgThread?.interrupt()
            msgThread = null
        }
    }
}
