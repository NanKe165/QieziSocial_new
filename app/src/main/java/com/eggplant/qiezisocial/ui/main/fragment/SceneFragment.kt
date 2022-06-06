package com.eggplant.qiezisocial.ui.main.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.MysceneEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.SetSceneActivity
import com.eggplant.qiezisocial.ui.main.adapter.FreshSelectObjectAdapter
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.ft_scence.*

/**
 * Created by Administrator on 2021/11/23.
 */

class SceneFragment : BaseFragment() {
    companion object {
        fun newFragment(bundle: Bundle?): SceneFragment {
            val fragment = SceneFragment()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var selectObjectAdapter: FreshSelectObjectAdapter
    var sceneModel: String? = null
    var data: FilterEntry = FilterEntry()
    var canceledOutside = false
    override fun getLayoutId(): Int {
        return R.layout.ft_scence
    }

    override fun initView() {
        selectObjectAdapter = FreshSelectObjectAdapter(null)
        ft_scene_ry.layoutManager = GridLayoutManager(mContext, 2)
        ft_scene_ry.addItemDecoration(GridSpacingItemDecoration(2, resources.getDimension(R.dimen.qb_px_18).toInt(), resources.getDimension(R.dimen.qb_px_25).toInt()))
        ft_scene_ry.adapter = selectObjectAdapter

        sceneModel = arguments?.getString("scene_model")
        val sid = arguments?.getString("sid")
        val goal = arguments?.getString("goal")
        val type = arguments?.getString("type")
        if (sid != null && goal != null) {
            data.goal = goal
            data.sid = sid
            if (type == null)
                data.type = "0"
            else
                data.type = type
        }
    }

    override fun initEvent() {
        selectObjectAdapter.selectMyscenenListener = {
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).setMyScene()
            }
        }
        selectObjectAdapter.sceneClickListener = { v, p ->
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).selectScene()
            }
        }


    }

    override fun initData() {
        Log.i("sceneFt", "initData--$sceneModel")
        when (sceneModel) {
            null -> setRecomData(false)
            "collect" -> getCollectList()
            "my_scene" -> setMyScene()
            "browse" -> getBrowse()
        }

    }

    private fun getBrowse() {
        OkGo.post<BaseEntry<ScenesEntry>>(API.GET_BROWSE_SCENE)
                .tag(this)
                .params("b", 0)
                .execute(object : JsonCallback<BaseEntry<ScenesEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<ScenesEntry>>?) {
                        Log.i("sceneFt", "${response!!.body().stat} ")
                        if (response.isSuccessful) {
                            selectObjectAdapter.setNewData(response.body().list)
                            selectObjectAdapter.bindToRecyclerView(ft_scene_ry)
                            selectObjectAdapter.setEmptyView(R.layout.layout_empty_collect_scene)
                            setSelectScene()
                        }
                    }

                    override fun onError(response: Response<BaseEntry<ScenesEntry>>?) {
                        Log.i("sceneFt", "onError ${response!!.exception}   ${response.message()} ")
                        super.onError(response)
                    }
                })
    }

    fun setMyScene() {
        getMyScene(true)
    }

    fun setRecomData(myScene: Boolean) {
        var data = QzApplication.get().loginEntry!!.scenes
        var newData = ArrayList(data)
        selectObjectAdapter.setNewData(newData)
        setSelectScene()
//        getMyScene(myScene)
    }

    private fun getMyScene(sce: Boolean) {
        val myScene = ArrayList<ScenesEntry>()
        selectObjectAdapter.mySelectPosition.clear()
        OkGo.post<MysceneEntry>(API.GET_MYSCENE)
                .tag(this)
                .execute(object : JsonCallback<MysceneEntry>() {
                    override fun onSuccess(response: Response<MysceneEntry>?) {
                        if (response!!.isSuccessful) {
                            if (response.body().college != null) {
                                myScene.add(response.body().college)
                            }
                            if (response.body().star != null) {
                                myScene.add(response.body().star)
                            }
                            if (response.body().factory != null) {
                                myScene.add(response.body().factory)
                            }
                            if (sce) {
                                myScene.add(ScenesEntry())
                                selectObjectAdapter.setNewData(myScene)
                            } else {
                                selectObjectAdapter.addData(0, myScene)
                            }

                        }

                        ft_scene_ry?.scrollToPosition(0)
                    }
                })
    }


    private fun getCollectList() {
        OkGo.post<BaseEntry<ScenesEntry>>(API.GET_COLLECT_SCENE)
                .tag(this)
                .execute(object : JsonCallback<BaseEntry<ScenesEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<ScenesEntry>>?) {
                        if (response!!.isSuccessful) {
                            selectObjectAdapter.setNewData(response.body().list)
                            selectObjectAdapter.bindToRecyclerView(ft_scene_ry)
                            selectObjectAdapter.setEmptyView(R.layout.layout_empty_collect_scene)
                            setSelectScene()
                        }
                    }
                })
    }


    fun getFilterData(): FilterEntry {
        data.people = "全部"
        data.goal = selectObjectAdapter.selectItem
        data.sid = selectObjectAdapter.selectSid
        data.type = selectObjectAdapter.selecdtType
        data.moment = selectObjectAdapter.selectMoment
        if (selectObjectAdapter.selectPosition != -1 && selectObjectAdapter.selectPosition < selectObjectAdapter.data.size)
            data.scenes = selectObjectAdapter.data[selectObjectAdapter.selectPosition]
        canceledOutside = !(data.goal == null || data.goal.isEmpty())
        return data
    }

    fun setSelectScene() {
        val goal = data.goal
        val sid = data.sid
        val type = data.type
        canceledOutside = if (goal.isNotEmpty()) {
            val goals = goal.split(",")
            selectObjectAdapter.setSingleItem(goals[0], sid, type)
            true
        } else {
            false
        }
        selectObjectAdapter.notifyDataSetChanged()
    }
}
