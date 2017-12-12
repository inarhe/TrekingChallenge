package edu.uoc.iartal.trekkingchallenge.challenge;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class FinishedChallengeActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private String dateFormat, user;
    private EditText editTextDate, editTextDist, editTextHour, editTextMinute;
    private Context context = this;
    private DatePickerDialog.OnDateSetListener date;
    private Challenge challenge;
    private DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_challenge);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        Toolbar toolbar = (Toolbar) findViewById(R.id.finishedChallengeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.finishChallengeActivity);

        // Get Firebase authentication instance

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }

        dateSelected = Calendar.getInstance();
        dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

        editTextDate = (EditText) findViewById(R.id.etDateFinish);
        editTextDist = (EditText) findViewById(R.id.etDistFinish);
        editTextHour = (EditText) findViewById(R.id.etHourFinish);
        editTextMinute = (EditText) findViewById(R.id.etMinFinish);

        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        editTextDate.setText(dateString);

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

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(mail);

        // Query database to get user admin information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class).getIdUser();

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

    private void updateDate() {
        editTextDate.setText(sdf.format(dateSelected.getTime()));
    }

    public void registerFinish(View view){
        String idResult;
        String finishDate = editTextDate.getText().toString().trim();
        String finishDist = editTextDist.getText().toString().trim();
        String finishHour = editTextHour.getText().toString().trim();
        String finishMin = editTextMinute.getText().toString().trim();
        final DatabaseReference databaseResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(finishDist)) {
            Toast.makeText(this, getString(R.string.distAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(finishHour)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(finishMin)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_LONG).show();
            return;
        }



        // Add group to firebase database
        //  idGroup = databaseGroup.push().s.getKey();
        try {
            idResult = databaseChallenge.push().getKey();
            ChallengeResult challengeResult = new ChallengeResult(idResult, finishDist, finishHour, finishMin, user, challenge.getIdChallenge(), finishDate);


            databaseResult.child(idResult).setValue(challengeResult).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(FinishedChallengeActivity.this, getString(R.string.finishedSaved), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FinishedChallengeActivity.this, getString(R.string.finishedFailed), Toast.LENGTH_SHORT).show();
                       // finish();
                    }
                }
            });

            databaseUser.child(user).child(FireBaseReferences.USERRESULT_REFERENCE).child(idResult).setValue("true")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, getString(R.string.tripSaved), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context,getString(R.string.failedAddTrip),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            databaseChallenge.child(challenge.getIdChallenge()).child(FireBaseReferences.CHALLENGEFINISHED_REFERENCE).child(idResult).setValue("true")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, getString(R.string.tripSaved), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context,getString(R.string.failedAddTrip),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        finish();









    }

    public void cancelFinish (View view){
        finish();
    }
}
