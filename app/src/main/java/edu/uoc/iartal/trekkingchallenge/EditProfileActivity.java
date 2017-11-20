package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.User.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.User.RegisterActivity;
import edu.uoc.iartal.trekkingchallenge.User.UserAreaActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextidUser, editTextName, editTextMail, editTextPass, editTextRepeatPass;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.editProfileActivity));

        firebaseAuth = FirebaseAuth.getInstance();

        editTextidUser = (EditText) findViewById(R.id.editTextIdUser);
        editTextName = (EditText) findViewById(R.id.editTextUserName);
        editTextMail = (EditText) findViewById(R.id.editTextUserMail);
        editTextPass = (EditText) findViewById(R.id.editTextUserPass);
        editTextRepeatPass = (EditText) findViewById(R.id.editTextPassRepeat);

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            // Get database user data with current user mail
            final String mail = firebaseAuth.getCurrentUser().getEmail();
            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild("mailUser").equalTo(mail);

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    editTextidUser.setText(dataSnapshot.getValue(User.class).getIdUser());
                    editTextName.setText(dataSnapshot.getValue(User.class).getNameUser());
                    editTextMail.setText(dataSnapshot.getValue(User.class).getMailUser());
                    editTextPass.setText(dataSnapshot.getValue(User.class).getPasswordUser());
                    editTextRepeatPass.setText(dataSnapshot.getValue(User.class).getPasswordUser());
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

    public void editProfile (View view){
        // Check input parameters and register user when accept button is clicked
        String idUser = editTextidUser.getText().toString().trim();
        String userName = editTextName.getText().toString().trim();
        String userMail = editTextMail.getText().toString().trim();
        String userPassword = editTextPass.getText().toString().trim();
        String repeatPassword = editTextRepeatPass.getText().toString().trim();

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

        User user = new User(idUser,userName,userMail,userPassword);
        databaseUser.child(idUser).setValue(user);

        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile),Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
        finish();
    /*} else {
        Toast.makeText(RegisterActivity.this,getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
    }*/
    }
}
