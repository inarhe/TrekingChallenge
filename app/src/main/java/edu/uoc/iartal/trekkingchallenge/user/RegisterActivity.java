package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.History;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUserId, editTextUserName, editTextUserMail, editTextUserPass, editTextPassRepeat;
    private String idUser, userName, userMail, userPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser, databaseHistory;


    /**
     * Initialize variables on activity create
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.registerTitle));

        // Get Firebase authentication instance and database user reference instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseHistory = FirebaseDatabase.getInstance().getReference(FireBaseReferences.HISTORY_REFERENCE);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // Link layout elements with variables
        editTextUserId = (EditText) findViewById(R.id.etIdUser);
        editTextUserName = (EditText) findViewById(R.id.etUserName);
        editTextUserMail = (EditText) findViewById(R.id.etUserMail);
        editTextUserPass = (EditText) findViewById(R.id.etUserPass);
        editTextPassRepeat = (EditText) findViewById(R.id.etPassRepeat);
    }


    /**
     * Register user when accept button is clicked
     * @param view
     */
    public void registerUser (View view){

        // Get input parameters
        idUser = editTextUserId.getText().toString().trim();
        userName = editTextUserName.getText().toString().trim();
        userMail = editTextUserMail.getText().toString().trim();
        userPassword = editTextUserPass.getText().toString().trim();
        String repeatPassword = editTextPassRepeat.getText().toString().trim();

        CommonFunctionality common = new CommonFunctionality();

        // If some of the input parameters are incorrect or empty, stops the function execution further
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
        } else if (!common.validateEmail(userMail)){
            Toast.makeText(this, getString(R.string.mailFormat), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validatePassword(userPassword)){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        if(!userPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

        // Show message on progress dialog
        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();


        // Execute firebase user registration function
        firebaseAuth.createUserWithEmailAndPassword(userMail,userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If user is successfully registered and logged in, create user object and start main activity
                        if(task.isSuccessful()){
                            String idHistory = databaseHistory.push().getKey();
                            History history = new History(idHistory, 0.0, 0.0, 0, 0, idUser);
                            databaseHistory.child(idHistory).setValue(history);
                            User user = new User(idUser, userName,userMail,userPassword, idHistory);
                            databaseUser.child(idUser).setValue(user);

                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, getString(R.string.successfulRegister),Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Cancel user registration if cancel button is clicked. Starts Main activity
     * @param view
     */
    public void cancelRegister (View view){
        finish();
    }
}
