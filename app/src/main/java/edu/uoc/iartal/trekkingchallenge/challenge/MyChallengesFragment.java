package edu.uoc.iartal.trekkingchallenge.challenge;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.ChallengeAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.TripAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;

/**
 * Created by Ingrid Artal on 26/11/2017.
 */

public class MyChallengesFragment extends Fragment implements SearchView.OnQueryTextListener{

    private List<Challenge> challenges;
    private List<String> challengesNames;
    private ChallengeAdapter challengeAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUser, currentMail;
    private ImageButton imageButton;

    public MyChallengesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        challengesNames = new ArrayList<>();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challengesNames.removeAll(challengesNames);
                for (DataSnapshot challengesSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = challengesSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUser = user.getIdUser();
                        for (String key : user.getChallenges().keySet()) {
                            challengesNames.add(key);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_my_challenges,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyChallenges);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

   /*     DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        challengesNames = new ArrayList<>();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challengesNames.removeAll(challengesNames);
                for (DataSnapshot challengesSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = challengesSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUser = user.getIdUser();
                        for (String key : user.getChallenges().keySet()) {
                            challengesNames.add(key);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });*/


       // challengeAdapter = new ChallengeAdapter(challenges);

    //    recyclerView.setAdapter(challengeAdapter);

        // Show database groups in recycler view
      //  progressDialog.show();




        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);
        challenges = new ArrayList<>();
        challengeAdapter = new ChallengeAdapter(challenges);

        recyclerView.setAdapter(challengeAdapter);

        // Show database groups in recycler view
        progressDialog.show();
        databaseChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challenges.removeAll(challenges);
                for (DataSnapshot tripSnapshot :
                        dataSnapshot.getChildren()) {
                    Challenge challenge = tripSnapshot.getValue(Challenge.class);
                    if (challengesNames.contains(challenge.getIdChallenge())) {
                        challenges.add(challenge);

                        if (challenge.getUserAdmin().equals(currentUser)) {
                            challengeAdapter.setVisibility(true);
                        } else {
                            challengeAdapter.setVisibility(false);
                        }
                    }

                }
                challengeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_list_challenges, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        challengeAdapter.setFilter(challenges);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Challenge> filteredModelList = filter(challenges, newText);

        challengeAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<Challenge> filter(List<Challenge> models, String query) {
        query = query.toLowerCase();
        final List<Challenge> filteredModelList = new ArrayList<>();
        for (Challenge model : models) {
            final String text = model.getIdChallenge().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void removeChallenge (View view) {

    }
}
