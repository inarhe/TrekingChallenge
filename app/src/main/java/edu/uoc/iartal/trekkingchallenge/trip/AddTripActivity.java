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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddTripActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE = 1;
    private EditText editTextName, editTextDescription, dateEditText, editTextPlace;
    private DatabaseReference databaseTrip, databaseUser;
    private CheckBox checkBox;
    private String userAdmin, name, idTrip, dateFormat;
    private FirebaseAuth firebaseAuth;
    private Intent intent;
    private DatePickerDialog datePickerDialog;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    ListUsersActivity listUsersActivity;
    private DatePickerDialog.OnDateSetListener date;
    ArrayList<User> userMembers = new ArrayList<>();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addTripToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addTripActivity));

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseTrip = FirebaseDatabase.getInstance().getReference("trip");

        dateSelected = Calendar.getInstance();
        dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

        editTextName = (EditText) findViewById(R.id.etNameTrip);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionTrip);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        editTextPlace = (EditText) findViewById(R.id.etPlace);
        checkBox = (CheckBox) findViewById(R.id.cBPublicTrip);

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

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            String mail = firebaseAuth.getCurrentUser().getEmail();
            databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(mail);

            // Query database to get user admin information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    userAdmin = dataSnapshot.getValue(User.class).getIdUser();
                 //   userKey = dataSnapshot.getValue(User.class).getIdUser();
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

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDate() {
        dateEditText.setText(sdf.format(dateSelected.getTime()));
    }

    public void addTrip (View view) {
        // Add group to database when accept button is clicked
        Boolean isPublic = false;
        name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String place = editTextPlace.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.hintNameGroup), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.hintDescription), Toast.LENGTH_LONG).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Add group to firebase database
      //  idGroup = databaseGroup.push().s.getKey();
        idTrip = name;
        Trip trip = new Trip(idTrip, name, description, date, place, isPublic, userAdmin, 1);

        databaseTrip.child(idTrip).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(AddTripActivity.this, getString(R.string.failedAddGroup), Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseTrip.child(idTrip).child(FireBaseReferences.MEMBERSTRIP_REFERENCE).child(userAdmin).setValue("true");

        databaseUser.child(userAdmin).child(FireBaseReferences.USERTRIPS_REFERENCE).child(trip.getTripName()).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.groupSaved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddTripActivity.this,getString(R.string.failedAddGroup),Toast.LENGTH_SHORT).show();
                }
            }
        });


        inviteUsers();


    }

    public void cancelTrip (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void inviteUsers (){
        intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("TripName", name);
        intent.putExtra("idtrip", idTrip);
        startActivity(intent);
        finish();
    }
}
