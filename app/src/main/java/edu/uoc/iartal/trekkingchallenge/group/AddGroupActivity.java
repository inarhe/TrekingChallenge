package edu.uoc.iartal.trekkingchallenge.group;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.database.ValueEventListener;

import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class AddGroupActivity extends AppCompatActivity {
    private EditText editTextName, editTextDescription;
    private DatabaseReference databaseGroup;
    private CheckBox checkBox;
    private User currentUser;
    private Group group;
    private String name, description, idGroup;
    private Boolean isPublic, groupExists;
    private FirebaseController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addGroupToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addGroupActivity));

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseGroup = controller.getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);
        DatabaseReference databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etNameGroup);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionGroup);
        checkBox = (CheckBox) findViewById(R.id.cBPublicGgroup);

        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readData(databaseUser, new OnGetDataListener() {
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
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddGroup getAdmin error", databaseError.getMessage());
            }
        });
    }

    /**
     * Add group to database when accept button is clicked
     * @param view
     */
    public void addGroup (View view) {
        // Initialize variables with input parameters
        isPublic = false;
        groupExists = false;
        name = editTextName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descriptionField), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Execute controller method to get database groups objects. Use OnGetDataListener interface to know
        // when database data is retrieved and search if group already exists
        controller.readDataOnce(databaseGroup, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot groupSnapshot : data.getChildren()){
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group.getName().equals(name)){
                        groupExists = true;
                    }
                }

                // If group not exists, executes add group controller method
                if (!groupExists){
                    idGroup = controller.getFirebaseNewKey(databaseGroup);
                    if (idGroup == null){
                        Toast.makeText(getApplicationContext(), R.string.failedAddGroup, Toast.LENGTH_SHORT).show();
                    } else {
                        group = new Group(idGroup, name, description, isPublic, currentUser.getId(), 1);
                        controller.creatGroup(databaseGroup, group, currentUser.getId(), getApplicationContext());

                        // Select users that admin wants in the group
                        inviteUsers();
                    }
                } else {
                    Toast.makeText(AddGroupActivity.this,R.string.groupAlreadyExists,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("LoadAllGroups error", databaseError.getMessage());
            }
        });
    }

    /**
     * Cancel grup creation when cancel button is clicked. Start main activity
     * @param view
     */
    public void cancelGroup (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * Show user list and allow invite users to join the new group
     */
    private void inviteUsers (){
        Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
        finish();
    }
}
