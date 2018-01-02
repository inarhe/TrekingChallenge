package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ChallengeHistoryActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.route.RouteHistoryActivity;
import edu.uoc.iartal.trekkingchallenge.trip.TripHistoryActivity;

public class UserHistoryActivity extends AppCompatActivity {

    private User user;
    private DatabaseReference databaseHistory;
    private TextView tvTotalChall, tvTotalTrip, tvTotalRoute, tvTotalWin, tvTotalKm, tvTotalSlope, tvTotalHour;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.userHistoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.userHistoryActivity);

        // Initialize progressDialog
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get Firebase authentication instance and database references
        databaseHistory = controller.getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);

        // Link layout elements with variables
        tvTotalChall = (TextView) findViewById(R.id.tvUserTotChall);
        tvTotalTrip = (TextView) findViewById(R.id.tvUserTotTrip);
        tvTotalRoute = (TextView) findViewById(R.id.tvUserTotRoute);
        tvTotalWin = (TextView) findViewById(R.id.tvUserTotWin);
        tvTotalKm = (TextView) findViewById(R.id.tvUserTotKm);
        tvTotalSlope = (TextView) findViewById(R.id.tvUserTotSlope);
        tvTotalHour = (TextView) findViewById(R.id.tvUserTotHours);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("user");

        showHistoryValues();
    }

    /**
     * Start user challenge history when button is clicked
     * @param view
     */
    public void challengeHistory(View view){
        Intent intent = new Intent(getApplicationContext(), ChallengeHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    /**
     * Start user route history when button is clicked
     * @param view
     */
    public void routeHistory(View view){
        Intent intent = new Intent(getApplicationContext(), RouteHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    /**
     * Start user trip history when button is clicked
     * @param view
     */
    public void tripHistory(View view){
        Intent intent = new Intent(getApplicationContext(), TripHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    /**
     * Get user history values and show them
     */
    private void showHistoryValues(){
        // Show selected group information in the layout
        tvTotalChall.setText(Integer.toString(user.getChallengeResults().size()));
        tvTotalTrip.setText(Integer.toString(user.getTripsDone().size()));
        tvTotalRoute.setText(Integer.toString(user.getFinished().size()));

        controller.readDataOnce(databaseHistory, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot historySnapshot : data.getChildren()){
                    History history = historySnapshot.getValue(History.class);
                    if (history.getId().equals(user.getHistory())){
                        tvTotalSlope.setText(Integer.toString(history.getTotalSlope()) + " m");
                        tvTotalKm.setText(String.valueOf(history.getTotalDistance()) + " km");
                        tvTotalHour.setText(Integer.toString(history.getTotalHour()) + "h" + Integer.toString(history.getTotalMin()));
                        tvTotalWin.setText(Integer.toString(history.getChallengeWin()));
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),R.string.loadingDataFailed,Toast.LENGTH_SHORT).show();
                Log.e("UserHist error", databaseError.getMessage());
            }
        });
    }
}
