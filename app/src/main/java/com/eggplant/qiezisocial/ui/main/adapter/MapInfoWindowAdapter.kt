package com.eggplant.qiezisocial.ui.main.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import com.amap.api.maps.AMap
import com.amap.api.maps.model.Marker
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import kotlinx.android.synthetic.main.custom_info_window.view.*

/**
 * Created by Administrator on 2021/10/19.
 */

class MapInfoWindowAdapter(internal var mContext: Context) : AMap.InfoWindowAdapter {
    internal var infoWindow: View? = null
    internal var list: List<BoxEntry>? = null
    internal var manager: RecyclerView.LayoutManager? = null
    internal var imgAdapter: MapImgAdapter? = null


    override fun getInfoWindow(marker: Marker): View {
        if (infoWindow == null) {
            infoWindow = LayoutInflater.from(mContext).inflate(
                    R.layout.custom_info_window, null)
        }
        render(marker, infoWindow!!)
        return infoWindow!!
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    /**
     * 自定义infowinfow窗口
     */
    fun render(marker: Marker, view: View) {
        val title = view.findViewById<QzTextView>(R.id.map_window_title)
        val ry = view.findViewById<RecyclerView>(R.id.map_window_ry)
        view.map_window_bubble.isClickable=true
        val vimg = view.map_window_vimg
        val vimgGp = view.map_window_vimg_gp
        val lineView = view.map_window_line
        if (manager == null) {
            manager = LinearLayoutManager(mContext, HORIZONTAL, false)
            ry.layoutManager = manager
        }
        if (imgAdapter == null) {
            imgAdapter = MapImgAdapter(null)
            imgAdapter!!.setOnItemClickListener { adapter, view, position ->
//                var source=imgAdapter!!.data[position].org
//                PrevUtils.onImageItemClick(mContext,view,API.PIC_PREFIX+source,API.PIC_PREFIX+source)
                var imginfo = ArrayList<ImageInfo>()
                imgAdapter!!.data.forEach {
                    var info = ImageInfo()
                    info.bigImageUrl = API.PIC_PREFIX + it.org
                    info.thumbnailUrl = API.PIC_PREFIX + it.extra
                    imginfo.add(info)
                }
                PrevUtils.onImageItemClick(mContext, view, position, imginfo)
            }
            ry.adapter = imgAdapter
        }
        var entry: BoxEntry? = null
        if (list != null) {
            list!!.indices
                    .filter { list!![it].id.toString() + "" == marker.title }
                    .forEach { entry = list!![it] }
        }
        if (entry != null) {
            if (entry!!.text.isNotEmpty()) {
                title.visibility=View.VISIBLE
                title.text = entry!!.text
                lineView.visibility=View.VISIBLE
            }else{
                title.visibility=View.GONE
                lineView.visibility=View.GONE
            }
            if (entry!!.media != null && entry!!.media.size > 0) {
                var videoPath=""
                var imgPath=""
                entry!!.media.forEachIndexed { index, mediaEntry ->
                    if (mediaEntry.type=="video") {
                        videoPath = mediaEntry.org
                    }else{
                        imgPath=mediaEntry.org
                    }
                }
                if (videoPath.isEmpty()){
                    ry.visibility=View.VISIBLE
                    vimgGp.visibility=View.GONE
                    imgAdapter!!.setNewData(entry!!.media)
                }else{
                    vimgGp.visibility=View.VISIBLE
                    ry.visibility=View.GONE
                    Glide.with(mContext).load(API.PIC_PREFIX+imgPath).into(vimg)
                    vimgGp.setOnClickListener {
                        v->
                        PrevUtils.onVideoItemClick(mContext,v,API.PIC_PREFIX+videoPath,API.PIC_PREFIX+imgPath)
                    }
                }
            }else{
                lineView.visibility=View.GONE
                ry.visibility=View.GONE
                vimgGp.visibility=View.GONE
            }
        } else {
            //            marker.hideInfoWindow();
        }
    }

    fun setList(list: List<BoxEntry>) {
        this.list = list
    }
}
