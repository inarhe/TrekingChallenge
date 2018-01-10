/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.group;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.common.ConstantsUtils;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class AddGroupActivity extends AppCompatActivity {
    private EditText editTextName, editTextDescription;
    private DatabaseReference databaseGroup, databaseUser;
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
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etNameGroup);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionGroup);
        checkBox = (CheckBox) findViewById(R.id.cBPublicGgroup);

        getUserAdmin();
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
                        break;
                    }
                }

                // If group doesn't exist, executes add group controller method
                if (!groupExists){
                    idGroup = controller.getFirebaseNewKey(databaseGroup);
                    if (idGroup == null){
                        Toast.makeText(getApplicationContext(), R.string.failedAddGroup, Toast.LENGTH_SHORT).show();
                    } else {
                        group = new Group(idGroup, name, description, isPublic, currentUser.getId(), ConstantsUtils.DEFAULT_MEMBERS);
                        controller.addNewGroup(databaseGroup, group, currentUser.getId(), getApplicationContext());

                        // Select users that admin wants in the group
                        inviteUsers();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),R.string.groupAlreadyExists,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddGroup error", databaseError.getMessage());
            }
        });
    }

    /**
     * Cancel grup creation when cancel button is clicked.
     * @param view
     */
    public void cancelGroup (View view) {
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

    /**
     * Get current user information and know who is doing the action
     */
    private void getUserAdmin(){
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
                        currentUser = user;
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddGroup getAdmin error", databaseError.getMessage());
            }
        });
    }
}
