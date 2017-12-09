package edu.uoc.iartal.trekkingchallenge;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;

public class ShowTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trip);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        Trip trip = bundle.getParcelable("trip");

        Toolbar toolbar = (Toolbar) findViewById(R.id.showTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(trip.getTripName());

        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteTrip);


    }
}
