package com.eggplant.qiezisocial.entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/3/2.
 */

public class FilterEntry implements Serializable{
    public String goal="";
    public String people="";
    public String sid="";
    public String type;
    public boolean fav;
    public String moment="";
    public ScenesEntry scenes;
    @Override
    public String toString() {
        return "goal:"+goal+"\t\n people:"+people;
    }
}
