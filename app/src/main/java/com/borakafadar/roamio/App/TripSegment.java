package com.borakafadar.roamio.App;

import android.location.Location;

import com.borakafadar.roamio.App.Trip;

import java.util.ArrayList;

public class TripSegment {
    private ArrayList<SerializableLocation> serializableLocations;

    private transient ArrayList<Location> locations;
    private double distance;

    public TripSegment(){
        this.locations = new ArrayList<>();
        this.serializableLocations = new ArrayList<>();
        distance = 0;
    }

    public void calculateSegmentDistance(){
        if(locations.size() <= 1){
            distance = 0;
        }
        else{
            //distance += locations.get(locations.size() - 1).distanceTo(locations.get(locations.size() - 2)); //meter calculation, km is better imo
            Location location1 = locations.get(locations.size() - 1);
            Location location2 = locations.get(locations.size() - 2);
            distance += Trip.haversineFormula(location1.getLatitude(), location2.getLatitude(), location1.getLongitude(), location2.getLongitude());
        }
    }

    public void addLocation(Location location){
        this.locations.add(location);
        this.serializableLocations.add(SerializableLocation.fromLocation(location));
        calculateSegmentDistance();
    }

    public double getDistance() {
        return distance;
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }
}
