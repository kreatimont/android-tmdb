package io.kreatimont.cinematograph.ui.proto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nadto.cinematograph.R;

import java.util.ArrayList;
import java.util.List;

import io.kreatimont.cinematograph.ui.proto.viewHolder.MovieViewHolder;
import io.kreatimont.cinematograph.ui.proto.viewHolder.MovieWithProtoVH;

public abstract class ProtoMovieAdapter extends RecyclerView.Adapter<MovieViewHolder> implements ResponseRecyclerViewAdapter {

    public List mDataList;
    public Context mContext;
    public MovieCardLayoutType layoutType = MovieCardLayoutType.LinearWithPoster;

    public ProtoMovieAdapter(Context context, ArrayList data) {
        this.mContext = context;
        this.mDataList = data;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        int layoutId;

        switch (layoutType) {
            case Grid:
                layoutId = R.layout.card_movie_grid;
                break;
            case LinearWithBackdrop:
                layoutId = R.layout.card_movie_backdrop;
                break;
            case LinearWithPoster:
                layoutId = R.layout.card_movie_poster;
                break;
            case LinearHorizontal:
                layoutId = R.layout.card_movie_h_poster;

                //temporary
                view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
                return new MovieWithProtoVH(view, mContext, layoutType);

            default:
                layoutId = R.layout.card_movie_backdrop;
        }

        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        return new MovieViewHolder(view, mContext, layoutType);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void setLayout(MovieCardLayoutType type) {
        this.layoutType = type;
    }
}
