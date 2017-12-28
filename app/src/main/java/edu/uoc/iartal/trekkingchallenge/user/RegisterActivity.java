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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUserAlias, editTextUserName, editTextUserMail, editTextUserPass, editTextPassRepeat;
    private ProgressDialog progressDialog ;

    /**
     * Initialize variables and link view elements on activity create
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

        // Initialize progressDialog
        progressDialog =  new ProgressDialog(this);

        // Link layout elements with variables
        editTextUserAlias = (EditText) findViewById(R.id.etIdUser);
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
        String alias = editTextUserAlias.getText().toString().trim();
        String name = editTextUserName.getText().toString().trim();
        String mail = editTextUserMail.getText().toString().trim();
        String password = editTextUserPass.getText().toString().trim();
        String repeatPassword = editTextPassRepeat.getText().toString().trim();

        // Instantiate common functionality class
        CommonFunctionality common = new CommonFunctionality();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(alias)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(mail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_LONG).show();
            return;
        } else if (!common.validateEmail(mail)){
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

        if(!password.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        // Execute controller method to create user in database
        ((FirebaseController)getApplication()).createUserAndHistory(alias, name, mail, password, this, progressDialog);
    }

    /**
     * Cancel user registration if cancel button is clicked.
     * @param view
     */
    public void cancelRegister (View view) {
        finish();
    }
}
