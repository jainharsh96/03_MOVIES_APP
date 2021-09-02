package com.example.harsh.moviesapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.datastore.MovieReviewResult;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<MovieReviewResult> movieReviewResults;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.review_viewholder, parent, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        MovieReviewResult reviewResult = movieReviewResults.get(position);
        holder.author.setText(reviewResult.getAuthor());
        holder.review.setText(reviewResult.getContent());
    }

    @Override
    public int getItemCount() {
        if (movieReviewResults == null) {
            return 0;
        }
        return movieReviewResults.size();
    }

    public void setMovieReviews(List<MovieReviewResult> reviewlist) {
        movieReviewResults = reviewlist;
        notifyDataSetChanged();
    }

    public List<MovieReviewResult> getMovieReviews() {
        return movieReviewResults;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView review;

        public ReviewViewHolder(View itemview) {
            super(itemview);
            author = itemview.findViewById(R.id.movie_review_author);
            review = itemview.findViewById(R.id.movie_review);
        }
    }
}

