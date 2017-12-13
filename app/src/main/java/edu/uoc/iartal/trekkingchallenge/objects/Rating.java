package edu.uoc.iartal.trekkingchallenge.objects;

import android.os.Parcel;
import android.os.Parcelable;


// Rating object class. Implements Parcelable to pass rating object between activities
public class Rating implements Parcelable{
    private String id, title, opinion, route, user;
    private Float value;

    public Rating(){

    }

    public Rating(String id, String title, String opinion, String route, String user, Float value) {
        this.id = id;
        this.title = title;
        this.opinion = opinion;
        this.route = route;
        this.user = user;
        this.value = value;
    }

    public Rating(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.opinion = in.readString();
        this.route = in.readString();
        this.user = in.readString();
        this.value = in.readFloat();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(opinion);
        dest.writeString(route);
        dest.writeString(user);
        dest.writeFloat(value);
    }

    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {

        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
