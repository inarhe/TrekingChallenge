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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 26/11/2017.
 */

public class AllGroupsFragment extends Fragment implements SearchView.OnQueryTextListener{

    private List<Group> groups;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private ImageButton imageButton;
    private Boolean isVisible = false;
    private RecyclerView recyclerView;
    public AllGroupsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_all_groups,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListAllGroups);
     //   recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
       // imageButton = (ImageButton) rootView.findViewById(R.id.icDelGroupAdmin);

        // Floating button for adding new group
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddGroupActivity.class));
            }
        });

        DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        groups = new ArrayList<>();
        groupAdapter = new GroupAdapter(groups);

        recyclerView.setAdapter(groupAdapter);

        // Show database groups in recycler view
        progressDialog.show();
        databaseGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.removeAll(groups);
                for (DataSnapshot groupSapshot:
                        dataSnapshot.getChildren()) {
                    Group group = groupSapshot.getValue(Group.class);
                    if (group.getIsPublic()){
                        groups.add(group);
                        //groupAdapter.setVisibility(false);
                    }
                }
                groupAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_list_groups, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        groupAdapter.setFilter(groups);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Group> filteredModelList = filter(groups, newText);

        groupAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<Group> filter(List<Group> models, String query) {
        query = query.toLowerCase();
        final List<Group> filteredModelList = new ArrayList<>();
        for (Group model : models) {
            final String text = model.getIdGroup().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
