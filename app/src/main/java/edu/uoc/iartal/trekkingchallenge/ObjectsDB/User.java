package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

import java.util.List;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class User {

    public String idUser;
  //  public String nick;
    public String nameUser;
    public String mailUser;
    public String passwordUser;



    public String groups;

    public User() {

    }




    public User(String idUser, String nameUser, String mailUser, String passwordUser, String group) {
        this.idUser = idUser;

        this.nameUser = nameUser;
        this.mailUser = mailUser;
        this.passwordUser = passwordUser;
        this.groups = group;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }



    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
}
