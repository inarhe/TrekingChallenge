package edu.uoc.iartal.trekkingchallenge;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class Group {

    public String idGroup;
    public String groupName;
    public String groupDescritpion;


    public Group() {

    }

    public Group(String idGgroup, String nameGroup, String descriptionGroup) {
        this.idGroup = idGroup;
        this.groupName = nameGroup;
        this.groupDescritpion = descriptionGroup;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescritpion;
    }



}
