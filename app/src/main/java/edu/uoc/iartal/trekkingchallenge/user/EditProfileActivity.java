package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextidUser, editTextName, editTextMail, editTextPass, editTextRepeatPass;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private FirebaseUser firebaseUser;
    private  String currentIdUser, currentUserName, currentUserMail, currentUserPassword, userKey;
    private Intent result;
    private ProgressDialog progressDialog;

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

        firebaseAuth = FirebaseAuth.getInstance();
        result = new Intent();
        progressDialog = new ProgressDialog(this);

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



        }


    public void editProfile (View view){
        // Check input parameters and register user when accept button is clicked
        Boolean passwordChanged = false;
        Boolean mailChanged = false;
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String idUser = editTextidUser.getText().toString().trim();
        String userName = editTextName.getText().toString().trim();
        final String userMail = editTextMail.getText().toString().trim();
        final String userPassword = editTextPass.getText().toString().trim();
        String repeatPassword = editTextRepeatPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(idUser)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(userMail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        if (!userPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

        result.putExtra("userMail", currentUserMail);
        result.putExtra("userName", currentUserName);
        result.putExtra("idUser", currentIdUser);

        Log.i("NYE1", result.getExtras().getString("userMail"));

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        if ((!currentIdUser.equals(idUser)) && (!TextUtils.isEmpty(idUser))){
            databaseUser.child(userKey).child(FireBaseReferences.IDUSER_REFERENCE).setValue(idUser);
            result.putExtra("idUser", idUser);

        }
        if ((!currentUserName.equals(userName)) && (!TextUtils.isEmpty(userName))){
            databaseUser.child(userKey).child(FireBaseReferences.USERNAME_REFERENCE).setValue(userName);
            result.putExtra("userName", userName);

        }


     /*   if ((!currentUserMail.equals(userMail)) && (!TextUtils.isEmpty(userMail))) {
            mailChanged = true;
            reauthenticateUser(firebaseUser, currentUserMail, currentUserPassword);
            firebaseUser.updateEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseUser.child(userKey).child(FireBaseReferences.USERMAIL_REFERENCE).setValue(userMail);
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



        setResult(RESULT_OK, result);
        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();
      //  startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
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



