package com.borakafadar.roamio.App.Save;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.room.Room;

import com.borakafadar.roamio.App.Trip;
import com.borakafadar.roamio.App.User;
import com.borakafadar.roamio.TripMapsActivity;

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

    public static void deleteTrip(Context context, TripEntity tripEntity) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();
        new Thread(() -> {
            db.tripDao().delete(tripEntity);
            Log.d("tripLog", "trip successfully deleted");
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

    public static void updateTrip(Context context, TripEntity trip) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();
        new Thread(() -> {
            db.tripDao().update(trip);
            Log.d("tripLog", "trip successfully updated");
        }).start();
    }

    public static void getLatestTrip(Context context, TripsCallback callback) {
        TripDatabase db = Room.databaseBuilder(context, TripDatabase.class, "trip-database").build();

        new Thread(()->{
            TripEntity trip = db.tripDao().getLatestTrip();
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onTripLoaded(trip);
            });
        }).start();
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onUserCountLoaded(int count);
    }

    public static void saveUser(Context context, User user){
        UserDatabase db = Room.databaseBuilder(context, UserDatabase.class, "user-database").build();
        new Thread(() -> {
            user.setTripEntitiesToJson();
            db.userDao().insert(user);
            Log.d("userLog", "user successfully inserted");
        }).start();
    }

    public static void updateUser(Context context, User user){
        UserDatabase db = Room.databaseBuilder(context, UserDatabase.class, "user-database").build();
        new Thread(() -> {
            db.userDao().update(user);
        }).start();
    }

    public static void getUser(Context context, UserCallback userCallback){
        UserDatabase db = Room.databaseBuilder(context, UserDatabase.class, "user-database").build();
        new Thread(() -> {
            User user = db.userDao().getUser();

            new Handler(Looper.getMainLooper()).post(() -> {
                userCallback.onUserLoaded(user);
            });
        }).start();
    }
    public static void getUserCount(Context context, UserCallback userCallback){
        UserDatabase db = Room.databaseBuilder(context, UserDatabase.class, "user-database").build();
        new Thread(() -> {
            int count = db.userDao().userCount();
            new Handler(Looper.getMainLooper()).post(() -> {
                userCallback.onUserCountLoaded(count);
            });
        }).start();
    }



}