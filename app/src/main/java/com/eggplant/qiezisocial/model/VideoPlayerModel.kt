package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.entry.MovieEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2021/10/9.
 */

class VideoPlayerModel{
    fun getMovie(sid:String,callback: JsonCallback<MovieEntry>){
        OkGo.post<MovieEntry>(API.GET_MOVIE)
                .params("sid",sid)
                .execute(callback)
    }
}
