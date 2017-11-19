package edu.uoc.iartal.trekkingchallenge.Group;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.User.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.R;

public class ShowGroupActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewDescription;
    private TextView textViewMembers;
    private FirebaseAuth firebaseAuth;
    private String groupKey;
    private DatabaseReference databaseGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);


        textViewDescription = (TextView) findViewById(R.id.textViewGroupDescription);
        textViewMembers = (TextView) findViewById(R.id.textViewNumberMembers);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        Bundle groupData = getIntent().getExtras();
        String name = groupData.getString("groupName");
        String description = groupData.getString("groupDescription");
      //  int members = groupData.getInt("members");
        groupKey = groupData.getString("groupKey");

        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(name);

        textViewDescription.setText(description);
     //   textViewMembers.setText(String.valueOf(members));

        if (firebaseAuth.getCurrentUser() == null) {
            // start login activity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    public void joinGroup (View view) {
        String mail = firebaseAuth.getCurrentUser().getEmail();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        Query query = databaseUser.orderByChild("mailUser").equalTo(mail);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                databaseGroup.child(groupKey+"/members").child(user.getIdUser()).setValue("true");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
