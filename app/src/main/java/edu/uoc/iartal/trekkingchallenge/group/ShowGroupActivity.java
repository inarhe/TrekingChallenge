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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.R;

public class ShowGroupActivity extends AppCompatActivity {
    private DatabaseReference databaseGroup;
    private Group group;
    private CommonFunctionality common;
    private String currentMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        // Get Firebase authentication instance and database group reference
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
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
                leaveGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the menu option "join group" is clicked, this method is executed. Add current user to selected group, add group to user groups and
     * updates group members number
     */
    private void joinGroup () {
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.joinGroupAsk));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        common.updateJoins(currentMail, getString(R.string.setJoin), databaseGroup, group.getId(), FireBaseReferences.USER_GROUPS_REFERENCE);
                        databaseGroup.child(group.getId()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(group.getNumberOfMembers()+1);
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
     * When the menu option "leave group" is clicked, this method is executed. Delete current user from selected group, delete group from user groups and
     * updates group members number
     */
    public void leaveGroup () {
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.groupLeft));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        common.updateJoins(currentMail, getString(R.string.setLeave), databaseGroup, group.getId(), FireBaseReferences.USER_GROUPS_REFERENCE);
                        databaseGroup.child(group.getId()).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(group.getNumberOfMembers()-1);

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
}
