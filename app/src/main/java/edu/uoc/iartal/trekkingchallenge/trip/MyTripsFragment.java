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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetChildListener;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.adapter.TripAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;

public class MyTripsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private ArrayList<Trip> trips;
    private TripAdapter tripAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserId;
    private DatabaseReference databaseUser, databaseTrip;
    private FirebaseController controller;

    public MyTripsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(getActivity());
        trips = new ArrayList<>();
        tripAdapter = new TripAdapter(trips);

        // Get user database reference
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        getCurrentUserId();
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
     * Get database public and private trips which current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        recyclerView.setAdapter(tripAdapter);

        showTripsInView();
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
     * Add user trip to the list
     * @param trip
     */
    private void addTrip(Trip trip) {
        trips.add(trip);
        if (trip.getUserAdmin().equals(currentUserId)) {
            tripAdapter.setVisibility(true);
        } else {
            tripAdapter.setVisibility(false);
        }
    }

    /**
     * Get current user to get his trips list later
     */
    private void getCurrentUserId(){
        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUserId = user.getId();
                    }
                }
                getUserTrips();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("MyTripCurrentUser error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get current user trips list
     */
    private void getUserTrips(){
        // Execute controller method to get database trips objects. Use OnGetDataListener interface to know
        // when database data is retrieved and notify adapter to show them in recycler view
        controller.readChild(databaseTrip, new OnGetChildListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Trip trip = data.getValue(Trip.class);

                if (trip.getMembers().containsKey(currentUserId)){
                    addTrip(trip);
                }
                tripAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onChanged(DataSnapshot data) {
                // Get user trips and check if some trip has been updated
                Trip trip = data.getValue(Trip.class);
                if (trip.getMembers().containsKey(currentUserId)){
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
                progressDialog.dismiss();
            }

            @Override
            public void onRemoved(DataSnapshot data) {
                //Nothing to do
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListMyTrips error", databaseError.getMessage());
            }
        });
    }

    /**
     * Return list of trips
     * @return trips
     */
    private ArrayList<Trip> showTripsInView(){
        return trips;
    }
}