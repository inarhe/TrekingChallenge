package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// Group object class
public class Group {
    private String idGroup, groupName, groupDescription, userAdmin;
    private Boolean isPublic;
    private int numberOfMembers;

    public Group() {

    }


    public Group(String idGroup, String groupName, String groupDescription, Boolean isPublic, String userAdmin, int numberOfMembers) {
        this.idGroup = idGroup;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.isPublic = isPublic;
        this.userAdmin = userAdmin;
        this.numberOfMembers = numberOfMembers;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
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

}
