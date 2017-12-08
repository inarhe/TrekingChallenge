package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

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

import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.objectsDB.RouteAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.objectsDB.UserAdapter;

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

        toolbar = (Toolbar) findViewById(R.id.listRouteToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listRoutesActivity));

        recyclerView = (RecyclerView) findViewById(R.id.rvListRoutes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        routeAdapter = new RouteAdapter(routes, ListRoutesActivity.this);
        recyclerView.setAdapter(routeAdapter);

        databaseRoutes = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        // Show database users in recycler view
        databaseRoutes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.removeAll(routes);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_routes, menu);

        final MenuItem item = menu.findItem(R.id.action_searchRoutes);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        routeAdapter.setFilter(routes);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Route> filteredModelList = filter(routes, newText);

        routeAdapter.setFilter(filteredModelList);
        return true;
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advancedSearch:
                Intent intent = new Intent(this, SearchRoutesActivity.class);
                intent.putExtra("routes",routes);
                startActivityForResult(intent,ACTIVITY_CODE);
               // finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<Route> filterRoutes = new ArrayList<>();
       //  Bundle bundle = getIntent().getExtras();
        filterRoutes = data.getParcelableArrayListExtra("routes");
        routeAdapter.setFilter(filterRoutes);

    }
}
