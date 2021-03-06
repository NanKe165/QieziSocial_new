package com.mabeijianxi.jianxiexpression.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.text.Spannable;
import android.view.KeyEvent;
import android.widget.EditText;

import com.mabeijianxi.jianxiexpression.widget.ExpressionSpan;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jian on 2016/6/24.
 * mabeijianxi@gmail.com
 * <p>
 * 这个class算是比较核心的后期会继续优化
 */
public class ExpressionTransformEngine {

    public static Spannable transformExoression(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize) {
        int textLength = text.length();
        HashMap<Integer, Integer> SpanIndex = new HashMap<>();
        ExpressionSpan[] oldSpans = text.getSpans(0, textLength, ExpressionSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            SpanIndex.put(text.getSpanStart(oldSpans[i]), text.getSpanEnd(oldSpans[i]));
        }

//        String PATTERN = "\\[qz](.*?)\\[/qz]";
//        Pattern p = Pattern.compile(PATTERN);
//        Matcher m = p.matcher(text);


        String rexgString = "\\[qzxs\\d+\\]";
        Pattern compile = Pattern.compile(rexgString);
        Matcher matcher = compile.matcher(text);

        while (matcher.find()) {

            Integer maybeEnd = SpanIndex.get(matcher.start());
            if (maybeEnd != null && maybeEnd.intValue() == matcher.end()) {
                continue;
            }

            String beferGroup = matcher.group();
            Integer index = ExpressionCache.getAllExpressionTable().get(beferGroup);
            int id = -1;
            if (index != null && index >= 0) {
                id = index;
            } else {
//                String afterGroup = beferGroup.replaceAll("\\[qz]|\\[/qz]", "");
                String suffix = beferGroup.replaceAll("\\[qzxs\\]", "");
                if (suffix != null && Integer.parseInt(suffix) < 140) {
                    String afterGroup = "emoji" + (Integer.parseInt(suffix) + 1);
                    id = context.getResources().getIdentifier(afterGroup, "drawable", context.getPackageName());
                }
            }
            if (id <= 0) {
                continue;
            }
            text.setSpan(new ExpressionSpan(context, id, emojiSize, emojiAlignment, textSize), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return text;
    }

    public static String transformExoressionToTxt(String text) {
        String afterGroup = text;
//        String PATTERN = "\\[qz](.*?)\\[/qz]";
//        Pattern p = Pattern.compile(PATTERN);
//        Matcher m = p.matcher(text);

        String rexgString = "\\[qzxs\\d+\\]";
        Pattern compile = Pattern.compile(rexgString);
        Matcher matcher = compile.matcher(text);

        while (matcher.find()) {
            String beferGroup = matcher.group();
//            String replace = beferGroup.replaceAll("\\[qz]|\\[/qz]", "");
            String s = beferGroup.replaceAll("\\[qzxs", "");
            afterGroup = afterGroup.replaceAll(s, "[表情]").replaceAll("\\[qzxs", "");

        }
        return afterGroup;
    }

    /**
     * 每次点击表情会调用，已经处理长按选择多个表情然后再输入表情的情况
     *
     * @param editText
     * @param str
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void input(final EditText editText, String str) {


        if (editText == null || str == null) {
            return;
        }
        String t = editText.getText().toString();
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (editText.getFilters()!= null &&editText.getFilters().length>0&& editText.getFilters()[0] instanceof InputFilter.LengthFilter) {
            int max = ((InputFilter.LengthFilter) editText.getFilters()[0]).getMax();
//            Log.i("emojitest", "max:" + max + "  str.length:" + str.length() + "  edit:" + t.length());
            if (max - t.length() != 0) {
                max += str.length() - 1;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
            }
        }
        if (start < 0) {
            editText.append(str);
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), str, 0, str.length());
        }
    }


    public static void delete(final EditText editText) {

        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
//        editText.setOnKeyListener(null);

    }

    /**
     * 添加最近使用表情
     *
     * @param str
     */
    public static void addRecentExpression(String str) {
        int endIndex = getEndIndex();
        for (int i = 0; i < ExpressionCache.getRecentExpression().length; i++) {
//            防止重复
            if (ExpressionCache.getRecentExpression()[i] == str || i == ExpressionCache.getRecentExpression().length - 1) {
                //                    最多一页
                sort(i, str);
                return;
            }
        }

        sort(endIndex, str);

    }

    /**
     * 后移动一位,只保留最近的21个
     *
     * @param endIndex
     * @param newStr
     */
    private static void sort(int endIndex, String newStr) {
        for (int i = endIndex; i > 0; i--) {
            ExpressionCache.getRecentExpression()[i] = ExpressionCache.getRecentExpression()[i - 1];
        }
        ExpressionCache.getRecentExpression()[0] = newStr;
    }

    /**
     * 得到最后一位有效位
     *
     * @return
     */
    private static int getEndIndex() {
        int lastStr = 0;
        for (int i = 0; i < ExpressionCache.getRecentExpression().length; i++) {
            if (ExpressionCache.getRecentExpression()[i] == null || i == ExpressionCache.getRecentExpression().length - 1) {
                lastStr = i;
                break;
            }
        }
        return lastStr;
    }
}
