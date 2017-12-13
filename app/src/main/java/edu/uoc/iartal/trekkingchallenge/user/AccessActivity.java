package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.uoc.iartal.trekkingchallenge.R;


public class AccessActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.accessToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.accessActivity));
    }

    /**
     * Open register activity when register button is clicked
     * @param view
     */
    public void registerActivity (View view){
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
    }

    /**
     * Open login activity when login button is clicked
     * @param view
     */
    public void loginActivity (View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
