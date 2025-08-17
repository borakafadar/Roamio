package com.borakafadar.roamio.App.Save;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.room.Room;

import com.borakafadar.roamio.App.Trip;

import java.util.ArrayList;
import java.util.List;

public class SaveManager {

    public static ArrayList<TripEntity> allTrips = new ArrayList<>();

    public static void saveTrip(Context context, Trip trip) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();
        TripEntity tripEntity = Converter.tripToTripEntity(trip);
        new Thread(() -> {
            db.tripDao().insert(tripEntity);
            Log.d("tripLog", "trip successfully inserted");
        }).start();
    }

    // Make the callback public so callers can pass a lambda/anonymous class
    public interface TripsCallback {
        void onTripsLoaded(List<TripEntity> trips);
        void onTripLoaded(TripEntity trip);
    }

    // Expose a single public async API. Remove/stop using the sync-returning version.
    public static void getAllTrips(Context context, TripsCallback callback) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();
        new Thread(() -> {
            List<TripEntity> tripList = db.tripDao().getAllTrips();
            // Copy into our static arraylist
            allTrips.clear();
            allTrips.addAll(tripList);
            // Notify caller on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onTripsLoaded(allTrips);
            });
        }).start();
    }

    // Changed to async-only: no return value; result arrives via callback on main thread
    public static void getTripByID(Context context, int tripID, TripsCallback callback) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();

        new Thread(() -> {
            TripEntity trip = db.tripDao().getTripByID(tripID);
            Log.d("dbLog", "Loaded trip id=" + tripID + " found=" + (trip != null));
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onTripLoaded(trip);
            });
        }).start();
    }
}