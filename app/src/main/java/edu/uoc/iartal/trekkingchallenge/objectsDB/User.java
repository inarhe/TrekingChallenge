package edu.uoc.iartal.trekkingchallenge.objectsDB;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// User object class
public class User {
    public String idUser, userName, userMail, userPassword;
    public Map<String, String> groups = new HashMap<>();
    public Map<String, String> trips = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public User(String idUser, String userName, String userMail, String userPassword) {
        this.idUser = idUser;
        this.userName = userName;
        this.userMail = userMail;
        this.userPassword = userPassword;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    public Map<String, String> getGroups() {
        return this.groups;
    }

    public Map<String, String> getTrips() {
        return this.trips;
    }

}
