package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

// History object class. Implements Parcelable to pass history object between activities
public class History implements Parcelable {

    private double totalDistance;
    private int challengeWin, totalSlope, totalHour, totalMin;
    private String id, user;

    // Default constructor required for calls to DataSnapshot.getValue(History.class)
    public History() {

    }

    public History(String id, Double totalDistance, int totalHour, int totalMin, int challengeWin,  int totalSlope, String user) {
        this.id = id;
        this.totalDistance = totalDistance;
        this.totalHour = totalHour;
        this.totalMin = totalMin;
        this.challengeWin = challengeWin;
        this.totalSlope = totalSlope;
        this.user = user;
    }

    public History(Parcel in) {
        this.id = in.readString();
        this.totalDistance = in.readDouble();
        this.totalHour = in.readInt();
        this.totalMin = in.readInt();
        this.challengeWin = in.readInt();
        this.totalSlope = in.readInt();
        this.user = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(int totalMin) {
        this.totalMin = totalMin;
    }

    public int getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(int totalHour) {
        this.totalHour = totalHour;
    }

    public int getChallengeWin() {
        return challengeWin;
    }

    public void setChallengeWin(int challengeWin) {
        this.challengeWin = challengeWin;
    }

    public int getTotalSlope() {
        return totalSlope;
    }

    public void setTotalSlope(int totalSlope) {
        this.totalSlope = totalSlope;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(totalHour);
        dest.writeInt(totalMin);
        dest.writeDouble(totalDistance);
        dest.writeInt(challengeWin);
        dest.writeInt(totalSlope);
        dest.writeString(user);

    }

    public static final Creator<History> CREATOR = new Creator<History>() {

        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
