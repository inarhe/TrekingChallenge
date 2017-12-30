package edu.uoc.iartal.trekkingchallenge.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.adapter.TripHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class TripHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<TripDone> tripsDone;
    private ArrayList<String> done;
    private SimpleDateFormat formatter;
    private DatabaseReference databaseTripDone;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tripHistoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.tripHistoryActivity);

        // Initialize variables
        controller = new FirebaseController();
        done = new ArrayList<>();
        tripsDone = new ArrayList<>();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database reference
        databaseTripDone = controller.getDatabaseReference(FireBaseReferences.TRIPSDONE_REFERENCE);

        // Set listView and its header
        historyList = (ListView) findViewById(R.id.lvTripHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_trip_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from user history activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        // Get user trips finished ids
        done.addAll(user.getTripsDone().keySet());

        getHistoryValues();
    }

    /**
     * Get list of finished trips to show in the history
     */
    private void getHistoryValues(){
        // Execute controller method to get database current user trips done. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseTripDone, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                tripsDone.clear();
                for (DataSnapshot tripDoneSnapshot : data.getChildren()) {
                    TripDone tripDone = tripDoneSnapshot.getValue(TripDone.class);
                    if (done.contains(tripDone.getId())) {
                        tripsDone.add(tripDone);
                    }
                }
                formatter = new SimpleDateFormat("dd.MM.yyyy");

                Collections.sort(tripsDone, new Comparator<TripDone>() {
                    @Override
                    public int compare(TripDone o1, TripDone o2) {
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = formatter.parse(o1.getDate());
                            date2 =  formatter.parse(o2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);

                    }
                });
                TripHistoryAdapter adapter = new TripHistoryAdapter(getApplicationContext(), R.layout.adapter_trip_history, tripsDone);
                historyList.setAdapter(adapter);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("HistTrip error", databaseError.getMessage());
            }
        });
    }
}
