package com.eggplant.qiezisocial.ui.extend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.NearByModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.adapter.NearbyAdapter
import com.eggplant.qiezisocial.ui.main.UserInfoActivity
import com.eggplant.qiezisocial.utils.LocationUtils
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_nearby.*


/**
 * Created by Administrator on 2020/12/16.
 */

class NearbyActivity : BaseActivity() {
    lateinit var adapter: NearbyAdapter
    lateinit var emptyView: TextView
    override fun getLayoutId(): Int {
        return R.layout.activity_nearby
    }

    override fun initView() {
        adapter = NearbyAdapter(null)
        emptyView = TextView(mContext)
        emptyView.text = "当前没有数据"
        emptyView.gravity = Gravity.CENTER
        emptyView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        adapter.emptyView = emptyView
        nearby_ry.layoutManager = LinearLayoutManager(mContext)
        nearby_ry.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initData() {
        getLocation()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initEvent() {
        nearby_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        emptyView.setOnClickListener {
            getLocation()
        }
        adapter.setOnItemClickListener { _, view, position ->
            val userEntry = adapter.data[position]
            startActivity(Intent(mContext,UserInfoActivity::class.java).putExtra("bean",userEntry))
        }
//        adapter.setOnLoadMoreListener({
//
//        }, nearby_ry)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun getLocation() {
        //检查定位权限
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        //判断
        if (permissions.size === 0) {
            //有权限，直接获取定位
            getLocationLL()

        } else {//没有权限，获取定位权限
            requestPermissions(permissions.toArray(arrayOfNulls<String>(permissions.size)), 2)
        }
    }

    private fun getLocationLL() {
        LocationUtils.getInstance(this).addressCallback = object : LocationUtils.AddressCallback {
            override fun onGetAddress(address: Address?) {

            }

            override fun onGetLocation(lat: Double, lng: Double) {
                application.infoBean!!.latitude="$lat"
                application.infoBean!!.longitude="$lng"
                NearByModel().getNearByInfo("$lng", "$lat", object : JsonCallback<BaseEntry<UserEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                        if (response!!.isSuccessful) {
                            adapter.setNewData(response!!.body().list)
                        }
                    }
                })
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
                    emptyView.text = "未获取经纬度，点击获取"
                }
            }

        }
    }
}
