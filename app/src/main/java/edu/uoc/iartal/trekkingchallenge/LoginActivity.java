package edu.uoc.iartal.trekkingchallenge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ingrid Artal on 05/11/2017.
 */

public class LoginActivity extends Activity {
    private EditText editTextMail;
    private EditText editTextPass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {
           // start a profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        editTextMail = (EditText) findViewById(R.id.loginMail);
        editTextPass = (EditText) findViewById(R.id.loginPassword);

        progressDialog = new ProgressDialog(this);
    }

        public void userLogin (View view){
            String email = editTextMail.getText().toString().trim();
            String password = editTextPass.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
                //Stopping the function execution further
                return;
            }

            if(TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
                //Stopping the function execution further
                return;
            }else if (password.length() < 6){
                Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
                return;
            }

            progressDialog.setMessage("Login user...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   progressDialog.dismiss();
                   if(task.isSuccessful()) {
                       //start the profile activity
                       finish();
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                   }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.failedRegister),Toast.LENGTH_SHORT).show();
                   }

                }
            });
    }
}



        /**botó login

         * if empty password
         *
         * progressBar.setVisibility(View.VISIBLE);
         *
         * //authenticate user
         * auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
         * public void onComplete...{
         * progressBar.setVisibility(View.GONE);
         * if (!Task.isSuccessful()){
         * if (password <6)
         * inputPassword.setError(getStrin(R.string.xxx);
         * else
         * Toast auth failed
         * else
         * Intent intent = new Intent(LoginActivity.this, MainActivity.class);
         * startActivity(intent);
         * finish();
         */


