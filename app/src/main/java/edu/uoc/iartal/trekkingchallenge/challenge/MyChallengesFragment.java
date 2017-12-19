package edu.uoc.iartal.trekkingchallenge.challenge;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objects.Challenge;
import edu.uoc.iartal.trekkingchallenge.objects.ChallengeAdapter;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.User;

public class MyChallengesFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Challenge> challenges;
    private ChallengeAdapter challengeAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserName, currentMail;

    public MyChallengesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        challenges = new ArrayList<>();

        // Get current user
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUserName = user.getIdUser();
                        Log.i("currentoncreate", currentUserName);
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
     * Define recyclerview that will be show in this view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_my_challenges,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        challenges.clear();

        return rootView;
    }

    /**
     * Get database public and private challenges that current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        challengeAdapter = new ChallengeAdapter(challenges);

        recyclerView.setAdapter(challengeAdapter);

        // Get database challenges and notify adapter to show them in recycler view
        progressDialog.show();
        databaseChallenge.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Challenge challenge = dataSnapshot.getValue(Challenge.class);
                Log.i("adminaded", challenge.getUserAdmin());
                if (challenge.getMembers().containsKey(currentUserName)){
                    addChallenge(challenge);
                }
                challengeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Get user challenges
                Challenge challenge = dataSnapshot.getValue(Challenge.class);
                if (challenge.getMembers().containsKey(currentUserName)){
                    if (!challenges.contains(challenge)){
                        addChallenge(challenge);
                    } else {
                        int i = challenges.indexOf(challenge);
                        Challenge challengeArray = challenges.get(i);
                        if (!challengeArray.getName().equals(challenge.getName()) || !challengeArray.getDescription().equals(challenge.getDescription())
                                || !challengeArray.getLimitDate().equals(challenge.getLimitDate())){
                            challenges.set(i, challenge);
                        }
                    }
                } else {
                    if (challenges.contains(challenge)){
                        challenges.remove(challenge);
                    }
                }
                challengeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
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

    /**
     * Inflate menu with menu layout information and define search view
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_challenges, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        challengeAdapter.setFilter(challenges);
                        // Return true to collapse action view
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Return true to expand action view
                        return true;
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Pass new challenge list to Adapter
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Challenge> filteredModelList = filter(challenges, newText);
        challengeAdapter.setFilter(filteredModelList);
        return true;
    }

    /**
     * Get search result challenge list
     * @param models
     * @param query
     * @return
     */
    private List<Challenge> filter(List<Challenge> models, String query) {
        query = query.toLowerCase();
        final List<Challenge> filteredModelList = new ArrayList<>();
        for (Challenge model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * Add user challenge to the list
     * @param challenge
     */
    private void addChallenge(Challenge challenge) {
        challenges.add(challenge);
        if (challenge.getUserAdmin().equals(currentUserName)) {
            challengeAdapter.setVisibility(true);
        } else {
            challengeAdapter.setVisibility(false);
        }
    }
}
