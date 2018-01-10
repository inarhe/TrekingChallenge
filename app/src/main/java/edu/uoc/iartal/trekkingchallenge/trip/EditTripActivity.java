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

package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditTripActivity extends AppCompatActivity {
    private DatabaseReference databaseTrip, databaseDone;
    private Trip trip;
    private EditText editTextName, editTextDescription, dateEditText;
    private ProgressDialog progressDialog;
    private boolean updateDate, updateDesc, tripExists;
    private String newName, newDescription, newDate;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private FirebaseController controller;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.editTripActivityName);

        // Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(this);
        context = this;

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        databaseDone = controller.getDatabaseReference(FireBaseReferences.TRIPSDONE_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etTripName);
        editTextDescription = (EditText) findViewById(R.id.etTripDesc);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        ImageView buttonCalendar = (ImageView) findViewById(R.id.bDate);

        // Get data from item clicked on list trips activity
        Bundle bundle = getIntent().getExtras();
        trip = bundle.getParcelable("trip");

        // Show selected trip information in the layout
        editTextName.setText(trip.getName());
        editTextDescription.setText(trip.getDescription());

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        // Click listener on calendar icon to show calendar
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    /**
     * Executed when accept button is clicked. Verify input parameters and update values
     * @param view
     */
    public void editTrip (View view) {
        //Initialize variables
        Boolean updateName = false;
        updateDesc = false;
        updateDate = false;
        tripExists = false;

        // Get input parameters
        newName = editTextName.getText().toString().trim();
        newDescription = editTextDescription.getText().toString().trim();
        newDate = dateEditText.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, R.string.nameField, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(this, R.string.descriptionField, Toast.LENGTH_SHORT).show();
            return;
        }

        // Set progress dialog
        progressDialog.setMessage(getString(R.string.updating));
        progressDialog.show();

        // Check changes on trip name parameter
        if (!(trip.getName()).equals(newName)) {
            updateName = true;
        }

        // Check changes on trip description parameter
        if ((!(trip.getDescription()).equals(newDescription))) {
            updateDesc = true;
        }

        // Check changes on trip date parameter
        if ((!(trip.getDate()).equals(newDate))) {
            updateDate = true;
        }

        // Update database if its necessary
        if (updateName){
            // Execute controller method to check if trip name exists and if it doesn't, update database trip object.
            controller.readDataOnce(databaseTrip, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //Nothing to do
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    for (DataSnapshot tripSnapshot : data.getChildren()){
                        Trip trip = tripSnapshot.getValue(Trip.class);
                        if (trip.getName().equals(newName)){
                            tripExists = true;
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.tripAlreadyExists, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    // If trip doesn't exist, executes update parameter
                    if (!tripExists){
                        getTripResults();
                        controller.updateStringParameter(databaseTrip, trip.getId(), FireBaseReferences.TRIP_NAME_REFERENCE, newName);
                        if (updateDesc){
                            updateDescriptionValue();
                            if (updateDate) {
                                updateDateValue();
                            }
                        } else if (updateDate) {
                            updateDateValue();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.editTripOk, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.e("EditTrip error", databaseError.getMessage());
                }
            });
        } else {
            if (updateDesc){
                updateDescriptionValue();
                if (updateDate){
                    updateDateValue();
                }
            } else if (updateDate){
                updateDateValue();
            }
            Toast.makeText(getApplicationContext(), R.string.editChallengeOk, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }
    }

    /**
     * Cancel edit trip if cancel button is clicked
     * @param view
     */
    public void cancelEditTrip (View view) {
        finish();
    }

    /**
     * Set current date format and DatePickerDialog
     */
    private void setDate(){
        dateEditText.setText(trip.getDate());

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateSelected.set(Calendar.YEAR, year);
                dateSelected.set(Calendar.MONTH, month);
                dateSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };
    }

    /**
     * Set current date in layout edit text
     */
    private void updateDate() {
        dateEditText.setText(sdf.format(dateSelected.getTime()));
    }

    /**
     * Update trip description into database
     */
    private void updateDescriptionValue(){
        controller.updateStringParameter(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DESCRIPTION_REFERENCE, newDescription);
    }

    /**
     * Update trip date into database
     */
    private void updateDateValue(){
        controller.updateStringParameter(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DATE_REFERENCE, newDate);
    }

    /**
     * Update trip name of trips results
     */
    private void getTripResults(){
        final ArrayList<String> tripsDone = new ArrayList<>();
        tripsDone.addAll(trip.getDone().keySet());

        controller.readDataOnce(databaseDone, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot doneSnapshot : data.getChildren()){
                    TripDone tripDone = doneSnapshot.getValue(TripDone.class);
                    if (tripsDone.contains(tripDone.getId())){
                        controller.updateStringParameter(databaseDone, tripDone.getId(), FireBaseReferences.TRIPSDONE_TRIP_NAME, newName);
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("Edit tripDone", databaseError.getMessage());
            }
        });
    }
}
