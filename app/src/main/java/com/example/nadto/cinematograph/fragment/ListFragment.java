package com.example.nadto.cinematograph.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nadto.cinematograph.DetailedActivity;
import com.example.nadto.cinematograph.Film;
import com.example.nadto.cinematograph.FilmAdapter;
import com.example.nadto.cinematograph.Json.JsonParser;
import com.example.nadto.cinematograph.R;
import com.example.nadto.cinematograph.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.nadto.cinematograph.MainActivity.ARRAY;

public abstract class ListFragment extends Fragment {

    RecyclerView mRecyclerView;
    FilmAdapter mFilmAdapter;
    ArrayList<Film> movies;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.movieRecycler);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),  mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent  intent = new Intent(getActivity(), DetailedActivity.class);
                if(position >= 0) {
                    intent.putExtra(DetailedActivity.TITLE, movies.get(position).getTitle());
                    startActivity(intent);
                } else {
                    Snackbar.make(rootView, "Unable to load information at (" + position + ")",Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        addFakeItems();

        setUpData();

        return rootView;
    }

    void addFakeItems() {
        movies = new ArrayList<>();
        movies.add(new Film(44217, Film.TV, "Vikings","2011","Micheal Hirst", 7.8f, "Drama", "null", "null","Canada"));
    }
    abstract void setUpData();
    abstract public void addItem(int id);

    class GetMoviesTask extends AsyncTask<URL, Void, JSONObject> {

        String jsonType;
        int type;

        GetMoviesTask(String jsonType, int type) {
            this.jsonType = jsonType;
            this.type = type;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection httpUrlCon = null;

            try {
                httpUrlCon = (HttpURLConnection) urls[0].openConnection();
                int response = httpUrlCon.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK) {
                    StringBuilder stringBuilder = new StringBuilder();
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(httpUrlCon.getInputStream(),"UTF-8"))) {
                        String line;
                        Log.e("============","=======START READING=========");
                        Log.v("URL", urls[0].toString());
                        while((line = reader.readLine()) != null) {
                            Log.v("stream:", line);
                            stringBuilder.append(line);
                        }
                        Log.e("============","=======END READING=========");
                    } catch (IOException ex) {
                        Snackbar.make(getActivity().findViewById(R.id.activity_main),
                                getString(R.string.read_error), Snackbar.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }
                    return new JSONObject(stringBuilder.toString());

                } else {
                    Snackbar.make(getActivity().findViewById(R.id.activity_main),
                            getString(R.string.load_error) + "|" + response, Snackbar.LENGTH_LONG).show();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Snackbar.make(getActivity().findViewById(R.id.activity_main), ex.getMessage(), Snackbar.LENGTH_LONG).show();
            } finally {
                if(httpUrlCon != null) {
                    httpUrlCon.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null) {
                if(jsonType.equals(ARRAY)) {
                    try {
                        JSONArray jsonArray = jsonObject.has("results") ? jsonObject.getJSONArray("results") : null;

                        if(jsonArray != null) {
                            for(int i = 0; i < jsonArray.length(); i++) {
                                new GetMoviesTask("obj",type).
                                        execute(JsonParser.createURL(jsonArray.getJSONObject(i).getInt("id"), type, getActivity()));

                            }
                            mFilmAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        Snackbar.make(getActivity().findViewById(R.id.activity_main), "JSON parse error", Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    JsonParser.convertJSON(jsonObject,getActivity(),movies);
                    mFilmAdapter.notifyDataSetChanged();
                }
            } else {
                Snackbar.make(getActivity().findViewById(R.id.activity_main), getString(R.string.load_error), Snackbar.LENGTH_LONG).show();
            }
        }

    }

}
