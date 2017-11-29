package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 05/11/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private EditText editTextMail, editTextPass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Pattern pattern;
    private Matcher matcher;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout loginMailWrapper = (TextInputLayout) findViewById(R.id.loginMailWrapper);
        TextInputLayout loginPasswordWrapper = (TextInputLayout) findViewById(R.id.loginPasswordWrapper);
        loginMailWrapper.setHint(getString(R.string.hintMailUser));
        loginPasswordWrapper.setHint(getString(R.string.hintPassUser));

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.loginActivity));

        pattern = Pattern.compile(EMAIL_PATTERN);


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
        hideKeyboard();

        // Check input parameters and logging user when accept button is clicked
        String email = editTextMail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        } else if (!validateEmail(email)){
            Toast.makeText(this, getString(R.string.mailFormat), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (!validatePassword(password)){
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

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }
}



