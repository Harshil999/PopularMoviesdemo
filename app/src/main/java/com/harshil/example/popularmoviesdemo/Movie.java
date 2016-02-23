package com.harshil.example.popularmoviesdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by puneet on 17/02/2016.
 */
public class Movie implements Parcelable {

    String title;
    Double vote_average;
    String release_date;
    String overview;
    String poster_path;


    public Movie(String title1, Double vote_average1, String release_date1, String overview1, String poster_path1) {
        title = title1;
        vote_average = vote_average1;
        release_date = release_date1;
        overview = overview1;
        poster_path = poster_path1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(overview);
        out.writeDouble(vote_average);
        out.writeString(release_date);
        out.writeString(poster_path.toString());
    }

    private Movie(Parcel in) {
        title = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        poster_path = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
