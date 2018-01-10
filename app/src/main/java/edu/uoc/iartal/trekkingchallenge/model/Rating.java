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


// Rating object class. Implements Parcelable to pass rating object between activities
public class Rating implements Parcelable{

    private String id, title, opinion, route, user;
    private float value;

    // Default constructor required for calls to DataSnapshot.getValue(Rating.class)
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
