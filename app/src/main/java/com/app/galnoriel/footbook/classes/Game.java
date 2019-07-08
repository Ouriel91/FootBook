package com.app.galnoriel.footbook.classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class Game {
    private String pitch,date,price;
//    private LatLng location;
    private String location;

    public static String PITCH_TYPE = "PITCH_TYPE";
    public static String GAME_PRICE = "GAME_PRICE";
    public static String GAME_DATE = "GAME_DATE";
    public static String GAME_LAT = "GAME_LAT";
    public static String GAME_LON = "GAME_LON";
    public static String GAME_LOCATION = "GAME_LOCATION";

    //region functions

    public HashMap<String,Object> toHashMap(){
        HashMap<String, Object> game = new HashMap<>();
        try {
            game.put(PITCH_TYPE, getPitch());
            game.put(GAME_PRICE, getPrice());
            game.put(GAME_DATE, getDate());
            game.put(GAME_LOCATION, getLocation());
        }catch (Exception e){e.printStackTrace();return null;}
//        game.put(GAME_LAT, getLocation().latitude);
//        game.put(GAME_LON, getLocation().longitude);
        return game;
    }

    public String stringify(){
        HashMap<String,String> hash = new HashMap<>();
        hash.put(PITCH_TYPE,pitch);
        hash.put(GAME_PRICE,price);
        hash.put(GAME_DATE,date);
//        hash.put(GAME_LAT,String.valueOf(location.latitude));
//        hash.put(GAME_LON,String.valueOf(location.longitude));
        hash.put(GAME_LOCATION,location);
        return hash.toString();
    }

    //endregion

    //region constructors
    public Game(DocumentSnapshot game) {
        try {
            pitch = game.getString(PITCH_TYPE);
        } catch (Exception e) {
            pitch = "Synthetic";
        }
        try {
            price = game.getString(GAME_PRICE);
        } catch (Exception e) {
            price = "Free";
        }
        try {
            date = game.getString(GAME_DATE);
        } catch (Exception e) {
            date = "Not Set";
        }
//        try{location = new LatLng(Double.valueOf(game.getString(GAME_LAT)),Double.valueOf(game.getString(GAME_LON))); }
        try {location = game.getString(GAME_LOCATION);}
        catch(Exception e){location = null;e.printStackTrace();}

        }

    public Game(HashMap hash) {
        try {pitch = hash.get(PITCH_TYPE).toString();}
        catch (Exception e){pitch = "Grass";}
        try {price = hash.get(GAME_PRICE).toString();}
        catch (Exception e){price = "";e.printStackTrace();}
        try {date = hash.get(GAME_DATE).toString();}
        catch (Exception e){date = "";e.printStackTrace();}
        try{location = hash.get(GAME_LOCATION).toString();}
        catch (Exception e){location = "";e.printStackTrace();}
        }

    public Game(String date) {
            this.date = date;
            pitch = "Asphalt";
            price = "Free";
            location = null;
        }

    public Game(String pitch, String date, String price, String location) {
            this.pitch = pitch;
            this.date = date;
            this.price = price;
            this.location = location;
        }
        //endregion

        //region getters and setters

        public String getPitch () {
            return pitch;
        }

        public void setPitch (String pitch){
            this.pitch = pitch;
        }

        public String getDate () {
            return date;
        }

        public void setDate (String date){
            this.date = date;
        }

        public String getPrice () {
            return price;
        }

        public void setPrice (String price){
            this.price = price;
        }

        public String getLocation () {
            return location;
        }

        public void setLocation (String location){
            this.location = location;
        }


//endregion
    }