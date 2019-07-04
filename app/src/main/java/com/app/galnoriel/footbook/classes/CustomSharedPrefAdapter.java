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
    private static String PREFS_NAME = "PREFS_NAME";
    private static String FIRST_INITIALIZE = "FIRST_INITIALIZE";
    private static String USER_ID = "user_id";
    private static String USER_NAME = "user_name";
    private static String USER_WHERE_FROM = "whereFrom";
    private static String USER_POSITION = "position";
    private static String USER_PITCH = "pitch";
    private static String USER_WHERE_PLAY = "wherePlay";
    private static String USER_PICTURE = "picture";
    private static String USER_GROUPS = "groups";
    private static String NEXT_GAME_ID = "next_game_id";
    private static String NEXT_GAME_LOCATION = "NEXT_GAME_LOCATION";
    private static String NEXT_GAME_PRICE = "NEXT_GAME_PRICE";
    private static String NEXT_GAME_PITCH = "NEXT_GAME_PITCH";
    private static String DEFAULT_GROUP_ID = "DEFAULT_GROUP_ID";
    private static String DEFAULT_GROUP_NAME = "DEFAULT_GROUP_NAME";
    private static String DEFAULT_GROUP_WHERE_PLAY = "DEFAULT_GROUP_WHERE_PLAY";
    private static String DEFAULT_GROUP_TIME = "DEFAULT_GROUP_TIME";
    private static String DEFAULT_GROUP_PICTURE = "DEFAULT_GROUP_PICTURE";
    private static String DEFAULT_GROUP_MEMBERS = "DEFAULT_GROUP_MEMBERS";
    //endregion

//    public void saveProfileChanges(Player user){
//
//    }

    public String getUserId(){
        return getString(USER_ID,"Not Found");
    }
    public void removeCurrentUserInfo(){
        sharedPreferences.edit()
                .remove(USER_ID).remove(USER_NAME).remove(USER_WHERE_FROM)
                .remove(USER_POSITION).remove(USER_PITCH).remove(USER_WHERE_PLAY)
                .remove(USER_PICTURE).remove(USER_GROUPS).apply();
    }
    public void setUserId(String currentUserId){
        putString(USER_ID,currentUserId);
    }
    public CustomSharedPrefAdapter(Context context) { //constructor
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
        HashMap<String,String> values = new HashMap<>();
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
