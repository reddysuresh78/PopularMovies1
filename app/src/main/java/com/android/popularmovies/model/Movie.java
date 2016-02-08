package com.android.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.popularmovies.dbhelper.PopularMovieContractor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A POJO that represents a movie. This object is passed between list activity and detail activity
 * Created by Suresh on 23-12-2015.
 */
public class Movie implements Parcelable {
    private int ID;
    private String title;
    private String posterPath;
    private String backdrop;
    private String description;
    private String releaseDate;
    private Double rating;

    public Movie(JSONObject json) throws JSONException {
        this.ID = json.getInt("id");
        this.title = json.getString("original_title");
        this.posterPath = "http://image.tmdb.org/t/p/w185" + json.getString("poster_path");
        this.backdrop = "http://image.tmdb.org/t/p/original" + json.getString("backdrop_path");
        this.description = json.getString("overview");
        this.releaseDate = json.getString("release_date");
        this.rating = json.getDouble("vote_average");
    }

    public Movie(Cursor cursor) {
        this.ID = cursor.getInt(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.MOVIE_ID));
        this.title = cursor.getString(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.TITLE));
        this.description = cursor.getString(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.DESCRIPTION));
        this.posterPath = cursor.getString(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.POSTER_LINK));
        this.backdrop = cursor.getString(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.BACKGROUND_LINK));
        this.releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.RELEASE_DATE));
        this.rating = cursor.getDouble(cursor.getColumnIndexOrThrow(PopularMovieContractor.MovieEntry.RATING));
    }


    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.title);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdrop);
        dest.writeString(this.description);
        dest.writeString(this.releaseDate);
        dest.writeValue(this.rating);
    }

    protected Movie(Parcel in) {
        this.ID = in.readInt();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.backdrop = in.readString();
        this.description = in.readString();
        this.releaseDate = in.readString();
        this.rating = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
