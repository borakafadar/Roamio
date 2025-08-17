package com.borakafadar.roamio.App.Save;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.borakafadar.roamio.App.Trip;

import java.util.List;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trips ORDER BY tripID DESC")
    List<TripEntity> getAllTrips();
    @Insert
    void insert(TripEntity tripEntity);
    @Delete
    void delete(TripEntity tripEntity);
}
