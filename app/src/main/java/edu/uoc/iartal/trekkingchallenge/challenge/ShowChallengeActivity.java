package edu.uoc.iartal.trekkingchallenge.challenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowChallengeActivity extends AppCompatActivity {

    private DatabaseReference databaseChallenge, databaseUser;
    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenge);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        Toolbar toolbar = (Toolbar) findViewById(R.id.showChallengeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(challenge.getChallengeName());



        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteChallenge);
        TextView textViewDate = (TextView) findViewById(R.id.tvDateChallenge);
        TextView textViewDesc = (TextView) findViewById(R.id.tvDescChallenge);
        //TextView textViewMemb = (TextView) findViewById(R.id.tvM);


        textViewRoute.setText(challenge.getRoute());
        textViewDate.setText(challenge.getLimitDate());
        textViewDesc.setText(challenge.getChallengeDescription());
       // textViewMemb.setText(Integer.toString(trip.getNumberOfMembers()) + " membres");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_trip_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void joinTrip () {
        // Add new group member when join group button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.tripJoined));
        builder.setCancelable(true);



        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        updateJoins(currentMail, "join");
        updateMembers("join");

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();

    }

    public void leaveTrip () {
        // Add new group member when join group button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.tripLeft));
        builder.setCancelable(true);


        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        updateJoins(currentMail, "leave");
                        updateMembers("leave");
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

        AlertDialog alert11 = builder.create();
        alert11.show();

    }

    private void updateJoins(String currentMail, final String action){
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);
        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);


        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if (action.equals("join")) {
                    databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.MEMBERSTRIP_REFERENCE).child(user.getIdUser()).setValue("true");

              //      databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERTRIPS_REFERENCE).child(trip.getIdTrip()).setValue("true");
                } else {
                //    databaseChallenge.child(trip.getIdTrip()).child(FireBaseReferences.MEMBERSTRIP_REFERENCE).child(user.getIdUser()).removeValue();
            //        databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERTRIPS_REFERENCE).child(trip.getIdTrip()).removeValue();

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TO-DO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    private void updateMembers(final String action){

     /*   Query query = databaseTrip.orderByChild(FireBaseReferences.TRIPNAME_REFERENCE).equalTo(trip.getTripName());


        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //  Group group = dataSnapshot.getValue(Group.class);
                //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                int members = dataSnapshot.getValue(Trip.class).getNumberOfMembers();

                if (action.equals("join")) {
                    databaseTrip.child(trip.getIdTrip()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members + 1);
                } else {
                    databaseTrip.child(trip.getIdTrip()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members - 1);
                }

                // textViewMembers.setText(members+1);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TO-DO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TO-DO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });*/
    }
}
