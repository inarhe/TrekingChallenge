package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class ChallengeResult implements Parcelable{
    private String id, distance, hour, minute, user, challenge, date;
    private Boolean isWinner;

    public ChallengeResult() {

    }


    public ChallengeResult(String id, String distance, String hour, String minute, String user, String challenge, String date) {
        this.id = id;
        this.distance = distance;
        this.hour = hour;
        this.minute = minute;
        this.user = user;
        this.challenge = challenge;
        this.date = date;
    }

    public ChallengeResult(Parcel in) {
        this.id = in.readString();
        this.distance = in.readString();
        this.hour = in.readString();
        this.minute = in.readString();
        this.user = in.readString();
        this.challenge = in.readString();
      //  this.isWinner = in.readString();
        this.date = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getminute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public Boolean getWinner() {
        return isWinner;
    }

    public void setWinner(Boolean winner) {
        isWinner = winner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(distance);
        dest.writeString(hour);
        dest.writeString(minute);
        dest.writeString(user);
        dest.writeString(challenge);
        dest.writeString(date);
    }

    public static final Creator<ChallengeResult> CREATOR = new Creator<ChallengeResult>() {

        public ChallengeResult createFromParcel(Parcel in) {
            return new ChallengeResult(in);
        }

        public ChallengeResult[] newArray(int size) {
            return new ChallengeResult[size];
        }
    };
}
