package com.eggplant.qiezisocial.ui.extend.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.extend.PubGuessActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.scrollPicker.adapter.ScrollPickerAdapter
import com.eggplant.qiezisocial.widget.scrollPicker.view.ScrollPickerView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.fragment_set_guessnumber.*

/**
 * Created by Administrator on 2020/7/13.
 */

class SetGuessNumberFragment : BaseFragment() {
    var set_numb_data = listOf("4个", "5个", "6个", "7个", "8个")
    var picPath: String? = null
    var qsNumb = 0
    var eligibleQsNumb = 0
    val REQUEST_IMG = 111

    companion object {


        fun instanceFragment(bundle: Bundle?): SetGuessNumberFragment {
            var fragment = SetGuessNumberFragment()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_guessnumber
    }

    override fun initView() {
    }

    override fun initEvent() {
        set_guessnum_next.setOnClickListener {
            var qsnum = qs_num.text.toString()
            var eligible = eligible_qs_num.text.toString()
            var pattern = pattern.text.toString()
            if (!TextUtils.equals(qsnum, "选择个数") && !TextUtils.equals(eligible, "选择个数")) {
                qsNumb = qsnum.replace("个", "").toInt()
                eligibleQsNumb = eligible.replace("个", "").toInt()
            }
            if (qsNumb != 0 && eligibleQsNumb != 0) {
                (activity as PubGuessActivity)?.onNext(qsNumb, eligibleQsNumb, pattern, picPath)
            } else {
                TipsUtil.showToast(mContext, "请选择问题个数及合格题数")
            }
        }
        set_qs_num.setOnClickListener {
            showDialog(qs_num, set_numb_data)
        }
        set_eligible_qs_num.setOnClickListener {
            var qsnum = qs_num.text.toString().replace("个", "")
            if (!TextUtils.equals(qs_num.text.toString(), "选择个数") && !TextUtils.isEmpty(qsnum)) {
                var set_elige_numb_data = ArrayList<String>()

                (1..qsnum.toInt()).forEach {
                    set_elige_numb_data.add("${it}个")
                }
                showDialog(eligible_qs_num, set_elige_numb_data)
            }

        }
        set_pattern.setOnClickListener {

        }
        up_pic.setOnClickListener {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .maxSelectNum(1)
                    .compress(true)
                    .isCamera(false)
                    .forResult(REQUEST_IMG)
        }
        guessnumb_delete.setOnClickListener {
            up_pic.visibility = View.VISIBLE
            set_guess_pic.visibility = View.GONE
            picPath = null
        }

    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMG) {
                var data = PictureSelector.obtainMultipleResult(data)
                data?.let {
                    var select = it[0]
                    var path = select.androidQToPath
                    if (path == null || path.isEmpty()) {
                        path = select.path
                    }
                    picPath = path
                    if (select.isCompressed) {
                        picPath = select.compressPath
                        Glide.with(mContext!!).load(picPath).into(guessnumb_pic)
                        set_guess_pic.visibility = View.VISIBLE
                        up_pic.visibility = View.GONE
                    }

                }
            }
        }
    }

    private fun showDialog(tv: QzTextView, data: List<String>) {
        val rulerView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_ruler, null, false)
        val pickView = rulerView.findViewById<ScrollPickerView>(R.id.dialog_pickview)
        pickView.layoutManager = LinearLayoutManager(mContext)

        var selectValue = ""
        val builder = ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mContext)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#E5E5E5")
                .setItemViewProvider(null)
                .setDataList(data)
                .setOnScrolledListener { v ->
                    val text = v.tag as String
                    if (text != null) {
                        selectValue = text.replace("cm", "").replace("kg", "")
                    }
                }

        val mScrollPickerAdapter = builder.build()
        pickView.adapter = mScrollPickerAdapter

        var dialog = AlertDialog.Builder(mContext!!)
                .setTitle("选择个数")
                .setView(rulerView)
                .setPositiveButton("确定", { _, _ ->
                    if (!TextUtils.isEmpty(selectValue)) {
                        tv.text = "${selectValue}"

                    }
                }).create()
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.show()

        var attr = dialog.window.attributes
        attr.width = ScreenUtil.getDisplayWidthPixels(mContext)
        dialog.window.attributes = attr
    }

}
