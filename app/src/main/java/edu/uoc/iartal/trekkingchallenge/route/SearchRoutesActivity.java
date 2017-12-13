package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class SearchRoutesActivity extends AppCompatActivity {

    private ArrayList<Route> routes = new ArrayList<>();
    private RadioGroup rgDifficult, rgType, rgDistance;
    private ArrayList<Route> filteredModelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_routes);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.advancedSearch));

        // Link layout elements with variables
        rgDifficult = (RadioGroup) findViewById(R.id.rgDifficult);
        rgDistance = (RadioGroup) findViewById(R.id.rgDistance);
        rgType = (RadioGroup) findViewById(R.id.rgType);

        // Get data from list routes activity
        Bundle bundle = getIntent().getExtras();
        routes = bundle.getParcelableArrayList("routes");
    }


    public void advancedSearch (View view){
        // Initialize filter search with default value
        String difficult = getString(R.string.all);
        String distance = getString(R.string.all);
        String type = getString(R.string.all);

        // Get which difficult radio button is checked
        switch (rgDifficult.getCheckedRadioButtonId()){
            case R.id.rbEasy:
                difficult = getString(R.string.easy);
                break;
            case R.id.rbModerate:
                difficult = getString(R.string.moderate);
                break;
            case R.id.rbDifficult:
                difficult = getString(R.string.difficult);
                break;
        }

        // Get which distance radio button is checked
        switch (rgDistance.getCheckedRadioButtonId()){
            case R.id.rbLessDist:
                distance = getString(R.string.lessCheck);
                break;
            case R.id.rbMedDist:
                distance = getString(R.string.middleCheck);
                break;
            case R.id.rbHighDist:
                distance = getString(R.string.highCheck);
                break;
        }

        // Get which type radio button is checked
        switch (rgType.getCheckedRadioButtonId()){
            case R.id.rbCircular:
                type = getString(R.string.circular);
                break;
            case R.id.rbLineal:
                type = getString(R.string.goBack);
                break;
        }

        // Find routes in database according to radio button filters
        if (difficult.equals(getString(R.string.all)) && distance.equals(getString(R.string.all)) && type.equals(getString(R.string.all))) {
            filteredModelList.addAll(routes);
        } else {
            for (Route route : routes) {
                // Get values for each database route
                Boolean distExists = false;
                String queryDiff = route.getDifficult();
                String queryType = route.getType();
                Double queryDist = Double.parseDouble(route.getDistance());

                // Compare route distance according to radio button filter
                switch (distance) {
                    case "Less":
                        if (queryDist < 10) {
                            distExists = true;
                        }
                        break;
                    case "Middle":
                        if (queryDist >= 10 && queryDist <= 15) {
                            distExists = true;
                        }
                        break;
                    case "High":
                        if (queryDist > 15) {
                            distExists = true;
                        }
                        break;
                }

                // Verify that current route has all expected values
                if (difficult.equals(getString(R.string.all)) || queryDiff.contains(difficult)) {
                    if (type.equals(getString(R.string.all)) || queryType.contains(type)) {
                        if (distance.equals(getString(R.string.all)) || distExists) {
                            filteredModelList.add(route);
                        }
                    }
                }
            }
        }

        // Return search result list to list activity
        Intent result = new Intent();
        result.putExtra("routes", filteredModelList);
        setResult(RESULT_OK, result);
        finish();
    }
}
