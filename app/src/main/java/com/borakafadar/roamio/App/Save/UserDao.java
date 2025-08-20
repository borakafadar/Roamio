package com.borakafadar.roamio.App.Save;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.borakafadar.roamio.App.User;

@Dao
public interface UserDao {
    @Query( "SELECT * FROM user LIMIT 1")
    User getUser();
    @Query("SELECT COUNT(*) FROM user")
    int userCount();
    @Insert
    void insert(User user);
    @Update
    void update(User user);
}
