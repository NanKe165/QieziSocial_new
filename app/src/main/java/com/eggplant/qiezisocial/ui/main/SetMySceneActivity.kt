package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Window
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.SelectAdapter
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.PinyinUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.azlist.AZItemEntity
import com.eggplant.qiezisocial.widget.azlist.AZTitleDecoration
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_set_myscene.*
import java.util.*

/**
 * Created by Administrator on 2021/12/20.
 */

class SetMySceneActivity : BaseActivity() {
    lateinit var dialog: QzProgressDialog
    private var sortDataThread: Thread? = null
    private var mComparator: PinyinComparator? = null
    var selectAdapter: SelectAdapter? = null
    private var collegeData: List<AZItemEntity<ScenesEntry>>? = null
    private var textWatcher: TextWatcher? = null
    private val FROM_COLLEGE = "college"
    private val FROM_STAR = "star"
    private val FROM_FACTORY = "factory"
    var from: String? = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_set_myscene
    }

    override fun initView() {
        set_mys_ry.layoutManager = LinearLayoutManager(mContext)
        set_mys_ry.addItemDecoration(AZTitleDecoration(AZTitleDecoration.TitleAttributes(mContext)))
        mComparator = PinyinComparator()
        initDialog()
        from = intent.getStringExtra("from")
        if (from == null || from!!.isEmpty()) {
            finish()
        }
        if (from == FROM_COLLEGE) {

        } else if (from == FROM_STAR) {
            set_mys_bar.setTitle("选择我喜欢的明星")
            set_mys_search.hint = "搜索我喜欢的明星"
        } else if (from == FROM_FACTORY) {
            set_mys_bar.setTitle("选择我喜欢的大厂")
            set_mys_search.hint = "搜索我喜欢的大厂"
        }
    }

    private fun initDialog() {
        dialog = QzProgressDialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage("数据加载中...")
    }

    override fun initData() {
        if (from == FROM_COLLEGE) {
            val jsonString = FileUtils.readFromAssets("un.json", mContext)
            val collegeData = Gson().fromJson<CollegeEntry>(jsonString, CollegeEntry::class.java)
            if (collegeData != null && collegeData.college.isNotEmpty()) {
                dialog.show()
                setCollegeData(collegeData.college)
            }
        } else if (from == FROM_STAR) {
            setStarData()
        } else if (from == FROM_FACTORY) {
            setFactoryData()
        }
    }

    private fun setFactoryData() {
        OkGo.post<FactoryEntry>(API.GET_FACTORY)
                .execute(object : JsonCallback<FactoryEntry>() {
                    override fun onSuccess(response: Response<FactoryEntry>?) {
                        if (response!!.isSuccessful) {
                            if (response.body().factory != null && response.body().factory.isNotEmpty()) {
                                setCollegeData(response.body().factory)
                            }
                        }
                    }
                })
    }

    private fun setStarData() {
        OkGo.post<StarEntry>(API.GET_STAR)
                .execute(object : JsonCallback<StarEntry>() {
                    override fun onSuccess(response: Response<StarEntry>?) {
                        if (response!!.isSuccessful) {
                            if (response.body().star != null && response.body().star.isNotEmpty()) {
                                setCollegeData(response.body().star)
                            }
                        }
                    }
                })
    }

    private fun setCollegeData(college: List<ScenesEntry>) {
        if (sortDataThread == null)
            sortDataThread = Thread(Runnable {
                val mDateList = fillData(college)
                Collections.sort(mDateList, mComparator)
                set_mys_ry.post(Runnable {
                    collegeData = mDateList
                    if (activity.isDestroyed) {
                        return@Runnable
                    }
                    selectAdapter = SelectAdapter(mDateList)
                    set_mys_ry.adapter = selectAdapter
                    selectAdapter!!.selectClickListener = {
                        if (from == FROM_COLLEGE) {
                            getCollegeEntry(it.sid)
                        } else {
                            val intent = Intent()
                            intent.putExtra("data", it)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                    dialog.dismiss()
                })
                sortDataThread!!.interrupt()
                sortDataThread = null
            })
        sortDataThread!!.start()
    }

    private fun getCollegeEntry(sid: String) {
        OkGo.post<MysceneEntry>(API.GET_COLLEGE)
                .params("collegeid", sid)
                .execute(object : JsonCallback<MysceneEntry>() {
                    override fun onSuccess(response: Response<MysceneEntry>?) {
                        if (response!!.isSuccessful) {
                            if (response.body().college!=null){
                                val intent = Intent()
                                intent.putExtra("data", response.body().college)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }else{
                                TipsUtil.showToast(mContext,response.body().msg)
                            }
                        }
                    }
                })
    }

    private fun fillData(data: List<ScenesEntry>): List<AZItemEntity<ScenesEntry>>? {
        val sortList = java.util.ArrayList<AZItemEntity<ScenesEntry>>()
        for (aDate in data) {
            val item = AZItemEntity<ScenesEntry>()
            item.value = aDate
            //汉字转换成拼音
            val pinyin = PinyinUtils.getPingYin(aDate.title)
            //取第一个首字母
            val letters = pinyin.substring(0, 1).toUpperCase()
            // 正则表达式，判断首字母是否是英文字母
            if (letters.matches("[A-Z]".toRegex())) {
                item.setSortLetters(letters.toUpperCase())
            } else {
                item.setSortLetters("#")
            }
            sortList.add(item)
        }
        return sortList
    }

    private fun quareData(collegeData: List<AZItemEntity<ScenesEntry>>, s: String): List<AZItemEntity<ScenesEntry>> {
        val data = java.util.ArrayList<AZItemEntity<ScenesEntry>>()
        for (entry in collegeData) {
            val name = entry.value.title
            if (!TextUtils.isEmpty(name) && name.contains(s)) {
                data.add(entry)
            }
        }
        return data
    }


    override fun initEvent() {
        set_mys_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        set_mys_azview.setOnLetterChangeListener {
            if (selectAdapter != null) {
                val position = selectAdapter!!.getSortLettersFirstPosition(it)
                if (position != -1) {
                    if (set_mys_ry.layoutManager is LinearLayoutManager) {
                        val manager = set_mys_ry.layoutManager as LinearLayoutManager
                        manager.scrollToPositionWithOffset(position, 0)
                    } else {
                        set_mys_ry.layoutManager.scrollToPosition(position)
                    }
                }
            }
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (TextUtils.isEmpty(s)) {
                    selectAdapter?.setDataList(collegeData, false)
                } else {

                    if (collegeData != null) {
                        val azItemEntities = quareData(collegeData!!, s.toString())
                        selectAdapter?.setDataList(azItemEntities, false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }
        set_mys_search.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        set_mys_search.removeTextChangedListener(textWatcher)
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}
