/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.route;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.ConstantsUtils;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class FinishedRouteActivity extends AppCompatActivity {

    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private User currentUser;
    private Route route;
    private EditText editTextDate, editTextDist, editTextHour, editTextMin;
    private DatabaseReference databaseUser, databaseFinished, databaseHistory;
    private double historyDistance;
    private String finishDist, finishHour, finishMin;
    private int historySlope, historyHour, historyMin;
    private FirebaseController controller;
    private Context context;
    private CommonFunctionality common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_route);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.finishedRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.finishRouteActivity);

        // Initialize variables
        controller = new FirebaseController();
        context = this;
        common = new CommonFunctionality();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get data from show route activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Get database references
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseFinished = controller.getDatabaseReference(FireBaseReferences.FINISHED_REFERENCE);
        databaseHistory = controller.getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);

        // Link layout elements with variables
        editTextDate = (EditText) findViewById(R.id.etDateFinish);
        editTextDist = (EditText) findViewById(R.id.etDistFinish);
        editTextHour = (EditText) findViewById(R.id.etHourFinish);
        editTextMin = (EditText) findViewById(R.id.etMinFinish);

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        getCurrentUser();

        // Click listener on date edit text to show calendar
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    /**
     * Save user route result in database and update dependencies
     * @param view
     */
    public void registerFinish(View view){
        //Initialize variables
        String idFinish;
        String finishDate = editTextDate.getText().toString().trim();
        finishDist = editTextDist.getText().toString().trim();
        finishHour = editTextHour.getText().toString().trim();
        finishMin = editTextMin.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(finishDist)) {
            Toast.makeText(this, getString(R.string.distAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!common.validateDistance(finishDist)) {
            Toast.makeText(this, getString(R.string.distanceFormat), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(finishHour)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(finishMin)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        // Add route result to firebase database
        idFinish = controller.getFirebaseNewKey(databaseFinished);

        if (idFinish == null) {
            Toast.makeText(getApplicationContext(), R.string.finishedFailed, Toast.LENGTH_SHORT).show();
        } else {
            Finished finished = new Finished(idFinish, currentUser.getId(), route.getIdRoute(), finishDate, Double.parseDouble(finishDist), Integer.parseInt(finishHour), Integer.parseInt(finishMin), route.getName());
            controller.addNewRouteResult(databaseFinished, finished, currentUser.getId(), route.getIdRoute(), context);

            updateHistory();
        }

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
     * Get current user information and get his history values
     */
    private void getCurrentUser(){
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
                        getHistoryValues();
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("FinRoute getUsr error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get user history values
     */
    private void getHistoryValues(){

        controller.readDataOnce(databaseHistory, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot historySnapshot : data.getChildren()) {
                    History history = historySnapshot.getValue(History.class);
                    if (history.getId().equals(currentUser.getHistory())){
                        historyDistance = history.getTotalDistance();
                        historyHour = history.getTotalHour();
                        historyMin = history.getTotalMin();
                        historySlope = history.getTotalSlope();
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("FinRoute getHist error", databaseError.getMessage());
            }
        });
    }

    /**
     * Update user history with route results.
     */
    private void updateHistory(){
        int totalSlope = historySlope + route.getAscent() + route.getDecline();
        double totalDistance = common.round(historyDistance + Double.parseDouble(finishDist), ConstantsUtils.NUM_OF_DECIMALS);
        ArrayList<Integer> time = common.sumTime(historyHour, Integer.parseInt(finishHour), historyMin, Integer.parseInt(finishMin));

        // Update user history with trip values
        try{
            controller.updateIntParameter(databaseHistory, currentUser.getHistory(), FireBaseReferences.HISTORY_SLOPE_REFERENCE, totalSlope);
            controller.updateDoubleParameter(databaseHistory, currentUser.getHistory(), FireBaseReferences.HISTORY_DISTANCE_REFERENCE, totalDistance);
            controller.updateIntParameter(databaseHistory, currentUser.getHistory(), FireBaseReferences.HISTORY_HOUR_REFERENCE, time.get(0));
            controller.updateIntParameter(databaseHistory, currentUser.getHistory(), FireBaseReferences.HISTORY_MIN_REFERENCE, time.get(1));
        }catch (Exception e){
            e.printStackTrace();
            Log.e("FinRoute upHist error", e.getMessage());
        }
    }
}
