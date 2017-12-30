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
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditTripActivity extends AppCompatActivity {
    private DatabaseReference databaseTrip;
    private Trip trip;
    private EditText editTextName, editTextDescription, dateEditText;
    private ProgressDialog progressDialog;
    private Boolean updateDate, updateDesc;
    private String newDescription, newDate;
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

        // Get input parameters
        String newName = editTextName.getText().toString().trim();
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
        if (updateName) {
            // Execute controller method to update database group object. Use OnGetDataListener interface to know
            // when database is updated
            controller.executeTask(databaseTrip, trip.getId(), FireBaseReferences.TRIP_NAME_REFERENCE, newName, new OnCompleteTaskListener() {
                        @Override
                        public void onStart() {
                            //Nothing to do
                        }

                        @Override
                        public void onSuccess() {
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

                        @Override
                        public void onFailed() {
                            Toast.makeText(getApplicationContext(), R.string.editTripFail, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.editTripOk, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }
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
        // Execute controller method to update database group object.
        controller.editObjectParameter(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DESCRIPTION_REFERENCE, newDescription);
    }

    /**
     * Update trip date into database
     */
    private void updateDateValue(){
        // Execute controller method to update database group object.
        controller.editObjectParameter(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DATE_REFERENCE, newDate);
    }

    /**
     * Cancel edit trip if cancel button is clicked
     * @param view
     */
    public void cancelEditTrip (View view) {
        finish();
    }
}
