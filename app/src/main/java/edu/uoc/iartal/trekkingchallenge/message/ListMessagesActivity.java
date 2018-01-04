package edu.uoc.iartal.trekkingchallenge.message;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.adapter.MessageAdapter;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListMessagesActivity extends AppCompatActivity {

    private ArrayList<Message> messages;
    private Group group;
    private Trip trip;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseMessage, databaseUser;
    private String currentUserId;
    private FirebaseController controller;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.messageListToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.messageListActivity));

        //Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        controller = new FirebaseController();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter (messages);

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get route data from show route activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        trip = bundle.getParcelable("trip");

        // Initialize and set recycler view with its adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Initialize database references
        databaseMessage = controller.getDatabaseReference(FireBaseReferences.MESSAGE_REFERENCE);
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Define floating button for adding new rating
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddMessageActivity.class);
                if (group != null){
                    intent.putExtra("group", group);
                }

                if (trip != null){
                    intent.putExtra("trip", trip);
                }

                intent.putExtra("user", currentUserId);

                startActivity(intent);
            }
        });

        getMessagesList();
        getCurrentUserId();
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super. onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get current user id
     */
    private void getCurrentUserId(){
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
                        currentUserId = user.getId();
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("MssgCurrentUser error", databaseError.getMessage());
            }
        });
    }

    /**
     * Show database messages in recycler view
     */
    private void getMessagesList(){

        controller.readData(databaseMessage, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                messages.clear();
                for (DataSnapshot messageSnapshot : data.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (group != null) {
                        if (message.getGroup().equals(group.getId())){
                            messages.add(message);
                        }
                    }
                    if (trip != null) {
                        if (message.getTrip().equals(trip.getId())) {
                            messages.add(message);
                        }
                    }

                }
                messageAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListMssg error", databaseError.getMessage());
            }
        });
    }
}
