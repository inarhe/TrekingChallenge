package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.challenge.ListChallengesActivity;
import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Challenge;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.objects.UserAdapter;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.trip.ListTripsActivity;

public class ListUsersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private Group group;
    private Trip trip;
    private Challenge challenge;
    private Toolbar toolbar;
    private DatabaseReference databaseUser, databaseGroup, databaseTrip, databaseChallenge;
    private List<User> filteredModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.listUserToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listUsersActivity));

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        // Get database references
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        // Get data from previous activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        trip = bundle.getParcelable("trip");
        challenge = bundle.getParcelable("challenge");

        // Set recycler view and user adapter
        recyclerView = (RecyclerView) findViewById(R.id.rvListUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(users, ListUsersActivity.this);
        recyclerView.setAdapter(userAdapter);

        // Show database users in recycler view
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot userSnapshot:
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (!user.getUserMail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        users.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
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
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_users, menu);

        final MenuItem item = menu.findItem(R.id.action_searchUser);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        userAdapter.setFilter(users);
                        // Return true to collapse action view
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Return true to expand action view
                        return true;
                    }
                });
        return true;
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inviteUsers:
                inviteUsers();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get selected user from total list or result search list
     * @param view
     * @param position
     */
    public void prepareSelection (View view, int position){
        if (((CheckBox)view).isChecked()){
            if (!filteredModelList.isEmpty()){
                selectedUsers.add(filteredModelList.get(position));
            } else {
                selectedUsers.add(users.get(position));
            }
        } else {
            if (!filteredModelList.isEmpty()){
                selectedUsers.remove(filteredModelList.get(position));
            } else {
                selectedUsers.remove(users.get(position));
            }
        }
    }

    /**
     * Updates invited users groups list, trips list or challenge list
     */
    private void inviteUsers(){
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        if (!selectedUsers.isEmpty()){
            if (group != null){
                setGroups();
                Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvitedGroup),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
                finish();
            }

            if (trip != null){
                setTrips();
                Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvitedTrip),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ListTripsActivity.class));
                finish();
            }

            if (challenge != null){
                setChallenges();
                Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvitedChallenge),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ListChallengesActivity.class));
                finish();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Pass new user list to Adapter
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        filteredModelList.clear();
        filteredModelList = filter(users, newText);
        userAdapter.setFilter(filteredModelList);
        return true;
    }

    /**
     * Get user list search result
     * @param models
     * @param query
     * @return
     */
    private List<User> filter(List<User> models, String query) {
        query = query.toLowerCase();
        filteredModelList.clear();
        for (User model : models) {
            final String text = model.getIdUser().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * Updates group list of all invited users and members of new group
     */
    private void setGroups (){
        for (User user : selectedUsers){
            databaseUser.child(user.getIdUser()).child(FireBaseReferences.USER_GROUPS_REFERENCE).child(group.getId()).setValue("true");
            databaseGroup.child(group.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).setValue("true");
            databaseGroup.child(group.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(group.getNumberOfMembers()+1);
        }
    }

    /**
     * Updates trip list of all invited users and members of new trip
     */
    private void setTrips () {
        for (User user : selectedUsers) {
            databaseUser.child(user.getIdUser()).child(FireBaseReferences.USER_TRIPS_REFERENCE).child(trip.getId()).setValue("true");
            databaseTrip.child(trip.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).setValue("true");
            databaseTrip.child(trip.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(trip.getNumberOfMembers() + 1);
        }
    }

    /**
     * Updates challenge list of all invited users and members of new challenge
     */
    private void setChallenges (){
        for (User user : selectedUsers){
            databaseUser.child(user.getIdUser()).child(FireBaseReferences.USER_CHALLENGES_REFERENCE).child(challenge.getId()).setValue("true");
            databaseChallenge.child(challenge.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).setValue("true");
            databaseChallenge.child(challenge.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(challenge.getNumberOfMembers()+1);
        }
    }
}
