package com.eggplant.qiezisocial.entry

import android.transition.Scene
import java.io.Serializable

/**
 * Created by Administrator on 2021/11/3.
 */

class VisitEntry : Serializable {

    /**
     * newdiarycount : 1
     * newdiary : {"id":14244,"uid":"225","created":1635920715,"text":"圣经一共有多少章？","font":"","media":[],"type":"diary","userinfor":{"uid":225,"nick":"乌拉拉","sex":"男","weight":"57","height":"182","birth":"2003-12-31","careers":"行政","edu":"大学","xz":"摩羯座","topic":"","city":"深圳","face":"image/p/t/3rFLS_z9rDG9Z2xlQGadpz.jpg","object":"想恋爱","label":" 奶茶上瘾者 口头减肥者 不熬夜不睡觉 仙气十足 自闭型交友 ","level":3,"sign":"","pic":["image/p/o/D1gnrykuUV79Ebq_2eL_x.jpeg","image/p/o/1$HFvPPE6pFAmvqZpJ5sAE.jpeg","image/p/o/38Gup_ADpgSnJ3KbSuP_sg.jpeg"],"card":99977,"stat":"","att":"no","friend":"no","spaceback":"5","longitude":"116.306577","latitude":"39.916527","mood":"烦躁","newlevel":1,"nextexp":50,"expper":0,"continued":0,"card1":99977,"yjzq":"yes","exp":0},"like":0,"comment":0,"mylike":false,"commentlist":[]}
     * newvisitcount : 1
     * allvisitcount : 1
     * newvisit : [{"uid":232,"nick":"拳打南山敬老院","sex":"女","weight":"75","height":"133","birth":"1990-01-01","careers":"上班族","edu":"本科","xz":"","topic":"","city":"北京","face":"image/p/t/39QWkN2FIi$KPmFH11jXa9.jpg","object":"快约","label":"配音 麦霸","level":1,"sign":"","pic":["image/p/t/3NrSWCKFDlexd2koJa_JXV.jpg"],"card":104086,"stat":"","att":"no","friend":"no","spaceback":"","longitude":"116.306687","latitude":"40.025033","mood":"孤单","newlevel":1,"nextexp":50,"expper":0,"continued":0,"card1":104086,"yjzq":"no","exp":0}]
     * stat : ok
     * msg : 成功
     */

    var newdiarycount: Int = 0
    var newdiary: List<DiaryEntry>? = null
    var newvisitcount: Int = 0
    var allvisitcount: Int = 0
    var secretflag=false
    var stat: String? = null
    var msg: String? = null
    var newvisit: List<UserEntry>? = null
    var secretcount:Int=0
    var answercount:Int=0
    var jindoucount:Int=0
    var momentcount:Int=0
    var questioncount:Int=0
    var diarycount:Int=0
    var likecount:Int=0


    var selfscenes:List<ScenesEntry>?=null
    var currentmoment:List<DiaryEntry>?=null


}
