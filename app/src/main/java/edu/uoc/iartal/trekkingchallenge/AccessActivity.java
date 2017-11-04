package edu.uoc.iartal.trekkingchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class AccessActivity extends Activity {

    EditText userIdText;
    EditText userNameText;
    EditText userMailText;
    EditText userPasswordText;


    DatabaseReference databaseUser;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        databaseUser = FirebaseDatabase.getInstance().getReference("user");

        userIdText = (EditText) findViewById(R.id.userId);
        userNameText = (EditText) findViewById(R.id.userName);
        userMailText = (EditText) findViewById(R.id.userMail);
        userPasswordText = (EditText) findViewById(R.id.userPassword);

    }

    public void saveUser (View view){
        addUser();
    }

    public void addUser(){
        String idUser = userIdText.getText().toString().trim();
        String userName = userNameText.getText().toString().trim();
        String userMail = userMailText.getText().toString().trim();
        String userPassword = userPasswordText.getText().toString().trim();

        if(!TextUtils.isEmpty(userName)){
           // String id = databaseUser.push().getKey();

            User user = new User(idUser,userName,userMail, userPassword);
            databaseUser.child(idUser).setValue(user);

            Toast.makeText(this, "User created", Toast.LENGTH_LONG).show();


        }else {
            Toast.makeText(this, "Nom no buit", Toast.LENGTH_LONG).show();
        }



    }
}
