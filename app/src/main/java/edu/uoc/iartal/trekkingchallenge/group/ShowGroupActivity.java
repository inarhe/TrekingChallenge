package edu.uoc.iartal.trekkingchallenge.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.message.ListMessagesActivity;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowGroupActivity extends AppCompatActivity {
    private DatabaseReference databaseGroup, databaseUser;
    private Group group;
    private User currentUser;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = controller.getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);

        // Link layout elements with variables
        TextView textViewDescription = (TextView) findViewById(R.id.tvGroupDescription);
        TextView textViewMembers = (TextView) findViewById(R.id.tvNumberMembers);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");

        // Set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(group.getName());

        // Show selected group information in the layout
        textViewDescription.setText(group.getDescription());
        textViewMembers.setText(Integer.toString(group.getNumberOfMembers()) + " " + getString(R.string.members));

        getCurrentUser();
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                sendMessage();
                return true;
            case R.id.action_joinGroup:
                joinGroup();
                return true;
            case R.id.action_leaveGroup:
                leaveGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the menu message icon is clicked, this method is executed. Start ListMessage activity
     */
    private void sendMessage(){
        Intent intent = new Intent(getApplicationContext(), ListMessagesActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    /**
     * When the menu option "join group" is clicked, this method is executed. Check group members, add current user to selected group, add group to user groups and
     * updates group members number
     */
    private void joinGroup () {
        // Check if user is a group member
        if (checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.alreadyInGroup, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.joinGroupAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        controller.updateJoins(getString(R.string.setJoin), databaseGroup, FireBaseReferences.USER_GROUPS_REFERENCE, group.getId(), currentUser, group.getNumberOfMembers());
                        Toast.makeText(getApplicationContext(), getString(R.string.groupJoined), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * When the menu option "leave group" is clicked, this method is executed. Check group members, delete current user from selected group, delete group from user groups and
     * updates group members number
     */
    public void leaveGroup () {
        // Check if user is a group member
        if (!checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.noMemberGroup, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.groupLeftAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        controller.updateJoins(getString(R.string.setLeave), databaseGroup, FireBaseReferences.USER_GROUPS_REFERENCE, group.getId(), currentUser, group.getNumberOfMembers());
                        Toast.makeText(getApplicationContext(), getString(R.string.groupLeft), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Check if current user is a member of the group object
     * @return current user id
     */
    private Boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(group.getMembers().keySet());

        return members.contains(currentUser.getId());
    }

    /**
     * Get current user object from database
     */
    private void getCurrentUser(){
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
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ShowGroup getUser error", databaseError.getMessage());
            }
        });
    }
}
