package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Trip;
import edu.uoc.iartal.trekkingchallenge.objects.TripAdapter;
import edu.uoc.iartal.trekkingchallenge.objects.User;

public class MyTripsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Trip> trips;
    private TripAdapter tripAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserName, currentMail;

    public MyTripsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        trips = new ArrayList<>();

        // Get current user
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUserName = user.getIdUser();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    /**
     * Define recyclerview that will be show in this view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_my_trips,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyTrips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        trips.clear();

        return rootView;
    }

    /**
     * Get database public and private trips that current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        DatabaseReference databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);

        tripAdapter = new TripAdapter(trips);

        recyclerView.setAdapter(tripAdapter);

        // Get database trips and notify adapter to show them in recycler view
        progressDialog.show();
        databaseTrip.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip.getMembers().containsKey(currentUserName)){
                    addTrip(trip);
                }
                tripAdapter.notifyDataSetChanged();
                //progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Get user trips
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip.getMembers().containsKey(currentUserName)){
                    if (!trips.contains(trip)){
                        addTrip(trip);
                    } else {
                        int i = trips.indexOf(trip);
                        Trip tripArray = trips.get(i);
                        if (!tripArray.getName().equals(trip.getName()) || !tripArray.getDescription().equals(trip.getDescription())
                                || !tripArray.getDate().equals(trip.getDate())){
                            trips.set(i, trip);
                        }
                    }
                } else {
                    if (trips.contains(trip)){
                        trips.remove(trip);
                    }
                }
                tripAdapter.notifyDataSetChanged();
               // progressDialog.dismiss();
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
        progressDialog.dismiss();
    }

    /**
     * Inflate menu with menu layout information and define search view
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_trips, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        tripAdapter.setFilter(trips);
                        // Return true to collapse action view
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Return true to expand action view
                        return true;
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Pass new challenge list to Adapter
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Trip> filteredModelList = filter(trips, newText);
        tripAdapter.setFilter(filteredModelList);
        return true;
    }

    /**
     * Get search result trip list
     * @param models
     * @param query
     * @return
     */
    private List<Trip> filter(List<Trip> models, String query) {
        query = query.toLowerCase();
        final List<Trip> filteredModelList = new ArrayList<>();
        for (Trip model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * Add user challenge to the list
     * @param trip
     */
    private void addTrip(Trip trip) {
        trips.add(trip);
        if (trip.getUserAdmin().equals(currentUserName)) {
            tripAdapter.setVisibility(true);
        } else {
            tripAdapter.setVisibility(false);
        }
    }
}
