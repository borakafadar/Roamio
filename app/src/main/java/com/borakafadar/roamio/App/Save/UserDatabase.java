package com.borakafadar.roamio.App.Save;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.borakafadar.roamio.App.User;

@Database(entities = {User.class}, version = 1, exportSchema = true)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
