package com.borakafadar.roamio.App.Save;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.borakafadar.roamio.App.Trip;

import java.util.List;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trips ORDER BY tripID DESC")
    List<TripEntity> getAllTrips();
    @Query("SELECT * FROM trips WHERE tripID = :tripID LIMIT 1")
    TripEntity getTripByID(int tripID);
    @Query("SELECT * FROM trips ORDER BY tripID DESC LIMIT 1")
    TripEntity getLatestTrip();
    @Update
    void update(TripEntity tripEntity);
    @Insert
    void insert(TripEntity tripEntity);
    @Delete
    void delete(TripEntity tripEntity);

}
