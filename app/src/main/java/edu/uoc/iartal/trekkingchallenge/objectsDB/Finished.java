package edu.uoc.iartal.trekkingchallenge.objectsDB;


import android.os.Parcel;
import android.os.Parcelable;

// Finished object class. Implements Parcelable to pass finished object between activities
public class Finished implements Parcelable {
    private String id, user, route, date;
    private Double distance;
    private int hour, minute;

    public Finished (){

    }

    public Finished(String id, String user, String route, String date, Double distance, int hour, int minute) {
        this.id = id;
        this.user = user;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.hour = hour;
        this.minute = minute;
    }

    public Finished(Parcel in) {
        this.id = in.readString();
        this.user = in.readString();
        this.route = in.readString();
        this.date = in.readString();
        this.distance = in.readDouble();
        this.hour = in.readInt();
        this.minute = in.readInt();
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
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
        dest.writeInt(hour);
        dest.writeInt(minute);
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
