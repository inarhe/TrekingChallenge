package edu.uoc.iartal.trekkingchallenge.route;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Rating;
import edu.uoc.iartal.trekkingchallenge.adapter.RatingAdapter;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class RatingRouteActivity extends AppCompatActivity {

    private ArrayList<Rating> ratings;
    private Route route;
    private Dialog rateDialog;
    private DatabaseReference databaseRating, databaseUser, databaseRoute;
    private String idRate;
    private User currentUser;
    private RatingAdapter ratingAdapter;
    private RatingBar ratingBar;
    private Boolean isRated = false;
    private EditText editTextTitle, editTextBody;
    private ProgressDialog progressDialog;
    private FirebaseController controller;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_route);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.ratingRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.ratingActivity));

        // Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(this);
        ratings = new ArrayList<>();
        ratingAdapter = new RatingAdapter(ratings);
        rateDialog = new Dialog(this);
        context = this;

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get route data from show route activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Initialize and set recycler view with its adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRatings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Get database references
        databaseRating = controller.getDatabaseReference(FireBaseReferences.RATING_REFERENCE);
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseRoute = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Set rate dialog
        rateDialog.setContentView(R.layout.rating_route_dialog);
        rateDialog.setCancelable(true);

        // Define floating button for adding new rating
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddRate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRating();
                rateRoute();
            }
        });

        // Show route ratings in layout
        getRouteRatings();
        // Get current user
        getCurrentUser();
        // Search user ratings
        searchRating();
    }

    /**
     * Opens rating dialog when rate button is clicked. Save ratting and comment too.
     */
    public void rateRoute(){
        // Show rate dialog when rate button is clicked
        rateDialog.show();

        // Initialize and set dialog layout elements
        Button registerRateButton = (Button) rateDialog.findViewById(R.id.bAcceptRate);
        Button cancelRateButton = (Button) rateDialog.findViewById(R.id.bCancelRate);
        TextView textViewTitleRate = (TextView) rateDialog.findViewById(R.id.tvTitleRate);
        ratingBar = (RatingBar) rateDialog.findViewById(R.id.rbRateRoute);
        editTextTitle = (EditText) rateDialog.findViewById(R.id.etCommentTitle);
        editTextBody = (EditText) rateDialog.findViewById(R.id.etCommentBody);

        ratingBar.setRating(0);
        textViewTitleRate.setText(route.getName());
        editTextTitle.setText("");
        editTextBody.setText("");

        // Save rating and updates all its dependencies
        registerRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only one user rating
                if (isRated){
                    Toast.makeText(getApplicationContext(),getString(R.string.alreadyRated),Toast.LENGTH_SHORT).show();
                } else {
                    // Get comment from input parameter values
                    String title = editTextTitle.getText().toString().trim();
                    String body = editTextBody.getText().toString().trim();

                    // Check input parameters. If some parameter is incorrect or empty, stops the function execution
                    if(TextUtils.isEmpty(title)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.adviceTitle), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(TextUtils.isEmpty(body)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.adviceBody), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add rating to firebase database
                    idRate = controller.getFirebaseNewKey(databaseRating);

                    if (idRate == null){
                        Toast.makeText(getApplicationContext(), R.string.finishedFailed, Toast.LENGTH_SHORT).show();
                    } else {
                        Rating rating = new Rating(idRate, title, body, route.getIdRoute(), currentUser.getId(), ratingBar.getRating());
                        controller.addNewRating(databaseRating, rating, getApplicationContext());

                        // Update rating list in user and route database nodes
                        try{
                            controller.updateStringParameter(databaseUser, currentUser.getId(), FireBaseReferences.USER_RATINGS_REFERENCE, idRate);
                            controller.updateStringParameter(databaseRoute, route.getIdRoute(), FireBaseReferences.ROUTE_RATINGS_REFERENCE, idRate);
                            controller.updateIntParameter(databaseRoute, route.getIdRoute(), FireBaseReferences.ROUTE_NUM_RATINGS_REFERENCE, route.getNumRatings() + 1);
                            controller.updateFloatParameter(databaseRoute, route.getIdRoute(), FireBaseReferences.ROUTE_SUM_RATINGS_REFERENCE, route.getSumRatings() + ratingBar.getRating());
                            Toast.makeText(getApplicationContext(), R.string.rateSaved, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.rateNotSaved, Toast.LENGTH_SHORT).show();
                        }


                    }

                    rateDialog.dismiss();
                }
            }
        });

        // Dialog cancel button listener. Rating is not saved when this button is clicked
        cancelRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });
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
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListRating getUsr error", databaseError.getMessage());
            }
        });
    }

    /**
     * Show database route ratings in recycler view
     */
    private void getRouteRatings(){
        controller.readData(databaseRating, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                ratings.clear();
                for (DataSnapshot ratingSnapshot : data.getChildren()) {
                    Rating rate = ratingSnapshot.getValue(Rating.class);
                    if (rate.getRoute().equals(route.getIdRoute())){
                        ratings.add(rate);
                    }
                }
                ratingAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListRatings error", databaseError.getMessage());
            }
        });
    }

    /**
     * Search if current user has already rated the current route
     */
    private void searchRating(){
        controller.readDataOnce(databaseRating, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot ratingSnapshot : data.getChildren()){
                    Rating rate = ratingSnapshot.getValue(Rating.class);
                    if (rate.getRoute().equals(route.getName()) && rate.getUser().equals(currentUser.getId())){
                        isRated = true;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("SearchRating error", databaseError.getMessage());
            }
        });
    }
}
