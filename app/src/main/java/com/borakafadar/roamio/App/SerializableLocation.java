package com.borakafadar.roamio.App;

import android.location.Location;

public class SerializableLocation {
    private double latitude;
    private double longitude;
    private float accuracy;
    private long timestamp;


    public static SerializableLocation fromLocation(Location location) {
        SerializableLocation serializableLocation = new SerializableLocation();

        serializableLocation.setLatitude(location.getLatitude());
        serializableLocation.setLongitude(location.getLongitude());
        serializableLocation.setAccuracy(location.getAccuracy());
        serializableLocation.setTimestamp(location.getTime());

        return serializableLocation;
    }

    public static Location toLocation(SerializableLocation serializableLocation) {
        Location location = new Location("");

        location.setLatitude(serializableLocation.getLatitude());
        location.setLongitude(serializableLocation.getLongitude());
        location.setAccuracy(serializableLocation.getAccuracy());
        location.setTime(serializableLocation.getTimestamp());

        return location;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public float getAccuracy() {
        return accuracy;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
