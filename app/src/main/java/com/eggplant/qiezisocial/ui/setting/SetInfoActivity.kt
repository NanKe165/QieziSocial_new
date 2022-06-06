package com.eggplant.qiezisocial.ui.setting

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.SetInfoContract
import com.eggplant.qiezisocial.entry.AlbumMultiItem
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.SetInfoPresenter
import com.eggplant.qiezisocial.ui.main.adapter.MineAlbumAdapter
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.scrollPicker.adapter.ScrollPickerAdapter
import com.eggplant.qiezisocial.widget.scrollPicker.view.ScrollPickerView
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.luck.picture.lib.PictureSelector
import kotlinx.android.synthetic.main.activity_setinfo.*
import kotlinx.android.synthetic.main.layout_mine_mid.*


/**
 * Created by Administrator on 2020/6/22.
 */

class SetInfoActivity : BaseMvpActivity<SetInfoPresenter>(), SetInfoContract.View {
    private val REQUEST_MODIFY_INSTEREST = 108
    private val REQUEST_MODIFY_NICK = 107
    private val REQUEST_MODIFY_SIGN = 106
    private val REQUEST_SELECT_FACE = 105
    private val REQUEST_ADD_PIC = 103
    private val REQUEST_PREV = 104
    lateinit var albumAdapter: MineAlbumAdapter
    private val heightList = ArrayList<String>()
    private val weidthList = ArrayList<String>()
    private val eduList = ArrayList<String>()

    var eduItems = arrayOf("小学", "初中", "高中", "专科", "本科", "硕士", "博士")
    override fun initPresenter(): SetInfoPresenter {
        return SetInfoPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setinfo
    }

    override fun initView() {
        albumAdapter = MineAlbumAdapter(mContext, null)
        setinfo_album_ry.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        setinfo_album_ry.adapter = albumAdapter
        mPresenter.attachView(this)

    }

    override fun initData() {
        mPresenter.setInfo()
        (100..220).mapTo(heightList) { "${it}cm" }
        (30..120).mapTo(weidthList) { "${it}kg" }
        eduItems.forEach {
            eduList.add(it)
        }
    }

    override fun initEvent() {
        setinfo_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        albumAdapter.setOnItemClickListener { _, _, position ->
            val data = albumAdapter.data[position]
            when (data.type) {
//                AlbumMultiItem.TYPE_TAKEPHOTO -> {
//                    if (albumAdapter.data.size < 9)
//                }
                AlbumMultiItem.TYPE_ALBUM -> {
                    mPresenter.previewPic(activity, REQUEST_PREV, position)
                }
            }
        }
        setinfo_add_pic.setOnClickListener {
            mPresenter.addPic(this, 4 - albumAdapter.data.size, REQUEST_ADD_PIC)
        }
        setinfo_sign_gp.setOnClickListener {
            startActivityForResult(Intent(mContext, ModifyActivity::class.java).putExtra("type", "sign").putExtra("txt", setinfo_sign.text.toString()), REQUEST_MODIFY_SIGN)
        }
        setinfo_nick_gp.setOnClickListener {
            startActivityForResult(Intent(mContext, ModifyActivity::class.java).putExtra("type", "nick").putExtra("txt", setinfo_nick.text.toString()), REQUEST_MODIFY_NICK)
        }
        setinfo_head_gp.setOnClickListener {
            mPresenter.changeFace(activity, REQUEST_SELECT_FACE)
        }
        setinfo_career_gp.setOnClickListener {
            showRulerView("职业")
        }
        setinfo_edu_gp.setOnClickListener {
            showRulerView("学历")
        }
        setinfo_height_gp.setOnClickListener {

            showRulerView("身高")
        }
        setinfo_weight_gp.setOnClickListener {

            showRulerView("体重")
        }
        setinfo_object_gp.setOnClickListener {
            showRulerView("交友目的")
        }
        setinfo_insterest_gp.setOnClickListener {
            //            showInsterSelect()
            startActivityForResult(Intent(mContext, ModifyActivity::class.java).putExtra("type", "label"), REQUEST_MODIFY_INSTEREST)
        }
    }

    override fun setFace(face: String?) {
        if (!TextUtils.isEmpty(face))
            Glide.with(mContext).load(API.PIC_PREFIX + face).into(setinfo_head)
    }

    override fun setNick(nick: String?) {
        setinfo_nick.text = nick

    }

    override fun setSign(sign: String?) {
        setinfo_sign.text = sign
    }

    override fun setHeight(height: String?) {
        setinfo_height.text = height
    }

    override fun setWeight(weight: String?) {
        setinfo_weight.text = weight
    }

    override fun setEdu(edu: String?) {
        setinfo_edu.text = edu
    }

    override fun setCareers(careers: String?) {
        setinfo_career.text = careers
    }

    override fun setLabel(label: String?) {
        setinfo_insterest.text = label
    }

    override fun setObject(s: String?) {
        setinfo_object.text = s
    }

    override fun showTost(it: String) {
        TipsUtil.showToast(mContext, it)
    }


    override fun setPic(realPic: List<AlbumMultiItem<String>>?) {
//        if (realPic != null && realPic.size >= 4) {
//            setinfo_add_pic.visibility = View.GONE
//        } else {
//            setinfo_add_pic.visibility = View.VISIBLE
//        }
//        albumAdapter.setNewData(realPic)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PREV -> mPresenter.setAlbum(application.infoBean)
                REQUEST_ADD_PIC -> {
                    var selectList = PictureSelector.obtainMultipleResult(data)
                    mPresenter.upNewPic(activity, selectList)
                }
                REQUEST_SELECT_FACE -> {
                    var selectList = PictureSelector.obtainMultipleResult(data)
                    selectList?.let {
                        var selectPic = it[0]
                        if (selectPic.isCompressed) {
                            mPresenter.modifyHead(activity, selectPic.compressPath)
                        }
                    }
                }
                REQUEST_MODIFY_NICK -> {
                    var nick = data?.getStringExtra("txt")
                    if (nick != null) {
                        mPresenter.modifyInfo(activity, "nick", nick)
                    }
                }
                REQUEST_MODIFY_SIGN -> {
                    var sign = data?.getStringExtra("txt")
                    if (sign != null) {
                        mPresenter.modifyInfo(activity, "sign", sign)
                    }
                }
                REQUEST_MODIFY_INSTEREST -> {
                    var insterest = data?.getStringExtra("txt")
                    if (insterest != null) {
                        mPresenter.modifyInfo(activity, "label", insterest)
                    }
                }
            }

        }
    }


    /**
     *
     */
    private fun showRulerView(title: String) {
        val rulerView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_ruler, null, false)
        val pickView = rulerView.findViewById<ScrollPickerView>(R.id.dialog_pickview)
        pickView.layoutManager = LinearLayoutManager(this)

        var selectValue = ""
        var normalSelect = 0
        val builder = ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(this)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#E5E5E5")
                .setItemViewProvider(null)
                .setOnScrolledListener { v ->
                    val text = v.tag as String
                    if (text != null) {
                        selectValue = text.replace("cm", "").replace("kg", "").replace("CM", "").replace("KG", "")
                    }
                }
        when {
            TextUtils.equals(title, "身高") -> {
                builder.setDataList(heightList)
                normalSelect = 62
            }
            TextUtils.equals(title, "体重") -> {
                builder.setDataList(weidthList)
                normalSelect = 22
            }
            TextUtils.equals(title, "职业") -> {
                builder.setDataList(application.loginEntry?.careerlist)
            }
            TextUtils.equals(title, "学历") -> {
                builder.setDataList(eduList)
                normalSelect = 6
            }
            TextUtils.equals(title, "交友目的") -> builder.setDataList(application.loginEntry?.objectlist)
        }

        val mScrollPickerAdapter = builder.build()

        pickView.adapter = mScrollPickerAdapter


        var dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(rulerView)
                .setPositiveButton("确定", { _, _ ->
                    if (!TextUtils.isEmpty(selectValue)) {
                        when {
                            TextUtils.equals(title, "身高") -> mPresenter.modifyInfo(activity, "height", selectValue)
                            TextUtils.equals(title, "体重") -> mPresenter.modifyInfo(activity, "weight", selectValue)
                            TextUtils.equals(title, "学历") -> mPresenter.modifyInfo(activity, "edu", selectValue)
                            TextUtils.equals(title, "职业") -> mPresenter.modifyInfo(activity, "careers", selectValue)
                            TextUtils.equals(title, "交友目的") -> mPresenter.modifyInfo(activity, "object", selectValue)
                        }
                    }
                }).create()
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setOnShowListener {
            pickView.scrollToPosition(normalSelect)
        }
        dialog.show()


        var attr = dialog.window.attributes
        attr.width = ScreenUtil.getDisplayWidthPixels(mContext)
        dialog.window.attributes = attr
    }

    override fun onDestroy() {
        setResult(Activity.RESULT_OK, Intent())
        super.onDestroy()
    }
}
