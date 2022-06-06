package com.eggplant.qiezisocial.entry;

import android.text.TextUtils;

import com.eggplant.qiezisocial.greendao.entry.ChatEntry;
import com.eggplant.qiezisocial.utils.xml.MovieXb;
import com.eggplant.qiezisocial.utils.xml.PicXb;
import com.eggplant.qiezisocial.utils.xml.SoundXb;
import com.eggplant.qiezisocial.utils.xml.TxtXb;
import com.eggplant.qiezisocial.utils.xml.XmlUtils;

/**
 * Created by Administrator on 2021/4/27.
 */

public class PChatEntry {
    public String act;
    public long created;
    public DataEntry data;
    public String from;
    public UserEntry from_userinfor;
    public int groupid;
    public String id;
    public String range;
    public int to;
    public String type;

    public ChatEntry convertData(Integer myid) {
        ChatEntry entry = new ChatEntry();
        entry.setMsgId(id);
        entry.setType(act);
        entry.setFace(from_userinfor.face);
        entry.setCreated(created + "");
        if (!TextUtils.isEmpty(from))
            entry.setFrom(Long.parseLong(from));
        entry.setTo(to);
        entry.setChatId(0);
        entry.setUserId(myid);
        entry.setGsid(groupid);
        entry.setMsgRead(true);
        if (TextUtils.equals(act, "gtxt")) {
            TxtXb txtByXmlStr = XmlUtils.getTxtByXmlStr(data.content);
            if (txtByXmlStr != null) {
                String txt = txtByXmlStr.txt;
                entry.setContent(txt);
                entry.setExtra(data.extra);
            }
        } else if (TextUtils.equals(act, "gpic")) {
            PicXb picByXmlStr = XmlUtils.getPicByXmlStr(data.content);
            if (picByXmlStr != null) {
                String src = picByXmlStr.src;
                String height = picByXmlStr.height;
                String width = picByXmlStr.width;
                entry.setExtra(height + "&&" + width);
                entry.setContent("http://oss-cn-hangzhou.aliyuncs.com/qie-zi-pic/" + src);
            }
        } else if (TextUtils.equals(act, "gaudio")) {
            SoundXb soundByXmlStr = XmlUtils.getSoundByXmlStr(data.content);
            if (soundByXmlStr != null) {
                entry.setContent("http://oss-cn-hangzhou.aliyuncs.com/qie-zi-pic/" + soundByXmlStr.src);
                String dura = soundByXmlStr.dura.replace("dura", "");
                entry.setExtra(dura);
            }
        } else if (TextUtils.equals(act, "gvideo")) {
            MovieXb movieByXmlstr = XmlUtils.getMovieByXmlstr(data.content);
            if (movieByXmlstr != null) {
                entry.setContent("http://oss-cn-hangzhou.aliyuncs.com/qie-zi-pic/" + movieByXmlstr.src);
                entry.setExtra("http://oss-cn-hangzhou.aliyuncs.com/qie-zi-pic/" + movieByXmlstr.poster + "&&" + movieByXmlstr.dura);
            }
        }

        return entry;
    }
}
