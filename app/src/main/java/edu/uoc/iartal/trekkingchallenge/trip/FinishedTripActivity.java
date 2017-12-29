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
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class FinishedTripActivity extends AppCompatActivity {
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private User user;
    private EditText editTextDate, editTextDist, editTextHour;
    private Context context = this;
    private DatePickerDialog.OnDateSetListener date;
    private Trip trip;
    private DatabaseReference databaseUser, databaseTripsDone, databaseTrip, databaseHistory, databaseRoute;
    private Double historyTime, historyDistance;
    private int historySlope, routeSlope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_trip);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from show challenge activity
        Bundle bundle = getIntent().getExtras();
        trip = bundle.getParcelable("trip");

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.finishedTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.finishTripActivity);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get database references
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseTripsDone = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIPSDONE_REFERENCE);
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);
        databaseHistory = FirebaseDatabase.getInstance().getReference(FireBaseReferences.HISTORY_REFERENCE);
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Link layout elements with variables
        editTextDate = (EditText) findViewById(R.id.etDateFinish);
        editTextDist = (EditText) findViewById(R.id.etDistFinish);
        editTextHour = (EditText) findViewById(R.id.etHourFinish);

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        // Click listener on date edit text to show calendar
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getUserAdmin();
    }


    /**
     * Save user challenge result in database and update dependencies
     * @param view
     */
    public void registerFinish(View view){
        //Initialize variables
        String idDone;
        String finishDate = editTextDate.getText().toString().trim();
        String finishDist = editTextDist.getText().toString().trim();
        String finishHour = editTextHour.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(finishDist)) {
            Toast.makeText(this, getString(R.string.distAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(finishHour)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        // Add challenge result to firebase database
        idDone = databaseTripsDone.push().getKey();
        TripDone tripsDone = new TripDone(idDone, Double.parseDouble(finishDist), Double.parseDouble(finishHour), user.getId(), trip.getId(), finishDate, trip.getName(), trip.getRoute());

        databaseTripsDone.child(idDone).setValue(tripsDone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(FinishedTripActivity.this, getString(R.string.finishedSaved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FinishedTripActivity.this, getString(R.string.finishedFailed), Toast.LENGTH_LONG).show();
                }
            }
        });

        CommonFunctionality common = new CommonFunctionality();

        // Update result list in user and challenge database nodes
        common.updateResults (databaseUser, user.getId(), FireBaseReferences.USER_TRIPSDONE_REFERENCE, idDone, context);
        common.updateResults(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DONE_REFERENCE,idDone, context);

        databaseHistory.child(user.getHistory()).child(FireBaseReferences.HISTORY_DISTANCE_REFERENCE).setValue(historyDistance + Double.parseDouble(finishDist));
        databaseHistory.child(user.getHistory()).child(FireBaseReferences.HISTORY_TIME_REFERENCE).setValue(historyTime + Double.parseDouble(finishHour));

        Query query = databaseRoute.orderByChild(FireBaseReferences.ROUTE_NAME_REFERENCE).equalTo(trip.getRoute());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    routeSlope = route.getAscent() + route.getDecline();
                }
                databaseHistory.child(user.getHistory()).child(FireBaseReferences.HISTORY_SLOPE_REFERENCE).setValue(historySlope + routeSlope);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        finish();
    }

    /**
     * Cancel result registration when cancel button is clicked
     * @param view
     */
    public void cancelFinish (View view){
        finish();
    }

    /**
     * Set current date format and DatePickerDialog
     */
    private void setDate(){
        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        editTextDate.setText(dateString);

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
        editTextDate.setText(sdf.format(dateSelected.getTime()));
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
                user = dataSnapshot.getValue(User.class);
                getHistoryValues();
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

    /**
     * Query database to get current user information and know who is doing the action
     */
    private void getHistoryValues(){
        Query query = databaseHistory.orderByChild(FireBaseReferences.HISTORY_ID_REFERENCE).equalTo(user.getHistory());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                    History history = historySnapshot.getValue(History.class);
                    historyDistance = history.getTotalDistance();
                    historyTime = history.getTotalTime();
                    historySlope = history.getTotalSlope();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
