package com.app.galnoriel.footbook.maps;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GetNearByPlaces extends AsyncTask<Object,String,String> {

    private String data;
    private GoogleMap map;
    String url;

    @Override
    protected String doInBackground(Object... objects) {

        map = (GoogleMap) objects[0];
        url = (String) objects[1];

        try {
            data = readUrl(url);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return data;
    }


    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String,String>> nearbyList;
        DataParsing parsing = new DataParsing();
        nearbyList = parsing.parse(s);
        showNearbyPlaces(nearbyList);
    }



    private String readUrl(String myUrl) throws IOException {

        String data = "";
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        try {

            URL url = new URL(myUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null){

                sb.append(line);
            }

            data = sb.toString();
            br.close();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        finally {

            inputStream.close();
            connection.disconnect();
        }

        return data;
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyList) {

        for (int i = 0; i < nearbyList.size(); i++) {

            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> placeHM = nearbyList.get(i);

            String placeName = placeHM.get("place_name");
            String vicinity = placeHM.get("vicinity");
            Double lat = Double.parseDouble(placeHM.get("lat"));
            Double lng = Double.parseDouble(placeHM.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + ":" + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            map.addMarker(markerOptions);
//            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }
}
