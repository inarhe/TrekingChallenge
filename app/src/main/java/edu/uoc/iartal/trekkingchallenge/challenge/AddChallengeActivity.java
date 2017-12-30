package edu.uoc.iartal.trekkingchallenge.challenge;

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
import android.widget.RadioGroup;
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

import edu.uoc.iartal.trekkingchallenge.common.ConstantsUtils;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddChallengeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTextName, editTextDescription, dateEditText;
    private DatabaseReference databaseChallenge, databaseUser, databaseRoute;
    private CheckBox checkBox;
    private Spinner spinner;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private String idChallenge, name, challengeDate, description, classification, route;
    private User currentUser;
    private Challenge challenge;
    private ArrayList<String> nameRoutes;
    private Context context;
    private ArrayAdapter<String> spinnerAdapter;
    private boolean isPublic, challengeExists;
    private RadioGroup rgClassification;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addChallengeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addChallengeActivity));

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
        databaseChallenge = controller.getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseRoute = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etNameChallenge);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionChallenge);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        checkBox = (CheckBox) findViewById(R.id.cBPublicChallenge);
        spinner = (Spinner) findViewById(R.id.spinnerRoute);
        ImageView buttonCalendar = (ImageView) findViewById(R.id.bDate);
        rgClassification = (RadioGroup) findViewById(R.id.rgClassification);
        rgClassification.check(R.id.rbClassificationTime);

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        // Get routes to inflate spinner
        getRoutes();
        spinner.setOnItemSelectedListener(this);

        getUserAdmin();

        // Click listener on calendar icon text to show calendar
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
     * Add challenge to database when accept button is clicked
     * @param view
     */
    public void addChallenge (View view) {
        // Initialize variables with input parameters
        isPublic = false;
        challengeExists = false;
        name = editTextName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        challengeDate = dateEditText.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (route == null) {
            Toast.makeText(this, getString(R.string.chooseRoute), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Get which classification radio button is checked
        switch (rgClassification.getCheckedRadioButtonId()){
            case R.id.rbClassificationTime:
                classification = getString(R.string.time);
                break;
            case R.id.rbClassificationDist:
                classification = getString(R.string.distance);
                break;
        }

        // Execute controller method to get database challenges objects. Use OnGetDataListener interface to know
        // when database data is retrieved and search if challenge already exists
        controller.readDataOnce(databaseChallenge, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot challengeSnapshot : data.getChildren()){
                    Challenge challenge = challengeSnapshot.getValue(Challenge.class);
                    if (challenge.getName().equals(name)){
                        challengeExists = true;
                    }
                }

                // If challenge doesn't exist, executes add challenge controller method
                if (!challengeExists){
                    idChallenge = controller.getFirebaseNewKey(databaseChallenge);
                    if (idChallenge == null){
                        Toast.makeText(getApplicationContext(), R.string.failedAddChallenge, Toast.LENGTH_SHORT).show();
                    } else {
                        challenge = new Challenge(idChallenge, name, description, challengeDate, route, currentUser.getId(), isPublic, ConstantsUtils.DEFAULT_MEMBERS, classification);
                        controller.addNewChallenge(databaseChallenge, challenge, currentUser.getId(), getApplicationContext());

                        // Select users that admin wants in the challenge
                        inviteUsers();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),R.string.challAlreadyExists,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddChall error", databaseError.getMessage());
            }
        });
    }

    /**
     * Cancel challenge creation when cancel button is clicked.
     * @param view
     */
    public void cancelChallenge (View view) {
        finish();
    }

    /**
     * Show user list and allow invite users to join the new challenge
     */
    private void inviteUsers (){
        Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("challenge", challenge);
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
                spinnerAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, nameRoutes);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("AddChall getRoute error", databaseError.getMessage());
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
                Log.e("AddChall getAdmin error", databaseError.getMessage());
            }
        });
    }
}
