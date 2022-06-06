package com.eggplant.qiezisocial.greendao.entry;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/23.
 */
@Entity
public class ChatEntry implements Serializable {
    static final long serialVersionUID = 42L;
    //当前的登录用户id，非此entry的id
    private long userId;
    /**
     * 每条消息唯一id
     */
    private String msgId;
    //聊天对象id
    private long chatId;
    //内容类型
    private String type;
    //内容
    private String content;
    //附件
    private String extra;
    private long from;
    private long to;
    //时间戳
    private String created;

    private boolean isShowCreated;
    private String face;
    private String nick;
    private String sex;
    private String birth;
    private String edu;
    private String height;
    private String weight;
    private String careers;
    private String label;
    private String layoutType;


    private String layout;
    //0(默认)-成功  1-失败  2-加载中  3--解压
    private int msgStat;
    //私聊id
    private long qsid;


    //群聊id
    private long gsid;


    private String question1;
    private String question2;
    private String question3;
    private String answer1;
    private String answer2;
    private String answer3;

    private boolean msgRead;

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEdu() {
        return edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCareers() {
        return careers;
    }

    public void setCareers(String careers) {
        this.careers = careers;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isMsgRead() {
        return msgRead;
    }

    public void setMsgRead(boolean msgRead) {
        this.msgRead = msgRead;
    }

    public void setQsid(long qsid) {
        this.qsid = qsid;
    }

    public long getQsid() {
        return qsid;
    }

    public void setGsid(long gsid) {
        this.gsid = gsid;
    }

    public long getGsid() {
        return gsid;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    @Id
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMsgStat() {
        return this.msgStat;
    }

    public void setMsgStat(int msgStat) {
        this.msgStat = msgStat;
    }

    public String getLayout() {
        return this.layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getFace() {
        return this.face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public boolean getIsShowCreated() {
        return this.isShowCreated;
    }

    public void setIsShowCreated(boolean isShowCreated) {
        this.isShowCreated = isShowCreated;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getTo() {
        return this.to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;

    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getChatId() {
        return this.chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Generated(hash = 154603456)
    public ChatEntry(long userId, String msgId, long chatId, String type, String content, String extra, long from, long to,
            String created, boolean isShowCreated, String face, String nick, String sex, String birth, String edu,
            String height, String weight, String careers, String label, String layoutType, String layout, int msgStat,
            long qsid, long gsid, String question1, String question2, String question3, String answer1, String answer2,
            String answer3, boolean msgRead, Long id) {
        this.userId = userId;
        this.msgId = msgId;
        this.chatId = chatId;
        this.type = type;
        this.content = content;
        this.extra = extra;
        this.from = from;
        this.to = to;
        this.created = created;
        this.isShowCreated = isShowCreated;
        this.face = face;
        this.nick = nick;
        this.sex = sex;
        this.birth = birth;
        this.edu = edu;
        this.height = height;
        this.weight = weight;
        this.careers = careers;
        this.label = label;
        this.layoutType = layoutType;
        this.layout = layout;
        this.msgStat = msgStat;
        this.qsid = qsid;
        this.gsid = gsid;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.msgRead = msgRead;
        this.id = id;
    }

    public ChatEntry copyEntry() {
        ChatEntry entry = new ChatEntry();
        entry.userId = userId;
        entry.msgId = msgId;
        entry.chatId = chatId;
        entry.type = type;
        entry.content = content;
        entry.extra = extra;
        entry.from = from;
        entry.to = to;
        entry.created = created;
        entry.isShowCreated = isShowCreated;
        entry.face = face;
        entry.layout = layout;
        entry.msgStat = msgStat;
        entry.qsid = qsid;
        entry.gsid = gsid;
        entry.question1 = question1;
        entry.question2 = question2;
        entry.question3 = question3;
        entry.answer1 = answer1;
        entry.answer2 = answer2;
        entry.answer3 = answer3;
        entry.id = id;
        return entry;
    }

    @Generated(hash = 1403893132)
    public ChatEntry() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatEntry obj1 = (ChatEntry) obj;
        if (TextUtils.equals(msgId, obj1.getMsgId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getMsgRead() {
        return this.msgRead;
    }

}
