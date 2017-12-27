package edu.uoc.iartal.trekkingchallenge.message;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.Message;
import edu.uoc.iartal.trekkingchallenge.objects.MessageAdapter;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListMessagesActivity extends AppCompatActivity {

    private ArrayList<Message> messages = new ArrayList<>();
    private Group group;
    private Trip trip;
    private DatabaseReference databaseMessage, databaseUser, databaseGroup, databaseTrip;
    private String userName, idMessage;
    private MessageAdapter messageAdapter;
    private EditText editTextTitle, editTextBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.messageListToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.messageListActivity));

        // Get route data from show route activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        trip = bundle.getParcelable("trip");

        // Initialize and set recycler view with its adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter (messages);
        recyclerView.setAdapter(messageAdapter);

        // Initialize database references
        databaseMessage = FirebaseDatabase.getInstance().getReference(FireBaseReferences.MESSAGE_REFERENCE);
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);

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

                intent.putExtra("user", userName);

                startActivity(intent);
            }
        });

        // Show database route ratings in recycler view
        databaseMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot:
                        dataSnapshot.getChildren()) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });


        // Get current user name
        getCurrentUserName();
        // Search user messages
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
     * Search the name of current user, to know who is doing the action
     */
    private void getCurrentUserName(){
        // Get current user mail
        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Query database to find user who has the current mail
        Query query = databaseUser.orderByChild(FireBaseReferences.USER_MAIL_REFERENCE).equalTo(currentMail);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userName = dataSnapshot.getValue(User.class).getId();
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
