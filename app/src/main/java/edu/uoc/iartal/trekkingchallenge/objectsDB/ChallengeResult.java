package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class ChallengeResult implements Parcelable{
    private String id, distance, time, user, challenge;
    private Boolean isWinner;
    private int numberOfMembers;

    public ChallengeResult() {

    }


    public ChallengeResult(String id, String distance, String time, String user, String challenge, int numberOfMembers) {
        this.id = id;
        this.distance = distance;
        this.time = time;
        this.user = user;
        this.challenge = challenge;
        this.numberOfMembers = numberOfMembers;
    }

    public ChallengeResult(Parcel in) {
        this.id = in.readString();
        this.distance = in.readString();
        this.time = in.readString();
        this.user = in.readString();
        this.challenge = in.readString();
      //  this.isWinner = in.readString();
        this.numberOfMembers = in.readInt();
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeString(user);
        dest.writeString(challenge);
        dest.writeInt(numberOfMembers);
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
