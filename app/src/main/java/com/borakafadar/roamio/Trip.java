package com.borakafadar.roamio;

import android.location.Location;
import android.media.Image;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class Trip {
    private ArrayList<Location> locations;
    private long time;
    private ArrayList<Image> images;
    private String comments;
    private String title;
    private double distance;
    private String date;
    private LocalDateTime localDateTime;
    private long timestampMillis; //for time calculation
    private boolean tripStopped;

    public Trip(){
        localDateTime = LocalDateTime.now();
        timestampMillis = Instant.now().toEpochMilli();
        locations = new ArrayList<>();
        time = 0;
        images = new ArrayList<>();
        comments = "";
        title = "";
        distance = 0;
        date = "";
        tripStopped = true;
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

    public void addLocation(Location location){
        locations.add(location);
    }
    public Location getLastLocation(){
        return locations.get(locations.size() - 1);
    }
    public ArrayList<Location> getLocations(){
        return locations;
    }

    public String getDateTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return localDateTime.format(formatter);
    }

    //1 second
    public void calculateTime(){
        if(!tripStopped){
            long currentTime = Instant.now().toEpochMilli();
            long timeDifference = currentTime - timestampMillis;
            time += (long) (timeDifference / 1000.0);
            timestampMillis = currentTime;
        }
        //return (long) time;
    }

    public String parseTime(){
        calculateTime();
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);

        return String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void stopTrip(boolean tripStopped){
        this.tripStopped = tripStopped;
        timestampMillis = Instant.now().toEpochMilli();
    }

}
