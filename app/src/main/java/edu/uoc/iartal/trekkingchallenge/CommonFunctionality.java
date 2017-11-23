package edu.uoc.iartal.trekkingchallenge;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

/**
 * Created by Ingrid Artal on 22/11/2017.
 */

public class CommonFunctionality {

    public AuthCredential reauthenticateUser(String user, String mail){
        AuthCredential credential = EmailAuthProvider
                .getCredential(user, mail);

        return credential;
    }

    public void checkUserFields (String idUser, String userName, String userMail, String password, String repeatPassword){

    }


}
