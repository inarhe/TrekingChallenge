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

// User object class. Implements Parcelable to pass user object between activities
public class User implements Parcelable {

    private String id, alias, name, mail, password, history;
    private Map<String, String> groups = new HashMap<>();
    private Map<String, String> trips = new HashMap<>();
    private Map<String, String> tripsDone = new HashMap<>();
    private Map<String, String> finished = new HashMap<>();
    private Map<String, String> challenges = new HashMap<>();
    private Map<String, String> ratings = new HashMap<>();
    private Map<String, String> challengeResults = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {

    }


    public User(String id, String alias, String name, String mail, String password, String history) {
        this.id = id;
        this.alias = alias;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.history = history;
    }

    public User(Parcel in) {
        this.id = in.readString();
        this.alias = in.readString();
        this.name = in.readString();
        this.mail = in.readString();
        this.password = in.readString();
        this.history = in.readString();
        in.readMap(groups, String.class.getClassLoader());
        in.readMap(ratings, String.class.getClassLoader());
        in.readMap(challenges, String.class.getClassLoader());
        in.readMap(trips, String.class.getClassLoader());
        in.readMap(tripsDone, String.class.getClassLoader());
        in.readMap(finished, String.class.getClassLoader());
        in.readMap(challengeResults, String.class.getClassLoader());
        in.readMap(messages, String.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Map<String, String> getGroups() {
        return this.groups;
    }

    public Map<String, String> getTrips() {
        return this.trips;
    }

    public Map<String, String> getTripsDone() {
        return this.tripsDone;
    }

    public Map<String, String> getFinished() {
        return this.finished;
    }

    public Map<String, String> getChallenges() {
        return this.challenges;
    }

    public Map<String, String> getRatings() {
        return this.ratings;
    }

    public Map<String, String> getChallengeResults() {
        return this.challengeResults;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(alias);
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(password);
        dest.writeString(history);
        dest.writeMap(groups);
        dest.writeMap(ratings);
        dest.writeMap(challenges);
        dest.writeMap(trips);
        dest.writeMap(tripsDone);
        dest.writeMap(finished);
        dest.writeMap(challengeResults);
        dest.writeMap(messages);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
