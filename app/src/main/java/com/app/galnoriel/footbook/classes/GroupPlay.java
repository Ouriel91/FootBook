package com.app.galnoriel.footbook.classes;

import android.util.Log;

import com.app.galnoriel.footbook.GlobConst;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class GroupPlay {
    private String name, whenPlay,wherePlay,picture,id;
    private ArrayList<String> members_id,admins_id;
    @Nullable
    private Game nextGame;




    //region functions

    public HashMap<String,Object> toHashMap(){
        HashMap<String, Object> group = new HashMap<>();
        group.put(GlobConst.DB_GROUP_NAME, getName());
        group.put(GlobConst.DB_GROUP_WHENPLAY, getWhenPlay());
        group.put(GlobConst.DB_GROUP_NEXT_GAME,null);
        group.put(GlobConst.DB_GROUP_WHEREPLAY, getWherePlay());
        group.put(GlobConst.DB_GROUP_PICTURE, getPicture());
        group.put(GlobConst.DB_GROUP_MEMBERS, getMembers_id());
        group.put(GlobConst.DB_GROUP_ADMINS, getAdmins_id());

        Log.d("Group.toHash","hashed this: "+group.toString());
        return group;
    }

    public void removeMember(String member_id) {
        for (int i = 0; i < members_id.size(); i++)
            if (member_id.equals(members_id.get(i))){
                members_id.remove(i);
                return;
            }
    }

    public void addMember(String member){
        members_id.add(member);
    }

    public String toString(){
        return getId()+"\n"+getName()+"\n"+getPicture()+"\n"+getWhenPlay()+
                "\n"+getWherePlay()+"\n"+getAdmins_id().toString()+"\n"
                +getMembers_id().toString()+"\n"+getNextGame();
    }
    //endregion

    //region constructors

    public GroupPlay(String displayGroupId, String name, String wherePlay, String whenPlay, String picture, ArrayList<String> member_id,
                     ArrayList<String> admins_id, Game nextGame) {
        this.id = displayGroupId;
        this.name = name;
        this.whenPlay = whenPlay;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.members_id = member_id;
        this.admins_id = admins_id;
        this.nextGame = nextGame;


    }

    public GroupPlay(DocumentSnapshot group) { //construct player from server
        id = group.getId(); //document name is the group id
        name = group.getString(GlobConst.DB_GROUP_NAME);
        try{whenPlay= group.get(GlobConst.DB_USER_WHEREFROM).toString();}
        catch (Exception e){whenPlay = "City";e.printStackTrace();}
//        try {nextGame = group.get(GlobConst.DB_GROUP_NEXT_GAME).toString();}
//        catch (Exception e){nextGame = "Free Role";}
        nextGame= null;
        try{whenPlay = group.get(GlobConst.DB_GROUP_WHENPLAY).toString();}
        catch (Exception e){whenPlay = "Asphalt";e.printStackTrace();}
        try{wherePlay = group.get(GlobConst.DB_USER_WHEREPLAY).toString();}
        catch (Exception e){wherePlay = "Anywhere";e.printStackTrace();}
        try{picture = group.getString(GlobConst.DB_GROUP_PICTURE).toString();}
        catch (Exception e){picture = "";e.printStackTrace();}
        try {members_id = (ArrayList<String>) group.get(GlobConst.DB_GROUP_MEMBERS);
            Log.d("GROUP members ",members_id.get(0));}
        catch (Exception e){members_id = new ArrayList<String>();}
        try {admins_id = (ArrayList<String>) group.get(GlobConst.DB_GROUP_ADMINS);
            Log.d("GROUP admins ",admins_id.get(0));}
        catch (Exception e){admins_id = new ArrayList<String>();}
        Log.d("Group from server: ", group.toString());

    }



    public GroupPlay(String id, String name, String wherePlay, String whenPlay) {
        this.id = id;
        this.name = name;
        this.wherePlay = wherePlay;
        this.whenPlay = whenPlay;
        members_id = new ArrayList<String>();
        admins_id = new ArrayList<String>();
    }

    public GroupPlay(String id, String name, String wherePlay) {
        this.id = id;
        this.name = name;
        this.wherePlay = wherePlay;
        picture = null;
        members_id = new ArrayList<String>();
        whenPlay = null;
        admins_id = new ArrayList<String>();

    }
    public GroupPlay(String id, String name, String whenPlay, String wherePlay, String picture, ArrayList<String> members_id) {
        this.id = id;
        this.name = name;
        this.whenPlay = whenPlay;
        this.wherePlay = wherePlay;
        this.picture = picture;
        this.members_id = members_id;
    }

    //endregion

    //region getters and setters


    public ArrayList<String> getAdmins_id() {
        return admins_id;
    }

    public void setAdmins_id(ArrayList<String> admins_id) {
        this.admins_id = admins_id;
    }

    public ArrayList<String> getMembers_id() {
        return members_id;
    }

    public void setMembers_id(ArrayList<String> members_id) {
        this.members_id = members_id;
    }

    public Game getNextGame() {
        return nextGame;
    }

    public void setNextGame(Game nextGame) {
        this.nextGame = nextGame;
    }

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

    public void addAdmins(String userId) {
        admins_id.add(userId);
    }


    //endregion
}
