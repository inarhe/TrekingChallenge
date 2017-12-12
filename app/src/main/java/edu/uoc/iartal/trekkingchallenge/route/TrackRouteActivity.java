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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class TrackRouteActivity extends AppCompatActivity {

    private ImageView imageViewTrack, imageViewProfile;
    private TextView textViewLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_route);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        Route route = bundle.getParcelable("route");

        Toolbar toolbar = (Toolbar) findViewById(R.id.trackRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trackActivity));

        imageViewTrack = (ImageView) findViewById(R.id.ivTrack);
        imageViewProfile = (ImageView) findViewById(R.id.ivProfile);
        textViewLink = (TextView) findViewById(R.id.tvLinkTracks);
        textViewLink.setText(route.getTrackLink());


        // Get Firebase authentication instance and database group reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        String photoTrack = route.getTrackPhoto();
        String photoProfile = route.getProfilePhoto();

        storageReference.child(FireBaseReferences.TRACKS_STORAGE + photoTrack).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(TrackRouteActivity.this).load(uri).into(imageViewTrack);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TrackRouteActivity.this, R.string.notDownloaded, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
