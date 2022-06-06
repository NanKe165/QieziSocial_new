package com.eggplant.qiezisocial.ui.main.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.SetSceneActivity
import com.eggplant.qiezisocial.ui.main.adapter.FreshSelectObjectAdapter
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.ft_scence.*
import kotlinx.android.synthetic.main.layout_scene_bottom.view.*
import kotlinx.android.synthetic.main.layout_scene_head.view.*

/**
 * Created by Administrator on 2022/4/26.
 */

class SceneSelfBuildFragment : BaseFragment() {
    companion object {
        fun newFragment(bundle: Bundle?): SceneSelfBuildFragment {
            val fragment = SceneSelfBuildFragment()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var selectObjectAdapter: FreshSelectObjectAdapter
    lateinit var bottomSelectObjectAdapter: FreshSelectObjectAdapter
    lateinit var bottomView: View
    lateinit var bItemDec: GridSpacingItemDecoration
    lateinit var itemDec: GridSpacingItemDecoration
    lateinit var bhView: View
    lateinit var hView: View
    var canceledOutside = false
    var data: FilterEntry = FilterEntry()
    override fun getLayoutId(): Int {
        return R.layout.ft_scence
    }

    override fun initView() {
        selectObjectAdapter = FreshSelectObjectAdapter(null)
        bottomSelectObjectAdapter = FreshSelectObjectAdapter(null)
        bottomSelectObjectAdapter.adapterType = "friendScenes"
        ft_scene_ry.layoutManager = GridLayoutManager(mContext, 2)
        itemDec = GridSpacingItemDecoration(2, resources.getDimension(R.dimen.qb_px_18).toInt(), resources.getDimension(R.dimen.qb_px_25).toInt())
        itemDec.setHasHeader(true)

        bItemDec = GridSpacingItemDecoration(2, resources.getDimension(R.dimen.qb_px_18).toInt(), resources.getDimension(R.dimen.qb_px_25).toInt())


        ft_scene_ry.addItemDecoration(bItemDec)
        ft_scene_ry.adapter = bottomSelectObjectAdapter



        bottomView = LayoutInflater.from(mContext).inflate(R.layout.layout_scene_bottom, null, false)
        bottomView.layout_scene_ry.layoutManager = GridLayoutManager(mContext, 2)
        bottomView.layout_scene_ry.addItemDecoration(itemDec)
        bottomView.layout_scene_ry.adapter = selectObjectAdapter


        bhView = LayoutInflater.from(mContext).inflate(R.layout.layout_scene_head, null, false)
        bhView.layout_scene_tv.text = "好友自建"



        hView = LayoutInflater.from(mContext).inflate(R.layout.layout_scene_head, null, false)


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
                (activity as SetSceneActivity).setSelfScene()
            }
        }
        selectObjectAdapter.checkSceneClickListener = { v, p ->
            if (activity is SetSceneActivity) {
                val data = ArrayList(selectObjectAdapter.data)
                data.removeAt(data.size - 1)
                (activity as SetSceneActivity).checkScenes(data)
            }
        }
        selectObjectAdapter.sceneClickListener = { v, p ->
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).selectScene()
            }
        }
        selectObjectAdapter.primaryScenesListener={ view->
            if (bottomSelectObjectAdapter.selectPosition!=-1){
                val lastSelect=bottomSelectObjectAdapter.selectPosition
                bottomSelectObjectAdapter.selectPosition=-1
                bottomSelectObjectAdapter.selectItem=""
                bottomSelectObjectAdapter.notifyItemChanged(lastSelect)
            }
        }
        bottomSelectObjectAdapter.primaryScenesListener={ view->
            if (selectObjectAdapter.selectPosition!=-1){
                val lastSelect=selectObjectAdapter.selectPosition
                selectObjectAdapter.selectPosition=-1
                selectObjectAdapter.selectItem=""
                selectObjectAdapter.notifyItemChanged(lastSelect)
            }
        }
        bottomSelectObjectAdapter.sceneClickListener = { v, p ->
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).selectScene()
            }
        }

    }

    override fun initData() {
        getFriendScene(true)
    }

    private fun getFriendScene(loadFriend: Boolean) {
        OkGo.post<BaseEntry<ScenesEntry>>(API.GET_FRIEND_SCENES)
                .tag(this)
                .params("b", "")
                .execute(object : JsonCallback<BaseEntry<ScenesEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<ScenesEntry>>?) {
                        if (response!!.isSuccessful) {
                            val list = response.body().list
                            if (list != null && list.isNotEmpty()) {
                                bItemDec.setHasHeader(true)
                                bItemDec.setDataSize(list.size + bottomSelectObjectAdapter.headerLayoutCount)
                                bottomSelectObjectAdapter.setNewData(list)
//                                bottomSelectObjectAdapter.addData(list)
//                                bottomSelectObjectAdapter.addData(list)
                                selectObjectAdapter.setFooterView(bhView)
                                bottomSelectObjectAdapter.setHeaderView(bottomView)
                                itemDec.setHasFooter(true)

                            }
                            getMyselfScene(loadFriend)

                        }
                    }
                })
    }


    private fun getMyselfScene(loadFriend: Boolean) {
        OkGo.post<BaseEntry<ScenesEntry>>(API.GET_SELF_SCENES)
                .tag(this)
                .params("b", "")
                .execute(object : JsonCallback<BaseEntry<ScenesEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<ScenesEntry>>?) {
                        if (response!!.isSuccessful) {
                            val list = response.body().list
                            if (list != null && list.isNotEmpty()) {
                                itemDec.setDataSize(list.size + selectObjectAdapter.headerLayoutCount + selectObjectAdapter.footerLayoutCount)
                                val addData = ArrayList(list)
                                addData.add(ScenesEntry())
                                selectObjectAdapter.setHeaderView(hView)
                                selectObjectAdapter.setNewData(addData)
                                itemDec.setDataSize(selectObjectAdapter.data.size + selectObjectAdapter.headerLayoutCount + selectObjectAdapter.footerLayoutCount)


                            } else {
                                selectObjectAdapter.addData(ScenesEntry())
                            }
                            if (!loadFriend) {
                                data.goal = selectObjectAdapter.data[0].title
                                data.sid = selectObjectAdapter.data[0].sid
                                data.type = selectObjectAdapter.data[0].type
                            }

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

        if (selectObjectAdapter.selectPosition == -1 ) {
            data.goal = bottomSelectObjectAdapter.selectItem
            data.sid = bottomSelectObjectAdapter.selectSid
            data.type = bottomSelectObjectAdapter.selecdtType
            data.moment = bottomSelectObjectAdapter.selectMoment
            if (bottomSelectObjectAdapter.selectPosition != -1 && bottomSelectObjectAdapter.selectPosition < bottomSelectObjectAdapter.data.size)
                data.scenes = bottomSelectObjectAdapter.data[bottomSelectObjectAdapter.selectPosition]
        }
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
            bottomSelectObjectAdapter.setSingleItem(goals[0], sid, type)
            true
        } else {
            false
        }
        bottomSelectObjectAdapter.notifyDataSetChanged()
        selectObjectAdapter.notifyDataSetChanged()
    }

    fun setSelfScenes() {
        getFriendScene(false)
    }

}
