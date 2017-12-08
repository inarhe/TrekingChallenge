package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ListTripsActivity;
import edu.uoc.iartal.trekkingchallenge.user.AccessActivity;
import edu.uoc.iartal.trekkingchallenge.user.UserAreaActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //Get firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void mapActivity (View view){
        // Main map functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MapActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void searchActivity (View view){
        // Main search functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),ListRoutesActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void tripActivity (View view){
        // Main trip functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),ListTripsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void challengeActivity (View view){
        // Main challenge functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void userAreaActivity (View view){
        // Main user area functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void groupActivity (View view){
        // Main group functionality. If user isn't logged, access is forbidden and opens user access activity
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }
}
