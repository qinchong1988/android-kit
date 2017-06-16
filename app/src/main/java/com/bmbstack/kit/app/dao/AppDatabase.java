package com.bmbstack.kit.app.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static String DATABASE_NAME = "bianmei.db";

    public abstract UserDao userDao();
}
