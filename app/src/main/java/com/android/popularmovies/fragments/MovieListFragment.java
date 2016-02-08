package com.android.popularmovies.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.R;
import com.android.popularmovies.activities.MovieDetailActivity;
import com.android.popularmovies.adapter.ImageAdapter;
import com.android.popularmovies.dbhelper.MoviesDBHelper;
import com.android.popularmovies.dbhelper.PopularMovieContractor;
import com.android.popularmovies.model.Movie;

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
import java.util.List;


/**
 * Encapsulates fetching the movies and displaying it as a {@link GridView} layout.
 */
public class MovieListFragment extends Fragment {

    final static private String TAG = MovieListFragment.class.getName();

    private ImageAdapter mMoviesAdapter;

    private MovieDetailFragment movieDetailFragment;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ImageAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        mMoviesAdapter =
                new ImageAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.gridview_movie_item, // The name of the layout ID.
                        new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        movieDetailFragment = (MovieDetailFragment) getFragmentManager().findFragmentByTag("DETAILTAG");

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(


                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        movieDetailFragment = (MovieDetailFragment) getFragmentManager().findFragmentByTag("DETAILTAG");
                        if (movieDetailFragment == null) {
                            Movie item = mMoviesAdapter.getItem(position);
                            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                            intent.putExtra("Movie", item);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Movi detail fragment already present");
                            movieDetailFragment.update(mMoviesAdapter.getItem(position));
                        }

                    }
                });

        return rootView;
    }

    private void updateMovieList() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String result = prefs.getString("sort_by",getString(R.string.pref_sort_by_default));


        if(result.equalsIgnoreCase(getString(R.string.by_favorites))){
            new FetchMoviesFromDB().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getCursor());
        }else {
            new FetchMoviesTask().execute(result);
        }
     }

    private Cursor getCursor() {
        MoviesDBHelper helper = new MoviesDBHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                PopularMovieContractor.MovieEntry._ID,
                PopularMovieContractor.MovieEntry.MOVIE_ID,
                PopularMovieContractor.MovieEntry.TITLE,
                PopularMovieContractor.MovieEntry.DESCRIPTION,
                PopularMovieContractor.MovieEntry.POSTER_LINK,
                PopularMovieContractor.MovieEntry.BACKGROUND_LINK,
                PopularMovieContractor.MovieEntry.RELEASE_DATE,
                PopularMovieContractor.MovieEntry.RATING
        };

        String order = PopularMovieContractor.MovieEntry.RATING + " DESC";

        return db.query(PopularMovieContractor.MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                order);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_LIST = "results";
            final String MDB_POSTER = "poster_path";

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MDB_LIST);

            List<Movie> results = new ArrayList<>();

            for (int i = 0; i < moviesArray.length(); i++) {
               // Get the JSON object representing the movie
                JSONObject movieInfo = moviesArray.getJSONObject(i);
                results.add(new Movie(movieInfo));
            }
            return results.toArray(new Movie[0]);
         }

        @Override
        protected Movie[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the Movie DB query

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

                final String SORY_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORY_BY, params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.TM_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to Movies DB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);

            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMoviesAdapter.clear();

                for (Movie movie : result) {
                    mMoviesAdapter.add(movie);
                }

                if(mMoviesAdapter.getCount() > 0 ) {
                    if(movieDetailFragment != null) {
                        movieDetailFragment.update(mMoviesAdapter.getItem(0));
                    }

                }

            }else{
                Toast.makeText(getActivity(),  "There was some error retrieving movies information",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class FetchMoviesFromDB extends AsyncTask<Cursor, Integer, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(Cursor... params) {
            Cursor cursor = params[0];
            if(cursor.moveToFirst()) {
                cursor.moveToLast();
            }
            ArrayList<Movie> items = new ArrayList<>(cursor.getCount());
            if(cursor.moveToFirst()) {
                do {
                    items.add(new Movie(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieItems) {
            super.onPostExecute(movieItems);
            if (movieItems == null) {
                Log.e(TAG, "onPostExecute: NULL");
                return;
            }

            mMoviesAdapter.clear();
            mMoviesAdapter.addAll(movieItems);

            if(mMoviesAdapter.getCount() > 0 ) {
                if(movieDetailFragment != null) {
                    movieDetailFragment.update(mMoviesAdapter.getItem(0));
                }
            }
        }

    }
}
