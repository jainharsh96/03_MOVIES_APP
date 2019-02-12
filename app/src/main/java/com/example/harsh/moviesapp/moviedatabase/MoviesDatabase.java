package com.example.harsh.moviesapp.moviedatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.harsh.moviesapp.datastore.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    private static final String LOG_TAG = MoviesDatabase.class.getSimpleName();
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

