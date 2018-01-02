package edu.uoc.iartal.trekkingchallenge.challenge;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.adapter.ChallengeHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ChallengeHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<ChallengeResult> challengeResults;
    private ArrayList<String> results;
    private SimpleDateFormat formatter;
    private DatabaseReference databaseChallengeResult;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_history);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.challengeHistoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.challengeHistoryActivity);

        // Initialize variables
        controller = new FirebaseController();
        results = new ArrayList<>();
        challengeResults = new ArrayList<>();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database reference
        databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        // Set listView and its header
        historyList = (ListView) findViewById(R.id.lvChallengeHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_challenge_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from user history activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        // Get user challenges results ids
        results.addAll(user.getChallengeResults().keySet());

        getHistoryValues();
    }

    /**
     * Get list of finished challenges to show in the history
     */
    private void getHistoryValues(){
        // Execute controller method to get database current user challenge results. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseChallengeResult, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                challengeResults.clear();
                for (DataSnapshot challengesSnapshot : data.getChildren()) {
                    ChallengeResult chall = challengesSnapshot.getValue(ChallengeResult.class);
                    if (results.contains(chall.getId())) {
                        challengeResults.add(chall);
                    }
                }
                formatter = new SimpleDateFormat("dd.MM.yyyy");

                Collections.sort(challengeResults, new Comparator<ChallengeResult>() {
                    @Override
                    public int compare(ChallengeResult o1, ChallengeResult o2) {
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = formatter.parse(o1.getDate());
                            date2 =  formatter.parse(o2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);

                    }
                });
                ChallengeHistoryAdapter adapter = new ChallengeHistoryAdapter(getBaseContext(), R.layout.adapter_challenge_history, challengeResults);
                historyList.setAdapter(adapter);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("HistChall error", databaseError.getMessage());
            }
        });
    }
}
