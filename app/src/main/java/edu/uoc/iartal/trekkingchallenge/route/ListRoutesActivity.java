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

package edu.uoc.iartal.trekkingchallenge.route;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import edu.uoc.iartal.trekkingchallenge.adapter.RouteAdapter;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListRoutesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int ACTIVITY_CODE = 1;

    private RouteAdapter routeAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Route> routes;
    private DatabaseReference databaseRoutes;
    private FirebaseController controller;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CommonFunctionality common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.listRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listRoutesActivity));

        // Initialize variables
        progressDialog = new ProgressDialog(this);
        controller = new FirebaseController();
        routes = new ArrayList<>();
        common = new CommonFunctionality();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Initialize and set recycler view and its adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvListRoutes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        routeAdapter = new RouteAdapter(routes, ListRoutesActivity.this);
        recyclerView.setAdapter(routeAdapter);

        // Get route database reference
        databaseRoutes = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Set actionbar drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Set navigation view listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Show database routes in recycler view
        getRoutes();
    }

    /**
     * Inflate menu with menu layout information and define search view
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_routes, menu);
        final MenuItem item = menu.findItem(R.id.action_searchRoutes);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        routeAdapter.setFilter(routes);
                        // Return true to collapse action view
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Return true to expand action view
                        return true;
                    }
                });
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Pass new route list to Adapter
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Route> filteredModelList = filter(routes, newText);
        routeAdapter.setFilter(filteredModelList);
        return true;
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
     * Get route list search result
     * @param models
     * @param query
     * @return
     */
    private List<Route> filter(List<Route> models, String query) {
        query = query.toLowerCase();
        final List<Route> filteredModelList = new ArrayList<>();
        for (Route model : models) {
            final String text = model.getName().toLowerCase();
            final String text2 = model.getRegion().toLowerCase();
            if (text.contains(query) || text2.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advancedSearch:
                advancedSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Get data from advanced search activity when it has finished
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<Route> filterRoutes;
        if (resultCode == RESULT_OK) {
            filterRoutes = data.getParcelableArrayListExtra("routes");
            if (filterRoutes.size()==0){
                Toast.makeText(ListRoutesActivity.this,R.string.noMatches,Toast.LENGTH_LONG).show();
            }
            routeAdapter.setFilter(filterRoutes);
        }
    }

    /**
     * Start advanced search activity and pass routes list
     */
    private void advancedSearch(){
        Intent intent = new Intent(this, SearchRoutesActivity.class);
        intent.putExtra("routes", routes);
        startActivityForResult(intent, ACTIVITY_CODE);
    }

    /**
     * Get routes list from database
     */
    private void getRoutes(){
        controller.readData(databaseRoutes, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                routes.clear();
                for (DataSnapshot routeSnapshot : data.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    routes.add(route);
                }
                routeAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListRoutes error", databaseError.getMessage());
            }
        });
    }
}
