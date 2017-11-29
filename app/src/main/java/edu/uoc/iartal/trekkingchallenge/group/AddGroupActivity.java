package edu.uoc.iartal.trekkingchallenge.group;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class AddGroupActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE = 1;
    private EditText editTextName, editTextDescription;
    private DatabaseReference databaseGroup, databaseUser;
    private CheckBox checkBox;
    private String userAdmin, name, idGroup;
    private FirebaseAuth firebaseAuth;
    private Intent intent;
    ListUsersActivity listUsersActivity;
    ArrayList<User> userMembers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addGroupToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addGroupActivity));

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseGroup = FirebaseDatabase.getInstance().getReference("group");

        editTextName = (EditText) findViewById(R.id.etNameGroup);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionGroup);
        checkBox = (CheckBox) findViewById(R.id.cBPublicGgroup);

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            String mail = firebaseAuth.getCurrentUser().getEmail();
            databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(mail);

            // Query database to get user admin information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    userAdmin = dataSnapshot.getValue(User.class).getIdUser();
                 //   userKey = dataSnapshot.getValue(User.class).getIdUser();
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

    public void addGroup (View view) {
        // Add group to database when accept button is clicked
        Boolean isPublic = false;
        name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.hintNameGroup), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.hintDescription), Toast.LENGTH_LONG).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Add group to firebase database
      //  idGroup = databaseGroup.push().s.getKey();
        idGroup = name;
        Group group = new Group(idGroup, name, description, isPublic, userAdmin, 1);

        databaseGroup.child(idGroup).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(AddGroupActivity.this, getString(R.string.failedAddGroup), Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseGroup.child(idGroup).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(userAdmin).setValue("true");

        databaseUser.child(userAdmin).child(FireBaseReferences.USERGROUPS_REFERENCE).child(group.getGroupName()).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.groupSaved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddGroupActivity.this,getString(R.string.failedAddGroup),Toast.LENGTH_SHORT).show();
                }
            }
        });


        inviteUsers();


    }

    public void cancelGroup (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void inviteUsers (){
        intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("groupName", name);
        intent.putExtra("idGroup", idGroup);
        startActivity(intent);
        finish();
    }
}
