package edu.uoc.iartal.trekkingchallenge.objectsDB;

/**
 * Created by Ingrid Artal on 09/12/2017.
 */

public class Finished {

    private String idFinish, user, route, date, distance, hour, minute;

    public Finished (){

    }

    public Finished(String idFinish, String user, String route, String date, String distance, String hour, String minute) {
        this.idFinish = idFinish;
        this.user = user;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.hour = hour;
        this.minute = minute;
    }

    public String getIdFinish() {
        return idFinish;
    }

    public void setIdFinish(String idFinish) {
        this.idFinish = idFinish;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
