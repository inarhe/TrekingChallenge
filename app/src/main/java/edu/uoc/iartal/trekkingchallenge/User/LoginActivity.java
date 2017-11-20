package edu.uoc.iartal.trekkingchallenge.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 05/11/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText editTextMail, editTextPass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
           // If user is logged starts main activity with main menu
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        editTextMail = (EditText) findViewById(R.id.loginMail);
        editTextPass = (EditText) findViewById(R.id.loginPassword);
        progressDialog = new ProgressDialog(this);
    }

    public void userLogin (View view){
        // Check input parameters and logging user when accept button is clicked
        String email = editTextMail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (password.length() < 6){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.logging));
        progressDialog.show();

        // Execute firebase sign in function
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    // If logging is successful start main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



