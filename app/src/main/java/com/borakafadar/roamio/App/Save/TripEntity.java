package com.borakafadar.roamio.App.Save;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.borakafadar.roamio.App.TripSegment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

@Entity(tableName = "trips")
public class TripEntity {
    @PrimaryKey (autoGenerate = true)
    public int tripID;

    public String date;
    public String duration;
    public double distance;
    public String title;
    public String comments;
    public String segmentsJson;

    public int getTripID() {
        return tripID;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public String getTitle() {
        return title;
    }

    public String getComments() {
        return comments;
    }

    public String getSegmentsJson() {
        return segmentsJson;
    }
    public ArrayList<TripSegment> getTripSegments(){
        return Converter.convertJsonToTripSegments(getSegmentsJson());
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    //FIXME: this results in an runtime error java.util.ConcurrentModificationException
//    @NonNull
//    @Override
//    public String toString() {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(this);
//    }
}
