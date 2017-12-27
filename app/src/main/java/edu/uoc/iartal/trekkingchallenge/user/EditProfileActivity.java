package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import edu.uoc.iartal.trekkingchallenge.objects.User;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextAlias, editTextName, editTextMail, editTextPass, editTextRepeatPass;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private FirebaseUser firebaseUser;
    private Intent result;
    private ProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.editProfileActivity));

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize variables
        firebaseAuth = FirebaseAuth.getInstance();
        result = new Intent();
        progressDialog = new ProgressDialog(this);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        editTextAlias = (EditText) findViewById(R.id.etIdUser);
        editTextName = (EditText) findViewById(R.id.etUserName);
        editTextMail = (EditText) findViewById(R.id.etUserMail);
        editTextPass = (EditText) findViewById(R.id.etUserPass);
        editTextRepeatPass = (EditText) findViewById(R.id.etPassRepeat);

        // Get data from user area activity
        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("user");

        // Set text fields with current user information
        editTextAlias.setText(user.getAlias());
        editTextName.setText(user.getName());
        editTextMail.setText(user.getMail());
        editTextPass.setText(user.getPassword());
        editTextRepeatPass.setText(user.getPassword());

        // Reautheticate current user. Necessary to change user credentials
        reauthenticateUser(firebaseUser, user.getMail(), user.getPassword() );
    }

    /**
     * Executed when edit profile menu option is selected. Verify input parameters and update values
     * @param view
     */
    public void editProfile (View view){
        // Database references
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        firebaseUser = firebaseAuth.getCurrentUser();

        // Get input parameters
        String newAlias = editTextAlias.getText().toString().trim();
        String newUserName = editTextName.getText().toString().trim();
        final String newUserMail = editTextMail.getText().toString().trim();
        final String newUserPassword = editTextPass.getText().toString().trim();
        String newRepeatPassword = editTextRepeatPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(newAlias)) {
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

        // Set progress dialog
        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        // Check idUser parameter and update database if its necessary
        if (!(user.getAlias()).equals(newAlias)){
            databaseUser.child(user.getId()).child(FireBaseReferences.USER_ALIAS_REFERENCE).setValue(newAlias);
        }

        // Check user name parameter and update database if its necessary
        if ((!(user.getName()).equals(newUserName))){
            databaseUser.child(user.getId()).child(FireBaseReferences.USER_NAME_REFERENCE).setValue(newUserName);
        }

        // Check user mail parameter and update database if its necessary
        if ((!(user.getMail()).equals(newUserMail))) {
            // If mail has changed, needs to update firebase user credentials
            firebaseUser.updateEmail(newUserMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseUser.child(user.getId()).child(FireBaseReferences.USER_MAIL_REFERENCE).setValue(newUserMail);

                        // Verify if password has changed too, in order to reauthenticate user
                        if ((!(user.getPassword()).equals(newUserPassword))) {
                            firebaseUser.updatePassword(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseUser.child(user.getId()).child(FireBaseReferences.USER_PASSWORD_REFERENCE).setValue(newUserPassword);
                                        Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();

                                        reauthenticateUser(firebaseUser, newUserMail, newUserPassword);

                                        result.putExtra("userMail", newUserMail);
                                        setResult(RESULT_OK, result);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            reauthenticateUser(firebaseUser, newUserMail, user.getPassword());

                            result.putExtra("userMail", newUserMail);
                            setResult(RESULT_OK, result);
                            finish();
                        }
                    }
                }
            });
        } else {
            if ((!(user.getPassword()).equals(newUserPassword))) {
                // Only has changed password
                firebaseUser.updatePassword(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            databaseUser.child(user.getId()).child(FireBaseReferences.USER_PASSWORD_REFERENCE).setValue(newUserPassword);
                            Toast.makeText(EditProfileActivity.this, getString(R.string.confirmEditProfile), Toast.LENGTH_SHORT).show();

                            reauthenticateUser(firebaseUser, user.getMail(), newUserPassword);

                            finish();
                        }
                    }
                });
            }
        }
        progressDialog.dismiss();
    }

    /**
     * Cancel edit user profile if cancel button is clicked
     * @param view
     */
    public void editProfileCanceled (View view) {
        finish();
    }

    /**
     * Reuthenticate user with current/new credentials
     * @param firebaseUser
     * @param mail
     * @param password
     */
    public void reauthenticateUser (FirebaseUser firebaseUser, String mail, String password){
        AuthCredential credential = EmailAuthProvider
                .getCredential(mail, password);
        firebaseUser.reauthenticate(credential);
    }
}



