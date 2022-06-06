package com.eggplant.qiezisocial.greendao.entry;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/1/11.
 */

public class ListConverter implements PropertyConverter<List<String>, String> {

    @Override
    public ArrayList<String> convertToEntityProperty(String databaseValue) {
        ArrayList<String> arrayList;
        if (databaseValue == null) {
            return null;
        } else {
            List<String> list = Arrays.asList(databaseValue.split("&&"));
            arrayList = new ArrayList<>(list);
            return arrayList;
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String entity : entityProperty) {
                sb.append(entity);
                sb.append("&&");
            }
            return sb.toString();
        }
    }
}


