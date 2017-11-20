package edu.uoc.iartal.trekkingchallenge.ObjectsDB;


/**
 * Created by Ingrid Artal on 04/11/2017.
 */

// User object class
public class User {
    private String idUser, nameUser, mailUser, passwordUser;

    public User() {

    }

    public User(String idUser, String nameUser, String mailUser, String passwordUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.mailUser = mailUser;
        this.passwordUser = passwordUser;
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

}
