package com.example.harsh.moviesapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.datastore.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private String IMAGEBASEURL = "http://image.tmdb.org/t/p/w342/";
    private List<Movie> movies;
    private ItemClickListener itemClickListener;

    public MovieAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        itemClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movie_viewholder, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String posterimage = IMAGEBASEURL + movie.getPosterPath();
        Picasso.get().load(posterimage).placeholder(R.drawable.placeholder1).fit().into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    public void setMovie(List<Movie> movieList) {
        if (movies == null) {
            movies = movieList;
            notifyDataSetChanged();
        } else {
            movies.addAll(movieList);
            notifyItemRangeInserted(movies.size() - movieList.size(), movies.size());
        }
    }

    public List<Movie> getMovie() {
        return movies;
    }

    public interface ItemClickListener {
        void onItemClickListener(Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;

        public MovieViewHolder(View itemview) {
            super(itemview);
            poster = itemview.findViewById(R.id.movieposter);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Movie movie = movies.get(getAdapterPosition());
            itemClickListener.onItemClickListener(movie);
        }
    }

    public void clear() {
        if (movies == null)
            return;
        movies.clear();
        notifyDataSetChanged();
    }
}
