package edu.uoc.iartal.trekkingchallenge.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUserId, editTextUserName, editTextUserMail, editTextUserPass, editTextPassRepeat;
    private String idUser, userName, userMail, userPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get Firebase authentication instance and database user reference instance
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
        // Check input parameters and register user when accept button is clicked
        idUser = editTextUserId.getText().toString().trim();
        userName = editTextUserName.getText().toString().trim();
        userMail = editTextUserMail.getText().toString().trim();
        userPassword = editTextUserPass.getText().toString().trim();
        String repeatPassword = editTextPassRepeat.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if(TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(idUser)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(userMail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (userPassword.length() < 6){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        if(!userPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

      /*  databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TEST:","hola");
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Log.i("USERID:",child.getValue(User.class).getIdUser());
                    Log.i("USERMAIL:",child.getValue(User.class).getUserMail());
                    if (child.getValue(User.class).getIdUser().equals(idUser)){
                        Toast.makeText(getApplicationContext(), getString(R.string.idUserExists), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (child.getValue(User.class).getUserMail().equals(userMail)){
                        Toast.makeText(getApplicationContext(), getString(R.string.mailUserExists), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        // Execute firebase user registration function
        firebaseAuth.createUserWithEmailAndPassword(userMail,userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // If user is successfully registered and logged in, start main activity
                            User user = new User(idUser,userName,userMail,userPassword);
                            String id = databaseUser.push().getKey();
                            databaseUser.child(id).setValue(user);
                        //    databaseUser.child(idUser).setValue(user);
                            progressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this, getString(R.string.successfulRegister),Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
