package com.android.popularmovies.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.R;
import com.android.popularmovies.activities.ReviewsActivity;
import com.android.popularmovies.dbhelper.MoviesDBHelper;
import com.android.popularmovies.dbhelper.PopularMovieContractor;
import com.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
  Movie details fragment. This will be used by movie detail activity
 */
public class MovieDetailFragment extends Fragment {

    final static private String TAG = MovieDetailFragment.class.getName();

    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mDescriptionTextView;
    private ImageView mPosterImageView;
    private ImageView mBackgroundImageView;
    private Button mPlayButton;
    private Button mReviewsButton;
    private Button mMarkAsFavorite;

    private TextView mMsgTextView;
    private Movie  mMovie;
    private static final String ARG_MOVIE = "movieFragment";

    private MoviesDBHelper mDBHelper;


    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(ARG_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


        mMsgTextView = (TextView) rootView.findViewById(R.id.text_view_msg);
        mMsgTextView.setVisibility(View.VISIBLE);
        mTitleTextView = (TextView) rootView.findViewById(R.id.title);
        mRatingTextView = (TextView) rootView.findViewById(R.id.rating);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.description);
        mPlayButton = (Button) rootView.findViewById(R.id.play_button);
        mReviewsButton = (Button) rootView.findViewById(R.id.reviews_button);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
        mBackgroundImageView = (ImageView) rootView.findViewById(R.id.wallpaper_img);
        mMarkAsFavorite = (Button) rootView.findViewById(R.id.mark_as_favorite_button);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("append_to_response", "trailers");
//                Request request = new Request(mMovie.getID(), Request.Type.VIDEOS, map);
                new Fetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getID());
            }


        });

        mReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewsActivity.class);
                intent.putExtra("ID", mMovie.getID());
                intent.putExtra("Title", mMovie.getTitle());
                startActivity(intent);
            }
        });

        mDBHelper = new MoviesDBHelper(getActivity());

        mMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String result = saveToFavorites();

                Toast.makeText(getActivity(), mMovie.getTitle() + result, Toast.LENGTH_LONG).show();
            }
        });

        setVisibility(View.INVISIBLE);


        if(mMovie == null) {
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null) {
                mMovie = bundle.getParcelable("Movie");
                update(mMovie);
            } else
                return rootView;

        }else{
            update(mMovie);
        }
        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mMovie  = savedInstanceState.getParcelable("Movie");

            if (mMovie  != null) {
                update(mMovie );
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        if (mMovie  != null)
            outState.putParcelable("Movie", mMovie );

        super.onSaveInstanceState(outState);
    }

    private void setVisibility(int v) {
        mDescriptionTextView.setVisibility(v);
        mRatingTextView.setVisibility(v);
        mReleaseDateTextView.setVisibility(v);
        mReviewsButton.setVisibility(v);
//        mFab.setVisibility(v);
        mTitleTextView.setVisibility(v);
        mPlayButton.setVisibility(v);
        mMarkAsFavorite.setVisibility(v);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mMovie  == null) {
            return;
        }
        Picasso.with(getActivity()).
                load(mMovie.getPosterPath())
                .into(mPosterImageView);

        Picasso.with(getActivity()).
                load(mMovie.getBackdrop())
                .into(mBackgroundImageView);


    }

    public void update(Movie movie) {
        if(movie == null){
            return;
        }

        if (movie  != mMovie )
            mMovie = movie ;

        setVisibility(View.VISIBLE);
        mMsgTextView.setVisibility(View.INVISIBLE);
        mDescriptionTextView.setText(movie.getDescription());
        mRatingTextView.setText(String.format("%.2f", movie.getRating()));

        try {
            mReleaseDateTextView.setText(movie.getReleaseDate());
        } catch (Exception e) {
            Log.e(TAG, "Error while setting release date",e);
        }

        mTitleTextView.setText(movie.getTitle());
        this.getActivity().setTitle(movie.getTitle());

        Picasso.with(getActivity()).
                load(movie.getPosterPath())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(mPosterImageView);

        Picasso.with(getActivity()).
                load(movie.getBackdrop())
                          .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(mBackgroundImageView);

//        mPosterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
//        mBackgroundImageView = (ImageView) rootView.findViewById(R.id.wallpaper_img);
//
//        TextView textView = (TextView) rootView.findViewById(R.id.description);
//        textView.setText(mMovie.getDescription());
//
//        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating);
//        ratingTextView.setText(String.format("%.2f", mMovie.getRating()));
//
//        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
//        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        final Calendar calendar = Calendar.getInstance();
//
//        try {
//            Date date = dateFormat.parse(mMovie.getReleaseDate());
//            calendar.setTime(date);
//            releaseDateTextView.setText(String.format("%d", calendar.get(Calendar.YEAR)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
//        titleTextView.setText(mMovie.getTitle());
//        this.getActivity().setTitle(mMovie.getTitle());
    }

    private String saveToFavorites() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        if (db == null) {
           
            return "DB is Null";
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMovieContractor.MovieEntry.MOVIE_ID, mMovie.getID());
        contentValues.put(PopularMovieContractor.MovieEntry.TITLE, mMovie.getTitle());
        contentValues.put(PopularMovieContractor.MovieEntry.DESCRIPTION, mMovie.getDescription());
        contentValues.put(PopularMovieContractor.MovieEntry.POSTER_LINK, mMovie.getPosterPath());
        contentValues.put(PopularMovieContractor.MovieEntry.BACKGROUND_LINK, mMovie.getBackdrop());
        contentValues.put(PopularMovieContractor.MovieEntry.RATING, mMovie.getRating());
        contentValues.put(PopularMovieContractor.MovieEntry.RELEASE_DATE, mMovie.getReleaseDate());

        long newRowID = db.insert(PopularMovieContractor.MovieEntry.TABLE_NAME, null, contentValues);
        db.close();

        String toastMessage;
        if (newRowID >= 0) {
            toastMessage = " added to favorites";
        } else {
            toastMessage = " already saved";
        }

        return toastMessage;
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

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";

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
                Toast.makeText(getActivity(), "There was some error retrieving movie details", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("results");
                if (array.length() < 1) return;

                JSONObject obj = array.getJSONObject(0);
                String key = obj.getString("key");
                Log.d(TAG, "onPostExecute: " + key);

                Uri content = Uri.parse("vnd.youtube:" + key);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, content);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.d(TAG, "onPostExecute: Open at youtube app: " + content.toString());
                } catch (ActivityNotFoundException e) {
                    content = Uri.parse("http://youtu.be/" + key);
                    Intent intent = new Intent(Intent.ACTION_VIEW, content);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    Log.d(TAG, "onPostExecute: Open at browser app: " + content.toString());
                }

            } catch (JSONException e) {
                Log.e(TAG, "Error while parsing JSON: ", e);
            }

        }
    }
}

