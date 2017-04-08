package com.example.nadto.cinematograph.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nadto.cinematograph.R;
import com.example.nadto.cinematograph.model.tmdb_model.movie.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements ResponseRecyclerViewAdapter {

    private ArrayList<Movie> movies;
    private Context mContext;
    private boolean isGridLayout = false;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.movies = movies;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isGridLayout) {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_layout_grid, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void setGridLayout(boolean state) {
        this.isGridLayout = state;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView title, year;
        private ImageView backdrop;
        private RatingBar voteAverage;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.cardView);
            title = (TextView)itemView.findViewById(R.id.title);
            year = (TextView)itemView.findViewById(R.id.year);
            voteAverage = (RatingBar)itemView.findViewById(R.id.voteAverage);
            backdrop = (ImageView)itemView.findViewById(R.id.backdrop);

        }

        void bind(final Movie movie) {

            title.setText(movie.getTitle());
            String yearString = movie.getReleaseDate() != null && movie.getReleaseDate().length() > 4 ? movie.getReleaseDate().substring(0,4) : "N/A";
            year.setText(mContext.getString(R.string.year_template, yearString));
            voteAverage.setRating((float) ((movie.getVoteAverage() * 5.0f) / 10.0f));

            Picasso.with(mContext).load(mContext.getString(R.string.image_base) + movie.getPosterPath()).into(backdrop);
        }
    }

}
