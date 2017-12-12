package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextidUser, editTextName, editTextMail, editTextPass, editTextRepeatPass;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private FirebaseUser firebaseUser;
    private  String currentIdUser, currentUserName, currentUserMail, currentUserPassword, userKey;
    private Intent result;
    private ProgressDialog progressDialog;
    private User user;

 /*   final Lock lock = new ReentrantLock();
    final Condition notMailChanged = lock.newCondition();
    final Condition notPasswordChanged = lock.newCondition();*/


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

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        result = new Intent();
        progressDialog = new ProgressDialog(this);

        editTextidUser = (EditText) findViewById(R.id.etIdUser);
        editTextName = (EditText) findViewById(R.id.etUserName);
        editTextMail = (EditText) findViewById(R.id.etUserMail);
        editTextPass = (EditText) findViewById(R.id.etUserPass);
        editTextRepeatPass = (EditText) findViewById(R.id.etPassRepeat);

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

            // Get data from itemc clicked on list groups activity
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable("user");
          /*  currentIdUser = userData.getString("idUser");
            currentUserName = userData.getString("userName");
            currentUserMail = userData.getString("userMail");
            currentUserPassword = userData.getString("userPassword");
            userKey = userData.getString("userKey");*/

            editTextidUser.setText(user.getIdUser());
            editTextName.setText(user.getUserName());
            editTextMail.setText(user.getUserMail());
            editTextPass.setText(user.getUserPassword());
            editTextRepeatPass.setText(user.getUserPassword());

       /* AuthCredential credential = EmailAuthProvider
                .getCredential(user.getUserMail(), user.getUserPassword());*/
        firebaseAuth.getInstance().getCurrentUser().reauthenticate(EmailAuthProvider
                .getCredential(user.getUserMail(), user.getUserPassword()));

        }


    public void editProfile (View view){
        // Check input parameters and register user when accept button is clicked
        Boolean passwordChanged = false;
        Boolean mailChanged = false;
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String newIdUser = editTextidUser.getText().toString().trim();
        String newUserName = editTextName.getText().toString().trim();
        final String newUserMail = editTextMail.getText().toString().trim();
        final String newUserPassword = editTextPass.getText().toString().trim();
        String newRepeatPassword = editTextRepeatPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(newIdUser)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(newUserName)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(newUserMail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(newUserPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        }

        if (newUserPassword.length() < 6) {
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        if (!newUserPassword.equals(newRepeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

      /*  result.putExtra("userMail", currentUserMail);
        result.putExtra("userName", currentUserName);
        result.putExtra("idUser", currentIdUser);*/

        //Log.i("NYE1", result.getExtras().getString("userMail"));

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        if (!(user.getIdUser()).equals(newIdUser)){
            databaseUser.child(user.getIdUser()).child(FireBaseReferences.IDUSER_REFERENCE).setValue(newIdUser);
          //  result.putExtra("idUser", idUser);

        }
        if ((!(user.getUserName()).equals(newUserName))){
            databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERNAME_REFERENCE).setValue(newUserName);
         //   result.putExtra("userName", userName);

        }

        if ((!(user.getUserMail()).equals(newUserMail))) {
            //mailChanged = true;
           // reauthenticateUser(firebaseUser, currentUserMail, currentUserPassword);
            firebaseUser.updateEmail(newUserMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseUser.child(user.getIdUser()).child(FireBaseReferences.USER_MAIL_REFERENCE).setValue(newUserMail);
                       // result.putExtra("user", user);



                        if ((!(user.getUserPassword()).equals(newUserPassword))) {
                            // passwordChanged = true;
                            firebaseUser.updatePassword(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Log.i("SUCCESSPASS", "chachi pass");
                                        databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERPASSWORD_REFERENCE).setValue(newUserPassword);
                                        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
                                        reauthenticateUser(firebaseUser, newUserMail, newUserPassword);
                                        result.putExtra("userMail", newUserMail);
                                        setResult(RESULT_OK, result);
                                        finish();
                                    } else {

                                    }

                                }
                            });

                        } else {
                            reauthenticateUser(firebaseUser, newUserMail, user.getUserPassword());
                            result.putExtra("userMail", newUserMail);
                            setResult(RESULT_OK, result);
                            finish();
                            Toast.makeText(EditProfileActivity.this, "Mail no modificat", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            if ((!(user.getUserPassword()).equals(newUserPassword))) {
                // passwordChanged = true;
                firebaseUser.updatePassword(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Log.i("SUCCESSPASS", "chachi pass");
                            databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERPASSWORD_REFERENCE).setValue(newUserPassword);
                            Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
                            reauthenticateUser(firebaseUser, user.getUserMail(), newUserPassword);

                            finish();
                        } else {

                        }

                    }
                });

            } else {
               // reauthenticateUser(firebaseUser, newUserMail, newUserPassword);
              //  Toast.makeText(EditProfileActivity.this, "Mail no modificat", Toast.LENGTH_SHORT).show();
            }
        }

     /*   if ((!currentUserMail.equals(userMail)) && (!TextUtils.isEmpty(userMail))) {
            mailChanged = true;
            reauthenticateUser(firebaseUser, currentUserMail, currentUserPassword);
            firebaseUser.updateEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseUser.child(userKey).child(FireBaseReferences.USER_MAIL_REFERENCE).setValue(userMail);
                        result.putExtra("userMail", userMail);


                        Log.i("NYE2", result.getExtras().getString("userMail"));
                        Log.i("SUCCESSMAIL", "chachi mail");
                    } else {
                        Log.i("FAILMAIL", "caca mail");
                        Log.e("ERRORMAIL", "error mail", task.getException());
                        Toast.makeText(EditProfileActivity.this, "Mail no modificat", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            reauthenticateUser(firebaseUser, userMail, currentUserPassword);
        }
        Log.i("CURRENTPASS", currentUserPassword);
        Log.i("NEWPASS", userPassword);
        if ((!currentUserPassword.equals(userPassword)) && (!TextUtils.isEmpty(userPassword))){
           // passwordChanged = true;
            if (!mailChanged){
                reauthenticateUser(firebaseUser, currentUserMail, currentUserPassword);
            }
            firebaseUser.updatePassword(userPassword).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("SUCCESSPASS", "chachi pass");
                        databaseUser.child(userKey).child(FireBaseReferences.USERPASSWORD_REFERENCE).setValue(userPassword);
                        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
                        reauthenticateUser(firebaseUser, userMail, userPassword);
                    } else {
                        Log.i("FAILPASS", "caca pass");
                        Log.e("ERRORPASS", "error pass", task.getException());
                    }

                }
            });*/

        //}


        progressDialog.dismiss();




        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
      //  startActivity(new Intent(getApplicationContext(), MainActivity.class));



      //  setResult(RESULT_OK, result);
       // startActivity(new Intent(this, UserAreaActivity.class));
      //  finish();

    }

    public void editProfileCanceled (View view) {
     //   setResult(RESULT_CANCELED, null);
        finish();
    }






    //Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();*/

    public void reauthenticateUser (FirebaseUser firebaseUser, String mail, String password){
        Log.i("AUTHUSER", mail);
        Log.i("AUTHPASS", password);
        AuthCredential credential = EmailAuthProvider
                .getCredential(mail, password);
        firebaseUser.reauthenticate(credential);
    }




}



