package edu.uoc.iartal.trekkingchallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class AccessActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

    }

    public void registerActivity (View view){
        //Will open register activity
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
    }

    public void loginActivity (View view){
        //Will open login activity
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }






}
