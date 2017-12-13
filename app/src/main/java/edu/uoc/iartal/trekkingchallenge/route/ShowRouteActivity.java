package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.AddChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.challenge.FinishedChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.trip.AddTripActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowRouteActivity extends AppCompatActivity {
    private StorageReference storageReference;
    private ImageView imageViewHeader;
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from item clicked on list routes activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(route.getName());

        // Get storage and database references
        storageReference = FirebaseStorage.getInstance().getReference();

        // Link layout elements with variables
        imageViewHeader = (ImageView) findViewById(R.id.ivRoute);
        ImageView imageViewSeason = (ImageView) findViewById(R.id.icSeason);
        ImageView imageViewType = (ImageView) findViewById(R.id.icType);
        TextView textViewType = (TextView) findViewById(R.id.tvType);
        TextView textViewTime = (TextView) findViewById(R.id.tvTime);
        TextView textViewAscent = (TextView) findViewById(R.id.tvAscent);
        TextView textViewDecline = (TextView) findViewById(R.id.tvDecline);
        TextView textViewSeason = (TextView) findViewById(R.id.tvSeason);
        TextView textViewDifficult = (TextView) findViewById(R.id.tvDifficult);
        TextView textViewTownship = (TextView) findViewById(R.id.tvTownShip);
        TextView textViewRegion = (TextView) findViewById(R.id.tvRegion);

        // Set season icon according to route season
        if (route.getSeason().equals(getString(R.string.spring))){
            imageViewSeason.setImageResource(R.drawable.ic_spring);
        } else if (route.getSeason().equals(getString(R.string.fall))){
            imageViewSeason.setImageResource(R.drawable.ic_fall);
        } else if (route.getSeason().equals(getString(R.string.summer))) {
            imageViewSeason.setImageResource(R.drawable.ic_summer);
        } else if (route.getSeason().equals(getString(R.string.winter))) {
            imageViewSeason.setImageResource(R.drawable.ic_winter);
        }

        // Set type icon according to route season
        if (route.getType().equals(getString(R.string.circular))){
            imageViewType.setImageResource(R.drawable.ic_circular);
        } else {
            imageViewType.setImageResource(R.drawable.ic_goback);
        }

        // Get header photo name, download it and set into show route
        String namePhoto = route.getHeaderPhoto();
        storageReference.child(FireBaseReferences.HEADERS_STORAGE + namePhoto).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ShowRouteActivity.this).load(uri).into(imageViewHeader);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowRouteActivity.this, R.string.imageNotDonwloaded, Toast.LENGTH_LONG).show();
            }
        });

        // Show selected route information in the layout
        textViewType.setText(route.getDistance() + " km");
        textViewTime.setText(route.getTime());
        textViewAscent.setText(route.getAscent());
        textViewDecline.setText(route.getDecline());
        textViewSeason.setText(route.getSeason());
        textViewDifficult.setText(route.getDifficult());
        textViewRegion.setText(getString(R.string.region) + "  " + route.getRegion());
        textViewTownship.setText(getString(R.string.township) + "  " + route.getTownship());
    }

    /**
     * Starts detail information route activity when button is clicked
     * @param view
     */
    public void showDetails (View view){
        Intent intent = new Intent(this, DetailRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Show track and profile images when button is clicked
     * @param view
     */
    public void showTracks (View view) {
        Intent intent = new Intent(this, TrackRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    //TO-DO
    public void showPhotoGallery () {
        Intent intent = new Intent (this, PhotoGalleryActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_route, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finished:
                routeFinished();
                return true;
            case R.id.action_trip:
                newTrip();
                return true;
            case R.id.action_challenge:
                newChallenge();
                return true;
            case R.id.action_opinion:
                setOpinion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts finished route activity when menu option is selected
     */
    public void routeFinished() {
        Intent intent = new Intent(this, FinishedRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Starts new trip activity when menu option is selected
     */
    public void newTrip() {
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Starts new challenge activity when menu option is selected
     */
    public void newChallenge() {
        Intent intent = new Intent(this, AddChallengeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Starts opinion activity when menu option is selected
     */
    public void setOpinion() {
        Intent intent = new Intent(this, RatingRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }
}
