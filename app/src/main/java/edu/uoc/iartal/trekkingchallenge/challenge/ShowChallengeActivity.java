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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowChallengeActivity extends AppCompatActivity {

    private DatabaseReference databaseChallenge;
    private Challenge challenge;
    private String[] tableHeader = {"user","time", "distance"};
    private String [][] rankingTable;
    private ArrayList<String> challengesResultsIds;
    private ArrayList<ChallengeResult> challengeResults;
    private TableView<String[]> tableView;
    private CommonFunctionality common;
    private String currentMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenge);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showChallengeToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database trip reference
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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

        // Link table layout and set table
        tableView = (TableView<String[]>) findViewById(R.id.rankingTable);
        tableView.setColumnCount(3);
        tableView.setHeaderBackgroundColor(Color.parseColor("#E0E0E0"));

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

        challengesResultsIds = new ArrayList<>();

        // Get all challenge results
        for (String key : challenge.getResults().keySet()) {
            challengesResultsIds.add(key);
        }

        // Fill the table with challenge results
        populateTableRanking();
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_challenge_menu, menu);
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
     * When the menu option "join challenge" is clicked, this method is executed. Add current user to selected challenge, add challenge to user
     * challenges and updates challenge members number
     */
    public void joinChallenge () {
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.joinChallengeAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setJoin), databaseChallenge, challenge.getId(), FireBaseReferences.USER_CHALLENGES_REFERENCE);
                        databaseChallenge.child(challenge.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(challenge.getNumberOfMembers()+1);
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
     * When the menu option "leave challenge" is clicked, this method is executed. Delete current user from selected challenge, delete challenge from user challenges and
     * updates challenge members number
     */
    public void leaveChallenge () {
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.challengeLeft));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setLeave), databaseChallenge, challenge.getId(), FireBaseReferences.USER_CHALLENGES_REFERENCE);
                        databaseChallenge.child(challenge.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(challenge.getNumberOfMembers()-1);
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
     * Search challenge results of current challenge to fill the ranking table
     */
    private void populateTableRanking(){
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

                    fillTable();

                    //Set table header adapter and data adapter
                    tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), tableHeader));
                    if (rankingTable != null) {
                        tableView.setDataAdapter(new SimpleTableDataAdapter(getApplicationContext(), rankingTable));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });
        }



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
     * Fill table with challenge results
     */
    private void fillTable(){
        rankingTable = new String[challengeResults.size()][3];

        for (int i = 0; i < challengeResults.size(); i++) {
            ChallengeResult result = challengeResults.get(i);
            rankingTable[i][0] = result.getUser();
            String time = result.getHour() + "h" + result.getminute();
            rankingTable[i][1] = time;
            rankingTable[i][2] = result.getDistance();
        }
    }
}
