package com.android.popularmovies.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.R;
import com.android.popularmovies.adapter.ReviewsAdapter;
import com.android.popularmovies.model.MovieReview;

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


public class MovieReviewFragment extends Fragment {

        final static private String TAG = MovieReviewFragment.class.getName();
        private ReviewsAdapter mAdapter;
        private ListView mListView;
        private ArrayList<MovieReview> mReviews;
        private int mID;

        private String mTitle;

        public MovieReviewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_review_list, container, false);

            mID = getActivity().getIntent().getExtras().getInt("ID");
            mTitle = getActivity().getIntent().getExtras().getString("Title");

            mReviews = new ArrayList<>();
            mAdapter = new ReviewsAdapter(getActivity(), R.layout.fragment_movie_review, mReviews);

            mListView = (ListView) rootView.findViewById(R.id.reviews_list);
            mListView.setAdapter(mAdapter);

            new Fetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mID);

            this.getActivity().setTitle(getString(R.string.reviews_for) + " " +  mTitle  );

            return rootView;
        }

        private class Fetcher extends AsyncTask<Integer, Integer, String> {

            @Override
            protected String doInBackground(Integer... params) {
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

                    final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";

                    final String API_KEY = "api_key";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
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

                    return moviesJsonStr;
                } catch (IOException e) {
                    Log.e(TAG, "Error ", e);
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
                            Log.e(TAG, "Error closing stream", e);
                        }
                    }
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s == null) {
                    Toast.makeText(getActivity(), "There was some error retrieving reviews", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject object = new JSONObject(s);
                    JSONArray array = object.getJSONArray("results");

                    ArrayList<MovieReview> reviews = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        MovieReview r = new MovieReview(obj);

                        reviews.add(r);
                    }

                    mAdapter.clear();
                    mAdapter.addAll(reviews);
                } catch (JSONException e) {
                    Log.e(TAG, "Error reading results ", e);

                }

            }
        }
    }

