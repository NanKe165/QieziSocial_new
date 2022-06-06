package com.eggplant.qiezisocial.greendao.entry;

import com.eggplant.qiezisocial.entry.UserEntry;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/22.
 */
@Entity
public class MainInfoBean implements Serializable {

    private static final long serialVersionUID = 7743787813850898633L;

    @Id
    private Long id;


    //问题id
    private long qsid;
    private long gsid;
    //发布问题的人的uid
    private int qsuid;
    private String qsTxt;
    private String qsUserFace;
    private String qsNick;
    private String qsType;

    private long userId;
    private long uid;
    private long created;
    private String nick;
    private String remark;
    private String sex;
    private String birth;
    private String careers;
    private String face;
    private String card;
    private String edu;
    private String xz;
    private String topic;
    private String city;
    private String height;
    private String weight;
    private String object;
    private String label;
    @Convert(columnType = String.class, converter = ListConverter.class)
    private List<String> pic = new ArrayList<>();
    private String account;
    private String type;
    private String msg;
    private String msg_user_nick;
    private String msg_user_face;
    private long msg_user_id;
    private int msgNum;
    private long newMsgTime;
    // txt pic audio video
    private String msgType;
    // applyfriend extra
    private String source;
    private String online;
    private String mood;
    // applyfriend 添加友好信息
    private String message;
    //是否需要提取展示
    private boolean extractMark;
    private String media1;
    private String media2;
    private String media3;

    public String getMedia1() {
        return media1;
    }

    public void setMedia1(String media1) {
        this.media1 = media1;
    }

    public String getMedia2() {
        return media2;
    }

    public void setMedia2(String media2) {
        this.media2 = media2;
    }

    public String getMedia3() {
        return media3;
    }

    public void setMedia3(String media3) {
        this.media3 = media3;
    }

    public boolean isExtractMark() {
        return extractMark;
    }

    public void setExtractMark(boolean extractMark) {
        this.extractMark = extractMark;
    }

    public String getMsg_user_nick() {
        return msg_user_nick;
    }

    public void setMsg_user_nick(String msg_user_nick) {
        this.msg_user_nick = msg_user_nick;
    }

    public String getMsg_user_face() {
        return msg_user_face;
    }

    public void setMsg_user_face(String msg_user_face) {
        this.msg_user_face = msg_user_face;
    }

    public long getMsg_user_id() {
        return msg_user_id;
    }

    public void setMsg_user_id(long msg_user_id) {
        this.msg_user_id = msg_user_id;
    }

    public void setQsuid(int qsuid) {
        this.qsuid = qsuid;
    }

    public int getQsuid() {
        return qsuid;
    }

    public String getQsType() {
        return qsType;
    }

    public void setQsType(String qsType) {
        this.qsType = qsType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getQsNick() {
        return qsNick;
    }

    public void setQsNick(String qsNick) {
        this.qsNick = qsNick;
    }

    public String getQsUserFace() {
        return qsUserFace;
    }

    public void setQsUserFace(String qsUserFace) {
        this.qsUserFace = qsUserFace;
    }

    public void setQsTxt(String qsTxt) {
        this.qsTxt = qsTxt;
    }

    public String getQsTxt() {
        return qsTxt;
    }

    public long getQsid() {
        return qsid;
    }

    public void setQsid(long qsid) {
        this.qsid = qsid;
    }

    public long getGsid() {
        return gsid;
    }

    public void setGsid(long gsid) {
        this.gsid = gsid;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getOnline() {
        return online;
    }

    public int getMsgNum() {
        return this.msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCard() {
        return this.card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getFace() {
        return this.face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getCareers() {
        return this.careers;
    }

    public void setCareers(String careers) {
        this.careers = careers;
    }

    public String getBirth() {
        return this.birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public long getCreated() {
        return this.created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUid() {
        return this.uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 252184526)
    public MainInfoBean(Long id, long qsid, long gsid, int qsuid, String qsTxt, String qsUserFace, String qsNick,
            String qsType, long userId, long uid, long created, String nick, String remark, String sex,
            String birth, String careers, String face, String card, String edu, String xz, String topic,
            String city, String height, String weight, String object, String label, List<String> pic,
            String account, String type, String msg, String msg_user_nick, String msg_user_face, long msg_user_id,
            int msgNum, long newMsgTime, String msgType, String source, String online, String mood,
            String message, boolean extractMark, String media1, String media2, String media3) {
        this.id = id;
        this.qsid = qsid;
        this.gsid = gsid;
        this.qsuid = qsuid;
        this.qsTxt = qsTxt;
        this.qsUserFace = qsUserFace;
        this.qsNick = qsNick;
        this.qsType = qsType;
        this.userId = userId;
        this.uid = uid;
        this.created = created;
        this.nick = nick;
        this.remark = remark;
        this.sex = sex;
        this.birth = birth;
        this.careers = careers;
        this.face = face;
        this.card = card;
        this.edu = edu;
        this.xz = xz;
        this.topic = topic;
        this.city = city;
        this.height = height;
        this.weight = weight;
        this.object = object;
        this.label = label;
        this.pic = pic;
        this.account = account;
        this.type = type;
        this.msg = msg;
        this.msg_user_nick = msg_user_nick;
        this.msg_user_face = msg_user_face;
        this.msg_user_id = msg_user_id;
        this.msgNum = msgNum;
        this.newMsgTime = newMsgTime;
        this.msgType = msgType;
        this.source = source;
        this.online = online;
        this.mood = mood;
        this.message = message;
        this.extractMark = extractMark;
        this.media1 = media1;
        this.media2 = media2;
        this.media3 = media3;
    }

    @Generated(hash = 110662006)
    public MainInfoBean() {
    }


    public void updateUser(MainInfoBean bean) {
        this.uid = bean.getUid();
//        this.created = bean.getCreated();
        this.nick = bean.getNick();
        this.sex = bean.getSex();
        this.birth = bean.getBirth();
        this.careers = bean.getCareers();
        this.face = bean.getFace();
        this.card = bean.getCard();
        this.account = bean.getAccount();
        this.type = bean.getType();
        this.mood=bean.getMood();
        this.message=bean.getMessage();
        this.online=bean.getOnline();
//        this.msg=bean.getMsg();
//        this.msgNum=bean.getMsgNum();
    }

    public UserEntry convertUserEntry() {
        UserEntry entry = new UserEntry();
        entry.uid = (int) uid;
        entry.card = card;
        entry.nick = nick;
        entry.careers = careers;
        entry.sex = sex;
        entry.label = label;
        entry.face = face;
        entry.birth = birth;
        entry.pic=pic;
        entry.mood=mood;
        return entry;
    }

    public long getNewMsgTime() {
        return this.newMsgTime;
    }

    public void setNewMsgTime(long newMsgTime) {
        this.newMsgTime = newMsgTime;
    }

    public String getMsgType() {
        return this.msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getPic() {
        return this.pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public String getObject() {
        return this.object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getXz() {
        return this.xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    public String getEdu() {
        return this.edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj== null){
            return false;
        }
        MainInfoBean data = (MainInfoBean) obj;
        if (data.getId()==id){
            return true;
        }
        return super.equals(obj);
    }

    public boolean getExtractMark() {
        return this.extractMark;
    }
}
