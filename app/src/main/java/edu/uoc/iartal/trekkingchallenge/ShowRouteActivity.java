package edu.uoc.iartal.trekkingchallenge;

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

import org.w3c.dom.Text;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowRouteActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE = 1;
    private FirebaseAuth firebaseAuth;
    //private String groupKey, name;
    private DatabaseReference databaseRoute;
    private StorageReference storageReference;
    private ImageView imageViewHeader;
    private TextView textViewType, textViewTime, textViewAscent, textViewDecline,textViewSeason;
    private TextView textViewDifficult, textViewRegion, textViewTownship;
    private Route route;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

        Toolbar toolbar = (Toolbar) findViewById(R.id.showRouteToolbar);
        setSupportActionBar(toolbar);

        imageViewHeader = (ImageView) findViewById(R.id.ivRoute);
        textViewType = (TextView) findViewById(R.id.tvType);
        textViewTime = (TextView) findViewById(R.id.tvTime);
        textViewAscent = (TextView) findViewById(R.id.tvAscent);
        textViewDecline = (TextView) findViewById(R.id.tvDecline);
        textViewSeason = (TextView) findViewById(R.id.tvSeason);
        textViewDifficult = (TextView) findViewById(R.id.tvDifficult);
        textViewTownship = (TextView) findViewById(R.id.tvTownShip);
        textViewRegion = (TextView) findViewById(R.id.tvRegion);

        // Get Firebase authentication instance and database group reference
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Get data from item clicked on list groups activity
        bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(route.getName());

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

  /*  public void joinGroup () {
        // Add new group member when join group button is clicked
        String currentMail = firebaseAuth.getCurrentUser().getEmail();
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        updateJoins(currentMail);
        updateMembers();

        finish();

    }

    private void updateJoins(String currentMail){
        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getIdUser()).setValue("true");

                databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                //  int members = databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE);
                //   Log.i("MEM", Integer.toString(members));
                //  databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(2);
                // textViewMembers.setText(members+1);
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

    private void updateMembers(){
        Log.i("UP MEM", "mec mec");
        Query query = databaseGroup.orderByChild(FireBaseReferences.GROUPNAME_REFERENCE).equalTo(name);
        Log.i("UP NAME", name);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //  Group group = dataSnapshot.getValue(Group.class);
                //  databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                // databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                int members = dataSnapshot.getValue(Group.class).getNumberOfMembers();
                databaseGroup.child(groupKey).child(FireBaseReferences.NUMBERMEMBERS_REFERENCE).setValue(members+1);
                Log.i("MEM", Integer.toString(members));

                // textViewMembers.setText(members+1);
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
    }*/

    public void showDetails (View view){
        Intent intent = new Intent(this, DetailRouteActivity.class);
        intent.putExtra("route", route);
      //  intent.putExtra("meteo", route.getMeteo());

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
            case R.id.action_gallery:
                showPhotoGallery();
                return true;
            case R.id.action_leaveGroup:

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
}
