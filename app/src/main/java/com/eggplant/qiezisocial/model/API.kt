package com.eggplant.qiezisocial.model

/**
 * Created by Administrator on 2019/1/17.
 */

interface API {
    companion object {
        val VER_A = "/circular"
        val VER_B = "/newhq"
        //nv.qie-zi.com
        val HOST = "https://box.qie-zi.com" + VER_B

        val PIC_PREFIX = "http://oss-cn-hangzhou.aliyuncs.com/qie-zi-pic/"

        //修改个人信息
        val MODIFY = HOST + "/modify.php"
        //获取验证码
        val GET_CODE = HOST + "/yzm.php"
        //短信登录
        val LOGIN = HOST + "/smslogin.php"
        //自动登录
        val AUTO_LOGIN = HOST + "/alogin.php"
        //添加好友
        val ADD_FRIEND = HOST + "/applyfriend.php"
        //同意添加好友
        val AGREE_ADD_FRIEND = HOST + "/addfriend.php"
        //删除haoyou
        val REMOVE_FRIEND = HOST + "/removefriend.php"
        //检查版本
        val CHECK_VERSION = HOST + "/checkver.php"


        //上传附件（聊天）
        val UPLOAD_MEDIA = HOST + "/uploadmedia.php"
        //获取聊天id
        val GET_ID = HOST + "/getid.php"
        //添加相册
        val ADDPIC = HOST + "/addpic.php"
        //刪除相冊
        val DELPIC = HOST + "/delpic.php"
        //添加黑名单
        val ADD_BLACK = HOST + "/addblack.php"
        //黑名单列表
        val BLACK_LIST = HOST + "/blacklist.php"

        //获取首页信息
        val GET_BOX = HOST + "/getinbox.php"
        //获取我的问题
        val GET_MY_BOX: String? = HOST + "/myquestion.php"
        //获取推荐问题
        val GET_QUESTION = HOST + "/getsystemquestion.php"
        //发布问题
        val PUB_QUESTION = HOST + "/pubquestion.php"
        //设置问题已读

        val READ_QUESTION: String? = HOST + "/readquestion.php"
        //回答问题
        val PUB_ANSWER = HOST + "/pubanswer.php"
        val MY_QUESTION = HOST + "/getquesslist.php"
        //加入群聊
        val JOIN_GROUP: String? = HOST + "/joingroup.php"

        //VCR
        val VCR: String? = HOST + "/getvcrlist.php"
        //发布VCR
        val PUB_VCR = HOST + "/pubvcr.php"
        //删除Vcr
        val DELETE_VCR: String? = HOST + "/delvcr.php"
        //Vcr评论
        val VCR_COMMENT = HOST + "/getvcrcommentlist.php"
        //发布Vcr评论
        val PUB_VCR_COMMENT = HOST + "/pubvcrcomment.php"
        //举报Vcr评论
        val REPORT_VCR_COMMENT: String? = HOST + "/reportcomment.php"
        //点赞VCR
        val LIKE_VCR = HOST + "/likevcr.php"
        //关注（收藏）Vcr
        val COLLECT_VCR: String? = HOST + "/attvcr.php"
        //取消关注
        val CANCEL_COLLECT: String? = HOST + "/unattvcr.php"
        //关注的vcr列表
        val GET_COLLECT_VCR = HOST + "/getattvcrlist.php"
        //获取我的Vcr列表
        val GET_MY_VCR = HOST + "/myvcrlist.php"
        //举报VCR
        val REPORT_VCR: String? = HOST + "/reportvcr.php"
        //获得系统默认缘分问题
        val SYS_QUESTION: String? = HOST + "/getsystemquess.php"
        //发布缘分猜猜猜
        val PUB_GUESS: String? = HOST + "/pubguess.php"
        //回答缘分猜猜猜
        val ANSW_GUESS: String? = HOST + "/answerguess.php"

        //发布日记
        val PUB_DIARY: String? = HOST + "/pubdiary.php"
        //获取日记
        val GET_DIARY: String? = HOST + "/getdiary.php"
        //点赞日记
        val LIKE_DIARY: String = HOST + "/likediary.php"
        //炸掉日记
        val BOOM_DIARY: String? = HOST + "/disdiary.php"
        val GET_DIARY_COMMENT = HOST + "/getdiarycomment.php"
        //评论日记
        val DIARY_COMMENT: String? = HOST + "/pubdiaryucomment.php"


        //获取丑照馆
        val GET_CZG = HOST + "/getczglist.php"
        //关注的丑照馆
        val GET_ATT_CZG = HOST + "/getattczglist.php"
        //我的丑照馆
        val GET_MY_CZG = HOST + "/myczglist.php"
        //炸掉丑照馆
        val DIS_CZG = HOST + "/disczg.php"
        //发布丑照馆
        val PUB_CZG = HOST + "/pubczg.php"
        //发布丑照馆评论
        val PUB_CZG_COMMENT = HOST + "/pubczgcomment.php"
        //获取丑照馆评论
        val GET_CZG_COMMENT = HOST + "/getczgcommentlist.php"
        //点赞丑照馆
        val LIKE_CZG = HOST + "/likeczg.php"
        //关注丑照馆
        val ATT_CZG = HOST + "/attczg.php"
        //取消关注丑照馆
        val UNATT_CZG = HOST + "/unattczg.php"
        //举报丑照馆
        val REPORT_CZG = HOST + "/reportczg.php"
        //删除自己的丑照馆
        val DELETE_CZG = HOST + "/delczg.php"
        //举报丑照馆评论
        val REPORT_COMMENT = HOST + "/reportcomment.php"
        //消耗一次随机问题机会
        val COST_CHANCE = HOST + "/costchance.php"
        //获取话术和问题
        val GET_CURRENT_TOPIC = HOST + "/getcurrenttopic.php"
        //获取聊天emoji
        val GET_CHAT_EMOJI = HOST + "/getanimation.php"
        val GET_NEARBY_INFO: String = HOST + "/getlocal.php"
        //获取筛选器信息
        val GET_FILTER: String? = HOST + "/getfilter.php"
        val SET_FILTER: String? = HOST + "/setfilter.php"
        //获取个人信息
        val GET_USERINFO: String? = HOST + "/getuser.php"
        //修改头像，昵称，性别
        val RRGISTER: String? = HOST + "/setuserprofile.php"
        //获取往期话题
        val GET_PAST_TOPIC: String? = HOST + "/getgroup.php"
        //
        val GET_GROUP_CHAT: String? = HOST + "/getgroupmessage.php"
        //他人空间--私人提问
        val PUB_PRIVATE_QS: String? = HOST + "/pubprivatequestion.php"
        //加入黑名单
        val GCHAT_ADD_BLOCKLIST: String? = HOST + "/groupban.php"
        //好友列表--附近的人
        val GET_NEARBY_USER: String? = HOST + "/getnearuser.php"
        //首页--》动态--》发布
        val PUB_DYNAMIC: String? = HOST + "/pubmoment.php"
        //获取动态
        val GET_DYNAMIC: String? = HOST + "/getmoment.php"
        //点赞动态
        val LIKE_DYNAMIC: String? = HOST + "/likemoment.php"
        //获取我的动态
        val GET_MY_DYNAMIC: String? = HOST + "/mymoment.php"
        //删除动态
        val DELETE_DYNAMIC: String? = HOST + "/delmoment.php"
        //动态评论
        val DYNAMIC_COMMENT: String? = HOST + "/pubmomentcomment.php"
        //获取视频直播信息  /getmovie.php
        val GET_MOVIE: String?= HOST+"/getmedia.php"
        //获取地图动态
        val GET_MAP_DYNAMIC: String?= HOST+"/getmapmoment.php"
        //发布地图动态
        val PUB_MAP_DYNAMIC: String?= HOST+"/pubmapmoment.php"
        //访客列表
        val GET_VISIT= HOST+"/getvisit.php"

        val GET_SPACE: String?= HOST+"/getspace.php"
        val SECRET_LOVE: String?= HOST+"/secretlove.php"
        //收藏场景
        val COLLECT_SCENE: String?= HOST+"/favscene.php"
        //收藏场景列表
        val GET_COLLECT_SCENE: String?= HOST+"/favscenelist.php"
        val VISIT_QUESTION: String?= HOST+"/visitquestion.php"
        //注销账号
        val DIS_USER = HOST+"/disuser.php"
        //举报 动态moment  动态评论comment 聊天im
        val REPORT: String?= HOST+"/report.php"
        //获取我抢注的场景
        val GET_MYSCENE: String?= HOST+"/getmyscense.php"
        //获取明星场景列表
        val GET_STAR: String?= HOST+"/getstar.php"
        //获取大厂场景列表
        val GET_FACTORY: String?= HOST+"/getfactory.php"
        //获取完整大学数据
        val GET_COLLEGE: String?= HOST+"/getcollege.php"
        //设置抢注场景
        val SET_MYSCENE: String?= HOST+"/setmyscense.php"
        val GET_MX: String?= HOST+"/jindouget.php"
        //获取红包雨计划
        val GET_RED_PACKET: String?= HOST+"/gethongbao.php"
        //汇报红包
        val REPORT_RED_PACKET: String?= HOST+"/reporthongbao.php"
        //浏览过的场景
        val GET_BROWSE_SCENE: String?= HOST+"/getvisitscenes.php"
        //我的自建场景
        val GET_SELF_SCENES: String?= HOST+"/myselfscenes.php"
        //好友的自建场景
        val GET_FRIEND_SCENES: String?= HOST+"/myfriendscenes.php"
        //创建场景
        val CREATE_SCENES: String?= HOST+"/createscenes.php"
        //修改场景
        val MODIFY_SCENES: String?= HOST+"/modifyscenes.php"
        val GET_JINDOU: String?= HOST+"/gettodayjindou.php"
        //获取他人动态
        val GET_OTHER_DYNAMIC: String?= HOST+"/othermoment.php"
        //查询用户
        val FIND_USER: String?= HOST+"/finduser.php"


    }
}