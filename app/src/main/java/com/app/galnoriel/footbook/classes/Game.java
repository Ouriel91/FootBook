package com.app.galnoriel.footbook.classes;

import com.google.android.gms.maps.model.LatLng;

import java.sql.DataTruncation;
import java.util.HashMap;

public class Game {
    private String pitch,date,price;
    private LatLng location;

    public static String PITCH_TYPE = "PITCH_TYPE";
    public static String GAME_PRICE = "GAME_PRICE";
    public static String GAME_DATE = "GAME_DATE";
    public static String GAME_LAT = "GAME_LAT";
    public static String GAME_LON = "GAME_LON";

    //region functions
    public String stringify(){
        HashMap<String,String> hash = new HashMap<>();
        hash.put(PITCH_TYPE,pitch);
        hash.put(GAME_PRICE,price);
        hash.put(GAME_DATE,date);
        hash.put(GAME_LAT,String.valueOf(location.latitude));
        hash.put(GAME_LON,String.valueOf(location.longitude));
        return hash.toString();
    }

    //endregion

    //region constructors
    public Game(HashMap hash){
        pitch = hash.get(PITCH_TYPE).toString();
        price = hash.get(GAME_PRICE).toString();
        date  = hash.get(GAME_DATE).toString();
        if (hash.get(GAME_LAT).toString() != null)
            location = new LatLng(Double.valueOf(hash.get(GAME_LAT).toString())
                    ,Double.valueOf(hash.get(GAME_LON).toString()));
        else location = null;
    }

    public Game(String date) {
        this.date = date;
        pitch = "Asphalt" ;
        price = "Free" ;
        location = null;
    }

    public Game(String pitch, String date, String price, LatLng location) {
        this.pitch = pitch;
        this.date = date;
        this.price = price;
        this.location = location;
    }
    //endregion

    //region getters and setters

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    //endregion

}