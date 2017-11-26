package edu.uoc.iartal.trekkingchallenge;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;

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
