package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class UserAreaActivity extends AppCompatActivity {

    private TextView textViewUserName, textViewUserMail, textViewAlias;
    private ProgressDialog progressDialog;
    private User currentUser;
    private DatabaseReference databaseUser;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.userAreaActivity));

        // Initialize variables
        progressDialog = new ProgressDialog(this);
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database reference
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Link layout elements with variables
        textViewAlias = (TextView) findViewById(R.id.tvIdUser);
        textViewUserName = (TextView) findViewById(R.id.tvUserName);
        textViewUserMail = (TextView) findViewById(R.id.tvUserMail);

        // Load user information
        loadUser();
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_area, menu);
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
            case R.id.action_userHistory:
                showUserHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Load and show data of current user
     */
    private void loadUser(){
        // Execute controller method to get database users objects and find which has active session. Use OnGetDataListener
        // interface to know when database data is retrieved
        controller.readData(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUser = user;
                        textViewAlias.setText(user.getAlias());
                        textViewUserName.setText(user.getName());
                        textViewUserMail.setText(user.getMail());
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("LoadUser error", databaseError.getMessage());
            }
        });
    }

    /**
     * Start user history activity when button is clicked
     */
    private void showUserHistory(){
        Intent intent = new Intent(UserAreaActivity.this, UserHistoryActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    /**
     * Logout when sign out button is clicked
     * @param view
     */
    public void signOut(View view) {
        controller.signOutDatabase(this);
    }




}
