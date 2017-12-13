package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.objects.RouteAdapter;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListRoutesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int ACTIVITY_CODE = 1;
    private RouteAdapter routeAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Route> routes = new ArrayList<>();
    private Toolbar toolbar;
    private DatabaseReference databaseRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        toolbar = (Toolbar) findViewById(R.id.listRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listRoutesActivity));

        // Initialize and set recycler view and its adapter
        recyclerView = (RecyclerView) findViewById(R.id.rvListRoutes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        routeAdapter = new RouteAdapter(routes, ListRoutesActivity.this);
        recyclerView.setAdapter(routeAdapter);

        // Database reference
        databaseRoutes = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Show database users in recycler view
        databaseRoutes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.clear();
                for (DataSnapshot routeSnapshot:
                        dataSnapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                        routes.add(route);
                }
                routeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
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
                Intent intent = new Intent(this, SearchRoutesActivity.class);
                intent.putExtra("routes",routes);
                startActivityForResult(intent,ACTIVITY_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Get result from advanced search activity when it has finished
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<Route> filterRoutes;
        if (resultCode == RESULT_OK) {
            filterRoutes = data.getParcelableArrayListExtra("routes");
            routeAdapter.setFilter(filterRoutes);
        }
    }
}
