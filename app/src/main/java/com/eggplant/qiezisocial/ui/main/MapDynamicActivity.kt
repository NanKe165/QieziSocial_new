package com.eggplant.qiezisocial.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.ScaleAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.MapContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.MapDynaimcPresenter
import com.eggplant.qiezisocial.ui.main.adapter.MapInfoWindowAdapter
import com.eggplant.qiezisocial.utils.ClickUtil
import com.eggplant.qiezisocial.utils.GlideUtils
import com.eggplant.qiezisocial.utils.LocationUtils
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.luck.picture.lib.PictureSelector
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.map_head.view.*
import java.util.*
import kotlin.concurrent.timerTask


/**
 * Created by Administrator on 2021/10/19.
 */

class MapDynamicActivity : BaseMvpActivity<MapDynaimcPresenter>(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener, MapContract.View {

    override fun initPresenter(): MapDynaimcPresenter {
        return MapDynaimcPresenter()
    }

    override fun expressionClick(str: String?) {
        mdy_keyboard.input(mdy_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        mdy_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        mdy_keyboard.delete(mdy_edit)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mdy_map.onSaveInstanceState(outState)
    }

    private lateinit var progressDialog: QzProgressDialog
    var aMap: AMap? = null
    private var textWatch: TextWatcher? = null
    private val REQUEST_PHOTO_ALBUM = 110
    private var pubMediaPath = ArrayList<String>()
    private var pubMediaType = ""
    private lateinit var myLoca: Location
    private lateinit var adapter: MapInfoWindowAdapter
    private var sid = "0"
    private var goal = ""
    private lateinit var markers: ArrayList<Marker>
    var timer: Timer? = null
    private var markerIndex = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_map
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mdy_map.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = mdy_map.map
        }
        markers = ArrayList()

        val myLocationStyle = MyLocationStyle()
        //??????????????????????????????myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1???????????????????????????myLocationType????????????????????????????????????
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(resources, R.mipmap.icon_loca)))

        aMap!!.uiSettings.isZoomControlsEnabled = false//??????????????????
        aMap!!.myLocationStyle = myLocationStyle//?????????????????????Style
        aMap!!.uiSettings.isMyLocationButtonEnabled = false //?????????????????????????????????????????????????????????
        aMap!!.isMyLocationEnabled = true// ?????????true?????????????????????????????????false???????????????????????????????????????????????????
        aMap!!.uiSettings.isTiltGesturesEnabled = false//??????????????????????????????
        aMap!!.uiSettings.isRotateGesturesEnabled = false    //??????????????????????????????

        //?????????????????????????????? ?????????????????????[3, 20],???????????????????????????????????? ???
        aMap!!.maxZoomLevel = 16f
        // ???????????????????????? ?????????????????????[3, 20],????????????????????????????????????
        aMap!!.minZoomLevel = 5f

        //???????????????????????????????????????1????????????????????????????????????
        aMap!!.scalePerPixel
//        aMap!!.setLocationSource(this)
        //??????????????????
        aMap!!.moveCamera(CameraUpdateFactory.zoomTo(12f))
        adapter = MapInfoWindowAdapter(mContext)
        aMap!!.setInfoWindowAdapter(adapter)
//        val appearAnimation= AlphaAnimation(0f,1f)
//        val disappearAnimation= AlphaAnimation(0f,1f)
//        appearAnimation.setDuration(1000L)
//        disappearAnimation.setDuration(1000L)
//       aMap!!.infoWindowAnimationManager.setInfoWindowAppearAnimation(appearAnimation)
//        aMap!!.infoWindowAnimationManager.setInfoWindowDisappearAnimation(disappearAnimation)
//        aMap!!.infoWindowAnimationManager.setInfoWindowAnimation(appearAnimation,object :Animation.AnimationListener{
//            override fun onAnimationEnd() {
//
//            }
//
//            override fun onAnimationStart() {
//
//            }
//        })
        // ?????? Marker ??????????????????
        val markerClickListener = AMap.// marker ?????????????????????????????????
                // ?????? true ?????????????????????????????????????????????false
                OnMarkerClickListener { m ->
                    if (adapter.list != null) {
                        if (ClickUtil.isNotFastClick())
                            adapter.list!!.indices
                                    .filter { adapter.list!![it].id.toString() + "" == m.title }
                                    .forEach {
                                        startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", adapter.list!![it].userinfor))
                                        overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                                    }

                    }
                    true
                }
        // ?????? Marker ???????????????
        aMap!!.setOnMarkerClickListener(markerClickListener)
        aMap!!.setOnMyLocationChangeListener { l ->
            myLoca = l
            mdy_pub?.postDelayed({
                getData(myLoca, 0)
            }, 1000)

        }
        aMap!!.setOnMapClickListener { l ->
            mdy_keyboard.reset()
        }
    }


    override fun initView() {
        mPresenter.attachView(this)
        mdy_keyboard.setEmojiContent(mdy_edit)
        sid = intent.getStringExtra("sid")
        goal = intent.getStringExtra("goal")
        if (sid.isEmpty())
            sid = "217"
        initDialog()
        textWatch = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reSetMediaView()
            }
        }
    }

    private fun initDialog() {
        progressDialog = QzProgressDialog(mContext)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("????????????...")

    }


    override fun initData() {

    }

    override fun initEvent() {
        mdy_edit.addTextChangedListener(textWatch)
        mdy_rootView.setOnClickListener {
            mdy_keyboard.reset()
            finish()
        }

        mdy_close.setOnClickListener {
            mdy_keyboard.reset()
            finish()
        }
        mdy_pub.setOnClickListener {
            val txt = mdy_edit.text?.toString()?.trimEnd()
            if ((txt != null && txt.isNotEmpty()) || pubMediaPath.isNotEmpty()) {
                mPresenter.readyTopub(activity, goal, sid, txt, pubMediaPath, pubMediaType, myLoca)
                mdy_edit.setText("")
                pubMediaPath.clear()
                pubMediaType = ""
                reSetMediaView()
                mdy_keyboard.reset()
            }
        }
        mdy_media_delete.setOnClickListener {
            pubMediaPath.clear()
            pubMediaType = ""
            reSetMediaView()
        }
        mdy_sel_meida.setOnClickListener {
            mPresenter.openGallery(activity, REQUEST_PHOTO_ALBUM)
        }
//        mdy_sel_meida.setOnLongClickListener {
//            mPresenter.openGalleryVideo(activity, REQUEST_PHOTO_ALBUM)
//            true
//        }
    }

    private fun getData(myLoca: Location, begin: Int) {
        mPresenter.getData(begin, myLoca.longitude, myLoca.latitude, sid = sid, s = goal)
    }

    override fun setNewData(list: List<BoxEntry>) {
        var maxDistance = 0.0
        adapter.setList(list)
        var time = 0L
        list.forEachIndexed { index, it ->

            var dis = LocationUtils.getDistance(it.lat.toDouble(), it.lng.toDouble(), myLoca.latitude, myLoca.longitude)
            if (dis > maxDistance) {
                maxDistance = dis
            }
            mdy_close.postDelayed({ addMarker(it, index == list.size - 1) }, time)
            time += 400

        }
        val zoom = (maxDistance / (700 * 10)).toFloat()
        Log.i("mapDyna", "maxdis:$maxDistance  zoom:$zoom")
        //??????????????????
        aMap!!.animateCamera(CameraUpdateFactory.zoomTo(13 - zoom), 1000L, object : AMap.CancelableCallback {
            override fun onFinish() {

            }

            override fun onCancel() {

            }
        })


    }

    private fun startShowInfo() {
        Log.i("mapDy", "startShowInfo ${markers.size}")
        if (markers.isNotEmpty()) {
            var task = timerTask {
                if (markers.isNotEmpty()) {
                    Log.i("mapDy", "startShowInfo size:${markers.size} ${System.currentTimeMillis()}   index:$markerIndex")
                    if (markerIndex - 1 >= 0) {
                        markers[markerIndex - 1].hideInfoWindow()
                    }
                    if (markerIndex >= markers.size) {
                        markerIndex = 0
                    }
                    markers[markerIndex].showInfoWindow()
                    markerIndex++
                }
                Thread.sleep(5000)
            }
            if (timer == null) {
                timer = Timer()
                timer!!.schedule(task, 0, 1 * 1000)
            }
        }
    }

    fun addMarker(entry: BoxEntry, showinfo: Boolean) {
        addMarker(entry, showinfo, false)
    }

    fun addMarker(entry: BoxEntry, showinfo: Boolean, dismiss: Boolean) {
        val latLng = LatLng(entry.lat.toDouble(), entry.lng.toDouble())
        val markerOption = MarkerOptions()
        markerOption.position(latLng)
        markerOption.title("${entry.id}")
        markerOption.draggable(false)//??????Marker????????????
        val view = LayoutInflater.from(mContext).inflate(R.layout.map_head, null, false)
        val headView = view.map_marker_head
        if (activity.isDestroyed)
            return
        Glide.with(activity).asBitmap().load(API.PIC_PREFIX + entry.userinfor.face).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                headView.setImageBitmap(resource)
                val bf = BitmapDescriptorFactory.fromView(view)
                markerOption.icon(bf)
                // ???Marker????????????????????????????????????????????????????????????
                markerOption.isFlat = true//??????marker??????????????????
                val marker = aMap!!.addMarker(markerOption)
                if (!dismiss)
                    markers.add(marker)
                val animation = ScaleAnimation(0f, 1f, 0f, 1f)
                val duration = 800L
                animation.setDuration(duration)
                animation.setInterpolator(LinearInterpolator())
                marker.setAnimation(animation)
                marker.startAnimation()
                marker.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd() {
                        if (showinfo)
                            startShowInfo()
                        if (dismiss) {
                            mdy_close?.postDelayed({
                                marker.remove()
                            }, 1500)
                        }
                    }

                    override fun onAnimationStart() {

                    }
                })

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PHOTO_ALBUM -> {
                    // ??????????????????????????????????????????
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    // ?????? LocalMedia ??????????????????path
                    // 1.media.getPath(); ?????????path
                    // 2.media.getCutPath();????????????path????????????media.isCut();?????????true  ????????????????????????
                    // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true  ????????????????????????
                    // ????????????????????????????????????????????????????????????????????????????????????
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    imgs.forEach {
                        var path = it.androidQToPath
                        if (path == null || path.isEmpty()) {
                            path = it.path
                        }
                        when {
                            it.mimeType.contains("video/") -> {
                                Log.i("selectFile", " file is video")
                                pubMediaType = "video"
                                pubMediaPath.add(path)
                            }
                            it.mimeType.contains("image/") -> {
                                pubMediaType = "image"
                                pubMediaPath.add(if (it.isCompressed) {
                                    it.compressPath
                                } else {
                                    path
                                })
                            }
                            else -> {
                                Log.i("selectFile", " file is other  ${it.mimeType}")
                                pubMediaType = ""
                                pubMediaPath.clear()
                            }
                        }
                    }
                    reSetMediaView()

                }

            }
        }
    }

    private fun reSetMediaView() {
        mdy_media_play.visibility = View.GONE
        mdy_media_delete.visibility = View.GONE
//        dy_media_img.background=null
        if (pubMediaType == "") {
            Glide.with(mContext).load(R.mipmap.icon_dynamic_upload).into(mdy_media_img)
//            dy_media_img.setImageResource(R.mipmap.icon_dynamic_upload)
        } else if (pubMediaType == "video") {
            mdy_media_delete.visibility = View.VISIBLE
            mdy_media_play.visibility = View.VISIBLE
            GlideUtils.loadVideoScreenshot(mContext, pubMediaPath[0], mdy_media_img, 1)
        } else if (pubMediaType == "image") {
            mdy_media_delete.visibility = View.VISIBLE
            Glide.with(mContext).load(pubMediaPath[0]).into(mdy_media_img)
        }
        checkPubState()
    }

    private fun checkPubState() {
        val content = mdy_edit.text?.toString()?.trimEnd()
        if ((content != null && content.isNotEmpty()) || pubMediaPath.isNotEmpty()) {
            mdy_pub.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black5))
            mdy_pub.background = ContextCompat.getDrawable(mContext, R.drawable.dy_pub_selc_bg)
            mdy_pub.isClickable = true
        } else {
            mdy_pub.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray2))
            mdy_pub.background = ContextCompat.getDrawable(mContext, R.drawable.dy_pub_uselc_bg)
            mdy_pub.isClickable = false
        }
    }

    override fun showCompressView(s: String) {
        progressDialog.setMessage(s)
        progressDialog.show()
    }

    override fun hideCompressView() {
        progressDialog.dismiss()
    }

    override fun showUploadView(s: String) {
        progressDialog.setMessage(s)
        progressDialog.show()
    }

    override fun addPubData(record: BoxEntry) {
        addMarker(record, false, true)
    }

    override fun onPause() {
        super.onPause()
        mdy_map.onPause()
    }

    override fun onResume() {
        super.onResume()
        mdy_map.onResume()
    }

    override fun onDestroy() {
        mdy_edit.removeTextChangedListener(textWatch)
        if (timer != null)
            timer!!.cancel()
        timer = null
        super.onDestroy()
        mdy_map.onDestroy()
    }

    override fun finish() {
        super.finish()
        mdy_shadown.visibility = View.VISIBLE
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}
