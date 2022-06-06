package com.eggplant.qiezisocial.ui.main.adapter

import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.model.API
import kotlinx.android.synthetic.main.ap_fresh_select_object.view.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Administrator on 2021/2/2.
 */

class FreshSelectObjectAdapter(data: List<ScenesEntry>?) : BaseQuickAdapter<ScenesEntry, BaseViewHolder>(R.layout.ap_fresh_select_object, data) {

    private var imgs = ArrayList<Int>()
    //    private var txt = ArrayList<String>()
//    private var hint = ArrayList<String>()
    private var itembg = ArrayList<Int>()
    private var txtcolor = ArrayList<Int>()
    var mySelectPosition = ArrayList<Int>()
    var selectItem = ""
    var selectSid = ""
    var selecdtType = "0"
    var selectMoment = ""
    var selectPosition=-1
    var random: Random
    var angle: HashMap<Int, Float>
    var adapterType = ""


    init {
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
        txtcolor.add(R.color.white)
        txtcolor.add(R.color.white)
        txtcolor.add(R.color.tv_black)
        txtcolor.add(R.color.white)
        txtcolor.add(R.color.tv_black)
        txtcolor.add(R.color.white)
//        txt.clear()
//        hint.clear()
//        data?.forEach {
//            txt.add(it.title)
//            hint.add(it.des)
//        }
        itembg.add(R.drawable.filter_objitem_bg1)
        itembg.add(R.drawable.filter_objitem_bg2)
        itembg.add(R.drawable.filter_objitem_bg3)
        itembg.add(R.drawable.filter_objitem_bg4)
        itembg.add(R.drawable.filter_objitem_bg5)
        itembg.add(R.drawable.filter_objitem_bg6)
        itembg.add(R.drawable.filter_objitem_selct_bg1)
        itembg.add(R.drawable.filter_objitem_selct_bg2)
        itembg.add(R.drawable.filter_objitem_selct_bg3)
        itembg.add(R.drawable.filter_objitem_selct_bg4)
        itembg.add(R.drawable.filter_objitem_selct_bg5)
        itembg.add(R.drawable.filter_objitem_selct_bg6)
        random = Random()
        angle = HashMap()
    }

    override fun convert(helper: BaseViewHolder, entry: ScenesEntry) {
        if (entry.sid == null || entry.title == null) {
            helper.itemView.ap_f_selectobj_txt.visibility = View.GONE
            helper.itemView.ap_f_selectobj_hint.visibility = View.GONE
            helper.itemView.ap_f_selectobj_txt2.visibility = View.VISIBLE
            helper.itemView.ap_f_selectobj_hint2.visibility = View.VISIBLE
            helper.itemView.ap_f_selectobj_img.visibility = View.GONE
            helper.itemView.ap_f_selectobj_check.visibility = View.GONE
            helper.itemView.ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext, R.drawable.icon_select_myscene)
            helper.itemView.ap_f_selectobj_bg.setOnClickListener {
                selectMyscenenListener?.invoke(it)
            }
            return
        }
        helper.itemView.ap_f_selectobj_txt.visibility = View.VISIBLE
        helper.itemView.ap_f_selectobj_hint.visibility = View.VISIBLE
        helper.itemView.ap_f_selectobj_txt2.visibility = View.GONE
        helper.itemView.ap_f_selectobj_hint2.visibility = View.GONE
        helper.itemView.ap_f_selectobj_check.visibility = View.GONE
        val item = entry.title
        val hint = entry.des
        val stat = entry.stat
        //好友自建
        if (adapterType == "friendScenes") {
            if (entry.userinfor != null) {
                helper.itemView.ap_f_selectobj_user.visibility = View.VISIBLE
                helper.itemView.ap_f_selectobj_nick.text = entry.userinfor.nick
                Glide.with(mContext).load(API.PIC_PREFIX + entry.userinfor.face).into(helper.itemView.ap_f_selectobj_head)
            } else {
                helper.itemView.ap_f_selectobj_user.visibility = View.GONE
            }
        } else {
            //我的自建场景
            if (stat != "审核通过"&&stat!=null) {
                helper.itemView.ap_f_selectobj_check.visibility = View.VISIBLE
                helper.itemView.ap_f_selectobj_check.text=entry.stat
                helper.itemView.ap_f_selectobj_txt.alpha = 0.2f
                helper.itemView.ap_f_selectobj_hint.alpha = 0.2f
                helper.itemView.ap_f_selectobj_img.alpha = 0.2f
            }else{
                helper.itemView.ap_f_selectobj_check.visibility = View.GONE
                helper.itemView.ap_f_selectobj_txt.alpha = 1.0f
                helper.itemView.ap_f_selectobj_hint.alpha = 1.0f
                helper.itemView.ap_f_selectobj_img.alpha = 1.0f
            }
        }



        helper.itemView.ap_f_selectobj_lock.visibility = View.GONE
        if (entry.userinfor?.uid==QzApplication.get().loginEntry?.userinfor?.uid){


        }else {
            if (entry.code != null&&entry.code.isNotEmpty()) {
                helper.itemView.ap_f_selectobj_lock.visibility = View.VISIBLE
            } else {
                helper.itemView.ap_f_selectobj_lock.visibility = View.GONE
            }
        }




        if (hint == null || hint.isEmpty()) {
            helper.itemView.ap_f_selectobj_hint.visibility = View.GONE
        } else {
            helper.itemView.ap_f_selectobj_hint.visibility = View.VISIBLE
        }
        helper.itemView.ap_f_selectobj_txt.text = item
        helper.itemView.ap_f_selectobj_hint.text = hint
//        helper.itemView.ap_f_selectobj_txt.setTextColor(ContextCompat.getColor(mContext, txtcolor[helper.adapterPosition % 6]))
//        helper.itemView.ap_f_selectobj_hint.setTextColor(ContextCompat.getColor(mContext, txtcolor[helper.adapterPosition % 6]))
        if (entry.background != null && entry.background.isNotEmpty()) {
            helper.itemView.ap_f_selectobj_img.visibility = View.VISIBLE
            Glide.with(mContext).load(entry.background).into(helper.itemView.ap_f_selectobj_img)
        } else if (entry.pic != null) {
            helper.itemView.ap_f_selectobj_img.visibility = View.VISIBLE
            val pic = entry.pic.toInt()
            if (pic < imgs.size) {
                helper.itemView.ap_f_selectobj_img.setImageResource(imgs[pic])
            } else {
                helper.itemView.ap_f_selectobj_img.setImageDrawable(null)
            }
        }


        if (selectItem == item) {
            selectPosition=helper.adapterPosition
//            helper.itemView.ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext, itembg[helper.adapterPosition % 6 + 6])
            helper.itemView.ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext, R.drawable.filter_objitem_selct_bg7)
        } else {
//            helper.itemView.ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext, itembg[helper.adapterPosition % 6])
            helper.itemView.ap_f_selectobj_bg.background = ContextCompat.getDrawable(mContext, R.drawable.filter_objitem_bg7)
        }
        helper.itemView.ap_f_selectobj_bg.setOnClickListener {
            if (stat != "审核通过"&&stat!=null) {
                checkSceneClickListener?.invoke(it, helper.adapterPosition)
                return@setOnClickListener
            }
            val lastPosition=selectPosition
            if (selectItem == item) {
                selectPosition=helper.adapterPosition
                sceneClickListener?.invoke(it,helper.adapterPosition)
            } else {
                selectItem = item
                selectSid = entry.sid
                selecdtType = entry.type
                selectMoment = entry.moment
                selectPosition=helper.adapterPosition
//                notifyDataSetChanged()
                primaryScenesListener?.invoke(it)
                notifyItemChanged(lastPosition)
                notifyItemChanged(helper.adapterPosition)

            }
        }

//        if (mySelectPosition.contains(helper.adapterPosition)) {
//            val alphaAnim = ObjectAnimator.ofFloat(helper.itemView.ap_f_selectobj_bg, "alpha", 1F, 0.5F, 1F)
//            alphaAnim.duration = 1000
//            alphaAnim.repeatCount = 1
//            alphaAnim.start()
//            mySelectPosition.remove(helper.adapterPosition)
//        }
//        val adapterPosition = helper.adapterPosition
//        if (angle.containsKey(adapterPosition)){
//            helper.itemView.rotation= angle[adapterPosition]!!
//        }else {
//            val fl = 5.0f - random.nextInt(10)
//            helper.itemView.rotation =fl
//            angle.put(adapterPosition,fl)
//        }
    }


    fun setSingleItem(select: String, sid: String, type: String) {
        selectItem = select
        selectSid = sid
        selecdtType = type
    }

    var selectMyscenenListener: ((View) -> Unit)? = null
    var checkSceneClickListener: ((View, Int) -> Unit)? = null
    var sceneClickListener:((View, Int) -> Unit)? = null
    var primaryScenesListener:((View)->Unit)?=null
//    override fun setNewData(data: MutableList<ScenesEntry>?) {
//        txt.clear()
//        hint.clear()
//        data?.forEach {
//            txt.add(it.title)
//            hint.add(it.des)
//        }
//        super.setNewData(data)
//
//    }
//    fun addSelectItem(select: String) {
//        addSelectItem.add(select)
//    }
}