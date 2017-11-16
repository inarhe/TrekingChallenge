package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class Group {

    public String idGroup;
    public String groupName;
    public String groupDescription;
    public Boolean isPublic;


    public Group() {

    }

    public Group(String idGroup, String groupName, String groupDescription, Boolean isPublic) {
        this.idGroup = idGroup;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.isPublic = isPublic;
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


}
