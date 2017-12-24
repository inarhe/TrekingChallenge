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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.SortingOrder;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.objects.Challenge;
import edu.uoc.iartal.trekkingchallenge.objects.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.ChallengeResultAdapter;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowChallengeActivity extends AppCompatActivity {

    private DatabaseReference databaseChallenge;
    private Challenge challenge;
    private ListView rankingList;
    private ViewGroup headerView;
    private ArrayList<String> challengesResultsIds;
    private ArrayList<ChallengeResult> challengeResults;
    private CommonFunctionality common;
    private String currentMail, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenge);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showChallengeToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteChallenge);
        TextView textViewDate = (TextView) findViewById(R.id.tvDateChallenge);
        TextView textViewDesc = (TextView) findViewById(R.id.tvDescChallenge);
        rankingList = (ListView) findViewById(R.id.lvRanking);
        headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, rankingList, false);
        rankingList.addHeaderView(headerView);

        // Initialize variables
        common = new CommonFunctionality();
        currentMail = firebaseAuth.getCurrentUser().getEmail();

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(challenge.getName());

        // Show selected group information in the layout
        textViewRoute.setText(challenge.getRoute());
        textViewDate.setText(challenge.getLimitDate());
        textViewDesc.setText(challenge.getDescription());

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

        challengesResultsIds = new ArrayList<>();

        // Get all challenge results
        for (String key : challenge.getResults().keySet()) {
            challengesResultsIds.add(key);
        }

        challengeResults = new ArrayList<>();

        DatabaseReference databaseChallResults = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        if (databaseChallResults != null) {
            currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            databaseChallResults.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    challengeResults.clear();
                    for (DataSnapshot challengesSnapshot :
                            dataSnapshot.getChildren()) {
                        ChallengeResult chall = challengesSnapshot.getValue(ChallengeResult.class);
                        if (challengesResultsIds.contains(chall.getId())) {
                            challengeResults.add(chall);
                        }
                    }

                    Collections.sort(challengeResults, new Comparator<ChallengeResult>() {
                        @Override
                        public int compare(ChallengeResult o1, ChallengeResult o2) {
                            if (challenge.getClassification().equals(getString(R.string.time))) {
                                return o1.getTime().intValue() - o2.getTime().intValue();
                            } else {
                                return o1.getDistance().intValue() - o2.getDistance().intValue();
                            }
                        }
                    });


                    ChallengeResultAdapter adapter = new ChallengeResultAdapter(getApplicationContext(), R.layout.adapter_challenge_results, challengeResults);
                    rankingList.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });

            // Fill the table with challenge results
            //  populateTableRanking();


        }
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_challenge, menu);
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

    /**
     * When the menu option "join challenge" is clicked, this method is executed. Check group members, add current user to selected challenge, add challenge to user
     * challenges and updates challenge members number
     */
    public void joinChallenge () {
        // Check if user is a challenge member
        if (checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.alreadyInGroup, Toast.LENGTH_LONG).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.joinChallengeAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setJoin), databaseChallenge, challenge.getId(), challenge.getNumberOfMembers(), FireBaseReferences.USER_CHALLENGES_REFERENCE);
                        Toast.makeText(getApplicationContext(), getString(R.string.challengeJoined), Toast.LENGTH_LONG).show();
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
     * When the menu option "leave challenge" is clicked, this method is executed. Check group members, delete current user from selected challenge, delete challenge from user challenges and
     * updates challenge members number
     */
    public void leaveChallenge () {
        // Check if user is a challenge member
        if (!checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.noMemberGroup, Toast.LENGTH_LONG).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.challengeLeft));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setLeave), databaseChallenge, challenge.getId(), challenge.getNumberOfMembers(), FireBaseReferences.USER_CHALLENGES_REFERENCE);
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
     * Starts register challenge results activity when option menu is selected
     */
    public void challengeFinished() {
        Intent intent = new Intent(this, FinishedChallengeActivity.class);
        intent.putExtra("challenge", challenge);
        startActivity(intent);
    }


    /**
     * Check if current user is a member of the challenge object
     * @return
     */
    private Boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(challenge.getMembers().keySet());

        return members.contains(currentUserName);
    }

    private static class ChallengeResultComparator implements Comparator<Double>{
        @Override
        public int compare(Double result1, Double result2) {

            return result1.compareTo(result2);
        }
    }
}
