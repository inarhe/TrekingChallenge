package edu.uoc.iartal.trekkingchallenge.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.message.ListMessagesActivity;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.R;

public class ShowGroupActivity extends AppCompatActivity {
    private DatabaseReference databaseGroup;
    private Group group;
    private CommonFunctionality common;
    private String currentMail, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database references
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        // If user isn't logged, start login activity
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        TextView textViewDescription = (TextView) findViewById(R.id.tvGroupDescription);
        TextView textViewMembers = (TextView) findViewById(R.id.tvNumberMembers);

        // Initialize variables
        common = new CommonFunctionality();
        currentMail = firebaseAuth.getCurrentUser().getEmail();

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

        // Get current user name
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUserName = user.getIdUser();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
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
                Intent intent = new Intent(getApplicationContext(), ListMessagesActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
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
     * When the menu option "join group" is clicked, this method is executed. Check group members, add current user to selected group, add group to user groups and
     * updates group members number
     */
    private void joinGroup () {
        // Check if user is a group member
        if (checkIsMember()){
            Toast.makeText(getApplicationContext(), R.string.alreadyInGroup, Toast.LENGTH_LONG).show();
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
                        common.updateJoins(currentMail, getString(R.string.setJoin), databaseGroup, group.getId(), group.getNumberOfMembers(), FireBaseReferences.USER_GROUPS_REFERENCE);
                        Toast.makeText(getApplicationContext(), getString(R.string.groupJoined), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), R.string.noMemberGroup, Toast.LENGTH_LONG).show();
            return;
        }

        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.groupLeft));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setLeave), databaseGroup, group.getId(), group.getNumberOfMembers(), FireBaseReferences.USER_GROUPS_REFERENCE);
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
     * @return
     */
    private Boolean checkIsMember(){
        ArrayList<String> members = new ArrayList<>();
        members.addAll(group.getMembers().keySet());

        return members.contains(currentUserName);
    }
}
