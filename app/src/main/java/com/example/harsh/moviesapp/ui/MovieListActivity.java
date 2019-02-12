package com.example.harsh.moviesapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.harsh.moviesapp.FetchDetailFromApi;
import com.example.harsh.moviesapp.EndlessRecyclerViewScrollListener;
import com.example.harsh.moviesapp.MovieViewModel;
import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.moviedatabase.MoviesDatabase;
import com.example.harsh.moviesapp.datastore.Movie;
import com.example.harsh.moviesapp.datastore.MovieDetails;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.fabric.sdk.android.Fabric;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener, MovieDetailFragment.onSomeEventListener {
    private TextView toolbartitle;
    public ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private List<Movie> movies;
    public static String API_KEY = "e1fbaf815c2a7bd1b7195615631b6a75";
    private String SHARE_MOVIE = "";
    public static int sortby = R.id.Sort_by_most_popular;
    public static int pageno = 1;
    final private int number_of_column = 2;
    private boolean mTwoPan;
    private MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartitle = findViewById(R.id.title);
        toolbartitle.setText(getHomeTitle(sortby));
        progressBar = findViewById(R.id.progress_bar);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPan = true;
        }

        moviesDatabase = MoviesDatabase.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerViewTasks);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, number_of_column);
        recyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(movieAdapter);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (sortby != R.id.Sort_by_favorite)
                    getMovies(sortby);
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        progressBar.setVisibility(View.VISIBLE);

        setupViewModel();
        setupViewModelForDB();
    }

 /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        MenuItem item = menu.findItem(R.id.share_button);
     //  if (!mTwoPan) {
            item.setVisible(false);
      // }
        return true;
    }
  */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share_button) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, SHARE_MOVIE);
            startActivity(Intent.createChooser(sharingIntent, SHARE_MOVIE));
            return super.onOptionsItemSelected(item);
        }

        sortby = id;
        toolbartitle.setText(getHomeTitle(sortby));
        pageno = 1;

        if (id == R.id.Sort_by_favorite) {
            getFavoriteMovie();
            return true;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            movieAdapter.clear();
            getMovies(id);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMovies(int sortby) {
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FetchDetailFromApi.baseuri)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchDetailFromApi api = retrofit.create(FetchDetailFromApi.class);
        Call<MovieDetails> call;
        switch (sortby) {
            case R.id.Sort_by_rating:
                call = api.getMovieTopRated(API_KEY, pageno++);
                break;
            default:
                call = api.getMovieSortByPopularity(API_KEY, pageno++);
        }

        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                movies = response.body().getResults();
                progressBar.setVisibility(View.GONE);
                movieAdapter.setMovie(movies);
                if (pageno == 2)
                    loadDefaultMovie(movies);
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
                Log.d("error", t.getMessage());
            }
        });
    }

    @Override
    public void onItemClickListener(Movie movie) {
        if (mTwoPan) {
            toolbartitle.setText(movie.getTitle());
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.ARG_ITEM_ID, movie);
            bundle.putBoolean("mode", mTwoPan);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.MOVIE_KEY, movie);
            startActivity(intent);
        }
    }

    private void setupViewModel() {
        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movielist) {
                progressBar.setVisibility(View.GONE);
                movieAdapter.setMovie(movielist);
                loadDefaultMovie(movielist);
            }
        });
    }

    private void getFavoriteMovie() {
        movieAdapter.clear();
        setupViewModelForDB();
    }

    public String getHomeTitle(int sortby) {
        switch (sortby) {
            case R.id.Sort_by_rating:
                return getString(R.string.top_rated);
            case R.id.Sort_by_favorite:
                return getString(R.string.favorite);
            default:
                return getString(R.string.popular);
        }
    }

    private void setupViewModelForDB() {
        MovieViewModel viewModel1 = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel1.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movielist) {
                if (sortby == R.id.Sort_by_favorite) {
                    movieAdapter.clear();
                    Toast.makeText(getApplicationContext(),"trigred", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    movieAdapter.setMovie(movielist);
                    if (movielist.size() > 0)
                        loadDefaultMovie(movielist);
                }
            }
        });
    }

    private void loadDefaultMovie(List<Movie> movielist) {
        if (mTwoPan && movielist.size() > 0) {
            toolbartitle.setText(movielist.get(0).getTitle());
           // Toast.makeText(, mTwoPan, Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.ARG_ITEM_ID, movielist.get(0));
            bundle.putBoolean("mode", mTwoPan);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        }
    }

    @Override
    public void someEvent(String s) {
        SHARE_MOVIE = s;
        return;
    }

}
