package edu.uoc.iartal.trekkingchallenge.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.uoc.iartal.trekkingchallenge.R;

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
