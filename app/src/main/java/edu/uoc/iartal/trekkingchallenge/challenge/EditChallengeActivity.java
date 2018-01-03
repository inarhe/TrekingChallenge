package edu.uoc.iartal.trekkingchallenge.challenge;

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
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditChallengeActivity extends AppCompatActivity {
    private DatabaseReference databaseChallenge, databaseResults;
    private Challenge challenge;
    private EditText editTextName, editTextDescription, dateEditText;
    private ProgressDialog progressDialog;
    private boolean updateDate, updateDesc, challengeExists;
    private String newName, newDescription, newDate;
    private Context context;
    private FirebaseController controller;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_challenge);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editChallengeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.editChallengeActivityName);

        // Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(this);
        context = this;

        // Get database references
        databaseChallenge = controller.getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        databaseResults = controller.getDatabaseReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etChallengeName);
        editTextDescription = (EditText) findViewById(R.id.etChallengeDesc);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        ImageView buttonCalendar = (ImageView) findViewById(R.id.bDate);

        // Get data from item clicked on list challenges activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        // Show selected challenge information in the layout
        editTextName.setText(challenge.getName());
        editTextDescription.setText(challenge.getDescription());

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
     * Executed when accept button is selected. Verify input parameters and update values
     * @param view
     */
    public void editChallenge (View view) {
        //Initialize variables
        Boolean updateName = false;
        updateDesc = false;
        updateDate = false;
        challengeExists = false;

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

        // Check changes on challenge name parameter
        if (!(challenge.getName()).equals(newName)) {
            updateName = true;
        }

        // Check changes on challenge description parameter
        if ((!(challenge.getDescription()).equals(newDescription))) {
            updateDesc = true;
        }

        // Check changes on challenge date parameter
        if ((!(challenge.getLimitDate()).equals(newDate))) {
            updateDate = true;
        }

        // Update database if its necessary
        if (updateName){
            // Execute controller method to check if challenge name exists and if it doesn't, update database challenge object.
            controller.readDataOnce(databaseChallenge, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //Nothing to do
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    for (DataSnapshot challengeSnapshot : data.getChildren()){
                        Challenge challenge = challengeSnapshot.getValue(Challenge.class);
                        if (challenge.getName().equals(newName)){
                            challengeExists = true;
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.challAlreadyExists, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    // If challenge doesn't exist, executes update parameter
                    if (!challengeExists){
                        controller.updateStringParameter(databaseChallenge, challenge.getId(), FireBaseReferences.CHALLENGE_NAME_REFERENCE, newName);
                        getChallengeResults();
                        if (updateDesc){
                            updateDescriptionValue();
                            if (updateDate) {
                                updateDateValue();
                            }
                        } else if (updateDate) {
                            updateDateValue();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.editChallengeOk, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.e("EditChall error", databaseError.getMessage());
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
     * Cancel edit challenge if cancel button is clicked
     * @param view
     */
    public void cancelEditChallenge (View view) {
        finish();
    }

    /**
     * Set current date format and DatePickerDialog
     */
    private void setDate(){
        dateEditText.setText(challenge.getLimitDate());

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
     * Update challenge description into database
     */
    private void updateDescriptionValue(){
        controller.updateStringParameter(databaseChallenge, challenge.getId(), FireBaseReferences.CHALLENGE_DESCRIPTION_REFERENCE, newDescription);
    }

    /**
     * Update challenge date into database
     */
    private void updateDateValue(){
        controller.updateStringParameter(databaseChallenge, challenge.getId(), FireBaseReferences.CHALLENGE_DATE_REFERENCE, newDate);
    }

    /**
     * Update challenge name of challenge results
     */
    private void getChallengeResults(){
        final ArrayList<String> results = new ArrayList<>();
        results.addAll(challenge.getResults().keySet());

        controller.readDataOnce(databaseResults, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot resultSnapshot : data.getChildren()){
                    ChallengeResult challengeResult = resultSnapshot.getValue(ChallengeResult.class);
                    if (results.contains(challengeResult.getId())){
                        controller.updateStringParameter(databaseResults, challengeResult.getId(), FireBaseReferences.NAME_REFERENCE, newName);
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("Edit result", databaseError.getMessage());
            }
        });
    }
}
