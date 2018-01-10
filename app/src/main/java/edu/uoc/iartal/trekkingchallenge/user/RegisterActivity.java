/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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
    private FirebaseController controller;
    private CommonFunctionality common;
    private String password, repeatPassword;

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
        controller = new FirebaseController();
        common = new CommonFunctionality();

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

        try{
            password = common.encryptPassword(editTextUserPass.getText().toString().trim());
            repeatPassword = common.encryptPassword(editTextPassRepeat.getText().toString().trim());
        } catch (Exception e){
            e.printStackTrace();
        }

        // Instantiate common functionality class
        CommonFunctionality common = new CommonFunctionality();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(alias)) {
            Toast.makeText(this, getString(R.string.idField), Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(mail)) {
            Toast.makeText(this, getString(R.string.mailField), Toast.LENGTH_SHORT).show();
            return;
        } else if (!common.validateEmail(mail)){
            Toast.makeText(this, getString(R.string.mailFormat), Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(editTextUserPass.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.passwordField), Toast.LENGTH_SHORT).show();
            return;
        } else if (!common.validatePassword(editTextUserPass.getText().toString().trim())){
            Toast.makeText(this, getString(R.string.passwordTooShort), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.samePass), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        // Execute controller method to create user in database
        controller.addNewUser(alias, name, mail, password, this, progressDialog);
    }

    /**
     * Cancel user registration if cancel button is clicked.
     * @param view
     */
    public void cancelRegister (View view) {
        finish();
    }
}


