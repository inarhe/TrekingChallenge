package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetPhotoListener;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class TrackRouteActivity extends AppCompatActivity {
    private ImageView imageViewTrack, imageViewProfile;
    private FirebaseController controller;
    private  Route route;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_route);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trackRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trackActivity));

        // Get storage reference
        storageReference = controller.getStorageReference();

        // Get data from show route activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Link layout elements with variables and set values
        imageViewTrack = (ImageView) findViewById(R.id.ivTrack);
        imageViewProfile = (ImageView) findViewById(R.id.ivProfile);
        TextView textViewLink = (TextView) findViewById(R.id.tvLinkTracks);
        textViewLink.setText(route.getTrackLink());

        // Set images from storage into view
        showTrackPhoto();
        showProfilePhoto();
    }

    /**
     * Get track photo name, download it and set into track route
     */
    private void showTrackPhoto() {
        String photoTrack = route.getTrackPhoto();

        controller.readPhoto(storageReference, FireBaseReferences.TRACKS_STORAGE + photoTrack, new OnGetPhotoListener() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(TrackRouteActivity.this).load(uri).into(imageViewTrack);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Get profile photo name, download it and set into track route
     */
    private void showProfilePhoto() {
        String photoProfile = route.getProfilePhoto();

        controller.readPhoto(storageReference, FireBaseReferences.PROFILES_STORAGE + photoProfile, new OnGetPhotoListener() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(TrackRouteActivity.this).load(uri).into(imageViewProfile);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
