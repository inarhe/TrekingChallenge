package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ingrid Artal on 25/12/2017.
 */
// ChallengeResult object class. Implements Parcelable to pass ChallengeResult object between activities
public class ChallengeResult implements Parcelable {

        private String id, user, challenge, date, name;
        private Double distance, time;
        private int position;

        public ChallengeResult() {

        }

        public ChallengeResult(String id, Double distance, Double time, String user, String challenge, String date, int position, String name) {
            this.id = id;
            this.distance = distance;
            this.time = time;
            this.user = user;
            this.challenge = challenge;
            this.date = date;
            this.position = position;
            this.name = name;
        }

        public ChallengeResult(Parcel in) {
            this.id = in.readString();
            this.distance = in.readDouble();
            this.time = in.readDouble();
            this.user = in.readString();
            this.challenge = in.readString();
            this.date = in.readString();
            this.position = in.readInt();
            this.name = in.readString();
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

        public Double getTime() {
            return time;
        }

        public void setTime(Double time) {
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

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
            dest.writeDouble(distance);
            dest.writeDouble(time);
            dest.writeString(user);
            dest.writeString(challenge);
            dest.writeString(date);
            dest.writeInt(position);
            dest.writeString(name);
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
