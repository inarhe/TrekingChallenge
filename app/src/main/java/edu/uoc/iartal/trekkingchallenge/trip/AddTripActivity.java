package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.common.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.group.AddGroupActivity;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddTripActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTextName, editTextDescription, dateEditText, editTextPlace;
    private ImageView buttonCalendar;
    private DatabaseReference databaseTrip, databaseUser, databaseRoute;
    private CheckBox checkBox;
    private String userAdmin, name, description, place, tripDate;
    private Spinner spinner;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private ArrayList<String> nameRoutes = new ArrayList<>();
    private Context context = this;
    private ArrayAdapter<String> adapter;
    private String route;
    private Trip trip;
    private Boolean isPublic, tripExists = false;

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

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get database references
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etNameTrip);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionTrip);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        editTextPlace = (EditText) findViewById(R.id.etPlace);
        checkBox = (CheckBox) findViewById(R.id.cBPublicTrip);
        spinner = (Spinner) findViewById(R.id.spinnerRoute);
        buttonCalendar = (ImageView) findViewById(R.id.bDate);

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
        //TO-DO
    }

    /**
     * Add trip to database when accept button is clicked
     * @param view
     */
    public void addTrip (View view) {
        // Initialize variables with input parameters
        isPublic = false;
        name = editTextName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        tripDate = dateEditText.getText().toString().trim();
        place = editTextPlace.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(place)) {
            Toast.makeText(this, getString(R.string.placeAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (route==null) {
            Toast.makeText(this, getString(R.string.chooseRoute), Toast.LENGTH_LONG).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        databaseTrip.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()){
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip.getName().equals(name)){
                        tripExists = true;
                    }
                }

                if (!tripExists){
                    // Add trip to firebase database
                    final String idTrip = databaseTrip.push().getKey();
                    trip = new Trip(idTrip, name, description, tripDate, place, route, isPublic, userAdmin, 1);

                    databaseTrip.child(idTrip).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.failedAddTrip), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    databaseTrip.child(idTrip).child(FireBaseReferences.MEMBERS_REFERENCE).child(userAdmin).setValue("true");
                    databaseUser.child(userAdmin).child(FireBaseReferences.USER_TRIPS_REFERENCE).child(trip.getId()).setValue("true")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), getString(R.string.tripSaved), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),getString(R.string.failedAddTrip),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    // Select users that admin wants in the trip
                    inviteUsers();
                } else {
                    Toast.makeText(AddTripActivity.this, R.string.tripAlreadyExists,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Cancel trip creation when cancel button is clicked. Start main activity
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

        databaseRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String nameRoute = routeSnapshot.getValue(Route.class).getName();
                    nameRoutes.add(nameRoute);
                }
                adapter = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item, nameRoutes);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            //TO-DO
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
                // TODO Auto-generated method stub
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
     * Query database to get current user information and know who is doing the action
     */
    private void getUserAdmin(){
        String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Query query = databaseUser.orderByChild(FireBaseReferences.USER_MAIL_REFERENCE).equalTo(mail);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userAdmin = dataSnapshot.getValue(User.class).getIdUser();
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
