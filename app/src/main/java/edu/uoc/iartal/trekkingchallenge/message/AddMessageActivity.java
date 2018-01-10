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

package edu.uoc.iartal.trekkingchallenge.message;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddMessageActivity extends AppCompatActivity {

    private DatabaseReference databaseMessage;
    private EditText editTextMessage;
    private String user, idGroup, idTrip;
    private Group group;
    private Trip trip;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addMessageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.addMessageActivity));

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from item clicked on previous activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");
        trip = bundle.getParcelable("trip");
        user = bundle.getString("user");

        // Get database references
        databaseMessage = controller.getDatabaseReference(FireBaseReferences.MESSAGE_REFERENCE);


        // Link layout elements with variables
        editTextMessage = (EditText) findViewById(R.id.etMessage);
    }

    /**
     * Add message to database when publish button is clicked
     * @param view
     */
    public void publishMessage (View view) {
        // Initialize variables with input parameters
        String messageText = editTextMessage.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.adviceBody, Toast.LENGTH_SHORT).show();
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

        // Add message to firebase database
        String idMessage = controller.getFirebaseNewKey(databaseMessage);

        if (idMessage == null){
            Toast.makeText(getApplicationContext(), R.string.messageNotPublished, Toast.LENGTH_SHORT).show();
        } else {
            Message message = new Message(idMessage, messageText, idGroup, user, idTrip);
            controller.addNewMessage(databaseMessage, message, user, idGroup, idTrip, getApplicationContext());
        }

        finish();
    }

    /**
     * Cancel message publish when cancel button is clicked.
     * @param view
     */
    public void cancelMessage (View view) {
        finish();
    }
}
