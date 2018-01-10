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

package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.adapter.TripAdapter;

public class AllTripsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Trip> trips;
    private TripAdapter tripAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private DatabaseReference databaseTrip;

    public AllTripsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize progress dialog
        progressDialog = new ProgressDialog(getActivity());
        controller = new FirebaseController();
        trips = new ArrayList<>();
        tripAdapter = new TripAdapter(trips);

        // Get trip database reference
        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
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
        View rootView = inflater.inflate(R.layout.activity_list_all_trips,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListAllTrips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    /**
     * Get all database public trips
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        // Define floating button for adding new trip
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddTrip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTripActivity.class));
            }
        });

        recyclerView.setAdapter(tripAdapter);

        getListPublicTrips();
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
     * Pass new trip list to Adapter
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
     * Get list of public trips and notify adapter to show them in recyclerView
     */
    private void getListPublicTrips(){
        // Execute controller method to get database trip objects. Use OnGetDataListener interface to know
        // when database data is retrieved and notify adapter to show them in recycler view
        controller.readData(databaseTrip, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                trips.clear();
                for (DataSnapshot tripSnapshot : data.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip.getPublic()){
                        trips.add(trip);
                    }
                }
                tripAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListAllTrips error", databaseError.getMessage());
            }
        });
    }
}
