package com.mabeijianxi.jianxiexpression.widget.http;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mabeijianxi.jianxiexpression.R;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2018/3/25.
 */


public class HttpTextView extends AppCompatTextView {

    /*
    * 正则文本
    * ((http|ftp|https)://)(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?|(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?
    * */
    private String pattern =
            "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?|(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    //            "(https?|ftp|file|http)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
    String regex = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))";

    //    String regex1 = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$";
    // 创建 Pattern 对象
    Pattern r = Pattern.compile(regex);
    // 现在创建 matcher 对象
    Matcher m;

    int flag = Spanned.SPAN_INCLUSIVE_INCLUSIVE;
    int color = 0;
    private boolean needToRegionUrl = true;//是否开启识别URL，默认开启
    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;

    public HttpTextView(Context context) {
        this(context, null);

    }

    public HttpTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public HttpTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        color = getResources().getColor(R.color.black_bg);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (needToRegionUrl) {
            SpannableStringBuilderAllVer stringBuilderAllVer = recognUrl(text);
//            String newTxt = StringFilter(ToDBC(stringBuilderAllVer.toString()));
            super.setText(stringBuilderAllVer, type);
            this.setMovementMethod(LocalLinkMovementMethod.getInstance());
        } else {
            super.setText(text, type);
            this.setMovementMethod(getDefaultMovementMethod());
        }
    }



        @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);

        if (dontConsumeNonUrlClicks)
            return linkHit;
        return res;
    }

    public void setDontConsumeNonUrlClicks(boolean dontConsumeNonUrlClicks) {
        this.dontConsumeNonUrlClicks = dontConsumeNonUrlClicks;
    }

    /**
     * @param input String类型
     * @return String
     * @Description 解决textview的问题---半角字符与全角字符混乱所致；这种情况一般就是汉字与数字、英文字母混用
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * @param str String类型
     * @return String
     * @Description 替换、过滤特殊字符
     */
    public static String StringFilter(String str) throws PatternSyntaxException {
        str = str.replaceAll(" ", "").replaceAll(" ", "").replaceAll("：", ":").replaceAll("：", "：").replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");//替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public boolean getIsNeedToRegionUrl() {
        return needToRegionUrl;
    }

    public void setOpenRegionUrl(boolean needToRegionUrl) {
        this.needToRegionUrl = needToRegionUrl;
    }

    private SpannableStringBuilderAllVer recognUrl(CharSequence text) {
//        mStringList.clear();
//        mUrlInfos.clear();
        CharSequence preContextText = null;
        CharSequence contextText;
        //emoji
        CharSequence emojiText;
        text = text == null ? "" : text;
        //以下用于拼接本来存在的spanText
        SpannableStringBuilderAllVer span = new SpannableStringBuilderAllVer(text);
        DynamicDrawableSpan[] emojiSpans = span.getSpans(0, text.length(), DynamicDrawableSpan.class);
        if (emojiSpans.length > 0) {
            int start = 0;
            int end = 0;
            start = span.getSpanStart(emojiSpans[0]);
            end = span.getSpanEnd(emojiSpans[emojiSpans.length - 1]);
            if (start > 0) {
                //emoji文本前面的内容页
                preContextText = text.subSequence(0, start);
            }
            //emoji文本后面的内容页
            contextText = text.subSequence(end, text.length());
            //emoji文本
            emojiText = text.subSequence(start,
                    end);
        } else {
            contextText = text;
            emojiText = null;
        }
        SpannableStringBuilderAllVer spanBuilder = new SpannableStringBuilderAllVer();
        if (preContextText != null) {
            //记录网址的list
            LinkedList<String> mStringList;
            //记录该网址所在位置的list
            LinkedList<UrlInfo> mUrlInfos;
            mStringList = new LinkedList<>();
            mUrlInfos = new LinkedList<>();
            m = r.matcher(preContextText);
            //匹配成功
            while (m.find()) {
                //得到网址数
                UrlInfo info = new UrlInfo();
                info.start = m.start();
                info.end = m.end();
                mStringList.add(m.group());
                mUrlInfos.add(info);
            }
            jointText(spanBuilder, preContextText, mStringList, mUrlInfos);

        }
        if (emojiText != null)
            spanBuilder.append(emojiText);

        LinkedList<String> mStringList;
        //记录该网址所在位置的list
        LinkedList<UrlInfo> mUrlInfos;
        mStringList = new LinkedList<>();
        mUrlInfos = new LinkedList<>();
        m = r.matcher(contextText);
        //匹配成功
        while (m.find()) {
            //得到网址数
            UrlInfo info = new UrlInfo();
            info.start = m.start();
            info.end = m.end();
            mStringList.add(m.group());
            mUrlInfos.add(info);
        }
        jointText(spanBuilder, contextText, mStringList, mUrlInfos);
        return spanBuilder;
    }

    /**
     * 拼接文本
     */
    private SpannableStringBuilderAllVer jointText(SpannableStringBuilderAllVer spanBuilder,
                                                   CharSequence contentText
            , LinkedList<String> mStringList, LinkedList<UrlInfo> mUrlInfos) {
        appendURL(spanBuilder, contentText, mStringList, mUrlInfos);
        return spanBuilder;
    }

    private void appendURL(SpannableStringBuilderAllVer spanBuilder, CharSequence contentText, LinkedList<String> mStringList, LinkedList<UrlInfo> mUrlInfos) {
        if (mStringList.size() > 0) {
            //只有一个网址
            if (mStringList.size() == 1) {
                String preStr = contentText.toString().substring(0, mUrlInfos.get(0).start);
                spanBuilder.append(preStr);
                String url = mStringList.get(0);
                spanBuilder.append(" 网页链接 ", new URLClick(url), flag);
                String nextStr = contentText.toString().substring(mUrlInfos.get(0).end);
                Log.i("httpTextview", "  url:" + url + "  nextStr" + nextStr);
                spanBuilder.append(nextStr);
            } else {
                //有多个网址
                for (int i = 0; i < mStringList.size(); i++) {
                    if (i == 0) {
                        //拼接第1个span的前面文本
                        String headStr =
                                contentText.toString().substring(0, mUrlInfos.get(0).start);
                        spanBuilder.append(headStr);
                    }
                    if (i == mStringList.size() - 1) {
                        //拼接最后一个span的后面的文本
                        spanBuilder.append(" 网页链接 ", new URLClick(mStringList.get(i)),
                                flag);
                        String footStr = contentText.toString().substring(mUrlInfos.get(i).end);
                        spanBuilder.append(footStr);
                    }
                    if (i != mStringList.size() - 1) {
                        //拼接两两span之间的文本
                        spanBuilder.append(" 网页链接 ", new URLClick(mStringList.get(i)), flag);
                        String betweenStr = contentText.toString()
                                .substring(mUrlInfos.get(i).end,
                                        mUrlInfos.get(i + 1).start);
                        spanBuilder.append(betweenStr);
                    }
                }
            }
        } else {
            spanBuilder.append(contentText);
        }
    }

    //------------------------------------------定义-----------------------------------------------
    class UrlInfo {
        public int start;
        public int end;
    }

    class URLClick extends ClickableSpan {
        private String text;

        public URLClick(String text) {
            this.text = text;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");

            if (text.contains("http")) {
                Uri content_url = Uri.parse(text);
                intent.setData(content_url);

            } else {
                Uri content_url = Uri.parse("http://" + text);
                intent.setData(content_url);
            }
            getContext().startActivity(intent);
//            if (text.contains("https")){
//                String substring = text.substring(8, text.length());
//                WrapIntent intent = new WrapIntent(getContext(), "qzweb://12&/&"+substring);
//                if(intent.valid) {
//                    getContext().startActivity(intent);
//                }
//            }else {
//                if (text.contains("http")){
//                    String substring = text.substring(7, text.length());
//                    WrapIntent intent = new WrapIntent(getContext(), "qzweb://2&/&" + substring);
//                    if (intent.valid) {
//                        getContext().startActivity(intent);
//                    }
//                }else {
//                        WrapIntent intent = new WrapIntent(getContext(), "qzweb://2&/&"+text);
//                        if(intent.valid) {
//                            getContext().startActivity(intent);
//                        }
//                }
//            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(color);
            ds.setUnderlineText(true);

        }
    }

    public void setColor(int color) {
        this.color = color;
        this.setTextColor(color);
    }

}