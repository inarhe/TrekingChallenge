package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ChallengeHistoryActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.route.RouteHistoryActivity;
import edu.uoc.iartal.trekkingchallenge.trip.TripHistoryActivity;

public class UserHistoryActivity extends AppCompatActivity {

    private User user;
    private int challengeWin, slope;
    private Double km, hour;
    private TextView tvTotalWin, tvTotalKm, tvTotalSlope, tvTotalHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.userHistoryToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseHistory = FirebaseDatabase.getInstance().getReference(FireBaseReferences.HISTORY_REFERENCE);
        DatabaseReference databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        DatabaseReference databaseFinished = FirebaseDatabase.getInstance().getReference(FireBaseReferences.FINISHED_REFERENCE);
        final DatabaseReference databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        TextView tvTotalChall = (TextView) findViewById(R.id.tvUserTotChall);
        TextView tvTotalTrip = (TextView) findViewById(R.id.tvUserTotTrip);
        TextView tvTotalRoute = (TextView) findViewById(R.id.tvUserTotRoute);
        tvTotalWin = (TextView) findViewById(R.id.tvUserTotWin);
        tvTotalKm = (TextView) findViewById(R.id.tvUserTotKm);
        tvTotalSlope = (TextView) findViewById(R.id.tvUserTotSlope);
        tvTotalHour = (TextView) findViewById(R.id.tvUserTotHours);

        // Initialize variables
       //common = new CommonFunctionality();
        //currentMail = firebaseAuth.getCurrentUser().getEmail();

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("user");

        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.userHistoryActivity);

        // Show selected group information in the layout

        tvTotalChall.setText(Integer.toString(user.getChallengeResults().size()));
        tvTotalTrip.setText(Integer.toString(user.getTripsDone().size()));
        tvTotalRoute.setText(Integer.toString(user.getFinished().size()));

        Query query = databaseHistory.orderByChild(FireBaseReferences.HISTORY_ID_REFERENCE).equalTo(user.getHistory());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot historySnapshot : dataSnapshot.getChildren()){
                    History history = historySnapshot.getValue(History.class);
                    tvTotalSlope.setText(Integer.toString(history.getTotalSlope()) + " m");
                    tvTotalKm.setText(String.valueOf(history.getTotalDistance()) + " km");
                    tvTotalHour.setText(String.valueOf(history.getTotalTime()) + " h");
                    tvTotalWin.setText(Integer.toString(history.getChallengeWin()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void challengeHistory(View view){
        Intent intent = new Intent(getApplicationContext(), ChallengeHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void routeHistory(View view){
        Intent intent = new Intent(getApplicationContext(), RouteHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void tripHistory(View view){
        Intent intent = new Intent(getApplicationContext(), TripHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
