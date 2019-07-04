package com.app.galnoriel.footbook.classes;

import java.util.ArrayList;

public class Player {
    private int _id;
    private String name,whereFrom,position,pitch, wherePlay,picture;
    private ArrayList<GroupPlay> groups;
    private Game next_game;


    //region functions
    public void removeGroup(GroupPlay group){
        groups.remove(group);

    }

    public void addGroup(GroupPlay group){
        groups.add(group);
    }
    //endregion

    //region constructors
    public Player(int _id, String name, String whereFrom) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.groups = new ArrayList<>();
        this.wherePlay = "Not set";
        this.pitch = "Mixed";
        this.position = "Free Role";
        this.picture = null;
    }

    public Player(int _id, String name, String whereFrom, String position, String pitch, String wherePlay, ArrayList<GroupPlay> groups, String picture) {
        this._id = _id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.position = position;
        this.pitch = pitch;
        this.wherePlay = wherePlay;
        this.groups = groups;
        this.picture = picture;
    }

    public Player(int _id, String name, String whereFrom, String position, String pitch, String wherePlay, String picture, ArrayList<GroupPlay> groups, Game next_game) {
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



    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
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
