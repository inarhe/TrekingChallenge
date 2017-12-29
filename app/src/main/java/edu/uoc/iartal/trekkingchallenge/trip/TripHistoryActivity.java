package edu.uoc.iartal.trekkingchallenge.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.adapter.TripHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class TripHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<TripDone> tripsDone;
    private ArrayList<String> done;
    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tripHistoryToolbar);
        setSupportActionBar(toolbar);
        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.tripHistoryActivity);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        DatabaseReference databaseTripDone = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIPSDONE_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        historyList = (ListView) findViewById(R.id.lvTripHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_trip_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        done = new ArrayList<>();
        done.addAll(user.getTripsDone().keySet());


        tripsDone = new ArrayList<>();

        DatabaseReference databaseTripsDone = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIPSDONE_REFERENCE);


        databaseTripsDone.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tripsDone.clear();
                    for (DataSnapshot tripDoneSnapshot :
                            dataSnapshot.getChildren()) {
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
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });



    }
}
