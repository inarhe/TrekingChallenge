package edu.uoc.iartal.trekkingchallenge.route;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.Rating;
import edu.uoc.iartal.trekkingchallenge.adapter.RatingAdapter;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class RatingRouteActivity extends AppCompatActivity {

    private ArrayList<Rating> ratings = new ArrayList<>();
    private Route route;
    private Dialog rateDialog;
    private DatabaseReference databaseRating, databaseUser, databaseRoute;
    private String userName, idRate;
    private RatingAdapter ratingAdapter;
    private RatingBar ratingBar;
    private Boolean isRated = false;
    private EditText editTextTitle, editTextBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_route);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.ratingRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.ratingActivity));

        // Get route data from show route activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Initialize and set recycler view with its adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRatings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingAdapter = new RatingAdapter(ratings);
        recyclerView.setAdapter(ratingAdapter);

        // Initialize database references
        databaseRating = FirebaseDatabase.getInstance().getReference(FireBaseReferences.RATING_REFERENCE);
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Initialize and set rate dialog
        rateDialog = new Dialog(this);
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

        // Show database route ratings in recycler view
        databaseRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ratings.clear();
                for (DataSnapshot ratingSnapshot:
                        dataSnapshot.getChildren()) {
                    Rating rate = ratingSnapshot.getValue(Rating.class);
                    if (rate.getRoute().equals(route.getIdRoute())){
                        ratings.add(rate);
                    }
                }
                ratingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });

        // Get current user name
        getCurrentUserName();
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
        ratingBar = (RatingBar) rateDialog.findViewById(R.id.rbRateRoute);
        ratingBar.setRating(0);
        editTextTitle = (EditText) rateDialog.findViewById(R.id.etCommentTitle);
        editTextBody = (EditText) rateDialog.findViewById(R.id.etCommentBody);
        TextView textViewTitleRate = (TextView) rateDialog.findViewById(R.id.tvTitleRate);
        textViewTitleRate.setText(route.getName());
        editTextTitle.setText("");
        editTextBody.setText("");

        // Save rating and updates all its dependencies
        registerRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only one user rating
                if (isRated){
                    Toast.makeText(getApplicationContext(),getString(R.string.alreadyRated),Toast.LENGTH_LONG).show();
                } else {
                    // Get comment from input parameter values
                    String title = editTextTitle.getText().toString().trim();
                    String body = editTextBody.getText().toString().trim();

                    // If some of the input parameters are incorrect or empty, stops the function execution further
                    if(TextUtils.isEmpty(title)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.adviceTitle), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(TextUtils.isEmpty(body)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.adviceBody), Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Add rating to database
                    idRate = databaseRating.push().getKey();
                    Rating newRate = new Rating(idRate, title, body, route.getIdRoute(), userName, ratingBar.getRating());

                    databaseRating.child(idRate).setValue(newRate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                updateRouteDependencies();
                                updateUserDependencies();
                                Toast.makeText(getApplicationContext(), getString(R.string.rateSaved), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),getString(R.string.rateNotSaved),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
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
     * Search the name of current user, to know who is doing the action
     */
    private void getCurrentUserName(){
        // Get current user mail
        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Query database to find user who has the current mail
        Query query = databaseUser.orderByChild(FireBaseReferences.USER_MAIL_REFERENCE).equalTo(currentMail);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userName = dataSnapshot.getValue(User.class).getId();
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
     * Updates rating list, rate average and number of ratings in the current route object
     */
    private void updateRouteDependencies(){
        databaseRoute.child(route.getIdRoute()).child(FireBaseReferences.ROUTE_RATINGS_REFERENCE).child(idRate).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    int numRatings = route.getNumRatings()+1;
                    Float averageRoute = route.getRatingAverage();
                    Float average = averageRoute + ratingBar.getRating();
                    databaseRoute.child(route.getIdRoute()).child(FireBaseReferences.ROUTE_NUM_RATINGS_REFERENCE).setValue(numRatings);
                    databaseRoute.child(route.getIdRoute()).child(FireBaseReferences.ROUTE_RATING_AVERAGE_REFERENCE).setValue(average/numRatings);
                }
            }
        });
    }

    /**
     * Updates rating list in the current user object
     */
    private void updateUserDependencies(){
        databaseUser.child(userName).child(FireBaseReferences.USER_RATINGS_REFERENCE).child(idRate).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), getString(R.string.rateSaved), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),getString(R.string.rateNotSaved),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Search if current user has already rated the current route
     */
    private void searchRating(){
        databaseRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()){
                    Rating rate = ratingSnapshot.getValue(Rating.class);
                    if (rate.getRoute().equals(route.getName()) && rate.getUser().equals(userName)){
                        isRated = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
