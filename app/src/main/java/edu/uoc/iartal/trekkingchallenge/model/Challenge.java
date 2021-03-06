package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

// Challenge object class. Implements Parcelable to pass challenge object between activities
public class Challenge implements Parcelable{
    private String id, name, description, limitDate, route, userAdmin, classification;
    private boolean isPublic;
    private int numberOfMembers;
    private Map<String, String> results = new HashMap<>();
    private Map<String, String> members = new HashMap<>();

    // Default constructor required for calls to DataSnapshot.getValue(Challenge.class)
    public Challenge() {

    }

    public Challenge(String id, String name, String description, String limitDate, String route, String userAdmin, Boolean isPublic, int numberOfMembers, String classification) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.limitDate = limitDate;
        this.route = route;
        this.userAdmin = userAdmin;
        this.isPublic = isPublic;
        this.numberOfMembers = numberOfMembers;
        this.classification = classification;
    }

    public Challenge(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.limitDate = in.readString();
        this.route = in.readString();
        this.userAdmin = in.readString();
        this.numberOfMembers = in.readInt();
        this.classification = in.readString();
        in.readMap(results, String.class.getClassLoader());
        in.readMap(members, String.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Map<String, String> getResults() {
        return this.results;
    }

    public Map<String, String> getMembers() {
        return this.members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(limitDate);
        dest.writeString(route);
        dest.writeString(userAdmin);
        dest.writeInt(numberOfMembers);
        dest.writeString(classification);
        dest.writeMap(results);
        dest.writeMap(members);
    }

    // Method to compare two challenge objects
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Challenge){
            Challenge challengeObject = (Challenge) obj;
            if (challengeObject.getId().equals(this.id)){
                return true;
            }
        }
        return false;
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
