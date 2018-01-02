package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

// TripDone object class. Implements Parcelable to pass tripDone object between activities
public class TripDone implements Parcelable {

    private String id, user, trip, date, tripName, routeName;
    private double distance;
    private int hours, minutes;

    // Default constructor required for calls to DataSnapshot.getValue(TripDone.class)
    public TripDone() {

    }

    public TripDone(String id, Double distance, int hours, int minutes, String user, String trip, String date, String tripName, String routeName) {
        this.id = id;
        this.distance = distance;
        this.hours = hours;
        this.minutes = minutes;
        this.user = user;
        this.trip = trip;
        this.date = date;
        this.tripName = tripName;
        this.routeName = routeName;
    }

    public TripDone(Parcel in) {
        this.id = in.readString();
        this.distance = in.readDouble();
        this.hours = in.readInt();
        this.minutes = in.readInt();
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

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
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
        dest.writeInt(hours);
        dest.writeInt(minutes);
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
