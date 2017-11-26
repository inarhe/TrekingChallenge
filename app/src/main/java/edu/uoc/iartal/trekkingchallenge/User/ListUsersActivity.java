package edu.uoc.iartal.trekkingchallenge.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.Group.AddGroupActivity;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.UserAdapter;
import edu.uoc.iartal.trekkingchallenge.R;

public class ListUsersActivity extends AppCompatActivity implements View.OnLongClickListener {
    public boolean isInActionMode = false;
    private RecyclerView.Adapter userAdapter;
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private String idGroup, groupName;
    int counter = 0;
    private Toolbar toolbar;

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


        recyclerView = (RecyclerView) findViewById(R.id.rvListUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        userAdapter = new UserAdapter(users, ListUsersActivity.this);
        recyclerView.setAdapter(userAdapter);

        // Show database groups in recycler view
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.removeAll(users);
                for (DataSnapshot userSnapshot:
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                userAdapter.notifyDataSetChanged();
                Log.i("TEST", "ok");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        isInActionMode = true;
        userAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_users_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inviteUsers:
                inviteUsers();
               // startActivityForResult(intent, ACTIVITY_CODE);
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void prepareSelection (View view, int position){
        if (((CheckBox)view).isChecked()){
            selectedUsers.add(users.get(position));
            counter = counter +1;
        } else {
            selectedUsers.remove(users.get(position));
            counter = counter -1;
        }
    }

    private void inviteUsers(){
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        for (User user : selectedUsers){
            databaseUser.child(user.getIdUser()).child("groups").child(groupName).setValue("true");
            databaseGroup.child(idGroup).child("members").child(user.getAlias()).setValue("true");
        }

        if (!selectedUsers.isEmpty()){
            Toast.makeText(ListUsersActivity.this,"Usuaris afegits al grup",Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<User> getSelectedUsers(){
        return selectedUsers;
    }

}
