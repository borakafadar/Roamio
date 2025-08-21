package com.borakafadar.roamio.App;

import android.content.Context;
import android.location.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.borakafadar.roamio.App.Save.Converter;
import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Save.TripEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "user")
public class User {
    //TODO: later
    //public ArrayList<Location> pointOfInterests;
    @PrimaryKey (autoGenerate = true)
    public int userID;

    public String name;
    public double distance;
    public long duration;
    public String appSettingsJson;
    //FIXME make a type converter thingy
    public transient ArrayList<TripEntity> tripEntities;
    public String tripEntitiesJson;

    public User(String name, String appSettingsJson){
        this.name = name;
        this.appSettingsJson = appSettingsJson;
        this.distance = 0;
        this.duration = 0;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public String getAppSettingsJson() {
        return appSettingsJson;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAppSettingsJson(String appSettingsJson) {
        this.appSettingsJson = appSettingsJson;
    }

    public void addTrip(TripEntity tripEntity){
        tripEntities.add(tripEntity);
        calculateData();
    }

    public void calculateData(){
        distance = 0;
        duration = 0;
        for(TripEntity trip : tripEntities){
            distance += trip.getDistance();
            duration += parseToTime(trip.getDuration());
        }
    }

    public String parseTime(){
        long totalTime = duration;

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = totalTime % 60;

        return String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds);

    }

    //formatted 01:23:45
    public long parseToTime(String timeString){
        String[] timeStrings = timeString.split(":");

        long hours = Long.parseLong(timeStrings[0]);
        long minutes = Long.parseLong(timeStrings[1]);
        long seconds = Long.parseLong(timeStrings[2]);

        return hours * 3600 + minutes * 60 + seconds;

    }

    public ArrayList<TripEntity> getTripEntities(){
        return tripEntities;
    }

    public void setTripEntitiesFromJson(){
        if (tripEntitiesJson == null){
            tripEntities = new ArrayList<TripEntity>();
            return;
        }
        tripEntities = Converter.convertJsonToTripEntities(tripEntitiesJson);
    }
    public void setTripEntitiesToJson(){
        if(tripEntities == null){
            return;
        }
        tripEntitiesJson = Converter.convertTripEntitiesToJson(tripEntities);
    }

    public void setTripEntitiesFromTripsTable(Context context){
        SaveManager.getAllTrips(context, new SaveManager.TripsCallback() {
            @Override
            public void onTripsLoaded(List<TripEntity> trips) {
                tripEntities = (ArrayList<TripEntity>) trips;
                calculateData();
                SaveManager.updateUser(context, User.this);
            }

            @Override
            public void onTripLoaded(TripEntity trip) {
                //nothing
            }
        });
    }

}
