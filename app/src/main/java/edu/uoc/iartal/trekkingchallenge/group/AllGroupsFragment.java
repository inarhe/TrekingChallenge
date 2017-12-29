package edu.uoc.iartal.trekkingchallenge.group;

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

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.adapter.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.R;


public class AllGroupsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Group> groups;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private DatabaseReference databaseGroup;

    public AllGroupsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        progressDialog = new ProgressDialog(getActivity());
        controller = new FirebaseController();
        groups = new ArrayList<>();
        groupAdapter = new GroupAdapter(groups);

        // Get group database reference
        databaseGroup = controller.getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);
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
        View rootView = inflater.inflate(R.layout.activity_list_all_groups,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListAllGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    /**
     * Get all database public groups
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        // Define floating button for adding new group
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddGroupActivity.class));
            }
        });

        recyclerView.setAdapter(groupAdapter);

        // Execute controller method to get database groups objects. Use OnGetDataListener interface to know
        // when database data is retrieved and notify adapter to show them in recycler view
        controller.readData(databaseGroup, new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                groups.clear();
                for (DataSnapshot groupSnapshot:data.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group.getIsPublic()){
                        groups.add(group);
                    }
                }
                groupAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("LoadAllGroups error", databaseError.getMessage());
            }
        });
    }

    /**
     * Inflate menu with menu layout information and define search view
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_groups, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        groupAdapter.setFilter(groups);
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
     * Pass new group list to Adapter
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Group> filteredModelList = filter(groups, newText);
        groupAdapter.setFilter(filteredModelList);
        return true;
    }

    /**
     * Get group list search result
     * @param models
     * @param query
     * @return
     */
    private List<Group> filter(List<Group> models, String query) {
        query = query.toLowerCase();
        final List<Group> filteredModelList = new ArrayList<>();
        for (Group model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
