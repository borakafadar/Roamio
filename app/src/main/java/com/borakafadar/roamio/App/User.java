package com.borakafadar.roamio.App;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.borakafadar.roamio.App.Save.Converter;
import com.borakafadar.roamio.App.Save.TripEntity;

import java.util.ArrayList;

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
        distance += tripEntity.getDistance();
        duration += parseToTime(tripEntity.getDuration());
        tripEntities.add(tripEntity);
    }

    public String parseTime(){
        long totalTime = duration;

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = totalTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

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

}
