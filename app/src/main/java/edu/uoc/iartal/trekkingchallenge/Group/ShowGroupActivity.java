package edu.uoc.iartal.trekkingchallenge.Group;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.User.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.R;

public class ShowGroupActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupKey, name;
    private DatabaseReference databaseGroup;
    private DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        TextView textViewDescription = (TextView) findViewById(R.id.textViewGroupDescription);
        TextView textViewMembers = (TextView) findViewById(R.id.textViewNumberMembers);

        // Get Firebase authentication instance and database group reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        // Get data from item clicked on list groups activity
        Bundle groupData = getIntent().getExtras();
        name = groupData.getString("groupName");
        String description = groupData.getString("groupDescription");
        int members = groupData.getInt("members");
        groupKey = groupData.getString("groupKey");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(name);

        textViewDescription.setText(description);
        textViewMembers.setText(Integer.toString(members));

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public void joinGroup () {
        // Add new group member when join group button is clicked
        String currentMail = firebaseAuth.getCurrentUser().getEmail();
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        updateJoins(currentMail);
        updateMembers();

        finish();

    }

    private void updateJoins(String currentMail){
        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getIdUser()).setValue("true");

                databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
              //  int members = databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE);
             //   Log.i("MEM", Integer.toString(members));
              //  databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(2);
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

    private void updateMembers(){
        Log.i("UP MEM", "mec mec");
        Query query = databaseGroup.orderByChild(FireBaseReferences.GROUPNAME_REFERENCE).equalTo(name);
        Log.i("UP NAME", name);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              //  Group group = dataSnapshot.getValue(Group.class);
              //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

               // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                  int members = dataSnapshot.getValue(Group.class).getNumberOfMembers();
                databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members+1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_joinGroup:
                joinGroup();
                return true;
            case R.id.action_leaveGroup:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
