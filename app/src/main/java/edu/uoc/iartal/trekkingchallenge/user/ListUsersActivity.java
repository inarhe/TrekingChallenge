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

import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.objectsDB.UserAdapter;
import edu.uoc.iartal.trekkingchallenge.R;

public class ListUsersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private String idGroup, groupName;
    private Toolbar toolbar;
    private DatabaseReference databaseUser, databaseGroup;
    private List<User> filteredModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        toolbar = (Toolbar) findViewById(R.id.listUserToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listUsersActivity));

        Bundle groupData = getIntent().getExtras();
        idGroup = groupData.getString("idGroup");
        groupName = groupData.getString("groupName");

        recyclerView = (RecyclerView) findViewById(R.id.rvListMyGroups);
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
        Log.i("filter", filteredModelList.toString());
        Log.i("users", users.toString());
        if (((CheckBox)view).isChecked()){
            if (!filteredModelList.isEmpty()){
                selectedUsers.add(filteredModelList.get(position));
                Log.i("filter", filteredModelList.get(position).toString());
                Log.i("sel", selectedUsers.toString());
            } else {
                selectedUsers.add(users.get(position));
                Log.i("us", users.toString());
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

        if (!selectedUsers.isEmpty()){
            for (User user : selectedUsers){
                databaseUser.child(user.getIdUser()).child("groups").child(groupName).setValue("true");
                databaseGroup.child(idGroup).child("members").child(user.getIdUser()).setValue("true");

                Query query = databaseGroup.orderByChild(FireBaseReferences.GROUPNAME_REFERENCE).equalTo(groupName);

                // Query database to get user information
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //  Group group = dataSnapshot.getValue(Group.class);
                        //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                        // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                        int members = dataSnapshot.getValue(Group.class).getNumberOfMembers();
                        databaseGroup.child(idGroup).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members+1);
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
            Toast.makeText(ListUsersActivity.this,getString(R.string.usersInvited),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<User> filteredModelList = filter(users, newText);

        userAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<User> filter(List<User> models, String query) {
        query = query.toLowerCase();
        filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.getIdUser().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
