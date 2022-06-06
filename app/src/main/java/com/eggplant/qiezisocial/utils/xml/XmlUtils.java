package com.eggplant.qiezisocial.utils.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Administrator on 2018/11/28.
 */

public class XmlUtils {
    public static TxtXb getTxtByXmlStr(String xml) {
        TxtXb txtXb = null;
        //得到pull解析对象
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG://开始标签
                        if (xmlPullParser.getName().equals("txt")) {
                            txtXb = new TxtXb();
                            String txt = xmlPullParser.nextText();
                            txtXb.txt = txt;
                        }
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txtXb;
    }

    public static PicXb getPicByXmlStr(String xml) {
        PicXb picXb = null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("pic")) {
                            picXb = new PicXb();
                            String src = xmlPullParser.getAttributeValue(null, "src");
                            String width = xmlPullParser.getAttributeValue(null, "width");
                            String height = xmlPullParser.getAttributeValue(null, "height");
                            picXb.src = src;
                            picXb.width = width;
                            picXb.height = height;
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picXb;
    }

    public static SoundXb getSoundByXmlStr(String xml) {

        SoundXb soundXb = null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("sound")) {
                            soundXb = new SoundXb();
                            String src = xmlPullParser.getAttributeValue(null, "src");
                            String dura = xmlPullParser.getAttributeValue(null, "dura");
                            soundXb.src = src;
                            soundXb.dura = dura;
                        }
                        break;

                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soundXb;
    }

    public static MovieXb getMovieByXmlstr(String xml) {
        MovieXb movieXb = null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("movie")) {
                            movieXb = new MovieXb();
                            String src = xmlPullParser.getAttributeValue(null, "src");
                            String width = xmlPullParser.getAttributeValue(null, "width");
                            String height = xmlPullParser.getAttributeValue(null, "height");
                            String poster = xmlPullParser.getAttributeValue(null, "poster");
                            String dura = xmlPullParser.getAttributeValue(null, "dura");
                            movieXb.src = src;
                            movieXb.width = width;
                            movieXb.height = height;
                            movieXb.poster = poster;
                            movieXb.dura=dura;

                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieXb;
    }



}
