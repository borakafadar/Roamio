package com.borakafadar.roamio.App.Save;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
}
