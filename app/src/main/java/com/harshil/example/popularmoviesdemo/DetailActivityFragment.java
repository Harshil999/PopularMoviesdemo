package com.harshil.example.popularmoviesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    TextView title;
    TextView vote_Average;
    TextView overview;
    TextView release_date;
    ImageView poster;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");

            title = (TextView) rootview.findViewById(R.id.movieTitleTextView);
            title.setText(movie.title);

            vote_Average = (TextView) rootview.findViewById(R.id.movieVoteTextView);
            vote_Average.setText(movie.vote_average + "/10");

            overview = (TextView) rootview.findViewById(R.id.movieOverviewTextView);
            overview.setText(movie.overview);

            release_date = (TextView) rootview.findViewById(R.id.movieDateTextView);
            release_date.setText(movie.release_date);

            poster = (ImageView) rootview.findViewById(R.id.movieImageView);

            String url = "http://image.tmdb.org/t/p/w185/" + movie.poster_path.toString();
            Picasso.with(getActivity()).load(url).into(poster);
        }
        return rootview;
    }
}
