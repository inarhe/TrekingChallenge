package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.R;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextMail, editTextPass;
    private ProgressDialog progressDialog;
    private FirebaseController controller;
    private CommonFunctionality common;
    private String password;

    /**
     * Initialize variables and link view elements on activity create
     * @param savedInstanceState
     */
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

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        controller = new FirebaseController();
        common = new CommonFunctionality();

        // If user is logged starts main activity with main menu
        if (controller.getActiveUserSession() != null) {
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
        try{
            password = common.encryptPassword(editTextPass.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Instantiate common functionality class
        CommonFunctionality common = new CommonFunctionality();

        // Check input parameters. If some parameter is incorrect, stops the execution
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validateEmail(email)){
            Toast.makeText(this, getString(R.string.mailFormat), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(editTextPass.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validatePassword(password)){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_LONG).show();
            return;
        }

        // Show message on progress dialog
        progressDialog.setMessage(getString(R.string.logging));
        progressDialog.show();

        // Execute controller method to create user in database
        controller.loginDatabase(email, password, progressDialog, this);
    }
}