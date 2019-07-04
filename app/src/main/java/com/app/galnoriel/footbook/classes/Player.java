package com.app.galnoriel.footbook.classes;

import com.app.galnoriel.footbook.GlobConst;
import com.app.galnoriel.footbook.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private String _id,name,whereFrom,position,pitch, wherePlay,picture;
    private ArrayList<GroupPlay> groups;
    private Game next_game;

    //region functions
    public void removeGroup(GroupPlay group){
        groups.remove(group);

    }

    public void addGroup(GroupPlay group){
        groups.add(group);
    }

    public HashMap<String,Object> toHashMap(){
        HashMap<String, Object> user = new HashMap<>();
        user.put(GlobConst.DB_USER_NAME, getName());
        user.put(GlobConst.DB_USER_WHEREFROM, getWhereFrom());
        user.put(GlobConst.DB_USER_POSITION, getPosition());
        user.put(GlobConst.DB_USER_PITCH, getPitch());
        user.put(GlobConst.DB_USER_WHEREPLAY, getWherePlay());
        user.put(GlobConst.DB_USER_PICTURE, getPicture());
        user.put(GlobConst.DB_USER_GROUPS, getGroups());
        return user;
    }
    //endregion

    //region constructors


    public Player(String name, String whereFrom, String position, String pitch, String wherePlay, String picture, ArrayList<GroupPlay> groups, Game next_game) {
        this._id = null;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.groups = groups;
        this.next_game = next_game;
    }

    public  Player(HashMap user){
//        String _id,name,whereFrom,position,pitch, wherePlay,picture;
        set_id(user.get("id").toString());
        setName(user.get("name").toString());
        setWhereFrom(user.get("whereFrom").toString());
        setPosition(user.get("position").toString());
        setPitch(user.get("pitch").toString());
        setWherePlay(user.get("wherePlay").toString());
        setPicture(user.get("picture").toString());
    }
    public Player(String _id, String name, String whereFrom) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.groups = new ArrayList<>();
        this.wherePlay = "Not set";
        this.pitch = "Mixed";
        this.position = "Free Role";
        this.picture = null;
    }

    public Player(String _id, String name, String whereFrom, String position, String pitch, String wherePlay, ArrayList<GroupPlay> groups, String picture) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.groups = groups;
        this.picture = picture;
    }

    public Player(String _id, String name, String whereFrom, String position, String pitch, String wherePlay, String picture, ArrayList<GroupPlay> groups, Game next_game) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.groups = groups;
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

    public ArrayList<GroupPlay> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<GroupPlay> groups) {
        this.groups = groups;
    }
//endregion


}
