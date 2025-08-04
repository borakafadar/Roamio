package com.borakafadar.roamio;

import android.location.Location;
import android.media.Image;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Trip {
    private ArrayList<Location> locations;
    private double time;
    private ArrayList<Image> images;
    private String comments;
    private String title;
    private double distance;
    private String date;

    public Trip(){
        Calendar calendar = Calendar.getInstance();
        this.date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar);
    }



    //https://mapsplatform.google.com/resources/blog/how-calculate-distances-map-maps-javascript-api/
    //https://en.wikipedia.org/wiki/Haversine_formula
    //not used for now, maybe i could use it some other time
    public double haversineFormula(double lat1,double lat2, double long1, double long2) {

        double R = 6371.2; // Radius of the Earth in kilometers

        double rlat1 = lat1 * (Math.PI / 180); // Convert degrees to radians
        double rlat2 = lat2 * (Math.PI / 180); // Convert degrees to radians
        double difflat = rlat2 - rlat1; // Radian difference (latitudes), delta lat
        double difflon = (long2 - long1) * (Math.PI / 180); // Radian difference (longitudes), delta lon
        double d = 2 * R * Math.asin(Math.sqrt(Math.sin(difflat / 2) * Math.sin(difflat / 2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(difflon / 2) * Math.sin(difflon / 2)));
        return d;

    }

    public void calculateTotalDistance(int index){
        if(index == 0){
            distance += 0;
        } else{
            distance += locations.get(index).distanceTo(locations.get(index -1));
            calculateTotalDistance(index - 1);
        }
    }

}
