package com.android.popularmovies.dbhelper;

import android.provider.BaseColumns;

public class PopularMovieContractor {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "desc";
        public static final String POSTER_LINK = "poster_link";
        public static final String BACKGROUND_LINK = "background_link";
        public static final String RELEASE_DATE = "release_date";
        public static final String RATING = "rating_date";
    }
}