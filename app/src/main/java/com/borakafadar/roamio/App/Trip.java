package com.borakafadar.roamio.App;

import android.location.Location;
import android.media.Image;
import android.util.Log;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class Trip {
    private int tripID;
    private ArrayList<TripSegment> tripSegments;
    private long time;
    //private ArrayList<Image> images;
    private String comments;
    private String title;
    private double distance;
    private String date;
    private long timestampMillis; //for time calculation
    private boolean tripStopped;
    private TripSegment latestSegment;


    //TODO: maybe i can add an option to merge trips.

    //TODO: implement Image saving
    public Trip(){
        LocalDateTime localDateTime = LocalDateTime.now();
        date = localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy--HH-mm-ss"));
        //Log.d("a",date);
        timestampMillis = Instant.now().toEpochMilli();
        time = 0;
        //images = new ArrayList<>();
        comments = "";
        title = "Trip "+date;
        distance = 0;
        tripStopped = true;
        latestSegment = new TripSegment();
        tripSegments = new ArrayList<>();
        tripSegments.add(latestSegment);

    }



    //https://mapsplatform.google.com/resources/blog/how-calculate-distances-map-maps-javascript-api/
    //https://en.wikipedia.org/wiki/Haversine_formula
    //not used for now, maybe i could use it some other time
    public static double haversineFormula(double lat1,double lat2, double long1, double long2) {

        double R = 6371.2; // Radius of the Earth in kilometers

        double rlat1 = lat1 * (Math.PI / 180); // Convert degrees to radians
        double rlat2 = lat2 * (Math.PI / 180); // Convert degrees to radians
        double difflat = rlat2 - rlat1; // Radian difference (latitudes), delta lat
        double difflon = (long2 - long1) * (Math.PI / 180); // Radian difference (longitudes), delta lon
        double d = 2 * R * Math.asin(Math.sqrt(Math.sin(difflat / 2) * Math.sin(difflat / 2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(difflon / 2) * Math.sin(difflon / 2)));
        return d;

    }

    public void calculateTotalDistance(){
        if(tripStopped){
            return;
        }
        distance = 0;
        for(TripSegment tripSegment : tripSegments){
            distance += tripSegment.getDistance();
        }
    }


    public String getDateTimeString(){
        return date;
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

    private String parseTime(){
        calculateTime();
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);

        return String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getDuration(){
        return parseTime();
    }

    public void stopTrip(boolean tripStopped){
        this.tripStopped = tripStopped;
        timestampMillis = Instant.now().toEpochMilli();
    }

    public String getDistanceString(){
        calculateTotalDistance();
        return Double.toString(distance);
    }

    public void changeSegments(TripSegment tripSegment){
        tripSegments.add(tripSegment);
        this.latestSegment = tripSegment;
    }

    public TripSegment getLatestSegment(){
        return latestSegment;
    }

    public ArrayList<TripSegment> getTripSegments(){
        return tripSegments;
    }

    public String getComments(){
        return comments;
    }
    public double getDistance(){
        return distance;
    }
    public String getTitle(){
        return title;
    }


}
