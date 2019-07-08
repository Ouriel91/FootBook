package com.app.galnoriel.footbook.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class CustomSharedPrefAdapter {
    private SharedPreferences sharedPreferences;
    private Context context;

    //region constants
    private static String PREFS_NAME = "PREFS_NAME";
    private static String IS_SIGNED_IN = "IS_SIGNED_IN";
    private static String FIRST_INITIALIZE = "FIRST_INITIALIZE";
    private static String MY_USER_ID = "user_id";
    private static String USER_NAME = "user_name";
    private static String USER_WHERE_FROM = "whereFrom";
    private static String USER_POSITION = "position";
    private static String USER_PITCH = "pitch";
    private static String USER_WHERE_PLAY = "wherePlay";
    private static String USER_PICTURE = "picture";
    private static String USER_GROUPS = "groups";
    private static String DISPLAY_USER_ID = "DISPLAY__USER_ID";
    private static String DISPLAY_USER_NAME = "DISPLAY_USER_NAME";
    private static String DISPLAY_USER_WHERE_FROM = "DISPLAY_USER_WHERE_FROM";
    private static String DISPLAY_USER_POSITION = "DISPLAY_USER_POSITION";
    private static String DISPLAY_USER_PITCH = "DISPLAY_USER_PITCH";
    private static String DISPLAY_USER_WHERE_PLAY = "DISPLAY_USER_WHERE_PLAY";
    private static String DISPLAY_USER_PICTURE = "DISPLAY_USER_PICTURE";
    private static String DISPLAY_USER_GROUPS = "DISPLAY_USER_GROUPS";
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
    private static String DISPLAY_GROUP_ID = "DISPLAY_GROUP_ID";
    private static String DISPLAY_GROUP_NAME = "DISPLAY_GROUP_NAME";
    private static String DISPLAY_GROUP_WHERE_PLAY = "DISPLAY_GROUP_WHERE_PLAY";
    private static String DISPLAY_GROUP_TIME = "DISPLAY_GROUP_TIME";
    private static String DISPLAY_GROUP_PICTURE = "DISPLAY_GROUP_PICTURE";
    private static String DISPLAY_GROUP_MEMBERS = "DISPLAY_GROUP_MEMBERS";
    //endregion

//    public void saveProfileChanges(Player user){
//
//    }

    public GroupPlay getDisplayGroup(){
        String tmp = sharedPreferences.getString(DISPLAY_GROUP_MEMBERS,null);
        ArrayList<String> members = null;
        try {
            members = (ArrayList<String>) Arrays.asList(tmp.split(","));
        }catch (Exception e){e.printStackTrace(); }
        return new GroupPlay( sharedPreferences.getString(DISPLAY_GROUP_ID,getDisplayGroupId()),
        sharedPreferences.getString(DISPLAY_GROUP_NAME,null),
        sharedPreferences.getString(DISPLAY_GROUP_TIME,null),
        sharedPreferences.getString(DISPLAY_GROUP_WHERE_PLAY,null),
        sharedPreferences.getString(DISPLAY_GROUP_PICTURE,null),
        members);

    }

    public boolean getLoginStatus(){
        return sharedPreferences.getBoolean(IS_SIGNED_IN,false);
    }

    public void setLoginStatus(boolean isLogged){
        sharedPreferences.edit().putBoolean(IS_SIGNED_IN,isLogged).apply();
    }

    public String getUserId(){
        return getString(MY_USER_ID,"Not Found");
    }

    public void setDisplayProfile(Player player) {
        sharedPreferences.edit()
                .putString(DISPLAY_USER_ID,player.get_id())
                .putString(DISPLAY_USER_NAME,player.getName())
                .putString(DISPLAY_USER_WHERE_FROM,player.getWhereFrom())
                .putString(DISPLAY_USER_POSITION,player.getPosition())
                .putString(DISPLAY_USER_PITCH,player.getPitch())
                .putString(DISPLAY_USER_WHERE_PLAY,player.getWherePlay())
//                .putString(DISPLAY_USER_PICTURE,player.getPicture())
                .putString(DISPLAY_USER_GROUPS,player.getGroups_ids().toString())
                .apply();
        Log.d("CURRENT display user:\n",player.get_id());
    }

    public Player getDisplayProfile() {
        Player player = new Player(
                sharedPreferences.getString(DISPLAY_USER_ID,getUserId()),
                sharedPreferences.getString(DISPLAY_USER_NAME,null),
                sharedPreferences.getString(DISPLAY_USER_WHERE_FROM,null),
                sharedPreferences.getString(DISPLAY_USER_POSITION,null),
                sharedPreferences.getString(DISPLAY_USER_PITCH,null),
                sharedPreferences.getString(DISPLAY_USER_WHERE_PLAY,null),
                sharedPreferences.getString(DISPLAY_USER_PICTURE,null)
        );
        Log.d("CURRENT display user:\n",player.toLogString());
        return player;
    }

    public void clearDisplayProfileInfo(){
        sharedPreferences.edit()
                .remove(DISPLAY_USER_ID).remove(DISPLAY_USER_NAME).remove(DISPLAY_USER_WHERE_FROM)
                .remove(DISPLAY_USER_POSITION).remove(DISPLAY_USER_PITCH).remove(DISPLAY_USER_WHERE_PLAY)
                .remove(DISPLAY_USER_PICTURE).remove(DISPLAY_USER_GROUPS).apply();
    }

    public void setCurrentUser(Player player) {
        sharedPreferences.edit()
                .putString(MY_USER_ID,player.get_id())
                .putString(USER_NAME,player.getName())
                .putString(USER_WHERE_FROM,player.getWhereFrom())
                .putString(USER_POSITION,player.getPosition())
                .putString(USER_PITCH,player.getPitch())
                .putString(USER_WHERE_PLAY,player.getWherePlay())
//                .putString(USER_PICTURE,player.getPicture())
//                .putString(USER_GROUPS,player.getGroups_ids().toString())
                .apply();
        Log.d("CURRENT display user:\n",player.toLogString());
    }

    public void removeCurrentUserInfo(){
        sharedPreferences.edit()
                .remove(MY_USER_ID).remove(USER_NAME).remove(USER_WHERE_FROM)
                .remove(USER_POSITION).remove(USER_PITCH).remove(USER_WHERE_PLAY)
                .remove(USER_PICTURE).remove(USER_GROUPS).apply();
    }

    public void setUserId(String currentUserId){
        putString(MY_USER_ID,currentUserId);
    }

    public void setDisplayGroupId(String groupId){
        putString(DISPLAY_GROUP_ID,groupId);
    }

    public String getDisplayGroupId(){
        return getString(DISPLAY_GROUP_ID,null);
    }

    public void setDisplayProfileId(String groupId){
        putString(DISPLAY_USER_ID,groupId);
    }

    public String getDisplayUserId(){
        return getString(DISPLAY_USER_ID,null);
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
                .putString(DEFAULT_GROUP_ID,g.getId())
                .putString(DEFAULT_GROUP_NAME,g.getName())
                .putString(DEFAULT_GROUP_TIME,g.getWhenPlay())
                .putString(DEFAULT_GROUP_WHERE_PLAY,g.getWherePlay())
                .putString(DEFAULT_GROUP_PICTURE,g.getPicture())
                .putString(DEFAULT_GROUP_MEMBERS,g.getMembers_id().toString())
                .apply();
        //also set default group id on server
    }

    public void setDisplayGroup(GroupPlay g){
        sharedPreferences.edit()
                .putString(DISPLAY_GROUP_ID,g.getId())
                .putString(DISPLAY_GROUP_NAME,g.getName())
                .putString(DISPLAY_GROUP_TIME,g.getWhenPlay())
                .putString(DISPLAY_GROUP_WHERE_PLAY,g.getWherePlay())
                .putString(DISPLAY_GROUP_PICTURE,g.getPicture())
                .putString(DISPLAY_GROUP_MEMBERS,g.getMembers_id().toString())
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
