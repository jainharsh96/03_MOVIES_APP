package com.example.harsh.moviesapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harsh.moviesapp.FetchDetailFromApi;
import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.datastore.Movie;
import com.example.harsh.moviesapp.datastore.MovieReviewDetail;
import com.example.harsh.moviesapp.datastore.MovieReviewResult;
import com.example.harsh.moviesapp.datastore.MovieVideoDetails;
import com.example.harsh.moviesapp.datastore.MovieVideoResult;
import com.example.harsh.moviesapp.moviedatabase.MoviesDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailFragment extends Fragment implements TrailerAdapter.ItemClickListener {
    public static final String ARG_ITEM_ID = "item_id";
    private String IMAGEBASEURL = "http://image.tmdb.org/t/p/w342/";
    final String API_KEY = "e1fbaf815c2a7bd1b7195615631b6a75";
    private boolean like;
    private static boolean panalmode;
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Movie> movies;
    private List<MovieVideoResult> mresults;
    private List<MovieReviewResult> movieReviewResults;
    static String TRAILER_KEY = "";
    private MoviesDatabase moviesDatabase;
    private Movie movie;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPoster;
    private ImageView mLikeButton;
    private MenuItem item;

    public MovieDetailFragment() {
    }

    public interface onSomeEventListener {
        void someEvent(String s);
    }

    onSomeEventListener someEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + getString(R.string.error_eventlistener));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            movie = getArguments().getParcelable(ARG_ITEM_ID);
        }

        if (getArguments().containsKey("mode")) {
            panalmode = getArguments().getBoolean("mode");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
      //  Toast.makeText(getActivity(), "onmenucreate" + getActivity().toString(), Toast.LENGTH_SHORT).show();
       if (panalmode) {
           inflater.inflate(R.menu.menu_list, menu);
            item = menu.findItem(R.id.share_button);
        }
      /*  if(getActivity && item != null) {
            Toast.makeText(getActivity(), "hiding"+ panalmode, Toast.LENGTH_SHORT).show();
            item.setVisible(false);
        }  */
       // item.setVisible(false);
    }

    @Override
    public void onPause() {
        super.onPause();
      //  Toast.makeText(getActivity(), "onpause" + getActivity().toString(), Toast.LENGTH_SHORT).show();
       if (!panalmode && item != null)
           item.setVisible(false);
    }

    @Override
    public void onStop() {
        super.onStop();
      //  Toast.makeText(getActivity(), "onstop" + getActivity().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    //    Toast.makeText(getActivity(), "onresume" + getActivity().toString(), Toast.LENGTH_SHORT).show();
     //   if (getActivity() instanceof MovieListActivity)
        //   item.setVisible(false);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
     //   Toast.makeText(getActivity(), "ondestroy" + getActivity().toString(), Toast.LENGTH_SHORT).show();
        // if (getActivity() instanceof MovieListActivity)
        //    item.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fregment_movie_detail, container, false);
      //  Toast.makeText(getActivity(), "oncreateview" + getActivity().toString(), Toast.LENGTH_SHORT).show();

        recyclerView = rootView.findViewById(R.id.trailersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        trailerAdapter = new TrailerAdapter(getActivity(), this);
        recyclerView.setAdapter(trailerAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        reviewRecyclerView = rootView.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewAdapter = new ReviewAdapter(getActivity());
        reviewRecyclerView.setAdapter(reviewAdapter);

        mReleaseDate = rootView.findViewById(R.id.movie_releaseDate);
        mRating = rootView.findViewById(R.id.movie_rating);
        mOverview = rootView.findViewById(R.id.movie_overview);
        mPoster = rootView.findViewById(R.id.movie_poster);
        mLikeButton = rootView.findViewById(R.id.like_icon);

        mReleaseDate.setText(movie.getReleaseDate().substring(0, 4));
        mRating.setText(movie.getVoteAverage().toString() + "/10.0");
        mOverview.setText(movie.getOverview());
        String posterimage = IMAGEBASEURL + movie.getPosterPath();
        Picasso.get().load(posterimage).placeholder(R.drawable.place_small).fit().into(mPoster);

        getMovieReview();
        getVideoLink();

        if (panalmode) {

//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            toolbar.setTitle("Feedback");
            //getActivity().getActionBar().setTitle("Feedback");
            mLikeButton.setVisibility(View.VISIBLE);
            moviesDatabase = MoviesDatabase.getInstance(getContext());
            movies = moviesDatabase.movieDao().loadAllMovies();
            if (movies != null) {
                for (int i = 0; i < movies.size(); i++) {
                    if (movie.getId().equals(movies.get(i).getId())) {
                        mLikeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_like_red_fregment));
                        this.like = true;
                    }
                }
                mLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickLike(like);
                    }
                });
            }
        }
        return rootView;
    }

    private void getMovieReview() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(FetchDetailFromApi.baseuri)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchDetailFromApi api1 = retrofit1.create(FetchDetailFromApi.class);
        Call<MovieReviewDetail> call = api1.getMovieReview(movie.getId(), API_KEY);

        call.enqueue(new Callback<MovieReviewDetail>() {
            @Override
            public void onResponse(Call<MovieReviewDetail> call, Response<MovieReviewDetail> response) {
                movieReviewResults = response.body().getResults();
                reviewAdapter.setMovieReviews(movieReviewResults);
            }

            @Override
            public void onFailure(Call<MovieReviewDetail> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }

    private void getVideoLink() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FetchDetailFromApi.baseuri)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchDetailFromApi api = retrofit.create(FetchDetailFromApi.class);
        Call<MovieVideoDetails> call = api.getMoviesTrailer(movie.getId(), API_KEY);

        call.enqueue(new Callback<MovieVideoDetails>() {
            @Override
            public void onResponse(Call<MovieVideoDetails> call, Response<MovieVideoDetails> response) {
                mresults = response.body().getResults();
                trailerAdapter.setMovieTrailers(mresults);
                try {
                    someEventListener.someEvent("http://www.youtube.com/watch?v=" + mresults.get(0).getKey());
                } catch (IndexOutOfBoundsException e) {
                    someEventListener.someEvent(movie.getTitle());
                }
            }

            @Override
            public void onFailure(Call<MovieVideoDetails> call, Throwable t) {

            }
        });
    }

    private void onClickLike(boolean like) {
        if (like) {
            mLikeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_like_fregment));
            moviesDatabase.movieDao().deleteMovie(movie);
            this.like = !like;
        } else {
            mLikeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_like_red_fregment));
            moviesDatabase.movieDao().insertMovie(movie);
            this.like = !like;
        }
    }

    @Override
    public void onItemClickListener(MovieVideoResult result) {
        TRAILER_KEY = result.getKey();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_baseuri) + TRAILER_KEY)));
    }
}
