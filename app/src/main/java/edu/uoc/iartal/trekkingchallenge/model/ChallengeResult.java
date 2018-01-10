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

// ChallengeResult object class. Implements Parcelable to pass ChallengeResult object between activities
public class ChallengeResult implements Parcelable {

    private String id, user, userAlias, challenge, date, name;
    private double distance;
    private int position, hours, minutes;

    // Default constructor required for calls to DataSnapshot.getValue(ChallengeResult.class)
    public ChallengeResult() {

    }

    public ChallengeResult(String id, Double distance, int hours, int minutes, String user, String userAlias, String challenge, String date, int position, String name) {
        this.id = id;
        this.distance = distance;
        this.hours = hours;
        this.minutes = minutes;
        this.user = user;
        this.userAlias = userAlias;
        this.challenge = challenge;
        this.date = date;
        this.position = position;
        this.name = name;
    }

    public ChallengeResult(Parcel in) {
        this.id = in.readString();
        this.distance = in.readDouble();
        this.hours = in.readInt();
        this.minutes = in.readInt();
        this.user = in.readString();
        this.userAlias = in.readString();
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

    public String getUser() {
            return user;
        }

    public void setUser(String user) {
            this.user = user;
        }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
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
        dest.writeInt(hours);
        dest.writeInt(minutes);
        dest.writeString(user);
        dest.writeString(userAlias);
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