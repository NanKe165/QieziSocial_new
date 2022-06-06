package com.eggplant.qiezisocial.model

import android.location.Location
import com.eggplant.qiezisocial.contract.MapContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2021/10/21.
 */

class MapDynaimcModel : MapContract.Model {
    override fun getData(begin: Int, longitude: Double, latitude: Double, sid: String, s: String, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_MAP_DYNAMIC)
                .params("b", begin)
                .params("s", s)
                .params("sid", sid)
                .params("long", longitude)
                .params("lat", latitude)
                .execute(jsonCallback)
    }

    override fun pubMapDynamic(txt: String?, goal: String, mediaList: ArrayList<String>, sid: String, location: Location, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        var post = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_MAP_DYNAMIC)
                .params("s", goal)
                .params("sid", sid)
                .params("long", location.longitude)
                .params("lat", location.latitude)
        if (txt != null && txt.isNotEmpty()) {
            post.params("w", txt)
        }
        mediaList.forEach {
            post.params("file[]", File(it))
        }

        post.execute(jsonCallback)
    }
}
