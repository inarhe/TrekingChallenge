package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 04/11/2017.
 */

public class AccessActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        Toolbar toolbar = (Toolbar) findViewById(R.id.accessToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.accessActivity));
    }

    public void registerActivity (View view){
        // Open register activity when register button is clicked
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
    }

    public void loginActivity (View view){
        // Open login activity when login button is clicked
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
