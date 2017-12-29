package edu.uoc.iartal.trekkingchallenge.challenge;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.adapter.ChallengeHistoryAdapter;
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ChallengeHistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private ArrayList<ChallengeResult> challengeResults;
    private ArrayList<String> results;
    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_history);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.challengeHistoryToolbar);
        setSupportActionBar(toolbar);
        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.challengeHistoryActivity);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        DatabaseReference databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        historyList = (ListView) findViewById(R.id.lvChallengeHistory);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_challenge_history, historyList, false);
        historyList.addHeaderView(headerView);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

        results = new ArrayList<>();
        results.addAll(user.getChallengeResults().keySet());


        challengeResults = new ArrayList<>();

        DatabaseReference databaseChallResults = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);


            databaseChallResults.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    challengeResults.clear();
                    for (DataSnapshot challengesSnapshot :
                            dataSnapshot.getChildren()) {
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


                    ChallengeHistoryAdapter adapter = new ChallengeHistoryAdapter(getApplicationContext(), R.layout.adapter_challenge_history, challengeResults);
                    historyList.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });



    }
}
