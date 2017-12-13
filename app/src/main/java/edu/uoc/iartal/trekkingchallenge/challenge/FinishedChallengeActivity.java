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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class FinishedChallengeActivity extends AppCompatActivity {
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private String user;
    private EditText editTextDate, editTextDist, editTextHour, editTextMinute;
    private Context context = this;
    private DatePickerDialog.OnDateSetListener date;
    private Challenge challenge;
    private DatabaseReference databaseUser, databaseResult, databaseChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_challenge);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from show challenge activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.finishedChallengeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.finishChallengeActivity);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get database references
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        // Link layout elements with variables
        editTextDate = (EditText) findViewById(R.id.etDateFinish);
        editTextDist = (EditText) findViewById(R.id.etDistFinish);
        editTextHour = (EditText) findViewById(R.id.etHourFinish);
        editTextMinute = (EditText) findViewById(R.id.etMinFinish);

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
        String idResult;
        String finishDate = editTextDate.getText().toString().trim();
        String finishDist = editTextDist.getText().toString().trim();
        String finishHour = editTextHour.getText().toString().trim();
        String finishMin = editTextMinute.getText().toString().trim();

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

        // Add challenge result to firebase database
        idResult = databaseChallenge.push().getKey();
        ChallengeResult challengeResult = new ChallengeResult(idResult, Double.parseDouble(finishDist), Integer.parseInt(finishHour), Integer.parseInt(finishMin), user, challenge.getId(), finishDate);

        databaseResult.child(idResult).setValue(challengeResult).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(FinishedChallengeActivity.this, getString(R.string.finishedSaved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FinishedChallengeActivity.this, getString(R.string.finishedFailed), Toast.LENGTH_LONG).show();
                }
            }
        });

        CommonFunctionality common = new CommonFunctionality();

        // Update result list in user and challenge database nodes
        common.updateResults (databaseUser, user, FireBaseReferences.USER_RESULT_REFERENCE, idResult, context);
        common.updateResults(databaseChallenge, challenge.getId(), FireBaseReferences.CHALLENGE_FINISHED_REFERENCE,idResult, context);

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
}
