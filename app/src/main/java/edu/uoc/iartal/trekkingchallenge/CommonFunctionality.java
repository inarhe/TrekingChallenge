package edu.uoc.iartal.trekkingchallenge;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;

/**
 * Created by Ingrid Artal on 22/11/2017.
 */

public class CommonFunctionality {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private String userAdmin;

    /**
     *  Validate mail format
     * @param email
     * @return if mail is ok
     */
    public boolean validateEmail(String email) {
        Pattern pattern;

        // Define mail pattern
        pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validate password length
     * @param password
     * @return if password is ok
     */
    public boolean validatePassword(String password) {

        return password.length() > 5;
    }

    public void updateJoins(String currentMail, final String action, final DatabaseReference databaseObject, final String id, final String objectReference){
        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if (action.equals("join")) {
                    databaseObject.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).setValue("true");
                    databaseUser.child(user.getIdUser()).child(objectReference).child(id).setValue("true");
                } else {
                    databaseObject.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).removeValue();
                    databaseUser.child(user.getIdUser()).child(objectReference).child(id).removeValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TO-DO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }





}
