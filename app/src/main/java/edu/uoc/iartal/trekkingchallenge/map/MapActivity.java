/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.route.ShowRouteActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private List<Route> routes;
    private DatabaseReference databaseRoute;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CommonFunctionality common;

    public MapActivity() {

    }

    public static MapActivity newInstance() {
        return new MapActivity();
    }

    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.mapActivity);

        // Initialize variables
        controller = new FirebaseController();
        routes = new ArrayList<>();
        common = new CommonFunctionality();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database reference
        databaseRoute = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Set actionbar drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Set navigation view listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Define user actions not allowed
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);

        // Define map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set marks of aplication routes
        loadMarkRoutes();

        // Set the map position
        centerMap();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String routeName = marker.getTitle();

                for (Route route : routes){
                    if (route.getName().equals(routeName)){
                        Intent intent = new Intent(getApplicationContext(), ShowRouteActivity.class);
                        intent.putExtra("route", route);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    /**
     * Handle navigation drawer view item clicked
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                common.startHomeNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.mapItem:
                common.startMapNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.routeItem:
                common.startRouteNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.tripItem:
                common.startTripNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.challengeItem:
                common.startChallengeNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.userItem:
                common.startUserNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.groupItem:
                common.startGroupNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logoutItem:
                common.startLogOutNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }

    /**
     * Set map position in Catalunya
     */
    private void centerMap(){
        LatLngBounds catalunya = new LatLngBounds(
                new LatLng(40.6363, 0.1968), new LatLng(42.4832, 3.2876));

        mMap.setLatLngBoundsForCameraTarget(catalunya);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(catalunya.getCenter(), 7));
    }

    /**
     * Get all database routes and add a map marker for each one
     */
    private void loadMarkRoutes(){

        controller.readDataOnce(databaseRoute, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                routes.clear();
                for (DataSnapshot routeSnapshot : data.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    routes.add(route);
                }

                for (Route route : routes) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(route.getLat(), route.getLng())).title(route.getName()).snippet(route.getTownship()));
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("Map routes error", databaseError.getMessage());
            }
        });
    }
}


