package edu.uoc.iartal.trekkingchallenge.message;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.group.AddGroupActivity;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.Message;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddMessageActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextMessage;
    private DatabaseReference databaseMessage, databaseUser, databaseTrip, databaseGroup;
    private String user, messageText, idGroup, idTrip;
    private Group group;
    private Message message;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addMessageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addMessageActivity));

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from item clicked on list routes activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        trip = bundle.getParcelable("trip");
        user = bundle.getString("user");

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get database references
        databaseMessage = FirebaseDatabase.getInstance().getReference(FireBaseReferences.MESSAGE_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        // Link layout elements with variables
        editTextMessage = (EditText) findViewById(R.id.etMessage);
    }

    /**
     * Add group to database when accept button is clicked
     * @param view
     */
    public void publishMessage (View view) {
        // Initialize variables with input parameters
        messageText = editTextMessage.getText().toString().trim();

        // If some of the input parameters are incorrect, stops execution
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.adviceBody, Toast.LENGTH_LONG).show();
            return;
        }

        if (group != null) {
            idGroup = group.getId();
        } else {
            idGroup = getString(R.string.none);
        }

        if (trip != null) {
            idTrip = trip.getId();
        } else {
            idTrip = getString(R.string.none);
        }

        // Add group to firebase database
        final String idMessage = databaseMessage.push().getKey();
        message = new Message(idMessage, messageText, idGroup, user, idTrip);

        databaseMessage.child(idMessage).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    updateUserDependencies(idMessage);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.messageNotPublished, Toast.LENGTH_LONG).show();
                }
            }
        });
        finish();
    }

    /**
     * Cancel grup creation when cancel button is clicked. Start main activity
     * @param view
     */
    public void cancelMessage (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void updateUserDependencies(final String idMessage){
        databaseUser.child(user).child(FireBaseReferences.MESSAGES_REFERENCE).child(idMessage).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if (group != null){
                                updateGroupDependencies(idMessage);
                            } else {
                                updateTripDependencies(idMessage);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.messageNotPublished,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateGroupDependencies (final String idMessage){
        databaseGroup.child(idGroup).child(FireBaseReferences.MESSAGES_REFERENCE).child(idMessage).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.messagePublished, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.messageNotPublished,Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void updateTripDependencies(final String idMessage){
        databaseTrip.child(idTrip).child(FireBaseReferences.MESSAGES_REFERENCE).child(idMessage).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), R.string.messagePublished, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.messageNotPublished,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
