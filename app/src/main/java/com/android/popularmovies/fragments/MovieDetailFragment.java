package com.android.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popularmovies.model.Movie;
import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
  Movie details fragment. This will be used by movie detail activity
 */
public class MovieDetailFragment extends Fragment {

    private Movie mMovie ;

    private ImageView mPosterImageView;

    private ImageView mBackgroundImageView;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null)
            mMovie  = bundle.getParcelable("Movie");
        else
            return rootView;

        mPosterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
        mBackgroundImageView = (ImageView) rootView.findViewById(R.id.wallpaper_img);

        TextView textView = (TextView) rootView.findViewById(R.id.description);
        textView.setText(mMovie.getDescription());

        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating);
        ratingTextView.setText(String.format("%.2f", mMovie.getRating()));

        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Calendar calendar = Calendar.getInstance();

        try {
            Date date = dateFormat.parse(mMovie.getReleaseDate());
            calendar.setTime(date);
            releaseDateTextView.setText(String.format("%d", calendar.get(Calendar.YEAR)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
        titleTextView.setText(mMovie.getTitle());
        this.getActivity().setTitle(mMovie.getTitle());

        return rootView;
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
}

