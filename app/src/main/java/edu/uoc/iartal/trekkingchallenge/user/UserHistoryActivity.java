package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.objects.Finished;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.objects.User;

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
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
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

        tvTotalChall.setText(Integer.toString(user.getChallenges().size()));
        tvTotalTrip.setText(Integer.toString(user.getTrips().size()));
        tvTotalRoute.setText(Integer.toString(user.getFinished().size()));

        ArrayList<String> challengeResults = new ArrayList<>();
        challengeResults.addAll(user.getChallengesResults().keySet());

        databaseChallengeResult.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challengeWin = 0;
                for (DataSnapshot result : dataSnapshot.getChildren()){
                    ChallengeResult challengeResult = result.getValue(ChallengeResult.class);
                    if (challengeResult.getUser().equals(user.getIdUser())){
                        if (challengeResult.getPosition() == 1){
                            challengeWin ++;
                        }
                    }
                }
                tvTotalWin.setText(Integer.toString(challengeWin));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<String> finishedRoutes = new ArrayList<>();
        finishedRoutes.addAll(user.getFinished().keySet());

        databaseFinished.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                km = 0.0;
                hour = 0.0;
                slope = 0;
                for (DataSnapshot finish : dataSnapshot.getChildren()){
                    final Finished finished = finish.getValue(Finished.class);
                    if (finished.getUser().equals(user.getIdUser())){
                        km = km + finished.getDistance();
                        hour = hour + finished.getTime();
                    }
                    databaseRoute.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot routes : dataSnapshot.getChildren()) {
                                Route route = routes.getValue(Route.class);
                                if (route.getIdRoute().equals(finished.getRoute())) {
                                    slope = slope + route.getAscent() + route.getDecline();
                                }
                            }
                            tvTotalSlope.setText(Integer.toString(slope) + " m");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                tvTotalKm.setText(String.valueOf(km) + " km");
                tvTotalHour.setText(String.valueOf(hour) + " h");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
