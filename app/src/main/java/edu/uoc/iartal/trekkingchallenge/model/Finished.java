package edu.uoc.iartal.trekkingchallenge.model;


import android.os.Parcel;
import android.os.Parcelable;

// Finished object class. Implements Parcelable to pass finished object between activities
public class Finished implements Parcelable {
    private String id, user, route, date, name;
    private Double distance, time;

    public Finished (){

    }

    public Finished(String id, String user, String route, String date, Double distance, Double time, String name) {
        this.id = id;
        this.user = user;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.name = name;
    }

    public Finished(Parcel in) {
        this.id = in.readString();
        this.user = in.readString();
        this.route = in.readString();
        this.date = in.readString();
        this.distance = in.readDouble();
        this.time = in.readDouble();
        this.name = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(route);
        dest.writeString(date);
        dest.writeDouble(distance);
        dest.writeDouble(time);
        dest.writeString(name);
    }

    // Method to compare two group objects
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Finished){
            Finished finishedObject = (Finished) obj;
            if (finishedObject.getId().equals(this.id)){
                return true;
            }
        }
        return false;
    }

    public static final Creator<Finished> CREATOR = new Creator<Finished>() {

        public Finished createFromParcel(Parcel in) {
            return new Finished(in);
        }

        public Finished[] newArray(int size) {
            return new Finished[size];
        }
    };
}
