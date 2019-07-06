package com.app.galnoriel.footbook.classes;

import java.util.ArrayList;

public class GroupPlay {

    private String name, whenPlay,wherePlay,picture,id;
    private ArrayList<Player> members;

    public GroupPlay(String id, String name, String wherePlay, String whenPlay) {
        this.id = id;
        this.name = name;
        this.wherePlay = wherePlay;
        this.whenPlay = whenPlay;
    }


    //region functions
    public void removeMember(Player member) {
        for (int i = 0; i < members.size(); i++)
            if (member.get_id() == members.get(i).get_id()){
                members.remove(i);
                return;
            }
    }

    public void addMember(Player member){
        members.add(member);
    }
    //endregion

    //region constructors


    public GroupPlay(String id, String name, String wherePlay) {
        this.id = id;
        this.name = name;
        this.wherePlay = wherePlay;
        picture = null;
        members = null;
        whenPlay = null;

    }

    public GroupPlay(String id, String name, String whenPlay, String wherePlay, String picture, ArrayList<Player> members) {
        this.id = id;
        this.name = name;
        this.whenPlay = whenPlay;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.members = members;
    }

    //endregion

    //region getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhenPlay() {
        return whenPlay;
    }

    public void setWhenPlay(String whenPlay) {
        this.whenPlay = whenPlay;
    }

    public String getWherePlay() {
        return wherePlay;
    }

    public void setWherePlay(String wherePlay) {
        this.wherePlay = wherePlay;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<Player> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Player> members) {
        this.members = members;
    }

    //endregion
}
