package com.borakafadar.roamio.App.Save;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;

@Database(entities = {TripEntity.class}, version = 1, exportSchema = true)
public abstract class TripDatabase extends RoomDatabase{
    public abstract TripDao tripDao();
}
