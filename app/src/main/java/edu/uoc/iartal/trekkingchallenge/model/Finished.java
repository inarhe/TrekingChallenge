/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.model;


import android.os.Parcel;
import android.os.Parcelable;

// Finished object class. Implements Parcelable to pass finished object between activities
public class Finished implements Parcelable {

    private String id, user, route, date, name;
    private double distance;
    private int hours, minutes;

    // Default constructor required for calls to DataSnapshot.getValue(Finished.class)
    public Finished (){

    }

    public Finished(String id, String user, String route, String date, Double distance, int hours, int minutes, String name) {
        this.id = id;
        this.user = user;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.hours = hours;
        this.minutes = minutes;
        this.name = name;
    }

    public Finished(Parcel in) {
        this.id = in.readString();
        this.user = in.readString();
        this.route = in.readString();
        this.date = in.readString();
        this.distance = in.readDouble();
        this.hours = in.readInt();
        this.minutes = in.readInt();
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
        dest.writeInt(hours);
        dest.writeInt(minutes);
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
