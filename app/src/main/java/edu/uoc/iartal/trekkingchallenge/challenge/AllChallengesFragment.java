package edu.uoc.iartal.trekkingchallenge.challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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


public class AllChallengesFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Challenge> challenges;
    private ChallengeAdapter challengeAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    public AllChallengesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));
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
        View rootView = inflater.inflate(R.layout.activity_list_all_challenges,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListAllChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    /**
     * Get all database public challenges
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        // Define floating button for adding new challenge
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddChallenge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddChallengeActivity.class));
            }
        });

        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);
        challenges = new ArrayList<>();
        challengeAdapter = new ChallengeAdapter(challenges);

        recyclerView.setAdapter(challengeAdapter);

        /// Get database challenges and notify adapter to show them in recycler view
        progressDialog.show();
        databaseChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challenges.clear();
                for (DataSnapshot challengeSnapshot:
                        dataSnapshot.getChildren()) {
                    Challenge challenge = challengeSnapshot.getValue(Challenge.class);
                    if (challenge.getPublic()){
                        challenges.add(challenge);
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
     * Pass new trip list to Adapter
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
     * Get search result trip list
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
}
