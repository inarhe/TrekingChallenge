package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class Challenge implements Parcelable{
    private String idChallenge, challengeName, challengeDescription, limitDate, route, userAdmin;
    private Boolean isPublic;
    private int numberOfMembers;
    private Map<String, String> results = new HashMap<>();

    public Challenge() {

    }

    public Challenge(String idChallenge, String challengeName, String challengeDescription, String limitDate, String route, String userAdmin, Boolean isPublic, int numberOfMembers) {
        this.idChallenge = idChallenge;
        this.challengeName = challengeName;
        this.challengeDescription = challengeDescription;
        this.limitDate = limitDate;
        this.route = route;
        this.userAdmin = userAdmin;
        this.isPublic = isPublic;
        this.numberOfMembers = numberOfMembers;
    }

    public Challenge(Parcel in) {
        this.idChallenge = in.readString();
        this.challengeName = in.readString();
        this.challengeDescription = in.readString();
        this.limitDate = in.readString();
        this.route = in.readString();
        this.userAdmin = in.readString();
        this.numberOfMembers = in.readInt();
        in.readMap(results, String.class.getClassLoader());
    }

    public String getIdChallenge() {
        return idChallenge;
    }

    public void setIdChallenge(String idChallenge) {
        this.idChallenge = idChallenge;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    public String getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(String limitDate) {
        this.limitDate = limitDate;
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

    public Map<String, String> getResults() {
        return this.results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idChallenge);
        dest.writeString(challengeName);
        dest.writeString(challengeDescription);
        dest.writeString(limitDate);
        dest.writeString(route);
        dest.writeString(userAdmin);
        dest.writeInt(numberOfMembers);
        dest.writeMap(results);
    }

    public static final Creator<Challenge> CREATOR = new Creator<Challenge>() {

        public Challenge createFromParcel(Parcel in) {
            return new Challenge(in);
        }

        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };
}
