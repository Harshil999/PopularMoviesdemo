package com.harshil.example.popularmoviesdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    ArrayList<Movie> popularMovies = new ArrayList<Movie>();
    ImageAdapter adp;
    GridView gridView;
    private static final String SELECTED_KEY = "selected_position";
    private int mPosition = 0;

    public MainActivityFragment() {
    }

    int index;
    int top;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            sendData();
        } else {
            popularMovies = savedInstanceState.getParcelableArrayList("movie");
        }

        this.setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != gridView.INVALID_POSITION) {
            int currentPosition = gridView.getFirstVisiblePosition();
            outState.putInt(SELECTED_KEY,
                    currentPosition);
        }
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", popularMovies);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            sendData();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendData() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_list_key),
                getString(R.string.pref_default_value));

        fetchMovies mov = new fetchMovies();

        mov.execute(location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);


        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            mPosition = savedInstanceState.getInt("movie");
        }

        adp = new ImageAdapter(getContext(), popularMovies);

        gridView = (GridView) rootview.findViewById(R.id.gridview);
        gridView.setAdapter(adp);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movies = popularMovies.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", movies);
                startActivity(intent);
            }
        });

        return rootview;
    }

    public class fetchMovies extends AsyncTask<String, Void, ArrayList<Movie>> {


        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            int Format_Cnt_Value = 7;
            String Format_Api = "api_key";

            try {

                String Format_Mode_Val = params[0];
                String Format_Mode = "sort_by";
                String Format_Mode_Filter = "";
                int Format_Mode_Filter_Val = 0;

                if (Format_Mode_Val.equals(getString(R.string.pref_value_highest_rated))) {
                    Format_Mode_Val = "vote_average.desc";
                    Format_Mode_Filter = "vote_count.gte";
                    Format_Mode_Filter_Val = 1000;
                } else if (Format_Mode_Val.equals(getString(R.string.pref_default_value))) {
                    Format_Mode_Val = "popularity.desc";
                    Format_Mode_Filter = "popularity";
                    Format_Mode_Filter_Val = 100;
                }
                String OPEN_MOVIES_API_KEY = "8ca65782c2cc2d48c954d5f9caa87b7b";
                String baseUrl = "http://api.themoviedb.org/3/discover/movie?";

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(Format_Mode, Format_Mode_Val)
                        .appendQueryParameter(Format_Mode_Filter, Integer.toString(Format_Mode_Filter_Val))
                        .appendQueryParameter(Format_Api, OPEN_MOVIES_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuffer stringBuffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                forecastJsonStr = stringBuffer.toString();

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            ;
            try {

                return getMovieDataFromJson(forecastJsonStr, Format_Cnt_Value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr, int format_cnt_value) throws JSONException {

            JSONObject movieJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            ArrayList<Movie> storeMovies = new ArrayList<Movie>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject obj1 = movieArray.getJSONObject(i);

                String title = obj1.getString("title");
                String overview = obj1.getString("overview");
                Double vote_average = obj1.getDouble("vote_average");
                String poster = obj1.getString("poster_path");
                String release_date = obj1.getString("release_date");

                storeMovies.add(new Movie(title, vote_average, release_date, overview, poster));
            }

            return storeMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                popularMovies.clear();
                popularMovies.addAll(movies);
            }
            adp.notifyDataSetChanged();
        }
    }

}
