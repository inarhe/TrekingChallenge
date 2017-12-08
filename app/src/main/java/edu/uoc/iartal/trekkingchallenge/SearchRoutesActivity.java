package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.objectsDB.RouteAdapter;

public class SearchRoutesActivity extends AppCompatActivity {

    private ArrayList<Route> routes = new ArrayList<>();
    private RouteAdapter routeAdapter;
    private RadioGroup rgDifficult, rgType, rgDistance;
    private ArrayList<Route> filteredModelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_routes);

     //   routeAdapter = new RouteAdapter(routes, SearchRoutesActivity.this);
       // recyclerView.setAdapter(routeAdapter);


        rgDifficult = (RadioGroup) findViewById(R.id.rgDifficult);
        rgDistance = (RadioGroup) findViewById(R.id.rgDistance);
        rgType = (RadioGroup) findViewById(R.id.rgType);

        Bundle bundle = getIntent().getExtras();
        routes = bundle.getParcelableArrayList("routes");


    }

    public void advancedSearch (View view){
        String difficult = "Qualsevol";
        String distance = "Qualsevol";
        String type = "Qualsevol";


        switch (rgDifficult.getCheckedRadioButtonId()){
            case R.id.rbEasy:
                difficult = "Fàcil";
                break;
            case R.id.rbModerate:
                difficult = "Moderat";
                break;
            case R.id.rbDifficult:
                difficult = "Difícil";
                break;
        }

        switch (rgDistance.getCheckedRadioButtonId()){
            case R.id.rbLessDist:
                distance = "Less";
                break;
            case R.id.rbMedDist:
                distance = "Middle";
                break;
            case R.id.rbHighDist:
                distance = "High";
                break;
        }

        switch (rgType.getCheckedRadioButtonId()){
            case R.id.rbCircular:
                type = "Circular";
                break;
            case R.id.rbLineal:
                type = "Anada/Tornada";
                break;
        }

        if (difficult.equals("Qualsevol") && distance.equals("Qualsevol") && type.equals("Qualsevol")) {
            filteredModelList.addAll(routes);
        } else {
            for (Route route : routes) {
                Boolean distExists = false;
                String queryDiff = route.getDifficult();
                String queryType = route.getType();
                Double queryDist = Double.parseDouble(route.getDistance());

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

                if (difficult.equals("Qualsevol") || queryDiff.contains(difficult)) {
                    if (type.equals("Qualsevol") || queryType.contains(type)) {
                        if (distance.equals("Qualsevol") || distExists) {
                            filteredModelList.add(route);
                        }
                    }
                }
            }





        /*    if (difficult.equals("Qualsevol")){
                filteredModelList.addAll(routes);
            } else {
                checkResults(routes, queryDiff, difficult);
            }

            if (!type.equals("Qualsevol")){
                checkResults(filteredModelList, queryType, type);
            }

            if (!distance.equals("Qualsevol")){
                checkResults(filteredModelList, queryType, type);
            } else {

                switch (distance) {
                    case "Less":
                        if (queryDist < 10) {
                            filteredModelList.add(route);
                        }
                        break;
                    case "Middle":
                        if (queryDist >= 10 && queryDist <= 15) {
                            filteredModelList.add(route);
                        }
                        break;
                    case "High":
                        if (queryDist > 15) {
                            filteredModelList.add(route);
                        }
                        break;
                }
            }*/










       /*     if (queryDiff.contains(difficult) && queryType.contains(type)) {
                switch (distance){
                    case "Less":
                        if (queryDist < 10){
                            filteredModelList.add(route);
                        }
                        break;
                    case "Middle":
                        if (queryDist >= 10 && queryDist <= 15){
                            filteredModelList.add(route);
                        }
                        break;
                    case "High":
                        if (queryDist > 15){
                            filteredModelList.add(route);
                        }
                        break;
                }

            } else {
               // filteredModelList.addAll(routes);
            }*/
        }
        Intent result = new Intent();
        result.putExtra("routes", filteredModelList);
        setResult(RESULT_OK, result);
        finish();

    }

    private void checkResults (ArrayList<Route> routes, String query, String value){

        for (Route route : routes) {
            if (query.contains(value)) {
                filteredModelList.add(route);
            }
          /*  if (filteredModelList.isEmpty()) {
                filteredModelList.addAll(routes);
            }*/
        }

    }



    public boolean onQueryTextChange(String newText) {
        final List<Route> filteredModelList = filter(routes, newText);

        routeAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<Route> filter(List<Route> models, String query) {
        query = query.toLowerCase();
        final List<Route> filteredModelList = new ArrayList<>();
        for (Route model : models) {
            final String text = model.getName().toLowerCase();
            final String text2 = model.getRegion().toLowerCase();
            if (text.contains(query) || text2.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
