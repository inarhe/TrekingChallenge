package edu.uoc.iartal.trekkingchallenge.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.map.MapActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ListChallengesActivity;
import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
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

    /**
     * Main map functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void mapActivity (View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MapActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main list routes functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void routesActivity(View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),ListRoutesActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main trip functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void tripActivity (View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),ListTripsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main challenge functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void challengeActivity (View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),ListChallengesActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main user area functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void userAreaActivity (View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserAreaActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main group functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void groupActivity (View view){
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }
}
