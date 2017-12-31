package edu.uoc.iartal.trekkingchallenge.challenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.adapter.ChallengeResultAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowChallengeActivity extends AppCompatActivity {

    private DatabaseReference databaseChallenge, databaseUser,databaseResults;
    private Challenge challenge;
    private ListView rankingList;
    private User currentUser;
    private ArrayList<String> challengesResultsIds;
    private ArrayList<ChallengeResult> challengeResults;
    private FirebaseController controller;
    private boolean isFinished;
    private String currentMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenge);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showChallengeToolbar);
        setSupportActionBar(toolbar);

        // Initialize variables
        controller = new FirebaseController();
        challengesResultsIds = new ArrayList<>();
        challengeResults = new ArrayList<>();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseChallenge = controller.getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        databaseResults = controller.getDatabaseReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        // Link layout elements with variables
        TextView textViewRoute = (TextView) findViewById(R.id.tvRouteChallenge);
        TextView textViewDate = (TextView) findViewById(R.id.tvDateChallenge);
        TextView textViewDesc = (TextView) findViewById(R.id.tvDescChallenge);
        rankingList = (ListView) findViewById(R.id.lvRanking);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_ranking_challenge, rankingList, false);
        rankingList.addHeaderView(headerView);

        // Get data from item clicked on list challenges activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(challenge.getName());

        // Show selected challenge information in the layout
        textViewRoute.setText(challenge.getRoute());
        textViewDate.setText(challenge.getLimitDate());
        textViewDesc.setText(challenge.getDescription());

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
            Toast.makeText(getApplicationContext(), R.string.alreadyInChallenge, Toast.LENGTH_SHORT).show();
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
                        controller.updateJoins(getString(R.string.setJoin), databaseChallenge, FireBaseReferences.USER_CHALLENGES_REFERENCE, challenge.getId(), currentUser, challenge.getNumberOfMembers());
                        Toast.makeText(getApplicationContext(), getString(R.string.challengeJoined), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.noMemberChallenge, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.challengeLeftAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        controller.updateJoins(getString(R.string.setLeave), databaseChallenge, FireBaseReferences.USER_CHALLENGES_REFERENCE, challenge.getId(), currentUser, challenge.getNumberOfMembers());
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
     * Check if user is a challenge member and doesn't have finished yet
     */
    public void challengeFinished() {
        if (!checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.noMemberChallenge, Toast.LENGTH_SHORT).show();
            return;
        } else {
            checkAlreadyFinished();
        }
    }

    /**
     * Starts register challenge result activity if user doesn't have finished yet and it's a member
     */
    private void startResultRegister(){
        Intent intent = new Intent(this, FinishedChallengeActivity.class);
        intent.putExtra("challenge", challenge);
        startActivity(intent);
    }

    /**
     * Check if current user is a member of the challenge object
     * @return current user id
     */
    private boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(challenge.getMembers().keySet());

        return members.contains(currentUser.getId());
    }

    private static class ChallengeResultComparator implements Comparator<Double>{
        @Override
        public int compare(Double result1, Double result2) {

            return result1.compareTo(result2);
        }
    }

    /**
     * Check if current user has already registered a result
     * @return current user id
     */
    private void checkAlreadyFinished(){
        isFinished = false;
        final ArrayList<String> results = new ArrayList<>();
        results.addAll(currentUser.getChallengeResults().keySet());

        controller.readDataOnce(databaseResults, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot resultSnapshot : data.getChildren()){
                    ChallengeResult challengeResult = resultSnapshot.getValue(ChallengeResult.class);
                    if ((results.contains(challengeResult.getId())) && (challengeResult.getChallenge().equals(challenge.getName()))){
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
                Log.e("ShowChall check error", databaseError.getMessage());
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
                currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUser = user;
                        getChallengeResults();
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ShowChall getUser error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get list of challenge results to show in ranking list
     */
    private void getChallengeResults(){

        // Get all challenge results
        for (String key : challenge.getResults().keySet()) {
            challengesResultsIds.add(key);
        }

        if (databaseResults != null) {
            controller.readData(databaseResults, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //Nothing to do
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    challengeResults.clear();
                    for (DataSnapshot challengesSnapshot : data.getChildren()) {
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
                public void onFailed(DatabaseError databaseError) {
                    Log.e("ShowChall getRes error", databaseError.getMessage());
                }
            });
        }
    }
}
