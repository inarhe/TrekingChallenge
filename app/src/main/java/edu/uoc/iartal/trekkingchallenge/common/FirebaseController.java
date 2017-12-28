package edu.uoc.iartal.trekkingchallenge.common;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.objects.History;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.RegisterActivity;

import static java.security.AccessController.getContext;

/**
 * Created by Ingrid Artal on 27/12/2017.
 */

public class FirebaseController extends MultiDexApplication {


    public void createUserAndHistory (final String alias, final String name, final String mail, final String password, final Context context, final ProgressDialog progressDialog){

        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        final DatabaseReference databaseHistory = getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);

        // Execute firebase user registration function
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If user is successfully registered and logged in, create user object and start main activity
                        if(task.isSuccessful()){
                            try {
                                String idHistory = databaseHistory.push().getKey();
                                String idUser = databaseUser.push().getKey();

                                History history = new History(idHistory, 0.0, 0.0, 0, 0, idUser);
                                databaseHistory.child(idHistory).setValue(history);

                                User user = new User(idUser, alias, name, mail, password, idHistory);
                                databaseUser.child(idUser).setValue(user);

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.successfulRegister),Toast.LENGTH_SHORT).show();
                                ((Activity)context).finish();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                            ((Activity)context).finish();
                        }
                    }
                });
    }

    public FirebaseUser checkActiveUserSession (){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void loginDatabase(String email, String password, final ProgressDialog progressDialog, final Context context){
        // Execute firebase sign in function
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If logging is successful start main activity
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),getString(R.string.loginSuccess),Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),getString(R.string.failedLogin),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getCurrentUserEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }


    private DatabaseReference getDatabaseReference (String reference){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(reference);

        return databaseReference;
    }
}
