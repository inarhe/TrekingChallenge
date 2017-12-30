package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.ConstantsUtils;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddTripActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTextName, editTextDescription, dateEditText, editTextPlace;
    private DatabaseReference databaseTrip, databaseUser, databaseRoute;
    private CheckBox checkBox;
    private Spinner spinner;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private String name, description, place, tripDate, route, idTrip;
    private ArrayList<String> nameRoutes;
    private ArrayAdapter<String> spinnerAdapter;
    private Trip trip;
    private User currentUser;
    private boolean isPublic, tripExists;
    private FirebaseController controller;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addTripActivity));

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize variables
        controller = new FirebaseController();
        nameRoutes = new ArrayList<>();
        context = this;

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseRoute = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etNameTrip);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionTrip);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        editTextPlace = (EditText) findViewById(R.id.etPlace);
        checkBox = (CheckBox) findViewById(R.id.cBPublicTrip);
        spinner = (Spinner) findViewById(R.id.spinnerRoute);
        ImageView buttonCalendar = (ImageView) findViewById(R.id.bDate);

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        // Get routes to inflate spinner
        getRoutes();
        spinner.setOnItemSelectedListener(this);

        getUserAdmin();

        // Click listener on date edit text to show calendar
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
     * Listener of spinner item selected. Save position unless first with default text
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextSize(18);

        if (position != 0){
            route = spinner.getSelectedItem().toString();
        } else {
            route = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing to do
    }

    /**
     * Add trip to database when accept button is clicked
     * @param view
     */
    public void addTrip (View view) {
        // Initialize variables with input parameters
        isPublic = false;
        tripExists = false;
        name = editTextName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        tripDate = dateEditText.getText().toString().trim();
        place = editTextPlace.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(place)) {
            Toast.makeText(this, getString(R.string.placeAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (route == null) {
            Toast.makeText(this, getString(R.string.chooseRoute), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Execute controller method to get database trips objects. Use OnGetDataListener interface to know
        // when database data is retrieved and search if trip already exists
        controller.readDataOnce(databaseTrip, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot tripSnapshot : data.getChildren()){
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip.getName().equals(name)){
                        tripExists = true;
                    }
                }

                // If trip doesn't exist, executes add trip controller method
                if (!tripExists){
                    idTrip = controller.getFirebaseNewKey(databaseTrip);
                    if (idTrip == null){
                        Toast.makeText(getApplicationContext(), R.string.failedAddTrip, Toast.LENGTH_SHORT).show();
                    } else {
                        trip = new Trip(idTrip, name, description, tripDate, place, route, isPublic, currentUser.getId(), ConstantsUtils.DEFAULT_MEMBERS);
                        controller.addNewTrip(databaseTrip, trip, currentUser.getId(), getApplicationContext());

                        // Select users that admin wants in the trip
                        inviteUsers();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),R.string.tripAlreadyExists,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddTrip error", databaseError.getMessage());
            }
        });
    }

    /**
     * Cancel trip creation when cancel button is clicked.
     * @param view
     */
    public void cancelTrip (View view) {
        finish();
    }

    /**
     * Show user list and allow invite users to join the new trip
     */
    private void inviteUsers (){
        Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
        finish();
    }

    /**
     * Get database routes to inflate spinner with their names
     */
    private void getRoutes(){
        nameRoutes.clear();
        nameRoutes.add(getString(R.string.chooseRouteSpinner));

        // Execute controller method to get database route objects. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseRoute, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot routeSnapshot : data.getChildren()) {
                    String nameRoute = routeSnapshot.getValue(Route.class).getName();
                    nameRoutes.add(nameRoute);
                }
                spinnerAdapter = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item, nameRoutes);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddTrip getRoute error", databaseError.getMessage());
            }
        });
    }

    /**
     * Set current date format and DatePickerDialog
     */
    private void setDate(){
        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        dateEditText.setText(dateString);

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
     * Get current user information and know who is doing the action
     */
    private void getUserAdmin(){
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
                Log.e("AddTrip getAdmin error", databaseError.getMessage());
            }
        });
    }
}
