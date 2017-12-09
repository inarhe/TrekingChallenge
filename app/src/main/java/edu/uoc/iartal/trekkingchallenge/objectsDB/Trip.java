package edu.uoc.iartal.trekkingchallenge.objectsDB;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class Trip {
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
}
