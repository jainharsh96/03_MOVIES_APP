package com.example.harsh.moviesapp.dataclients.localdbclient;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.harsh.moviesapp.datastore.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    private static final String TAG = MoviesDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "MovieDatabase";
    private static MoviesDatabase moviesDatabase;

    public static MoviesDatabase getInstance(Context context) {
        if (moviesDatabase == null) {
            synchronized (LOCK) {
                moviesDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        MoviesDatabase.class,
                        MoviesDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return moviesDatabase;
    }

    public abstract MovieDao movieDao();
}

