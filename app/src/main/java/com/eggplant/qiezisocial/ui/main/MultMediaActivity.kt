package com.eggplant.qiezisocial.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.ui.main.adapter.MulMediaAdapter
import com.eggplant.qiezisocial.utils.DateUtils
import com.luck.picture.lib.photoview.PhotoView
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.xiao.nicevideoplayer.NiceVideoPlayer
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import kotlinx.android.synthetic.main.activity_multmedia.*
import kotlinx.android.synthetic.main.ap_media_video.view.*



/**
 * Created by Administrator on 2021/5/6.
 */

class MultMediaActivity : BaseActivity(), ViewTreeObserver.OnPreDrawListener, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {


    private var imageHeight: Int = 0
    private var imageWidth: Int = 0
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var imageViewHeight: Int = 0
    private var imageViewWidth: Int = 0
    private var imageViewX: Int = 0
    private var imageViewY: Int = 0
    private var currentPosition=0
    private val ANIMATE_DURATION = 300
    lateinit var adapter: MulMediaAdapter
    private var currentEntry: ChatMultiEntry<ChatEntry>? = null
    private val mTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            Log.i("emojitest", "onTextChanged s=$s  start:$start  befor:$before  count:$count")
            if (before >= 7 && count == 0) {
                if (mul_media_edit.filters != null && mul_media_edit.filters[0] is InputFilter.LengthFilter) {
                    var max = (mul_media_edit.filters[0] as InputFilter.LengthFilter).max
                    max -= before - 1
                    mul_media_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
                }
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun setSnapHelper() {
        val snapHelper = object : PagerSnapHelper() {
            // 在 Adapter的 onBindViewHolder 之后执行
            override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                //  找到对应的Index
//                Log.i("mulmedia: ", "---findTargetSnapPosition---")
                val targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
//                Log.i("mulmedia: ", "targetPos: " + targetPos)
                return targetPos
            }

            // 在 Adapter的 onBindViewHolder 之后执行
            override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
                //  找到对应的View
//                Log.i("mulmedia: ", "---findSnapView---")
                val view = super.findSnapView(layoutManager)
                //  第一次view为null ，所以需要配合addOnChildAttachStateChangeListener进行第一次的设置
                Log.i("mulmedia: ", "tag: " + view?.tag + "  view is empty:${view == null}")
                if (view != null) {
                    recyclerChildChanged(view)
                }
                return view
            }
        }
        snapHelper.attachToRecyclerView(mul_media_ry)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_multmedia
    }

    override fun initView() {
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        screenWidth = metric.widthPixels
        screenHeight = metric.heightPixels
        imageViewHeight = intent.getIntExtra("imageViewHeight", 0)
        imageViewWidth = intent.getIntExtra("imageViewWidth", 0)
        imageViewX = intent.getIntExtra("imageViewX", 0)
        imageViewY = intent.getIntExtra("imageViewY", 0)
        var data = intent.getSerializableExtra("data") as ArrayList<ChatMultiEntry<ChatEntry>>
        currentPosition=intent.getIntExtra("position",0)
        adapter = MulMediaAdapter(activity, data)
        adapter.firstPosition=currentPosition
        mul_media_ry.isNestedScrollingEnabled = false
        mul_media_ry.layoutManager = LinearLayoutManager(mContext)
        setSnapHelper()
        mul_media_ry.adapter = adapter
        mul_media_ry.scrollToPosition(currentPosition)
//        var snapHelpter = PagerSnapHelper()
//        snapHelpter.attachToRecyclerView(mul_media_ry)

        mul_media_keyboard.setEmojiContent(mul_media_edit)


    }

    override fun initData() {


    }

    override fun initEvent() {
        mul_media_edit.addTextChangedListener(mTextWatcher)
        adapter.addPreDrawListener = {
            mul_media_ry.viewTreeObserver.addOnPreDrawListener(this)
        }
        mul_media_ry.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View?) {
//                Log.i("mulmedia", "onChildViewAttachedToWindow ")
                //配合PagerSnapHelper进行第一次的设置，而后不再进行任何设置， 原因为每次滑动时，ry最新的一个child哪怕只划出一点，而不全部划出，也会调用此方法。
                if (adapter.primaryItem == null && adapter.primaryItemImageView == null)
                    recyclerChildChanged(view!!)
            }

            override fun onChildViewDetachedFromWindow(view: View?) {
//                Log.i("mulmedia", "onChildViewDetachedFromWindow tag:${view?.tag}  p:${view?.getTag(R.id.tag_meida)}")
            }
        })
        mul_media_ry.setRecyclerListener { holder ->
            if (holder is BaseViewHolder && holder.itemViewType == ChatMultiEntry.CHAT_OTHER_VIDEO) {
                val videoPlayer = holder.itemView.ap_media_player
                if (videoPlayer === NiceVideoPlayerManager.instance().currentNiceVideoPlayer) {
                    NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
                }
            }
        }
        mul_media_edit.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val txt = mul_media_edit.text.toString().trimEnd()
                if (!TextUtils.isEmpty(txt) && currentEntry != null) {
                    mul_media_keyboard.reset()
                    returnSendMsg(txt)
                }
            }
            false
        })
    }

    private fun returnSendMsg(txt: String) {
//        val itemType = currentEntry!!.itemType
        val bean = currentEntry!!.bean
        var intent = Intent()
        intent.putExtra("txt", txt)
        intent.putExtra("uid", bean.from)
        intent.putExtra("type", bean.type)
        intent.putExtra("content", bean.content)
        intent.putExtra("extra", bean.extra)
        intent.putExtra("id",bean.id)
        setResult(Activity.RESULT_OK, intent)
        mul_media_edit.setText("")
        finishActivityAnim()
    }

    private fun recyclerChildChanged(view: View) {
        adapter.primaryItem = view
        var tag = 0
        tag = (view!!.tag) as Int
        var currentPlayer = NiceVideoPlayerManager.instance().currentNiceVideoPlayer
        if (tag == ChatMultiEntry.CHAT_OTHER_VIDEO||tag == ChatMultiEntry.CHAT_MINE_VIDEO) {
            if (currentPlayer != null) {
                currentPlayer.start()
            }
            adapter.primaryItemImageView = (view.findViewById<NiceVideoPlayer>(R.id.ap_media_player) as NiceVideoPlayer).controller.imageView()
        } else if (tag == ChatMultiEntry.CHAT_OTHER||tag == ChatMultiEntry.CHAT_MINE) {
            if (currentPlayer != null) {
                currentPlayer.release()
            }
            adapter.primaryItemImageView = (view.findViewById(R.id.ap_media_pv))
            (adapter.primaryItemImageView as PhotoView).setOnPhotoTapListener { view, x, y ->
                onPhotoTap()
            }
            (adapter.primaryItemImageView as PhotoView).setOnViewTapListener  { view, x, y ->
                onPhotoTap()
            }
        } else {
            if (currentPlayer != null) {
                currentPlayer.release()
            }

        }
        val position = view.getTag(R.id.tag_meida)
        if (position != null) {
            var p = position as Int
            val bean = adapter.data[p]
            currentEntry = bean
            setInfo()
        }
//        Log.i("mulmedia", "recyclerChildChanged tag: $tag  position:$position")
    }

    private fun setInfo() {
        mul_media_keyboard.reset()
        mul_media_edit.setText("")
        if (currentEntry != null&&!activity.isDestroyed) {
            val bean = currentEntry!!.bean
            if (bean.from==application.infoBean!!.uid.toLong()){
                mul_media_edit_gp.visibility=View.GONE
            }else{
                mul_media_edit_gp.visibility=View.VISIBLE
            }

            Glide.with(mContext).load(API.PIC_PREFIX + bean!!.face).into(mul_media_head)
            mul_media_nick.text = bean.nick
            val txt = StringBuffer()
            if (!TextUtils.isEmpty(bean.birth) && bean.birth != "0") {
                txt.append(DateUtils.dataToAge(bean.birth))
                txt.append("·")
            }
            if (!TextUtils.isEmpty(bean.edu)) {
                txt.append(bean.edu)
                txt.append("·")
            }
            if (!TextUtils.isEmpty(bean.careers)) {
                txt.append(bean.careers)
                txt.append("·")
            }
            if (!TextUtils.isEmpty(bean.height)) {
                txt.append(bean.height)
                txt.append("cm")
                if (!TextUtils.isEmpty(bean.weight)) {
                    txt.append(bean.weight)
                    txt.append("kg·")
                } else {
                    txt.append("·")
                }
            }

            if (!TextUtils.isEmpty(bean.label)) {
                val s = bean.label.replace(" ".toRegex(), "·")
                txt.append(s)
            }
            mul_media_info.text = txt.toString()
        }
    }

    override fun onPreDraw(): Boolean {
        rootView.viewTreeObserver.removeOnPreDrawListener(this)
        val view = adapter.primaryItem
        val imageView = adapter.primaryItemImageView
        computeImageWidthAndHeight(imageView!!)
        val vx = imageViewWidth * 1.0f / imageWidth
        val vy = imageViewHeight * 1.0f / imageHeight
        val valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        valueAnimator.addUpdateListener { animation ->
            val duration = animation.duration
            val playTime = animation.currentPlayTime
            var fraction = if (duration > 0) playTime.toFloat() / duration else 1f
            if (fraction > 1) fraction = 1f
            view!!.translationX = evaluateInt(fraction, imageViewX + imageViewWidth / 2 - imageView.width / 2, 0).toFloat()
            view.translationY = evaluateInt(fraction, imageViewY + imageViewHeight / 2 - imageView.height / 2, 0).toFloat()
            view.scaleX = evaluateFloat(fraction, vx, 1)
            view.scaleY = evaluateFloat(fraction, vy, 1)
            view.alpha = fraction
            rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, Color.BLACK))
        }
        addIntoListener(valueAnimator)
        valueAnimator.duration = ANIMATE_DURATION.toLong()
        valueAnimator.start()


        return true
    }


    /**
     * 进场动画过程监听
     */
    private fun addIntoListener(valueAnimator: ValueAnimator) {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                rootView.setBackgroundColor(0x0)
            }

            override fun onAnimationEnd(animation: Animator) {
//                mul_media_bottom.visibility = View.VISIBLE
//                mul_media_edit_gp.visibility = View.VISIBLE
//                setInfo()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    /**
     * 退场动画过程监听
     */
    private fun addOutListener(valueAnimator: ValueAnimator) {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mul_media_bottom.visibility = View.GONE
                mul_media_edit_gp.visibility = View.GONE
                rootView.setBackgroundColor(0x0)
            }

            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    /**
     * activity的退场动画
     */
    fun finishActivityAnim() {
        val view = adapter.primaryItem
        val imageView = adapter.primaryItemImageView
        computeImageWidthAndHeight(imageView!!)


        val vx = imageViewWidth * 1.0f / imageWidth
        val vy = imageViewHeight * 1.0f / imageHeight
        val valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        valueAnimator.addUpdateListener { animation ->
            val duration = animation.duration
            val playTime = animation.currentPlayTime
            var fraction = if (duration > 0) playTime.toFloat() / duration else 1f
            if (fraction > 1) fraction = 1f
            view!!.translationX = evaluateInt(fraction, 0, imageViewX + imageViewWidth / 2 - imageView.width / 2).toFloat()
            view.translationY = evaluateInt(fraction, 0, imageViewY + imageViewHeight / 2 - imageView.height / 2).toFloat()
            view.scaleX = evaluateFloat(fraction, 1, vx)
            view.scaleY = evaluateFloat(fraction, 1, vy)
            view.alpha = 1 - fraction
            rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.TRANSPARENT))
        }
        addOutListener(valueAnimator)
        valueAnimator.duration = ANIMATE_DURATION.toLong()
        valueAnimator.start()
    }

    /**
     * 计算图片的宽高
     */
    private fun computeImageWidthAndHeight(imageView: ImageView) {

        // 获取真实大小
        val drawable = imageView.drawable
        if (drawable != null) {
            val intrinsicHeight = drawable.intrinsicHeight
            val intrinsicWidth = drawable.intrinsicWidth
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            var h = screenHeight * 1.0f / intrinsicHeight
            var w = screenWidth * 1.0f / intrinsicWidth
            if (h > w)
                h = w
            else
                w = h

            // 得出当宽高至少有一个充满的时候图片对应的宽高
            imageHeight = (intrinsicHeight * h).toInt()
            imageWidth = (intrinsicWidth * w).toInt()
        }
    }

    /**
     * Integer 估值器
     */
    fun evaluateInt(fraction: Float, startValue: Int?, endValue: Int?): Int {
        val startInt = startValue!!
        return (startInt + fraction * (endValue!! - startInt)).toInt()
    }

    /**
     * Float 估值器
     */
    fun evaluateFloat(fraction: Float, startValue: Number, endValue: Number): Float {
        val startFloat = startValue.toFloat()
        return startFloat + fraction * (endValue.toFloat() - startFloat)
    }

    /**
     * Argb 估值器
     */
    fun evaluateArgb(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return (startA + (fraction * (endA - startA)).toInt() shl 24
                or (startR + (fraction * (endR - startR)).toInt() shl 16//
                )
                or (startG + (fraction * (endG - startG)).toInt() shl 8//
                )
                or startB + (fraction * (endB - startB)).toInt())
    }

    override fun onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return
        Log.i("mulmedia", "show: ${mul_media_keyboard.isEmojiShow} ")
        if (mul_media_keyboard.isEmojiShow) {
            mul_media_keyboard.reset()
            return
        } else {
            returnSendMsg("")
        }
    }


    override fun onStop() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
        super.onStop()
    }

    fun onViewTap() {
        Log.i("mulmedia", "onViewTap: ${mul_media_keyboard.isEmojiShow} ")
        if (mul_media_keyboard.isEmojiShow) {
            mul_media_keyboard.reset()
        } else {
            returnSendMsg("")
        }
    }

    fun onPhotoTap() {
        Log.i("mulmedia", "onPhotoTap: ${mul_media_keyboard.isEmojiShow} ")
        if (mul_media_keyboard.isEmojiShow) {
            mul_media_keyboard.reset()
        } else {
            returnSendMsg("")
        }
    }

    override fun expressionClick(str: String?) {
        mul_media_keyboard.input(mul_media_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        mul_media_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        mul_media_keyboard.delete(mul_media_edit)
    }

    override fun onDestroy() {
        mul_media_edit.removeTextChangedListener(mTextWatcher)
        super.onDestroy()
    }
}
