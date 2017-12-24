package edu.uoc.iartal.trekkingchallenge.trip;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.message.ListMessagesActivity;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowTripActivity extends AppCompatActivity {
    private DatabaseReference databaseTrip;
    private Trip trip;
    private CommonFunctionality common;
    private String currentMail, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trip);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showTripToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteTrip);
        TextView textViewDate = (TextView) findViewById(R.id.tvDateTrip);
        TextView textViewPlace = (TextView) findViewById(R.id.tvPlaceTrip);
        TextView textViewDesc = (TextView) findViewById(R.id.tvDescTrip);
        TextView textViewMemb = (TextView) findViewById(R.id.tvMembTrip);

        // Initialize variables
        common = new CommonFunctionality();
        currentMail = firebaseAuth.getCurrentUser().getEmail();

        // Get data from item clicked on list trips activity
        Bundle bundle = getIntent().getExtras();
        trip = bundle.getParcelable("trip");

        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(trip.getName());

        // Show selected group information in the layout
        textViewRoute.setText(trip.getRoute());
        textViewDate.setText(trip.getDate());
        textViewPlace.setText(trip.getPlace());
        textViewDesc.setText(trip.getDescription());
        textViewMemb.setText(Integer.toString(trip.getNumberOfMembers()) + " " + getString(R.string.members));

        // Get current user name
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUserName = user.getIdUser();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_trip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                Intent intent = new Intent(getApplicationContext(), ListMessagesActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
                return true;
            case R.id.action_joinTrip:
                joinTrip();
                return true;
            case R.id.action_leaveTrip:
                leaveTrip();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the menu option "join trip" is clicked, this method is executed. Check group members, add current user to selected trip, add trip to user trips and
     * updates trip members number
     */
    public void joinTrip () {
        // Check if user is a trip member
        if (checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.alreadyInGroup, Toast.LENGTH_LONG).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.joinTripAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setJoin), databaseTrip, trip.getId(), trip.getNumberOfMembers(), FireBaseReferences.USER_TRIPS_REFERENCE);
                        Toast.makeText(getApplicationContext(), getString(R.string.tripJoined), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * When the menu option "leave trip" is clicked, this method is executed. Check group members, delete current user from selected trip, delete trip from user trips and
     * updates trip members number
     */
    public void leaveTrip () {
        // Check if user is a trip member
        if (!checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.noMemberGroup, Toast.LENGTH_LONG).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.tripLeft));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setLeave), databaseTrip, trip.getId(), trip.getNumberOfMembers(), FireBaseReferences.USER_TRIPS_REFERENCE);
                        finish();
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Check if current user is a member of the trip object
     * @return
     */
    private Boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(trip.getMembers().keySet());

        return members.contains(currentUserName);
    }
}
