package edu.uoc.iartal.trekkingchallenge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class RegisterActivity extends Activity {

    private EditText editTextUserId, editTextUserName, editTextUserMail, editTextUserPass, editTextPassRepeat;
    private String idUser, userName, userMail, userPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        progressDialog = new ProgressDialog(this);

        editTextUserId = (EditText) findViewById(R.id.editTextIdUser);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserMail = (EditText) findViewById(R.id.editTextUserMail);
        editTextUserPass = (EditText) findViewById(R.id.editTextUserPass);
        editTextPassRepeat = (EditText) findViewById(R.id.editTextPassRepeat);
    }

    public void registerUser (View view){
        idUser = editTextUserId.getText().toString().trim();
        userName = editTextUserName.getText().toString().trim();
        userMail = editTextUserMail.getText().toString().trim();
        userPassword = editTextUserPass.getText().toString().trim();
        String repeatPassword = editTextPassRepeat.getText().toString().trim();

        if(TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(idUser)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(userMail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }else if (userPassword.length() < 6){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        if(!userPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(userMail,userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user is successfully registered and logged in
                            //We will start the profile activity here
                            User user = new User(idUser,userName,userMail,userPassword, "none");
                            // FirebaseUser user = firebaseAuth.getCurrentUser();
                            //.child(user.getUid())
                            databaseUser.child(idUser).setValue(user);
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, getString(R.string.successfulRegister),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this,getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                // String id = databaseUser.push().getKey();


    }


}
