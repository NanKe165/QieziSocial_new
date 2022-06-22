package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.SetSceneActivity
import com.eggplant.qiezisocial.ui.main.adapter.SingleImageAdapter
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager
import kotlinx.android.synthetic.main.fragment_set_selfscene.*
import java.util.*


/**
 * Created by Administrator on 2022/4/27.
 */

class SetSelfSceneFragment : BaseFragment() {

    companion object {
        private var fragment: SetSelfSceneFragment? = null
            get() {
                if (field == null)
                    field = SetSelfSceneFragment()
                return field
            }

        fun newFragment(bundle: Bundle?): SetSelfSceneFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }

    }

    lateinit var adapter: SingleImageAdapter
    lateinit var txtWatcher: TextWatcher
    lateinit var txtWatcher2: TextWatcher
    var sceneEntry: ScenesEntry? = null
    var imgPosition = 0
    var pubState = 0
    //邀请码
    var pubCode = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_selfscene
    }

    override fun initView() {
        val scene = arguments?.getSerializable("scene")
        if (scene != null) {
            sceneEntry = scene as ScenesEntry
            set_selfscene_sure.text = "修改"
        }
        adapter = SingleImageAdapter(null)
        val manager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        manager.attach(set_selfscene_ry)
        manager.setItemTransformer(Transformer())
        manager.setOnItemSelectedListener { recyclerView, item, position ->
            //滑动到某一项的position
//            Log.i("setself", "select $position")
            imgPosition = position
        }
        set_selfscene_ry.layoutManager = manager
        set_selfscene_ry.adapter = adapter

    }

    override fun initEvent() {
        set_selfscene_title.setOnEditorActionListener { v, actionId, event ->
            (event.keyCode == KeyEvent.KEYCODE_ENTER)

        }
        set_selfscene_des.setOnEditorActionListener { v, actionId, event ->
            (event.keyCode == KeyEvent.KEYCODE_ENTER)

        }
        txtWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null)
                    set_selfscene_title_numb.text = "(${s.length}/6)"
                else
                    set_selfscene_title_numb.text = "(0/6)"
            }
        }
        txtWatcher2 = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null)
                    set_selfscene_des_numb.text = "(${s.length}/10)"
                else
                    set_selfscene_des_numb.text = "(0/10)"
            }
        }
        set_selfscene_title.addTextChangedListener(txtWatcher)
        set_selfscene_des.addTextChangedListener(txtWatcher2)
        adapter.setOnItemClickListener { adapter, view, position ->
            set_selfscene_ry.smoothScrollToPosition(position)
        }
        set_selfscene_gp.setOnClickListener {
            hideInput(it)
        }
        set_selfscene_close.setOnClickListener {
            hideInput(it)
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).setMysceneClose()
            }
        }
        set_selfscene_sure.setOnClickListener {
            val title = set_selfscene_title.text?.toString()?.trim()
            if (title == null || title.isEmpty()) {
                TipsUtil.showToast(mContext, "请填写主标题")
                return@setOnClickListener
            }
            val des = set_selfscene_des.text?.toString()?.trim()
            if (des == null || des.isEmpty()) {
                TipsUtil.showToast(mContext, "请填写副标题")
                return@setOnClickListener
            }
            if (pubState != 0 && pubCode == "") {
                TipsUtil.showToast(mContext, "请设置邀请码")
                return@setOnClickListener
            }
            hideInput(it)
            if (sceneEntry == null) {
                createScenes(title, des, imgPosition,pubCode)
            } else {
                modifyScenes(title, des, imgPosition,pubCode)
            }


        }
        set_selfscene_ok.setOnClickListener {
            set_selfscene_sub_success.visibility = View.GONE
            if (activity is SetSceneActivity) {
                (activity as SetSceneActivity).setModifysceneSucess()
            }
        }
        set_selfscene_state_all.setOnClickListener {
            pubState = 0
            resetState()
        }
        set_selfscene_state_invite.setOnClickListener {
            pubState = 1
            resetState()
        }
        set_selfscene_setcode.setOnClickListener {
            if (pubState != 0)
                set_selfscene_setcode_gp.visibility = View.VISIBLE
        }
        set_selfscene_setcode_ok.setOnClickListener {
            val code = set_selfscene_setcode_code.text.toString().trim()
            if (code.isEmpty() || code.length != 6) {
                TipsUtil.showToast(mContext, "请设置全6位邀请码")
                return@setOnClickListener
            }
            hideInput(it)
            pubCode = code
            set_selfscene_setcode.text = "（邀请码：$pubCode）"
            set_selfscene_setcode_gp.visibility = View.GONE
        }

        set_selfscene_setcode_gp.setOnClickListener {
            hideInput(it)
        }

    }

    private fun resetState() {
        if (pubState == 0) {
            set_selfscene_state_all.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_select_state, 0)
            set_selfscene_state_invite.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_unselect_state, 0)
        } else {
            set_selfscene_state_invite.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_select_state, 0)
            set_selfscene_state_all.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_unselect_state, 0)
        }
    }

    private fun modifyScenes(title: String?, des: String?, imgPosition: Int, pubCode: String) {

        OkGo.post<BaseEntry<*>>(API.MODIFY_SCENES)
                .tag(this)
                .params("sid", sceneEntry!!.sid)
                .params("t", title)
                .params("s", des)
                .params("p", imgPosition)
                .params("code",pubCode)
                .execute(object : JsonCallback<BaseEntry<*>>() {
                    override fun onSuccess(response: Response<BaseEntry<*>>?) {
                        if (response!!.isSuccessful) {
                            TipsUtil.showToast(mContext, response.body().msg!!)
                            if (response.body().stat == "ok") {
                                createOrModifySuccess()
                            }
                        }
                    }
                })
    }

    private fun createOrModifySuccess() {
        set_selfscene_sub_success.visibility = View.VISIBLE
    }

    private fun createScenes(title: String, des: String, i: Int, pubCode: String) {
        OkGo.post<BaseEntry<*>>(API.CREATE_SCENES)
                .tag(this)
                .params("t", title)
                .params("s", des)
                .params("p", i)
                .params("code",pubCode)
                .execute(object : JsonCallback<BaseEntry<*>>() {
                    override fun onSuccess(response: Response<BaseEntry<*>>?) {
                        if (response!!.isSuccessful) {
                            TipsUtil.showToast(mContext, response.body().msg!!)
                            if (response.body().stat == "ok") {
                                createOrModifySuccess()
                            }
                        }
                    }
                })
    }

    override fun initData() {
        setNewImages()
        if (sceneEntry != null) {
            set_selfscene_title.setText(sceneEntry!!.title)
            set_selfscene_des.setText(sceneEntry!!.des)
            val position = sceneEntry!!.pic.toInt()
            set_selfscene_ry.smoothScrollToPosition(position)
        }
    }
    private fun setNewImages(){
        var imgs = ArrayList<Int>()
        imgs.add(R.drawable.icon_scene_img1)
        imgs.add(R.drawable.icon_scene_img2)
        imgs.add(R.drawable.icon_scene_img3)
        imgs.add(R.drawable.icon_scene_img4)
        imgs.add(R.drawable.icon_scene_img5)
        imgs.add(R.drawable.icon_scene_img6)
        imgs.add(R.drawable.icon_scene_img7)
        imgs.add(R.drawable.icon_scene_img8)
        imgs.add(R.drawable.icon_scene_img9)
        imgs.add(R.drawable.icon_scene_img10)
        imgs.add(R.drawable.icon_scene_img11)
        imgs.add(R.drawable.icon_scene_img12)
        imgs.add(R.drawable.icon_scene_img13)
        imgs.add(R.drawable.icon_scene_img14)
        imgs.add(R.drawable.icon_scene_img15)
        imgs.add(R.drawable.icon_scene_img16)
        imgs.add(R.drawable.icon_scene_img17)
        imgs.add(R.drawable.icon_scene_img18)
        imgs.add(R.drawable.icon_scene_img19)
        imgs.add(R.drawable.icon_scene_img20)
        imgs.add(R.drawable.icon_scene_img21)
        imgs.add(R.drawable.icon_scene_img22)
        imgs.add(R.drawable.icon_scene_img23)
        imgs.add(R.drawable.icon_scene_img24)
        imgs.add(R.drawable.icon_scene_img25)
        imgs.add(R.drawable.icon_scene_img26)
        imgs.add(R.drawable.icon_scene_img27)
        imgs.add(R.drawable.icon_scene_img28)
        adapter.setNewData(imgs)
    }

    //滑动过程中的缩放
    inner class Transformer : GalleryLayoutManager.ItemTransformer {
        override fun transformItem(layoutManager: GalleryLayoutManager, item: View, fraction: Float) {
            //以圆心进行缩放
            item.pivotX = item.width / 2.0f
            item.pivotY = item.height / 2.0f
            val scale = 1 - 0.5f * Math.abs(fraction)
            val alpha = 1 - 0.3f * Math.abs(fraction)
            item.scaleX = scale
            item.scaleY = scale
            item.alpha = alpha
        }
    }

    fun resetData(scenesEntry: ScenesEntry?) {
        pubCode = ""
        pubState = 0
        resetState()
        set_selfscene_setcode_gp.visibility = View.GONE
        set_selfscene_setcode.text = "（点击设置邀请码）"
        set_selfscene_sub_success.visibility = View.GONE
        this.sceneEntry = scenesEntry
        if (scenesEntry != null) {
            set_selfscene_title.setText(scenesEntry.title)
            set_selfscene_des.setText(scenesEntry.des)
            imgPosition = scenesEntry.pic.toInt()
            set_selfscene_ry?.post {
                set_selfscene_ry.smoothScrollToPosition(imgPosition)
            }
            set_selfscene_sure.text = "修改"
        } else {
            set_selfscene_title.setText("")
            set_selfscene_des.setText("")
            imgPosition = 0
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            set_selfscene_ry.smoothScrollToPosition(imgPosition)
            set_selfscene_ry.postDelayed({
                setNewImages()
            },200)

            set_selfscene_sure.text = "自建"
        }
    }


    private fun hideInput(view: View) {
        //如果不是edittext，则隐藏键盘
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        set_selfscene_title.removeTextChangedListener(txtWatcher)
        set_selfscene_des.removeTextChangedListener(txtWatcher2)
        sceneEntry=null
        pubCode = ""
        pubState = 0
        imgPosition = 0
        super.onDestroyView()
    }
}
