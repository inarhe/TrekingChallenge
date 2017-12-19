package edu.uoc.iartal.trekkingchallenge.challenge;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Challenge;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditChallengeActivity extends AppCompatActivity {
    private DatabaseReference databaseChallenge;
    private Challenge challenge;
    private EditText editTextName, editTextDescription, dateEditText;
    private ImageView buttonCalendar;
    private ProgressDialog progressDialog;
    private Boolean updateName, updateDate, updateDesc;
    private String newName, newDescription, newDate;
    private Context context = this;
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

        // Get database references
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Initialize variables
        progressDialog = new ProgressDialog(this);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etChallengeName);
        editTextDescription = (EditText) findViewById(R.id.etChallengeDesc);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        buttonCalendar = (ImageView) findViewById(R.id.bDate);

        // Get data from item clicked on list challenges activity
        Bundle bundle = getIntent().getExtras();
        challenge = bundle.getParcelable("challenge");

        // Show selected challemge information in the layout
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
        updateName = false;
        updateDesc = false;
        updateDate = false;

        // Get input parameters
        newName = editTextName.getText().toString().trim();
        newDescription = editTextDescription.getText().toString().trim();
        newDate = dateEditText.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, R.string.nameField, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(this, R.string.descriptionField, Toast.LENGTH_LONG).show();
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
        if (updateName) {
            databaseChallenge.child(challenge.getId()).child(FireBaseReferences.CHALLENGE_NAME_REFERENCE).setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (updateDesc){
                                    updateDescriptionValue();
                                    if (updateDate){
                                        updateDateValue();
                                    }
                                } else if (updateDate){
                                    updateDateValue();
                                }
                                Toast.makeText(getApplicationContext(), R.string.editChallengeOk, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.editChallengeFail, Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
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
            progressDialog.dismiss();
            finish();
        }
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
        databaseChallenge.child(challenge.getId()).child(FireBaseReferences.CHALLENGE_DESCRIPTION_REFERENCE).setValue(newDescription)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (updateDate){
                               updateDateValue();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.editChallengeOk, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.editChallengeFail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Update challenge date into database
     */
    private void updateDateValue(){
        databaseChallenge.child(challenge.getId()).child(FireBaseReferences.CHALLENGE_DATE_REFERENCE).setValue(newDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), R.string.editChallengeOk, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.editChallengeFail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Cancel edit challenge if cancel button is clicked
     * @param view
     */
    public void cancelEditChallenge (View view) {
        finish();
    }
}
