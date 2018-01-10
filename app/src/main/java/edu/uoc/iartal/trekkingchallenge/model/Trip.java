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

import java.util.HashMap;
import java.util.Map;

// Trip object class. Implements Parcelable to pass trip object between activities
public class Trip implements Parcelable{

    private String id, name, description, date, place, route, userAdmin;
    private boolean isPublic;
    private int numberOfMembers;
    private Map<String, String> members = new HashMap<>();
    private Map<String, String> done = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
    public Trip() {

    }

    public Trip(String id, String name, String description, String date, String place, String route,
                Boolean isPublic, String userAdmin, int numberOfMembers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.place = place;
        this.route = route;
        this.isPublic = isPublic;
        this.userAdmin = userAdmin;
        this.numberOfMembers = numberOfMembers;
    }

    public Trip(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.date = in.readString();
        this.place = in.readString();
        this.route = in.readString();
        this.userAdmin = in.readString();
        this.numberOfMembers = in.readInt();
        in.readMap(members, String.class.getClassLoader());
        in.readMap(done, String.class.getClassLoader());
        in.readMap(messages, String.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Map<String, String> getMembers() {
        return this.members;
    }

    public Map<String, String> getDone() {
        return this.done;
    }

    public Map<String, String> getMessages() {
        return this.messages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(place);
        dest.writeString(route);
        dest.writeString(userAdmin);
        dest.writeInt(numberOfMembers);
        dest.writeMap(members);
        dest.writeMap(done);
        dest.writeMap(messages);
    }

    // Method to compare two trip objects
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trip){
            Trip tripObject = (Trip) obj;
            if (tripObject.getId().equals(this.id)){
                return true;
            }
        }
        return false;
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
