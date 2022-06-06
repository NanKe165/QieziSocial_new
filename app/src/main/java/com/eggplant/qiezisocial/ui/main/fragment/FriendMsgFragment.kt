package com.eggplant.qiezisocial.ui.main.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.FriendMsgContract
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.presenter.FriendMsgPresenter
import com.eggplant.qiezisocial.socket.event.FriendListEvent
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.VerifyFriendActivity
import com.eggplant.qiezisocial.ui.main.adapter.FriendMsgAdapter
import com.eggplant.qiezisocial.ui.main.dialog.NormalDialog
import com.eggplant.qiezisocial.utils.LocationUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import kotlinx.android.synthetic.main.dialog_delete.view.*
import kotlinx.android.synthetic.main.ft_friend_msg.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2021/7/5.
 */

class FriendMsgFragment : BaseMvpFragment<FriendMsgPresenter>(), FriendMsgContract.View {
    lateinit var adapter: FriendMsgAdapter
    lateinit var dialog: NormalDialog
    var popWindow: BasePopupWindow? = null
    var deleteUser: MainInfoBean? = null
    val REQUEST_VERIFY_FRIEND = 101
//    private lateinit var headView: View
    private lateinit var emptyView: View
    private var touchX=0f
    private var displayWidth=0
    companion object {

        private var fragment: FriendMsgFragment? = null
            get() {
                if (field == null)
                    field = FriendMsgFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): FriendMsgFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
        fun newInstance(bundle: Bundle?): FriendMsgFragment {
            val fragment=FriendMsgFragment()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment

        }
    }

    override fun initPresenter(): FriendMsgPresenter {
        return FriendMsgPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.ft_friend_msg
    }

    override fun initView() {
        mPresenter.attachView(this)
        initPopWindow()
        initDialog()
        displayWidth=ScreenUtil.getDisplayWidthPixels(mContext)
        adapter = FriendMsgAdapter(null)
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_newfriend, null, false)
//        headView = LayoutInflater.from(mContext).inflate(R.layout.fmsg_head, null, false)
//        headView.fmsg_getloca.setOnClickListener {
//            getLocation()
//        }
//        headView.fmsg_refresh.setOnClickListener { v ->
//            var anim = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//            anim.duration = 300
//            anim.fillAfter = true
//            v.startAnimation(anim)
//            getLocation()
//        }
//        adapter.setHeaderView(headView)
        ft_fmsg_ry.layoutManager = LinearLayoutManager(mContext)
        ft_fmsg_ry.adapter = adapter
        mPresenter.from = arguments?.getString("from")


    }


    override fun initEvent() {
        adapter.setOnItemClickListener { _, view, position ->
            val mainInfoBean = adapter.data[position]
            if (mainInfoBean.type == "gfriendlist") {
                startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", mainInfoBean))
            } else if (mainInfoBean.type == "gapplylist") {
                startActivityForResult(Intent(mContext, VerifyFriendActivity::class.java).putExtra("bean", mainInfoBean), REQUEST_VERIFY_FRIEND)
            }
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            val bean = adapter.data[position]
            val entry = bean.convertUserEntry()
            if (bean.type == "gfriendlist") {
                entry.friend = "yes"
            } else if (bean.type == "gapplylist") {
                entry.friend = "no"
            }
            startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", entry))
            activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        adapter.setOnItemLongClickListener { _, view, position ->
            var data = adapter.data[position]
            if (data.type != "gapplylist") {
                var loca = IntArray(2)
                view.getLocationOnScreen(loca)
                deleteUser = data
                var x=displayWidth/2
                if (touchX>displayWidth/2){
                    x=(displayWidth/5)
                }
                popWindow?.showAtLocation(view, Gravity.NO_GRAVITY, x, loca[1] + view.height / 2)
            }
            true
        }
        ft_fmsg_shadown.setOnTouchListener { view, motionEvent ->
            Log.i("ceshi","x:${motionEvent.action}")
            if (motionEvent.action==MotionEvent.ACTION_DOWN)
                touchX=motionEvent.x
            false
        }
    }

    override fun initData() {

    }

    override fun setData(arrayList: ArrayList<MainInfoBean>) {
        adapter.setNewData(arrayList)
        if (arrayList.isEmpty()) {
            adapter.emptyView = emptyView
        }
    }

    override fun setNearUser(list: List<UserEntry>) {
//        resetHeadView()
//        list.forEachIndexed { index, userEntry ->
//            setHeadInfo(index, userEntry)
//        }
    }

//    private fun setHeadInfo(index: Int, user: UserEntry) {
//        if (index == 0) {
//            headView.fmsg_view1.visibility = View.VISIBLE
//            headView.fmsg_nick1.text = user.nick
////            if (user.birth.isNotEmpty()) {
////                headView.fmsg_birth1.visibility = View.VISIBLE
////                headView.fmsg_birth1.text = "${DateUtils.dataToAge(user.birth)}岁"
////            } else {
////                headView.fmsg_birth1.visibility = View.GONE
////            }
//            if (user.careers.isNotEmpty()) {
//                headView.fmsg_label1.visibility = View.VISIBLE
//                headView.fmsg_label1.text = "${user.careers}"
//            } else {
//                headView.fmsg_label1.visibility = View.GONE
//            }
//            val distance = LocationUtils.getDistance(user.latitude.toDouble(), user.longitude.toDouble(),
//                    application.infoBean!!.latitude.toDouble(), application.infoBean!!.longitude.toDouble()).toInt()
//            if (distance < 1000) {
//                headView.fmsg_loca1.text = "${distance}米以内"
//            } else {
//                headView.fmsg_loca1.text = "${distance / 1000}公里以内"
//            }
//            Glide.with(mContext!!).load(API.PIC_PREFIX + user.face).into(headView.fmsg_head1)
//            headView.fmsg_view1.setOnClickListener {
//                startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", user))
//                activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
//            }
//        } else if (index == 1) {
//            headView.fmsg_line.visibility = View.VISIBLE
//            headView.fmsg_view2.visibility = View.VISIBLE
//            headView.fmsg_nick2.text = user.nick
////            if (user.birth.isNotEmpty()) {
////                headView.fmsg_birth2.visibility = View.VISIBLE
////                headView.fmsg_birth2.text = "${DateUtils.dataToAge(user.birth)}岁"
////            } else {
////                headView.fmsg_birth2.visibility = View.GONE
////            }
//            if (user.careers.isNotEmpty()) {
//                headView.fmsg_label2.visibility = View.VISIBLE
//                headView.fmsg_label2.text = "${user.careers}"
//            } else {
//                headView.fmsg_label2.visibility = View.GONE
//            }
//            val distance = LocationUtils.getDistance(user.latitude.toDouble(), user.longitude.toDouble(),
//                    application.infoBean!!.latitude.toDouble(), application.infoBean!!.longitude.toDouble()).toInt()
//            if (distance < 1000) {
//                headView.fmsg_loca2.text = "${distance}米以内"
//            } else {
//                headView.fmsg_loca2.text = "${distance / 1000}公里以内"
//            }
//            Glide.with(mContext!!).load(API.PIC_PREFIX + user.face).into(headView.fmsg_head2)
//            headView.fmsg_view2.setOnClickListener {
//                startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", user))
//                activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
//            }
//        }
//    }
//
//    private fun resetHeadView() {
//        headView.fmsg_getloca_gp.visibility = View.GONE
//        headView.fmsg_view1.visibility = View.GONE
//        headView.fmsg_view2.visibility = View.GONE
//        headView.fmsg_line.visibility = View.GONE
//    }

    override fun onResume() {
        super.onResume()
        mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VERIFY_FRIEND && resultCode == RESULT_OK) {
            mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMsg(event: NewMsgEvent) {
        (activity as MainActivity).hasNewMsg(true)
        mPresenter.setData(MainDBManager.getInstance(context).queryMainUserList())
    }

    var lastRefreshTime = 0L
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFriendList(event: FriendListEvent) {
//        val currentTimeMillis = System.currentTimeMillis()
//        if (currentTimeMillis - lastRefreshTime > 1000) {
        val newData = MainDBManager.getInstance(mContext).queryMainUserList()
        mPresenter.setData(newData)
//
//        }
//        if (TextUtils.equals(event.type, "gfriendlist") || TextUtils.equals(event.type, "gapplylist")) {
//            lastRefreshTime = currentTimeMillis
//        }
    }

    var first = true
    fun getLocaOnec() {
        if (first)
            getLocation()
        first = false
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getLocation() {
        //检查定位权限
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        Log.i("fmsgFt", "getLocation---------${permissions.size}")
        //判断
        if (permissions.size === 0) {//有权限，直接获取定位
            getLocationLL()
        } else {//没有权限，获取定位权限
            requestPermissions(permissions.toArray(arrayOfNulls<String>(permissions.size)), 2)
        }
    }

    private fun getLocationLL() {
        val loca = LocationUtils.getLoca(activity)
        if (loca != null) {
            loca.latitude
            application.infoBean!!.latitude = "${loca.latitude}"
            application.infoBean!!.longitude = "${loca.longitude}"
            mPresenter.getNearbyInfo(mContext!!, loca.latitude, loca.longitude)
//            Log.i("fmsgFt", "getLocationLL---------la:${loca.latitude}   lon:${loca.longitude}")
        } else {
            val lng = application.infoBean!!.longitude
            val lat = application.infoBean!!.latitude
            if (lng.isNotEmpty() && lat.isNotEmpty() && lng != "0" && lat != "0") {
                mPresenter.getNearbyInfo(mContext!!, lat.toDouble(), lng = lng.toDouble())
            } else {
                LocationUtils.getInstance(mContext).addressCallback = object : LocationUtils.AddressCallback {
                    override fun onGetAddress(address: Address?) {

                    }

                    override fun onGetLocation(lat: Double, lng: Double) {
                        Log.i("fmsgFt", "getLocationLL---------")
                        application.infoBean!!.latitude = "$lat"
                        application.infoBean!!.longitude = "$lng"
//                NearByModel().setLocaltion("$lng", "$lat")
                        mPresenter.getNearbyInfo(mContext!!, lat, lng)
                    }

                }
            }
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationLL()
                } else {
//                     Toast.makeText(this, "未同意获取定位权限", Toast.LENGTH_SHORT).show()
                    Log.i("get_location", "getlocation error")
//                    headView.fmsg_getloca_gp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initDialog() {
        dialog = NormalDialog(mContext!!, intArrayOf(R.id.dlg_normal_cancel, R.id.dlg_normal_delete))
        dialog.setOnBaseDialogItemClickListener { _, view ->
            if (view.id == R.id.dlg_normal_cancel) {
                dialog.dismiss()
            } else if (view.id == R.id.dlg_normal_delete) {
                if (dialog.mode == 1 || dialog.mode == 3) {
                    deleteUser?.let {
                        mPresenter.deleteUser(mContext!!, it)
                    }
                    deleteUser = null
                    dialog.mode = 0
                } else if (dialog.mode == 2) {
                    deleteUser?.let {
                        mPresenter.deleteChat(mContext!!, it)
                    }
                    deleteUser = null
                    dialog.mode = 0
                } else if (dialog.mode == 3) {
                    deleteUser?.let {
                        mPresenter.addBlacklist(mContext!!, it)
                    }
                    deleteUser = null
                    dialog.mode = 0
                }
                dialog.dismiss()
            }
        }
    }


    private fun initPopWindow() {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete, null, false)
        popWindow = BasePopupWindow(mContext)
        popWindow?.contentView = popView
        popWindow?.showAnimMode = 1
        popView.dlg_delte_record.setOnClickListener {
            dialog.mode = 2
            dialog.show()
            popWindow?.dismiss()
        }
        popView.dlg_delte_friend.setOnClickListener {
            dialog.mode = 1
            dialog.show()
            popWindow?.dismiss()
        }
        popView.dlg_delte_block.setOnClickListener {
            dialog.mode = 3
            dialog.show()
            popWindow?.dismiss()
        }
    }

}
