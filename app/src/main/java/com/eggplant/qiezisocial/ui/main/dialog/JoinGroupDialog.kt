package com.eggplant.qiezisocial.ui.main.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import com.eggplant.qiezisocial.widget.image.CircleImageView
import java.util.*

/**
 * Created by Administrator on 2020/10/19.
 */

class JoinGroupDialog(context: Context, listenedItems: IntArray) : BaseDialog(context, R.layout.dialog_joingroup, listenedItems) {
    var bean: BoxEntry? = null
        private set
    private var content: QzTextView? = null
    private val imgs = ArrayList<ImageView>()
    private var numTv: QzTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.CENTER) // 此处可以设置dialog显示的位置为底部
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width // 设置dialog宽度为屏幕宽度
        getWindow()!!.attributes = lp
        content = findViewById(R.id.dlg_joingp_content)
        numTv = findViewById(R.id.dlg_joingp_numb)
        val img1 = findViewById<CircleImageView>(R.id.dlg_joingp_img1)
        val img2 = findViewById<CircleImageView>(R.id.dlg_joingp_img2)
        val img3 = findViewById<CircleImageView>(R.id.dlg_joingp_img3)
        val img4 = findViewById<CircleImageView>(R.id.dlg_joingp_img4)
        val img5 = findViewById<CircleImageView>(R.id.dlg_joingp_img5)
        imgs.add(img1)
        imgs.add(img2)
        imgs.add(img3)
        imgs.add(img4)
        imgs.add(img5)
        findViewById<View>(R.id.dlg_joingp_close).setOnClickListener { dismiss() }
    }

    override fun show() {
        super.show()
        refreshData()
    }

    fun setJoinData(bean: BoxEntry?) {
        this.bean = bean

    }

    private fun refreshData() {
        for (img in imgs) {
            img.visibility = View.GONE
        }
        content!!.text = bean!!.text
        numTv!!.text = bean!!.person.size.toString() + "人参与"
        val personinfor = bean!!.personinfor
        for (i in personinfor.indices) {
            if (i < 5) {
                val img = imgs[i]
                img.visibility = View.VISIBLE
                Glide.with(getContext()).load(API.PIC_PREFIX + personinfor[i].face).into(img)
            }
        }

    }
}
