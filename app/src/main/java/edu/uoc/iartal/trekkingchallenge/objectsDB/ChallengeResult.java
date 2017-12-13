package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.os.Parcel;
import android.os.Parcelable;


// ChallengeResult object class. Implements Parcelable to pass ChallengeResult object between activities
public class ChallengeResult implements Parcelable{
    private String id, user, challenge, date;
    private int hour, minute;
    private Double distance;
    private Boolean isWinner;

    public ChallengeResult() {

    }

    public ChallengeResult(String id, Double distance, int hour, int minute, String user, String challenge, String date) {
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
        this.distance = in.readDouble();
        this.hour = in.readInt();
        this.minute = in.readInt();
        this.user = in.readString();
        this.challenge = in.readString();
        this.date = in.readString();
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getminute() {
        return minute;
    }

    public void setMinute(int minute) {
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
        dest.writeDouble(distance);
        dest.writeInt(hour);
        dest.writeInt(minute);
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
