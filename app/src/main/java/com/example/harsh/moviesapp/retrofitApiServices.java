package com.example.harsh.moviesapp;

import com.example.harsh.moviesapp.datastore.MovieDetails;
import com.example.harsh.moviesapp.datastore.MovieReviewDetail;
import com.example.harsh.moviesapp.datastore.MovieVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface retrofitApiServices {
    public String baseuri = "https://api.themoviedb.org/3/";

    @GET("discover/movie")
    Call<MovieDetails> getMovieSortByPopularity(@Query("api_key") String apikey, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieDetails> getMovieTopRated(@Query("api_key") String apikey, @Query("page") int page);

    @GET("movie/{id}/reviews")
    Call<MovieReviewDetail> getMovieReview(@Path("id") int movieid, @Query("api_key") String apikey);

    @GET("movie/{id}/videos")
    Call<MovieVideoDetails> getMoviesTrailer(@Path("id") int id, @Query("api_key") String apiKey);
}
