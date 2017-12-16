package edu.uoc.iartal.trekkingchallenge.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;


// Group object class. Implements Parcelable to pass group object between activities
public class Group implements Parcelable {
    private String id, name, description, userAdmin;
    private Boolean isPublic;
    private int numberOfMembers;
    private Map<String, String> members = new HashMap<>();

    public Group() {

    }

    public Group(String id, String name, String description, Boolean isPublic, String userAdmin, int numberOfMembers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.userAdmin = userAdmin;
        this.numberOfMembers = numberOfMembers;
    }

    public Group(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.userAdmin = in.readString();
        this.numberOfMembers = in.readInt();
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(String userAdmin) {
        this.userAdmin = userAdmin;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
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
        dest.writeString(userAdmin);
        dest.writeInt(numberOfMembers);
        dest.writeMap(members);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {

        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // Method to compare two group objects
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group){
            Group groupObject = (Group) obj;
            if (groupObject.getId().equals(this.id)){
                return true;
            }
        }
        return false;
    }
}
