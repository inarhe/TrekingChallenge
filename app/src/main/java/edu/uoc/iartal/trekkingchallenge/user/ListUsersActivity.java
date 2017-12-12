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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.challenge.ListChallengesActivity;
import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.objectsDB.UserAdapter;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.trip.ListTripsActivity;

public class ListUsersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private String  idTrip, tripName, idChallenge, challengeName;
    private Group group;
    private Toolbar toolbar;
    private DatabaseReference databaseUser, databaseGroup, databaseTrip, databaseChallenge;
    private List<User> filteredModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        toolbar = (Toolbar) findViewById(R.id.listUserToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listUsersActivity));

        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        idTrip = bundle.getString("idTrip");
        tripName = bundle.getString("tripName");
        idChallenge = bundle.getString("idChallenge");
        challengeName = bundle.getString("challengeName");

        recyclerView = (RecyclerView) findViewById(R.id.rvListUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(users, ListUsersActivity.this);
        recyclerView.setAdapter(userAdapter);

        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        // Show database users in recycler view
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.removeAll(users);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_users_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_searchUser);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                      //  userAdapter.setFil
                        userAdapter.setFilter(users);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }

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

    public void prepareSelection (View view, int position){
      //  Log.i("filter", filteredModelList.toString());
      //  Log.i("users", users.toString());
        if (((CheckBox)view).isChecked()){
            if (!filteredModelList.isEmpty()){
                selectedUsers.add(filteredModelList.get(position));
      //          Log.i("filter", filteredModelList.get(position).toString());
      //          Log.i("sel", selectedUsers.toString());
            } else {
                selectedUsers.add(users.get(position));
       //         Log.i("us", users.toString());
            }

        } else {
            if (!filteredModelList.isEmpty()){
                selectedUsers.remove(filteredModelList.get(position));
            } else {
                selectedUsers.remove(users.get(position));
            }
        }
    }

    private void inviteUsers(){
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        if (!selectedUsers.isEmpty()){
            if (group.getId() != null){
                setGroups();
                Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvitedGroup),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
                finish();

            }

            if (idTrip != null){
                setTrips();
                Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvitedTrip),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ListTripsActivity.class));
                finish();
            }

            if (idChallenge != null){
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

    @Override
    public boolean onQueryTextChange(String newText) {
        filteredModelList.clear();
        filteredModelList = filter(users, newText);

        userAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<User> filter(List<User> models, String query) {
        query = query.toLowerCase();
        filteredModelList.clear();
       // filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.getIdUser().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void setGroups (){
        for (User user : selectedUsers){
            databaseUser.child(user.getIdUser()).child("groups").child(group.getId()).setValue("true");
            databaseGroup.child(group.getId()).child("members").child(user.getIdUser()).setValue("true");

            Query query = databaseGroup.orderByChild(FireBaseReferences.GROUP_REFERENCE).equalTo(group.getId());

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //  Group group = dataSnapshot.getValue(Group.class);
                    //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                    // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                    int members = dataSnapshot.getValue(Group.class).getNumberOfMembers();
                    databaseGroup.child(group.getId()).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(members+1);
                    Log.i("MEM", Integer.toString(members));

                    // textViewMembers.setText(members+1);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //TO-DO
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
    }

    private void setTrips () {
        for (User user : selectedUsers) {
            databaseUser.child(user.getIdUser()).child("trips").child(tripName).setValue("true");
            databaseTrip.child(idTrip).child("members").child(user.getIdUser()).setValue("true");

            Query query = databaseTrip.orderByChild(FireBaseReferences.TRIPNAME_REFERENCE).equalTo(tripName);

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //  Group group = dataSnapshot.getValue(Group.class);
                    //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                    // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                    int members = dataSnapshot.getValue(Trip.class).getNumberOfMembers();
                    databaseTrip.child(idTrip).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(members + 1);
                    Log.i("MEM", Integer.toString(members));

                    // textViewMembers.setText(members+1);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //TO-DO
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
    }

    private void setChallenges (){
        for (User user : selectedUsers){
            databaseUser.child(user.getIdUser()).child("challenges").child(challengeName).setValue("true");
            databaseChallenge.child(idChallenge).child("members").child(user.getIdUser()).setValue("true");

            Query query = databaseChallenge.orderByChild(FireBaseReferences.CHALLENGENAME_REFERENCE).equalTo(challengeName);

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //  Group group = dataSnapshot.getValue(Group.class);
                    //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                    // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                    int members = dataSnapshot.getValue(Trip.class).getNumberOfMembers();
                    databaseChallenge.child(idChallenge).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(members+1);
                    Log.i("MEM", Integer.toString(members));

                    // textViewMembers.setText(members+1);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //TO-DO
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
    }
}
