package com.app.galnoriel.footbook.maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParsing {

    private HashMap<String, String> getPlace(JSONObject jsonObject){

        HashMap<String, String> placeHM = new HashMap<>();

        String placeName = "";
        String vicinity = "";
        String latitude = "";
        String longtitude = "";
        //String reference = "";

        try {

            placeName = jsonObject.getString("name");
            vicinity = jsonObject.getString("vicinity");
            latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
            //reference = jsonObject.getString("reference");

            placeHM.put("place_name",placeName);
            placeHM.put("vicinity",vicinity);
            placeHM.put("lat",latitude);
            placeHM.put("lng",longtitude);
            //placeHM.put("ref",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return placeHM;
    }

    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){

        int count = jsonArray.length();
        List<HashMap<String,String>> placeList = new ArrayList<>();
        HashMap<String,String> placeHM = null;

        for (int i = 0; i < count ; i++) {

            try {

                placeHM = getPlace((JSONObject) jsonArray.get(i));
                placeList.add(placeHM);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placeList;
    }


    public List<HashMap<String,String>> parse(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }
}
