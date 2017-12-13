package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class TrackRouteActivity extends AppCompatActivity {
    private ImageView imageViewTrack, imageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_route);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trackRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trackActivity));

        // Get data from show route activity
        Bundle bundle = getIntent().getExtras();
        Route route = bundle.getParcelable("route");

        // Link layout elements with variables and set values
        imageViewTrack = (ImageView) findViewById(R.id.ivTrack);
        imageViewProfile = (ImageView) findViewById(R.id.ivProfile);
        TextView textViewLink = (TextView) findViewById(R.id.tvLinkTracks);
        textViewLink.setText(route.getTrackLink());

        // Get storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //Initialize variables
        String photoTrack = route.getTrackPhoto();
        String photoProfile = route.getProfilePhoto();

        // Set images from storage into view
        storageReference.child(FireBaseReferences.TRACKS_STORAGE + photoTrack).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(TrackRouteActivity.this).load(uri).into(imageViewTrack);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_LONG).show();
            }
        });

        storageReference.child(FireBaseReferences.PROFILES_STORAGE + photoProfile).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(TrackRouteActivity.this).load(uri).into(imageViewProfile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_LONG).show();
            }
        });
    }
}
