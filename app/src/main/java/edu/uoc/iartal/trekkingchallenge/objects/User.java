package edu.uoc.iartal.trekkingchallenge.objects;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;


// User object class. Implements Parcelable to pass user object between activities
public class User implements Parcelable {
    private String idUser, userName, userMail, userPassword;
    private Map<String, String> groups = new HashMap<>();
    private Map<String, String> trips = new HashMap<>();
    private Map<String, String> finished = new HashMap<>();
    private Map<String, String> challenges = new HashMap<>();
    private Map<String, String> ratings = new HashMap<>();
    private Map<String, String> challengesResults = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String idUser, String userName, String userMail, String userPassword) {
        this.idUser = idUser;
        this.userName = userName;
        this.userMail = userMail;
        this.userPassword = userPassword;
    }

    public User(Parcel in) {
        this.idUser = in.readString();
        this.userName = in.readString();
        this.userMail = in.readString();
        this.userPassword = in.readString();
        in.readMap(challenges, String.class.getClassLoader());
        in.readMap(trips, String.class.getClassLoader());
        in.readMap(finished, String.class.getClassLoader());
        in.readMap(challengesResults, String.class.getClassLoader());
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Map<String, String> getGroups() {
        return this.groups;
    }

    public Map<String, String> getTrips() {
        return this.trips;
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

    public Map<String, String> getChallengesResults() {
        return this.challengesResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(userName);
        dest.writeString(userMail);
        dest.writeString(userPassword);
        dest.writeMap(challenges);
        dest.writeMap(trips);
        dest.writeMap(finished);
        dest.writeMap(challengesResults);
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
