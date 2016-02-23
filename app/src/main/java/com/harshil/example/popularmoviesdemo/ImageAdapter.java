package com.harshil.example.popularmoviesdemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by puneet on 17/02/2016.
 */
public class ImageAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Movie> movies;

    public ImageAdapter(Context C, ArrayList<Movie> M) {
        mContext = C;
        movies = M;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(370, 556));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setPadding(4, 4, 4, 4);

        } else {
            view = (ImageView) convertView;
        }
        String baseImageUrl = "http://image.tmdb.org/t/p/w185/";
        String url = movies.get(position).poster_path;
        Picasso.with(mContext).load(baseImageUrl + url).into(view);

        return view;
    }
}
