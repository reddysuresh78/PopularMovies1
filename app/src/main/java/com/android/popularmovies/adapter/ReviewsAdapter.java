package com.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.MovieReview;

import java.util.ArrayList;

public class ReviewsAdapter extends ArrayAdapter<MovieReview> {
    private Context mContext;
    private ArrayList<MovieReview> mReviews;
    private int mResource;


    public ReviewsAdapter(Context context, int resource, ArrayList<MovieReview> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mReviews = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        MovieReview review = mReviews.get(position);
        ReviewViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mResource, parent, false);

            holder = new ReviewViewHolder();
            holder.author = (TextView) row.findViewById(R.id.author_name);
            holder.review = (TextView) row.findViewById(R.id.review);
            row.setTag(holder);
        } else {
            holder = (ReviewViewHolder) row.getTag();
        }

        holder.author.setText(review.getAuthor());
        holder.review.setText(review.getReview());
        return row;
    }

    private class ReviewViewHolder {
        TextView author;
        TextView review;
    }

}