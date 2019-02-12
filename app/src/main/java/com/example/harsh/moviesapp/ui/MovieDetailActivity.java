package com.example.harsh.moviesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.harsh.moviesapp.AppBarStateChangeListener;
import com.example.harsh.moviesapp.FetchDetailFromApi;
import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.datastore.Movie;
import com.example.harsh.moviesapp.datastore.MovieVideoDetails;
import com.example.harsh.moviesapp.moviedatabase.MoviesDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.onSomeEventListener {
    public static final String MOVIE_KEY = "movie_key";
    private MoviesDatabase moviesDatabase;
    private ImageView mPoster2;
    private ImageView msharebutton;
    private AppBarLayout appBarLayout;
    private Movie movie;
    private List<Movie> movies;
    private boolean like;
    static boolean mTwoPanal;
    static String TRAILER_KEY = "";
    private FloatingActionButton fab = null;
    private String IMAGEBASEURL = "http://image.tmdb.org/t/p/w342/";
    private String SHARE_MOVIE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        appBarLayout = findViewById(R.id.app_bar);
        fab = findViewById(R.id.fab);
        msharebutton = findViewById(R.id.share_button_second);
        mPoster2 = findViewById(R.id.poster2);

        movie = getIntent().getExtras().getParcelable(MOVIE_KEY);
        moviesDatabase = MoviesDatabase.getInstance(getApplicationContext());
        movies = moviesDatabase.movieDao().loadAllMovies();

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(movie.getTitle());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLike(like, view);
            }
        });

        if (movies != null) {
            for (int i = 0; i < movies.size(); i++) {
                if (movie.getId().equals(movies.get(i).getId())) {
                    fab.setImageDrawable(getDrawable(R.drawable.icon_like_button_red));
                    this.like = true;
                }
            }
        }

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        getVideoLink();

        msharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, SHARE_MOVIE);
                startActivity(Intent.createChooser(sharingIntent, SHARE_MOVIE));
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String posterimage = IMAGEBASEURL + movie.getBackdropPath();
        Picasso.get().load(posterimage).placeholder(R.drawable.placeholder_landscape).fit().into(mPoster2);

        Bundle bundle = new Bundle();
        bundle.putParcelable(MovieDetailFragment.ARG_ITEM_ID, movie);
        bundle.putBoolean("mode", mTwoPanal);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();
    }

    private void onClickLike(boolean like, View view) {

        if (like) {
            fab.setImageDrawable(getDrawable(R.drawable.icon_like_button));
            moviesDatabase.movieDao().deleteMovie(movie);
            this.like = !like;
        } else {
            fab.setImageDrawable(getDrawable(R.drawable.icon_like_button_red));
            moviesDatabase.movieDao().insertMovie(movie);
            this.like = !like;
        }
    }

    private void getVideoLink() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FetchDetailFromApi.baseuri)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchDetailFromApi api = retrofit.create(FetchDetailFromApi.class);
        Call<MovieVideoDetails> call = api.getMoviesTrailer(movie.getId(), MovieListActivity.API_KEY);

        call.enqueue(new Callback<MovieVideoDetails>() {
            @Override
            public void onResponse(Call<MovieVideoDetails> call, Response<MovieVideoDetails> response) {
                try {
                    TRAILER_KEY = response.body().getResults().get(0).getKey();
                } catch (IndexOutOfBoundsException e) {
                    Log.d("error", e.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieVideoDetails> call, Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    @Override
    public void someEvent(String s) {
        SHARE_MOVIE = s;
    }
}
