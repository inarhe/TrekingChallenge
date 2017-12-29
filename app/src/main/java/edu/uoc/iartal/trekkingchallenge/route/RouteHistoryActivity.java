package edu.uoc.iartal.trekkingchallenge.route;

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
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.adapter.RouteHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class RouteHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<Finished> finishedRoutes;
    private ArrayList<String> routes;
    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_history);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.routeHistoryToolbar);
        setSupportActionBar(toolbar);
        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.routeHistoryActivity);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        DatabaseReference databaseFinished = FirebaseDatabase.getInstance().getReference(FireBaseReferences.FINISHED_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        historyList = (ListView) findViewById(R.id.lvRouteHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_route_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        routes = new ArrayList<>();
        routes.addAll(user.getFinished().keySet());


        finishedRoutes = new ArrayList<>();

        //DatabaseReference databaseFinished = FirebaseDatabase.getInstance().getReference(FireBaseReferences.FINISHED_REFERENCE);


            databaseFinished.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    finishedRoutes.clear();
                    for (DataSnapshot finishedSnapshot :
                            dataSnapshot.getChildren()) {
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
                                date2 =  formatter.parse(o2.getDate());
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
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });



    }
}
