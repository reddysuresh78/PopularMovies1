package com.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.popularmovies.model.Movie;
import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    private ArrayList<Movie> mImages;
    private int mResource;


    public ImageAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mImages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.movie);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(mImages.get(position).getPosterPath())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.image);

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
    }
}