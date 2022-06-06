package com.eggplant.qiezisocial.entry

/**
 * Created by Administrator on 2019/1/9.
 */

class BaseEntry<T> {
    var msg: String? = null
    var stat: String? = null
    var userinfor: T? = null
    // 首页box信息，  推荐问题
    var list: List<T>? = null
    //发布box成功返回体；评论
    var record:T?=null
    //黑名单
    var black:List<T>?=null
    //版本号
    var ver:String?=null
    //点赞VCR成功返回
    var like:Int?=null
    //
    var att:List<T>?=null
    var filter:T?=null
    var feedback:String?=null
    var momentcount:Int?=null


}
