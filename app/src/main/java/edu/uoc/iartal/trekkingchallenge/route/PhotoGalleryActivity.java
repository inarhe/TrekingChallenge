package edu.uoc.iartal.trekkingchallenge.route;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.PhotoAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;

public class PhotoGalleryActivity extends AppCompatActivity {

    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private Route route;
    private Toolbar toolbar;
    private DatabaseReference databaseRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        toolbar = (Toolbar) findViewById(R.id.photoGalleryToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.photoGallery));

        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        recyclerView = (RecyclerView) findViewById(R.id.rvPhotoGallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoAdapter = new PhotoAdapter(route, PhotoGalleryActivity.this);
        recyclerView.setAdapter(photoAdapter);

        photoAdapter.notifyDataSetChanged();

    }
}
