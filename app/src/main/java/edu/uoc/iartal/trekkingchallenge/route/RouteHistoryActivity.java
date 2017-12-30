package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.adapter.RouteHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class RouteHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<Finished> finishedRoutes;
    private ArrayList<String> routes;
    private SimpleDateFormat formatter;
    private DatabaseReference databaseFinished;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_history);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.routeHistoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.routeHistoryActivity);

        // Initialize variables
        controller = new FirebaseController();
        routes = new ArrayList<>();
        finishedRoutes = new ArrayList<>();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database reference
        databaseFinished = controller.getDatabaseReference(FireBaseReferences.FINISHED_REFERENCE);

        // Set listView and its header
        historyList = (ListView) findViewById(R.id.lvRouteHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_route_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from user history activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        // Get user routes finished ids
        routes.addAll(user.getFinished().keySet());

        getHistoryValues();
    }

    /**
     * Get list of finished routes to show in the history
     */
    private void getHistoryValues(){
        // Execute controller method to get database current user routes done. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseFinished, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                finishedRoutes.clear();
                for (DataSnapshot finishedSnapshot :
                        data.getChildren()) {
                    Finished finished = finishedSnapshot.getValue(Finished.class);
                    if (routes.contains(finished.getId())) {
                        finishedRoutes.add(finished);
                    }
                }
                formatter = new SimpleDateFormat("dd.MM.yyyy");

                Collections.sort(finishedRoutes, new Comparator<Finished>() {
                    @Override
                    public int compare(Finished o1, Finished o2) {
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = formatter.parse(o1.getDate());
                            date2 = formatter.parse(o2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);
                    }
                });
                RouteHistoryAdapter adapter = new RouteHistoryAdapter(getApplicationContext(), R.layout.adapter_route_history, finishedRoutes);
                historyList.setAdapter(adapter);

            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("HistRoute error", databaseError.getMessage());
            }
        });
    }
}
