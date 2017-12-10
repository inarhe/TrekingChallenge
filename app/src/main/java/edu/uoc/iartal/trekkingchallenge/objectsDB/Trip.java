package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class Trip implements Parcelable{
    private String idTrip, tripName, tripDescription, date, place, route, userAdmin;
    private Boolean isPublic;
    private int numberOfMembers;

    public Trip() {

    }


    public Trip(String idTrip, String tripName, String tripDescription, String date, String place, String route,
                Boolean isPublic, String userAdmin, int numberOfMembers) {
        this.idTrip = idTrip;
        this.tripName = tripName;
        this.tripDescription = tripDescription;
        this.date = date;
        this.place = place;
        this.route = route;
        this.isPublic = isPublic;
        this.userAdmin = userAdmin;
        this.numberOfMembers = numberOfMembers;
    }

    public Trip(Parcel in) {
        this.idTrip = in.readString();
        this.tripName = in.readString();
        this.tripDescription = in.readString();
        this.date = in.readString();
        this.place = in.readString();
        this.route = in.readString();
        this.userAdmin = in.readString();
        this.numberOfMembers = in.readInt();
    }

    public String getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(String idTrip) {
        this.idTrip = idTrip;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(String userAdmin) {
        this.userAdmin = userAdmin;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idTrip);
        dest.writeString(tripName);
        dest.writeString(tripDescription);
        dest.writeString(date);
        dest.writeString(place);
        dest.writeString(route);
        dest.writeString(userAdmin);
        dest.writeInt(numberOfMembers);
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {

        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}
