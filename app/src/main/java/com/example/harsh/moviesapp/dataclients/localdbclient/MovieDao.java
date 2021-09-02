package com.example.harsh.moviesapp.dataclients.localdbclient;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.harsh.moviesapp.datastore.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM  Movie")
    List<Movie>loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM Movie WHERE id = :id")
    Movie loadMovieById(int id);

    @Query("SELECT * FROM  Movie")
    LiveData<List<Movie>> loadAllMoviesForViewModel();
}
