package com.eggplant.qiezisocial.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yiw
 * @ClassName: CommonUtils
 * @Description:
 * @date 2015-12-28 下午4:16:01
 */
public class CommentUtils {

    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    public static boolean isShowSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //获取状态信息
        return imm.isActive();//true 打开
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    public static String getRunningActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean checkEmail(String email) {
        Pattern pattern = Pattern
                .compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean checkPhone(String phone) {

        Pattern pattern = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(14[0-9])|(16[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isMobile(String phoneStr, Context context) {
        boolean b = false;
        try {
            int length = regexArray.length;
            for (int i = 0; i < length; i++) {
                b = matches(phoneStr, context, regexArray[i]);
                if (b) {
                    System.err.println("###########test####################phoneNumber---matches--"+phoneStr);
                    return b;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static boolean matches(String phoneStr, Context context, String regex) {
        boolean b = false;
        try {
            Pattern p = null;
            Matcher m = null;
            if (!TextUtils.isEmpty(phoneStr)) {
                p = Pattern.compile(regex);
                m = p.matcher(phoneStr);
                b = m.matches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (b) {
                //手机号是对的，那就存起来
                context.getSharedPreferences("com.test.demo", Context.MODE_PRIVATE).edit().putString("com.test.demo.p", phoneStr).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static final String[] regexArray = {
            "^(\\+?213|0)(5|6|7)\\d{8}$",
            "^(!?(\\+?963)|0)?9\\d{8}$",
            "^(!?(\\+?966)|0)?5\\d{8}$",
            "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$",
            "^(\\+?420)? ?[1-9][0-9]{2} ?[0-9]{3} ?[0-9]{3}$",
            "^(\\+?49[ \\.\\-])?([\\(]{1}[0-9]{1,6}[\\)])?([0-9 \\.\\-\\/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$",
            "^(\\+?45)?(\\d{8})$",
            "^(\\+?30)?(69\\d{8})$",
            "^(\\+?61|0)4\\d{8}$",
            "^(\\+?44|0)7\\d{9}$",
            "^(\\+?852\\-?)?[569]\\d{3}\\-?\\d{4}$",
            "^(\\+?91|0)?[789]\\d{9}$",
            "^(\\+?64|0)2\\d{7,9}$",
            "^(\\+?27|0)\\d{9}$",
            "^(\\+?26)?09[567]\\d{7}$",
            "^(\\+?34)?(6\\d{1}|7[1234])\\d{7}$",
            "^(\\+?358|0)\\s?(4(0|1|2|4|5)?|50)\\s?(\\d\\s?){4,8}\\d$",
            "^(\\+?33|0)[67]\\d{8}$",
            "^(\\+972|0)([23489]|5[0248]|77)[1-9]\\d{6}$",
            "^(\\+?36)(20|30|70)\\d{7}$",
            "^(\\+?39)?\\s?3\\d{2} ?\\d{6,7}$",
            "^(\\+?81|0)\\d{1,4}[ \\-]?\\d{1,4}[ \\-]?\\d{4}$",
            "^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$",
            "^(\\+?47)?[49]\\d{7}$",
            "^(\\+?32|0)4?\\d{8}$",
            "^(\\+?47)?[49]\\d{7}$",
            "^(\\+?48)? ?[5-8]\\d ?\\d{3} ?\\d{2} ?\\d{2}$",
            "^(\\+?55|0)\\-?[1-9]{2}\\-?[2-9]{1}\\d{3,4}\\-?\\d{4}$",
            "^(\\+?351)?9[1236]\\d{7}$",
            "^(\\+?7|8)?9\\d{9}$",
            "^(\\+3816|06)[- \\d]{5,9}$",
            "^(\\+?90|0)?5\\d{9}$",
            "^(\\+?84|0)?((1(2([0-9])|6([2-9])|88|99))|(9((?!5)[0-9])))([0-9]{7})$",
            "^(\\+?0?86\\-?)?1[345789]\\d{9}$",
            "^(\\+?886\\-?|0)?9\\d{8}$"
    };

    //验证密码
    public static boolean checkPwd(String pwd) {
        Pattern pattern = Pattern.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,22}$");
        Matcher matcher = pattern.matcher(pwd);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    //(x,y)是否在view的区域内
    public static boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        //view.isClickable() &&
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }
}
