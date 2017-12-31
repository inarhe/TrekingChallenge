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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetChildListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;
import edu.uoc.iartal.trekkingchallenge.adapter.ChallengeAdapter;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.User;

public class MyChallengesFragment extends Fragment implements SearchView.OnQueryTextListener{
    private ArrayList<Challenge> challenges;
    private ChallengeAdapter challengeAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserId;
    private DatabaseReference databaseUser, databaseChallenge;
    private FirebaseController controller;

    public MyChallengesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(getActivity());
        challenges = new ArrayList<>();
        challengeAdapter = new ChallengeAdapter(challenges);

        // Get user database reference
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        getCurrentUserId();
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
     * Get database public and private challenges which current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        databaseChallenge = controller.getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        recyclerView.setAdapter(challengeAdapter);

        showChallengesInView();
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
        if (challenge.getUserAdmin().equals(currentUserId)) {
            challengeAdapter.setVisibility(true);
        } else {
            challengeAdapter.setVisibility(false);
        }
    }

    /**
     * Get current user to get his challenges list later
     */
    private void getCurrentUserId(){
        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUserId = user.getId();
                    }
                }
                getUserChallenges();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("MyChallCurrentUsr error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get current user challenges list
     */
    private void getUserChallenges(){
        // Execute controller method to get database challenges objects. Use OnGetDataListener interface to know
        // when database data is retrieved and notify adapter to show them in recycler view
        controller.readChild(databaseChallenge, new OnGetChildListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Challenge challenge = data.getValue(Challenge.class);

                if (challenge.getMembers().containsKey(currentUserId)){
                    addChallenge(challenge);
                }
                challengeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onChanged(DataSnapshot data) {
                // Get user challenges and check if some challenge has been updated
                Challenge challenge = data.getValue(Challenge.class);
                if (challenge.getMembers().containsKey(currentUserId)){
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
                    if (challenges.contains(challenge)) {
                        challenges.remove(challenge);
                    }
                }
                challengeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onRemoved(DataSnapshot data) {
                //Nothing to do
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListMyChall error", databaseError.getMessage());
            }
        });
    }

    /**
     * Return list of challenges
     * @return challenges
     */
    private ArrayList<Challenge> showChallengesInView(){
        return challenges;
    }
}