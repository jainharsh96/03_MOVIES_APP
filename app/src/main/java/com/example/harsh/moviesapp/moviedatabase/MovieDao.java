package com.example.harsh.moviesapp.moviedatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.harsh.moviesapp.datastore.Movie;

import java.util.List;

import javax.sql.DataSource;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM  Movietable")
    List<Movie>loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM Movietable WHERE id = :id")
    Movie loadMovieById(int id);

    @Query("SELECT * FROM  Movietable")
    LiveData<List<Movie>> loadAllMoviesForViewModel();
}
