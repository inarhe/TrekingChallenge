package edu.uoc.iartal.trekkingchallenge.trip;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.message.ListMessagesActivity;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;


public class ShowTripActivity extends AppCompatActivity {
    private DatabaseReference databaseTrip, databaseUser, databaseTripDone;
    private Trip trip;
    private User currentUser;
    private FirebaseController controller;
    private boolean isFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trip);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showTripToolbar);
        setSupportActionBar(toolbar);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        databaseTripDone = controller.getDatabaseReference(FireBaseReferences.TRIPSDONE_REFERENCE);

        // Link layout elements with variables
        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteTrip);
        TextView textViewDate = (TextView) findViewById(R.id.tvDateTrip);
        TextView textViewPlace = (TextView) findViewById(R.id.tvPlaceTrip);
        TextView textViewDesc = (TextView) findViewById(R.id.tvDescTrip);
        TextView textViewMemb = (TextView) findViewById(R.id.tvMembTrip);

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

        getCurrentUser();
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
                sendMessage();
                return true;
            case R.id.action_joinTrip:
                joinTrip();
                return true;
            case R.id.action_leaveTrip:
                leaveTrip();
                return true;
            case R.id.action_result:
                tripFinished();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the menu message icon is clicked, this method is executed. Start ListMessage activity
     */
    private void sendMessage(){
        Intent intent = new Intent(getApplicationContext(), ListMessagesActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }

    /**
     * When the menu option "join trip" is clicked, this method is executed. Check group members, add current user to selected trip, add trip to user trips and
     * updates trip members number
     */
    public void joinTrip () {
        // Check if user is a trip member
        if (checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.alreadyInTrip, Toast.LENGTH_SHORT).show();
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
                        controller.updateJoins(getString(R.string.setJoin), databaseTrip, FireBaseReferences.USER_TRIPS_REFERENCE, trip.getId(), currentUser, trip.getNumberOfMembers());
                        Toast.makeText(getApplicationContext(), getString(R.string.tripJoined), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.noMemberTrip, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.tripLeftAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        controller.updateJoins(getString(R.string.setLeave), databaseTrip, FireBaseReferences.USER_TRIPS_REFERENCE, trip.getId(), currentUser, trip.getNumberOfMembers());
                        Toast.makeText(getApplicationContext(), getString(R.string.tripLeft), Toast.LENGTH_SHORT).show();
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
     * Check if user is a trip member and doesn't have finished yet
     */
    public void tripFinished() {
        if (!checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.noMemberTrip, Toast.LENGTH_SHORT).show();
            return;
        } else {
            checkAlreadyFinished();
        }
    }

    /**
     * Starts register trip result activity if user doesn't have finished yet and it's a member
     */
    private void startResultRegister(){
        Intent intent = new Intent(this, FinishedTripActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }

    /**
     * Check if current user is a member of the trip object
     * @return current user id
     */
    private Boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(trip.getMembers().keySet());

        return members.contains(currentUser.getId());
    }

    /**
     * Check if current user has already registered a result
     * @return current user id
     */
    private void checkAlreadyFinished(){
        isFinished = false;
        final ArrayList<String> dones = new ArrayList<>();
        dones.addAll(currentUser.getTripsDone().keySet());

        controller.readDataOnce(databaseTripDone, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot doneSnapshot : data.getChildren()){
                    TripDone tripDone = doneSnapshot.getValue(TripDone.class);
                    if ((dones.contains(tripDone.getId())) && (tripDone.getTrip().equals(trip.getName()))){
                        Toast.makeText(getApplicationContext(), R.string.alreadyFinish, Toast.LENGTH_SHORT).show();
                        isFinished = true;
                    }
                }

                if (!isFinished){
                    startResultRegister();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get current user object from database
     */
    private void getCurrentUser(){
        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ShowTrip getUser error", databaseError.getMessage());
            }
        });
    }
}
