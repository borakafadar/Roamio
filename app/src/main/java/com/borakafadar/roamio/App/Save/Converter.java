package com.borakafadar.roamio.App.Save;

import com.borakafadar.roamio.App.Trip;
import com.borakafadar.roamio.App.TripSegment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converter {
    public static String convertTripSegmentsToJson(Trip trip){
        Gson gson = new Gson();
        return gson.toJson(trip.getTripSegments());
    }
    public static ArrayList<TripSegment> convertJsonToTripSegments(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<TripSegment>>(){}.getType();
        ArrayList<TripSegment> tripSegments = gson.fromJson(json, type);;

        return tripSegments;
    }

    public static TripEntity tripToTripEntity(Trip trip){
        TripEntity tripEntity = new TripEntity();
        tripEntity.date= trip.getDateTimeString();
        tripEntity.duration = trip.getDuration();
        tripEntity.comments = trip.getComments();
        tripEntity.distance = trip.getDistance();
        tripEntity.title = trip.getTitle();
        tripEntity.segmentsJson = convertTripSegmentsToJson(trip);
        return tripEntity;
    }

    public static Trip tripEntityToTrip(TripEntity tripEntity){
        //TODO: implement method
        Trip trip = new Trip();


        return trip;
    }
}
