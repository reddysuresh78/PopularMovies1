package com.android.popularmovies.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "popular_movies.db";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + PopularMovieContractor.MovieEntry.TABLE_NAME +"("
            + PopularMovieContractor.MovieEntry._ID + " INTEGER PRIMARY KEY,"
            + PopularMovieContractor.MovieEntry.TITLE + " TEXT,"
            + PopularMovieContractor.MovieEntry.DESCRIPTION + " TEXT,"
            + PopularMovieContractor.MovieEntry.MOVIE_ID + " INTEGER UNIQUE,"
            + PopularMovieContractor.MovieEntry.POSTER_LINK + " TEXT,"
            + PopularMovieContractor.MovieEntry.BACKGROUND_LINK + " TEXT,"
            + PopularMovieContractor.MovieEntry.RATING + " FLOAT,"
            + PopularMovieContractor.MovieEntry.RELEASE_DATE + " DATE)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PopularMovieContractor.MovieEntry.TABLE_NAME;

    public MoviesDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}