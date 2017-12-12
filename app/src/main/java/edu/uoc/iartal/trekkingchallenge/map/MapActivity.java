package edu.uoc.iartal.trekkingchallenge.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.route.ShowRouteActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRoute;
    private List<Route> routes;

    public MapActivity() {

    }

    public static MapActivity newInstance() {
        return new MapActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.mapActivity);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Barcelona and move the camera
     /*   LatLng bcn = new LatLng(41.49, 1.28);

        mMap.addMarker(new MarkerOptions().position(bcn).title("Marker in Barcelona"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bcn));*/


        loadMarkRoutes();
        // Create a LatLngBounds that includes Australia.
        centerMap();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final String routeName = marker.getTitle();
                DatabaseReference databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

                Query query = databaseRoute.orderByChild(FireBaseReferences.ROUTENAME_REFERENCE).equalTo(routeName);

                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Route route = dataSnapshot.getValue(Route.class);
                        Intent intent = new Intent(getApplicationContext(), ShowRouteActivity.class);
                        intent.putExtra("route", route);
                        startActivity(intent);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void centerMap(){
        LatLngBounds catalunya = new LatLngBounds(
                new LatLng(40.6363, 0.1968), new LatLng(42.4832, 3.2876));

        mMap.setLatLngBoundsForCameraTarget(catalunya);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(catalunya.getCenter(), 8));

    }

    private void loadMarkRoutes(){

        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);
        routes = new ArrayList<>();

        databaseRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.removeAll(routes);
                for (DataSnapshot routeSnapshot:
                        dataSnapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                        routes.add(route);
                }

                for (Route route : routes) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(route.getLat(), route.getLng())).title(route.getName()).snippet(route.getTownship()));


                }
                //    LatLng bcn = new LatLng(41.49, 1.28);
              //    mMap.addMarker(new MarkerOptions().position(bcn).title("Marker in Barcelona"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(bcn));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.49, 1.28)));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(5.0f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });

    }
}


