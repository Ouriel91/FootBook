package com.app.galnoriel.footbook.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CustomSharedPrefAdapter {
    private SharedPreferences sharedPreferences;
    private Context context;

    //region constants
    public static String PREFS_NAME = "PREFS_NAME";
    public static String FIRST_INITIALIZE = "FIRST_INITIALIZE";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_WHERE_FROM = "whereFrom";
    public static String USER_POSITION = "position";
    public static String USER_PITCH = "pitch";
    public static String USER_WHERE_PLAY = "wherePlay";
    public static String USER_PICTURE = "picture";
    public static String USER_GROUPS = "groups";
    public static String NEXT_GAME_ID = "next_game_id";
    public static String NEXT_GAME_LOCATION = "NEXT_GAME_LOCATION";
    public static String NEXT_GAME_PRICE = "NEXT_GAME_PRICE";
    public static String NEXT_GAME_PITCH = "NEXT_GAME_PITCH";
    public static String DEFAULT_GROUP_ID = "DEFAULT_GROUP_ID";
    public static String DEFAULT_GROUP_NAME = "DEFAULT_GROUP_NAME";
    public static String DEFAULT_GROUP_WHERE_PLAY = "DEFAULT_GROUP_WHERE_PLAY";
    public static String DEFAULT_GROUP_TIME = "DEFAULT_GROUP_TIME";
    public static String DEFAULT_GROUP_PICTURE = "DEFAULT_GROUP_PICTURE";
    public static String DEFAULT_GROUP_MEMBERS = "DEFAULT_GROUP_MEMBERS";
    //endregion


    public CustomSharedPrefAdapter(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME,context.MODE_PRIVATE);
    }

    public void putString(String[] keys, String[] values){
        for (int i=0; i < keys.length;i++)
            sharedPreferences.edit().putString(keys[i],values[i]).apply();
    }
    
    public void putString(String key , String value){
        sharedPreferences.edit().putString(key,value).apply();
    }
     
    public int getInt(String key, int defaultValue){
       return sharedPreferences.getInt(key,defaultValue);
    }
    
    public HashMap<String,String> getMapOfParams(String[] keys){
        HashMap<String,String> values = null;
        for (String key: keys) {
            values.put(key,sharedPreferences.getString(key,"Not Found"));
        }
          return values;
    }
    
    public String getString(String key , String defaultValue){
        return sharedPreferences.getString(key,defaultValue);
    }
    
    
    
    public void putInt(String key, int value){
        sharedPreferences.edit().putInt(key,value).apply();
    }

    public void setDefaultGroup(GroupPlay g){
        sharedPreferences.edit()
                .putInt(DEFAULT_GROUP_ID,g.getId())
                .putString(DEFAULT_GROUP_NAME,g.getName())
                .putString(DEFAULT_GROUP_TIME,g.getTime())
                .putString(DEFAULT_GROUP_WHERE_PLAY,g.getWherePlay())
                .putString(DEFAULT_GROUP_PICTURE,g.getPicture())
                .putString(DEFAULT_GROUP_MEMBERS,g.getMembers().toString())
                .apply();
            //also set default group id on server
    }

    public void setNextGame(Game g){
        sharedPreferences.edit()
                .putString(NEXT_GAME_LOCATION,g.getLocation().toString())
                .putString(NEXT_GAME_PITCH,g.getPitch())
                .putString(NEXT_GAME_PRICE,g.getPrice())
                .apply();
    }

}
