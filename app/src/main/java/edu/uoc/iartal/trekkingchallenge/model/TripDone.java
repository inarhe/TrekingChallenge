package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

// TripDone object class. Implements Parcelable to pass tripDone object between activities
public class TripDone implements Parcelable {

    private String id, user, trip, date, tripName, routeName;
    private double distance, time;

    // Default constructor required for calls to DataSnapshot.getValue(TripDone.class)
    public TripDone() {

    }

    public TripDone(String id, Double distance, Double time, String user, String trip, String date, String tripName, String routeName) {
        this.id = id;
        this.distance = distance;
        this.time = time;
        this.user = user;
        this.trip = trip;
        this.date = date;
        this.tripName = tripName;
        this.routeName = routeName;
    }

    public TripDone(Parcel in) {
        this.id = in.readString();
        this.distance = in.readDouble();
        this.time = in.readDouble();
        this.user = in.readString();
        this.trip = in.readString();
        this.date = in.readString();
        this.tripName = in.readString();
        this.routeName = in.readString();
    }

    public String getId() {
            return id;
        }

    public void setId(String id) {
            this.id = id;
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

    public String getUser() {
            return user;
        }

    public void setUser(String user) {
            this.user = user;
        }

    public String getTrip() {
            return trip;
        }

    public void setTrip(String trip) {
            this.trip = trip;
        }

    public String getDate() {
            return date;
        }

    public void setDate(String date) {
            this.date = date;
        }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public int describeContents() {
            return 0;
        }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(distance);
        dest.writeDouble(time);
        dest.writeString(user);
        dest.writeString(trip);
        dest.writeString(date);
        dest.writeString(tripName);
        dest.writeString(routeName);
    }

    public static final Creator<TripDone> CREATOR = new Creator<TripDone>() {
         public TripDone createFromParcel(Parcel in) {
                return new TripDone(in);
            }
         public TripDone[] newArray(int size) {
                return new TripDone[size];
            }
    };
}
