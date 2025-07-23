package com.thesis.sprayerdrone.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.MakMoinee.library.services.MSqliteDBHelper;

public class MyLocalSQL extends MSqliteDBHelper {
    public MyLocalSQL(Context context) {
        super(context, "sprayers");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName TEXT NOT NULL, " +
                "middleName TEXT, " +
                "lastName TEXT NOT NULL, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS drones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userID INTEGER, " +
                "deviceName TEXT NOT NULL, " +
                "deviceIP TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "registeredDate DATE NOT NULL)");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS drones");
        onCreate(db);
    }
}
