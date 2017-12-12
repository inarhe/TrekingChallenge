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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import edu.uoc.iartal.trekkingchallenge.PhotoGalleryActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.FinishedChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Finished;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.trip.AddTripActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowRouteActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE = 1;
    private FirebaseAuth firebaseAuth;
    //private String groupKey, name;
    private DatabaseReference databaseRoute;
    private StorageReference storageReference;
    private ImageView imageViewHeader, imageViewSeason, imageViewType;
    private TextView textViewType, textViewTime, textViewAscent, textViewDecline,textViewSeason;
    private TextView textViewDifficult, textViewRegion, textViewTownship;
    private Route route;
    private Bundle bundle;
    private List<Finished> finished;
    private List<String> keys;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

        // Get data from item clicked on list groups activity
        bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        Toolbar toolbar = (Toolbar) findViewById(R.id.showRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(route.getName());

        imageViewHeader = (ImageView) findViewById(R.id.ivRoute);
        imageViewSeason = (ImageView) findViewById(R.id.icSeason);
        imageViewType = (ImageView) findViewById(R.id.icType);
        textViewType = (TextView) findViewById(R.id.tvType);
        textViewTime = (TextView) findViewById(R.id.tvTime);
        textViewAscent = (TextView) findViewById(R.id.tvAscent);
        textViewDecline = (TextView) findViewById(R.id.tvDecline);
        textViewSeason = (TextView) findViewById(R.id.tvSeason);
        textViewDifficult = (TextView) findViewById(R.id.tvDifficult);
        textViewTownship = (TextView) findViewById(R.id.tvTownShip);
        textViewRegion = (TextView) findViewById(R.id.tvRegion);
        final TextView textViewCalendar = (TextView) findViewById(R.id.tvCalenadar);

        // Get Firebase authentication instance and database group reference
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);



        if (route.getSeason().equals(getString(R.string.spring))){
            imageViewSeason.setImageResource(R.drawable.ic_spring);
        } else if (route.getSeason().equals(getString(R.string.fall))){
            imageViewSeason.setImageResource(R.drawable.ic_fall);
        } else if (route.getSeason().equals(getString(R.string.summer))) {
            imageViewSeason.setImageResource(R.drawable.ic_summer);
        } else if (route.getSeason().equals(getString(R.string.winter))) {
            imageViewSeason.setImageResource(R.drawable.ic_winter);
        }

        if (route.getType().equals(R.string.circular)){
            imageViewType.setImageResource(R.drawable.ic_circular);
        } else {
            imageViewType.setImageResource(R.drawable.ic_goback);
        }

    /*    mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        keys = new ArrayList<>();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keys.removeAll(keys);
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(mail)) {
                        String currentUser = user.getIdUser();
                        for (String key : user.getFinished().keySet()) {
                            keys.add(key);
                        }
                    }

                }
                for(String key:keys){
                    if (key.equals(route.getIdRoute())){
                        textViewCalendar.setText();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });*/




        //  textViewDescription.setText(description);
        //textViewMembers.setText(Integer.toString(members));

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

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
                Toast.makeText(ShowRouteActivity.this, "image not dowloaded", Toast.LENGTH_SHORT).show();
            }
        });

        textViewType.setText(route.getDistance() + " km");
        textViewTime.setText(route.getTime());
        textViewAscent.setText(route.getAscent());
        textViewDecline.setText(route.getDecline());
        textViewSeason.setText(route.getSeason());
        textViewDifficult.setText(route.getDifficult());
        textViewRegion.setText(getString(R.string.region) + "  " + route.getRegion());
        textViewTownship.setText(getString(R.string.township) + "  " + route.getTownship());



    }



    public void showDetails (View view){
        Intent intent = new Intent(this, DetailRouteActivity.class);
        intent.putExtra("route", route);
      //  intent.putExtra("meteo", route.getMeteo());

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

    public void showPhotoGallery () {
        Intent intent = new Intent (this, PhotoGalleryActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_route_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        route = data.getExtras().getParcelable("route");
    }

    public void routeFinished() {
        Intent intent = new Intent(this, FinishedRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    public void newTrip() {
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
        finish();
    }

    public void newChallenge() {
        Intent intent = new Intent(this, FinishedChallengeActivity.class);
        startActivity(intent);
        finish();
    }

    public void setOpinion() {
        Intent intent = new Intent(this, RatingRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }
}
