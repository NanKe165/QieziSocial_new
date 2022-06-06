package com.eggplant.qiezisocial.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Administrator on 2018/1/9.
 */
public class StorageUtil {
    /**
     * SharedPreferences
     */
    public static void SPSave(Context context, String filename, Map<String, Object> map){
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Map.Entry<String, Object> entry : map.entrySet()){
            if(entry.getValue() instanceof String){
                editor.putString(entry.getKey(), (String) entry.getValue());
            }else if(entry.getValue() instanceof Integer){
                editor.putInt(entry.getKey(), (Integer) entry.getValue());
            }else if(entry.getValue() instanceof Boolean){
                editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
            }else if(entry.getValue() instanceof Float){
                editor.putFloat(entry.getKey(), (Float) entry.getValue());
            }
        }
        editor.commit();
    }

//    public static String SPGet(Context context, String filename, String key){
//        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
//        return sp.getString(key, "");
//    }


    private static final String SETTING = "Setting";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }
}
