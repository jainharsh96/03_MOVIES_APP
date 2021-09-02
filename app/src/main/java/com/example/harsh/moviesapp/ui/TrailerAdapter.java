package com.example.harsh.moviesapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.harsh.moviesapp.R;
import com.example.harsh.moviesapp.datastore.MovieVideoResult;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private Context context;
    private ItemClickListener itemClickListener;
    private List<MovieVideoResult> movievideoresults;

    public TrailerAdapter(Context context, TrailerAdapter.ItemClickListener listener) {
        this.context = context;
        itemClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.trailer_viewholder, parent, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        MovieVideoResult videoResult = movievideoresults.get(position);
        holder.trailer.setText(videoResult.getName());
    }

    @Override
    public int getItemCount() {
        if (movievideoresults == null) {
            return 0;
        }
        return movievideoresults.size();
    }

    public void setMovieTrailers(List<MovieVideoResult> trailerlist) {
        movievideoresults = trailerlist;
        notifyDataSetChanged();
    }

    public List<MovieVideoResult> getMovieVideoResults() {
        return movievideoresults;
    }

    public interface ItemClickListener {
        void onItemClickListener(MovieVideoResult result);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trailer;

        public TrailerViewHolder(View itemview) {
            super(itemview);
            trailer = itemview.findViewById(R.id.trailer);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MovieVideoResult result = movievideoresults.get(getAdapterPosition());
            itemClickListener.onItemClickListener(result);
        }
    }
}
