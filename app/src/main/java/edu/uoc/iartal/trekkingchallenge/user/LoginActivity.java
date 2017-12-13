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

import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextMail, editTextPass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.loginActivity));

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // If user is logged starts main activity with main menu
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Link layout elements with variables
        editTextMail = (EditText) findViewById(R.id.loginMail);
        editTextPass = (EditText) findViewById(R.id.loginPassword);
    }


    /**
     * Login user when accept button is clicked
     * @param view
     */
    public void userLogin (View view){

        // Get input parameters
        String email = editTextMail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        CommonFunctionality common = new CommonFunctionality();

        // If some of the input parameters are incorrect, stops the function execution further
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validateEmail(email)){
            Toast.makeText(this, getString(R.string.mailFormat), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validatePassword(password)){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        // Show message on progress dialog
        progressDialog.setMessage(getString(R.string.logging));
        progressDialog.show();

        // Execute firebase sign in function
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                // If logging is successful start main activity
                if(task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.failedLogin),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



