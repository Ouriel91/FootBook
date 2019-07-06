package com.app.galnoriel.footbook.classes;

import android.util.Log;

import com.app.galnoriel.footbook.GlobConst;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Player {
    private String _id,name,whereFrom,position,pitch, wherePlay,picture;
    private ArrayList<String> groups_ids;
    private Game next_game;




    //region functions
    public void removeGroup(GroupPlay group){
        groups_ids.remove(group);

    }

    public void addGroup(String group){
        groups_ids.add(group);
    }

    public HashMap<String,Object> toHashMap(){
        HashMap<String, Object> user = new HashMap<>();
        user.put(GlobConst.DB_USER_NAME, getName());
        user.put(GlobConst.DB_USER_WHEREFROM, getWhereFrom());
        user.put(GlobConst.DB_USER_POSITION, getPosition());
        user.put(GlobConst.DB_USER_PITCH, getPitch());
        user.put(GlobConst.DB_USER_WHEREPLAY, getWherePlay());
        user.put(GlobConst.DB_USER_PICTURE, getPicture());
        user.put(GlobConst.DB_USER_GROUPS, getGroups_ids());
        return user;
    }

    public  String toLogString(){
        return get_id()+"\n"+getName()+"\n"+getWhereFrom()+"\n"+getPosition()+"\n"+getPitch()+"\n"+getWherePlay();
    }

    //endregion

    //region constructors
    public Player(){
        _id = "";
        name = "Guest";
        whereFrom = "City";
        wherePlay = "Anywhere";
        picture = null;
        position = "Free Role";
        pitch = "Asphalt";
        groups_ids = new ArrayList<String>();
    }

    public Player(DocumentSnapshot playerProfile) { //construct player from server
        _id = playerProfile.getId(); //document name is the user id
        name = playerProfile.getString(GlobConst.DB_USER_NAME);
        try{whereFrom = playerProfile.get(GlobConst.DB_USER_WHEREFROM).toString();}
        catch (Exception e){whereFrom = "City";}
        try {position = playerProfile.get(GlobConst.DB_USER_POSITION).toString();}
        catch (Exception e){position = "Free Role";}
        try{pitch = playerProfile.get(GlobConst.DB_USER_PITCH).toString();}
        catch (Exception e){pitch = "Asphalt";}
        try{wherePlay = playerProfile.get(GlobConst.DB_USER_WHEREPLAY).toString();}
        catch (Exception e){wherePlay = "Anywhere";}
        try{picture = playerProfile.getString(GlobConst.DB_USER_PICTURE).toString();}
        catch (Exception e){picture = null;}
        try {
            groups_ids.addAll((Collection) playerProfile.get(GlobConst.DB_USER_GROUPS));}
        catch (Exception e){groups_ids = new ArrayList<String>();}
        Log.d("Player Saved", _id+"\n"+name+"\n"+whereFrom+"\n"+position+"\n"+pitch+"\n"+ wherePlay+"\n"+picture +"\n"+groups_ids.toString());

    }

    public Player(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public Player(String name, String whereFrom, String position, String pitch, String wherePlay, String picture, ArrayList<String> groups_ids, Game next_game) {
        this._id = null;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.groups_ids = groups_ids;
        this.next_game = next_game;
    }

    public Player(HashMap user){
        _id = user.get("id").toString();
        name = user.get("name").toString();
        whereFrom = user.get("whereFrom").toString();
        position = user.get("position").toString();
        pitch = user.get("pitch").toString();
        wherePlay = user.get("wherePlay").toString();
        picture = user.get("picture").toString();
    }

    public Player(String _id, String name, String whereFrom) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.groups_ids = new ArrayList<String>();
        this.wherePlay = "Not set";
        this.pitch = "Mixed";
        this.position = "Free Role";
        this.picture = null;
    }

    public Player(String _id, String name, String whereFrom, String position, String pitch, String wherePlay, ArrayList<String> groups_ids, String picture) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.groups_ids = groups_ids;
        this.picture = picture;
    }

    public Player(String _id, String name, String whereFrom, String position, String pitch, String wherePlay, String picture, ArrayList<String> groups_ids, Game next_game) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.groups_ids = groups_ids;
        this.next_game = next_game;
    }
    //endregion

    //region getters and setters


    public Game getNext_game() {
        return next_game;
    }

    public void setNext_game(Game next_game) {
        this.next_game = next_game;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhereFrom() {
        return whereFrom;
    }

    public void setWhereFrom(String whereFrom) {
        this.whereFrom = whereFrom;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getWherePlay() {
        return wherePlay;
    }

    public void setWherePlay(String wherePlay) {
        this.wherePlay = wherePlay;
    }

    public ArrayList<String> getGroups_ids() {
        return groups_ids;
    }

    public void setGroups_ids(ArrayList<String> groups_ids) {
        this.groups_ids = groups_ids;
    }
//endregion


}
