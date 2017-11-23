package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private  String currentIdUser, currentUserName, currentUserMail, currentUserPassword, userKey;


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
        }

            // Get data from itemc clicked on list groups activity
            Bundle userData = getIntent().getExtras();
            currentIdUser = userData.getString("idUser");
            currentUserName = userData.getString("userName");
            currentUserMail = userData.getString("userMail");
            currentUserPassword = userData.getString("userPassword");
            userKey = userData.getString("userKey");

            editTextidUser.setText(currentIdUser);
            editTextName.setText(currentUserName);
            editTextMail.setText(currentUserMail);
            editTextPass.setText(currentUserPassword);
            editTextRepeatPass.setText(currentUserPassword);


            // Get database user data with current user mail
            /*final String mail = firebaseAuth.getCurrentUser().getEmail();
            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild("mailUser").equalTo(mail);

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //idUserOld = ;
                    editTextidUser.setText(dataSnapshot.getValue(User.class).getIdUser());
                    editTextName.setText(dataSnapshot.getValue(User.class).getUserName());
                    editTextMail.setText(dataSnapshot.getValue(User.class).getUserMail());
                    editTextPass.setText(dataSnapshot.getValue(User.class).getUserPassword());
                    editTextRepeatPass.setText(dataSnapshot.getValue(User.class).getUserPassword());
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
            });*/
        }


    public void editProfile (View view){
        // Check input parameters and register user when accept button is clicked
        Boolean passwordChanged = false;
        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        final FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String idUser = editTextidUser.getText().toString().trim();
        String userName = editTextName.getText().toString().trim();
        final String userMail = editTextMail.getText().toString().trim();
        final String userPassword = editTextPass.getText().toString().trim();
        String repeatPassword = editTextRepeatPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further


        if ((!currentIdUser.equals(idUser)) && (!TextUtils.isEmpty(idUser))){
            databaseUser.child(userKey).child(FireBaseReferences.IDUSER_REFERENCE).setValue(idUser);
        } else if (TextUtils.isEmpty(idUser)) {
                Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
                return;
        }

        if ((!currentUserName.equals(userName)) && (!TextUtils.isEmpty(userName))){
            databaseUser.child(userKey).child(FireBaseReferences.USERNAME_REFERENCE).setValue(userName);
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            return;
        }

        if ((!currentUserPassword.equals(userPassword)) && (!TextUtils.isEmpty(userPassword))){
            passwordChanged = true;
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (userPassword.length() < 6) {
            passwordChanged = false;
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        } else if (!userPassword.equals(repeatPassword)) {
            passwordChanged = false;
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

        if ((!currentUserMail.equals(userMail)) && (!TextUtils.isEmpty(userMail))) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(currentUserMail, userPassword);
            firebaseUser.reauthenticate(credential);

            if (passwordChanged) {
                firebaseUser.updateEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(userPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(userMail, userPassword);
                                        firebaseUser.reauthenticate(credential);
                                        databaseUser.child(userKey).child(FireBaseReferences.USERMAIL_REFERENCE).setValue(userMail);
                                        databaseUser.child(userKey).child(FireBaseReferences.USERPASSWORD_REFERENCE).setValue(userPassword);
                                        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
                                        Log.i("SUCCESSPASS", "chachi pass");
                                    } else {
                                        Log.i("FAILPASS", "caca pass");
                                        Log.e("ERRORPASS", "error pass", task.getException());
                                    }

                                }
                            });
                            Log.i("SUCCESSMAIL", "chachi mail");
                        } else {
                            Log.i("FAILMAIL", "caca mail");
                            Log.e("ERRORMAIL", "error mail", task.getException());
                        }


                    }
                });
            } else {
                firebaseUser.updateEmail(userMail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(userPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(userMail, currentUserMail);
                                                firebaseUser.reauthenticate(credential);
                                                databaseUser.child(userKey).child(FireBaseReferences.USERMAIL_REFERENCE).setValue(userMail);
                                                Log.i("SUCCESSMAILSOL", "chachi mail");
                                                Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("FAILMAILSOL", "caca mail");
                                                Log.e("ERRORMAILSOL", "error mail", task.getException());
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
        Intent result = new Intent();
        result.putExtra("idUser", idUser);
        result.putExtra("userMail", userMail);
        result.putExtra("userName", userName);
        setResult(RESULT_OK, result);
        // startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
        finish();
    }

    public void editProfileCanceled (View view) {
        setResult(RESULT_CANCELED, null);
        finish();
    }

           /* firebaseUser.updateEmail(userMail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(userMail, userPassword);


                                firebaseUser.reauthenticate(credential);
                                User user = new User(idUser,userName,userMail,userPassword);
                                //  databaseUser.child(dataSnapshot.getKey()).setValue(user);
                                Log.i("SUCCESS", "User email address updated.");

                            } else {
                                Log.i("FAIL", "caca.");
                                Log.e("ERROR", "fail", task.getException());

                            }



        } else if (TextUtils.isEmpty(userMail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        }*/





      //  User user = new User(idUser,userName,userMail,userPassword);
       // DatabaseReference child = databaseUser.child(idUserOld);


     //   databaseUser.child(idUserOld).setValue(user);



       // final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
       /* Query query = databaseUser.orderByChild("mailUser").equalTo(currentMail);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                final FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();

             //   if (!currentMail.equals(userMail)) {
                    Log.i("MAIL", currentMail);
                    Log.i("USERMAIL", userMail);

              //  if (currentMail.equals(userMail))
                AuthCredential credential = EmailAuthProvider
                        .getCredential(currentMail, userPassword);

                firebaseUser.reauthenticate(credential);

                    firebaseUser.updateEmail(userMail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(userMail, userPassword);


                                firebaseUser.reauthenticate(credential);
                                User user = new User(idUser,userName,userMail,userPassword);
                              //  databaseUser.child(dataSnapshot.getKey()).setValue(user);
                                Log.i("SUCCESS", "User email address updated.");
                                Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile),Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("FAIL", "caca.");
                                Log.e("ERROR", "fail", task.getException());

                            }
                        }
                    });
                    //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                /*    AuthCredential credential = EmailAuthProvider
                            .getCredential(userMail, userPassword);
                    firebaseUser.reauthenticate(credential);*/
            //    }*/


      /*      @Override
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
        });*/


  /*  Intent result = new Intent();
    result.putExtra("idUser", idUser);
    result.putExtra("userMail", userMail);
    result.putExtra("userName", userName);
    setResult(15, result);*/
       // startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
       // finish();
    /*} else {
        Toast.makeText(RegisterActivity.this,getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
    }*/
}



