package com.example.harsh.moviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.view.View;
import android.widget.Toast;

import com.example.harsh.moviesapp.moviedatabase.MoviesDatabase;
import com.example.harsh.moviesapp.datastore.Movie;
import com.example.harsh.moviesapp.datastore.MovieDetails;
import com.example.harsh.moviesapp.ui.MovieListActivity;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.harsh.moviesapp.ui.MovieListActivity.sortby;

public class MovieViewModel extends AndroidViewModel {

    public MutableLiveData<List<Movie>> movielist;
    private LiveData<List<Movie>> dbmovielist;

    public MovieViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Movie>> getMovies() {
        if (movielist == null) {
            movielist = new MutableLiveData<List<Movie>>();
            loadMovie();
        }
        return this.movielist;
    }

    public void loadMovie() {
        if (sortby != R.id.Sort_by_favorite) {
            new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FetchDetailFromApi.baseuri)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            FetchDetailFromApi api = retrofit.create(FetchDetailFromApi.class);
            Call<MovieDetails> call = api.getMovieSortByPopularity(MovieListActivity.API_KEY, MovieListActivity.pageno++);

            call.enqueue(new Callback<MovieDetails>() {
                @Override
                public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                    movielist.setValue(response.body().getResults());
                }

                @Override
                public void onFailure(Call<MovieDetails> call, Throwable t) {
                    //progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "no internet connection", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(this.getApplication());
        dbmovielist = moviesDatabase.movieDao().loadAllMoviesForViewModel();
        return dbmovielist;
    }
}
