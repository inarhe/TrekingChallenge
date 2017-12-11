package edu.uoc.iartal.trekkingchallenge.challenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.route.FinishedRouteActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowChallengeActivity extends AppCompatActivity {

    private DatabaseReference databaseChallenge, databaseUser;
    private Challenge challenge;
    private String[] rankingHeader = {"user","time", "distance"};
    private String [][] rankingChallenge;
    private ArrayList<String> resultsId;
    private String currentMail;
    private ArrayList<ChallengeResult> challengeResults;
    private ChallengeResult challengeRanking;
    private TableView<String[]> tb;

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

        tb = (TableView<String[]>) findViewById(R.id.rankingTable);
        tb.setColumnCount(3);
        tb.setHeaderBackgroundColor(Color.parseColor("#E0E0E0"));

        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);
        //String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        resultsId = new ArrayList<>();

        for (String key : challenge.getResults().keySet()) {
            resultsId.add(key);
        }

        populaterRanking();



       /* databaseChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultsId.removeAll(resultsId);
                for (DataSnapshot challengesSnapshot :
                        dataSnapshot.getChildren()) {
                    Challenge chall = challengesSnapshot.getValue(Challenge.class);
                    if (chall.getIdChallenge().equals(challenge.getIdChallenge())) {
                        for (String key : challenge.getResults().keySet()) {
                            resultsId.add(key);
                        }
                    }
                }
                populaterRanking();

                //adapter
                tb.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(),rankingHeader));
                if (rankingChallenge != null) {
                    tb.setDataAdapter(new SimpleTableDataAdapter(getApplicationContext(), rankingChallenge));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });*/

        //populate


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
        inflater.inflate(R.menu.show_challenge_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_joinChallenge:
                joinChallenge();
                return true;
            case R.id.action_leaveChallenge:
                leaveChallenge();
                return true;
            case R.id.action_result:
                challengeFinished();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void joinChallenge () {
        // Add new group member when join group button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.challengeJoined));
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

    public void leaveChallenge () {
        // Add new group member when join group button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.challengeLeft));
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
                    databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.MEMBERSCHALLENGE_REFERENCE).child(user.getIdUser()).setValue("true");

                    databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERCHALLENGES_REFERENCE).child(challenge.getIdChallenge()).setValue("true");
                } else {
                    databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.MEMBERSCHALLENGE_REFERENCE).child(user.getIdUser()).removeValue();
                    databaseUser.child(user.getIdUser()).child(FireBaseReferences.USERCHALLENGES_REFERENCE).child(challenge.getIdChallenge()).removeValue();

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

        Query query = databaseChallenge.orderByChild(FireBaseReferences.CHALLENGENAME_REFERENCE).equalTo(challenge.getChallengeName());


        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //  Group group = dataSnapshot.getValue(Group.class);
                //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                int members = dataSnapshot.getValue(Challenge.class).getNumberOfMembers();

                if (action.equals("join")) {
                    databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members + 1);
                } else {
                    databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members - 1);
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
        });
    }

    private void populaterRanking(){
        challengeRanking = new ChallengeResult();
        challengeResults = new ArrayList<>();

        DatabaseReference databaseResults = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        if (databaseResults != null) {
            currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            databaseResults.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    challengeResults.removeAll(challengeResults);
                    for (DataSnapshot challengesSnapshot :
                            dataSnapshot.getChildren()) {
                        ChallengeResult chall = challengesSnapshot.getValue(ChallengeResult.class);
                        if (resultsId.contains(chall.getId())) {
                            challengeResults.add(chall);
                        }

                    }
                    rankingChallenge = new String[challengeResults.size()][3];

                    for (int i = 0; i < challengeResults.size(); i++) {
                        ChallengeResult c = challengeResults.get(i);
                        rankingChallenge[i][0] = c.getUser();
                        String time = c.getHour() + "h" + c.getminute();
                        rankingChallenge[i][1] = time;
                        rankingChallenge[i][2] = c.getDistance();
                    }

                    //adapter
                    tb.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(),rankingHeader));
                    if (rankingChallenge != null) {
                        tb.setDataAdapter(new SimpleTableDataAdapter(getApplicationContext(), rankingChallenge));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });
        } else {
            rankingChallenge = new String[challengeResults.size()][3];

            for (int i = 0; i < challengeResults.size(); i++) {
                ChallengeResult c = challengeResults.get(i);
                rankingChallenge[i][0] = c.getUser();
                String time = c.getHour() + "h" + c.getminute();
                rankingChallenge[i][1] = time;
                rankingChallenge[i][2] = c.getDistance();
            }
        }



    }

    public void challengeFinished() {
        Intent intent = new Intent(this, FinishedChallengeActivity.class);
        intent.putExtra("challenge", challenge);
        startActivity(intent);
    }
}
