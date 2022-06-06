package com.eggplant.qiezisocial.entry;

import com.eggplant.qiezisocial.greendao.entry.MainInfoBean;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/9.
 */

public class UserEntry implements Serializable{
    /**
     * att"no"
     * uid : 6
     * nick :
     * sex :
     * weight :
     * height :
     * birth : 1990-01-01
     * careers :
     * edu :
     * xz :
     * question :
     * city :
     * face :
     * pic : []
     * card : 18223
     * friend:"no"
     */

    //经度
    public String longitude;
    //维度
    public String latitude;

    //兴趣标签
    public String label;
    //userid
    public int uid;
    //是否关注
    public String att;
    //昵称
    public String nick;
    //性别
    public String sex;
    //体重
    public String weight;
    //身高
    public String height;
    //生日
    public String birth;
    //职业
    public String careers;
    //学历
    public String edu;
    //星座
    public String xz;
    //
    public String topic;
    //城市
    public String city;
    //头像
    public String face;
    //
    public String card;
    //个人图片
    public List<String> pic;
    //是否为好友
    public String friend;
    //
    public String stat;
    //空间背景--弃用
    public String spaceback;
    //等级（old）
    public String level;
    //是否上次一见钟情图片
    public String yjzq;
    //当前经验
    public int exp;
    //下一级所需经验
    public int nextexp;
    //新等级
    public String newlevel;
    //连续登陆天数
    public int continued;
    //个性签名
    public String sign;
    //升级距离百分比
    public int  expper;
    //心情
    public String mood;
    //交友目的
    @Expose()
    public String object;
    @Expose()
    public String introduce;






    public MainInfoBean getMainInfoBean(UserEntry entry, long userId) {
        MainInfoBean bean = new MainInfoBean();
        bean.setUid(entry.uid);
        bean.setSex(entry.sex);
        bean.setNick(entry.nick);
        bean.setBirth(entry.birth);
        bean.setCard(entry.card);
        bean.setCareers(entry.careers);
        bean.setCity(entry.city);
        bean.setHeight(entry.height);
        bean.setWeight(entry.weight);
        bean.setEdu(entry.edu);
        bean.setTopic(entry.topic);
        bean.setXz(entry.xz);
        bean.setAccount(entry.card);
        bean.setPic((ArrayList<String>) entry.pic);
        bean.setFace(entry.face);
        bean.setLabel(entry.label);
        bean.setType("temporal");
        bean.setCreated(System.currentTimeMillis());
        bean.setUserId(userId);
        return bean;
    }

}
