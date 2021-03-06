package io.kreatimont.cinematograph.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nadto.cinematograph.R;

import io.kreatimont.cinematograph.ui.person.CastAdapter;
import io.kreatimont.cinematograph.helpers.RecyclerItemClickListener;
import io.kreatimont.cinematograph.data.service.RetrofitClient;
import io.kreatimont.cinematograph.data.model.tmdb.Genre;
import io.kreatimont.cinematograph.data.model.tmdb.credits.Crew;
import io.kreatimont.cinematograph.data.model.tmdb.movie.Movie;
import io.kreatimont.cinematograph.data.api.TMDbAPI;
import io.kreatimont.cinematograph.data.model.tmdb.credits.Cast;
import io.kreatimont.cinematograph.ui.main.MainActivity;
import io.kreatimont.cinematograph.ui.person.PersonDetailedActivity;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailedActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private ImageView backdrop, poster;
    private TextView overview, year, createdBy, budget, genres, popularity, vote, tagline, title;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CastAdapter castAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar preloader;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fabFavorite;
    private LinearLayout overviewForm;
    private FrameLayout mainForm;

    /*Activity lifecycle*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        if(getIntent() != null ) {
            if(getIntent().getExtras() != null) {
                int movieId = getIntent().getExtras().getInt(EXTRA_ID);
                initUI();
                loadData(movieId);
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Configure UI*/

    private void initUI() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detailedCoordinatorLayout);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overviewForm = (LinearLayout) findViewById(R.id.overviewForm);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        fabFavorite = (FloatingActionButton) findViewById(R.id.fabFavorite);
        genres = (TextView) findViewById(R.id.detailedGenres);
        backdrop = (ImageView) findViewById(R.id.detailedBackdrop);
        poster = (ImageView) findViewById(R.id.detailedPoster);
        title = (TextView) findViewById(R.id.detailedTitle);
        overview = (TextView) findViewById(R.id.detailedOverview);
        year = (TextView) findViewById(R.id.detailedYear);
        budget = (TextView) findViewById(R.id.detailedBudget);
        popularity = (TextView) findViewById(R.id.detailedPopularity);
        createdBy = (TextView) findViewById(R.id.detailedCreatedBy);
        vote = (TextView) findViewById(R.id.detailedVote);
        tagline = (TextView) findViewById(R.id.detailedTagline);

        mRecyclerView = (RecyclerView) findViewById(R.id.castRecycler);

    }

    /*Load data*/

    private void loadData(int filmId) {
        TMDbAPI apiService = RetrofitClient.getClient().create(TMDbAPI.class);

        retrofit2.Call<Movie> call = apiService.getMovieDetails(filmId, getString(R.string.api_key), "ru", "credits");

        call.enqueue(new retrofit2.Callback<Movie>() {

            @Override
            public void onResponse(retrofit2.Call<Movie> call, retrofit2.Response<Movie> response) {
                if(response.body() != null) {
                    Movie responseMovie = response.body();
                    updateInfo(responseMovie, responseMovie.getCredits().getCast());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Movie> call, Throwable t) {
                Log.e("Retrofit(failure)", t.getMessage());
            }

        });
    }

    /*Additional methods*/

    private void updateInfo(Movie film, final List<Cast> cast) {

        if(film != null) {

            Picasso.with(this).load(getString(R.string.image_base) + getString(R.string.backdrop_size_big) + film.getBackdropPath()).into(backdrop);
            Picasso.with(this).load(getString(R.string.image_base) + getString(R.string.poster_size_medium) + film.getPosterPath()).into(poster);

            collapsingToolbarLayout.setExpandedTitleMarginBottom(-999);
            collapsingToolbarLayout.setTitle(film.getTitle());



            for(Genre g : film.getGenres()) {
                genres.append(g.getName() + " ");
            }

            title.setText(film.getTitle());

            if(film.getOverview() != null && film.getOverview().length() < 1) {
                overviewForm.setVisibility(View.GONE);
            } else {
                overview.setText(film.getOverview());
            }

            for(Crew c : film.getCredits().getCrew()) {
                if (c.getJob().equals("Director")) {
                    createdBy.append(c.getName());
                }
            }


            year.setText(film.getReleaseDate());
            vote.setText(film.getVoteAverage() + "");
            popularity.setText(film.getPopularity() + "");
            budget.setText(film.getBudget() + "");
            tagline.setText(film.getTagline());

            if(cast != null) {
                castAdapter = new CastAdapter(this, cast);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
                mRecyclerView.setAdapter(castAdapter);
                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                        this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MovieDetailedActivity.this, PersonDetailedActivity.class);
                        if(position >= 0) {
                            intent.putExtra(PersonDetailedActivity.EXTRA_PERSON_ID, cast.get(position).getId());

                            /*clear activity stack*/
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                    MovieDetailedActivity.this,
                                    "Unable to load information at {" + position + "} pos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                }));

            }
        } else {
            Toast.makeText(this, R.string.parse_error, Toast.LENGTH_LONG).show();
        }
    }

    public void replaceFormWithProgressBar(boolean isVisible) {
        if(isVisible) {
            preloader.setVisibility(View.VISIBLE);

            mainForm.setVisibility(View.GONE);
        } else {
            preloader.setVisibility(View.GONE);

            mainForm.setVisibility(View.VISIBLE);
        }
    }
}
