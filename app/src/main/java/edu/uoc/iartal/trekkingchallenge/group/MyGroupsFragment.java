package edu.uoc.iartal.trekkingchallenge.group;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.objects.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.objects.User;
import edu.uoc.iartal.trekkingchallenge.R;

import android.view.MenuInflater;
import android.view.MenuItem;

public class MyGroupsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private List<Group> groups;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserName, currentMail;

    public MyGroupsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loadingData));

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        groups = new ArrayList<>();

        // Get current user
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getMail().equals(currentMail)) {
                        currentUserName = user.getId();
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
        View rootView = inflater.inflate(R.layout.activity_list_my_groups,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groups.clear();

        return rootView;
    }

    /**
     * Get database public and private groups that current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        groupAdapter = new GroupAdapter(groups);

        recyclerView.setAdapter(groupAdapter);

        // Get user groups and notify adapter to show them in recycler view
        progressDialog.show();
        databaseGroup.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group.getMembers().containsKey(currentUserName)){
                        addGroup(group);
                }
                groupAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Get user groups and check if some group has been updated
                Group group = dataSnapshot.getValue(Group.class);
                if (group.getMembers().containsKey(currentUserName)){
                    if (!groups.contains(group)){
                        addGroup(group);
                    } else {
                        int i = groups.indexOf(group);
                        Group groupArray = groups.get(i);
                        if (!groupArray.getName().equals(group.getName()) || !groupArray.getDescription().equals(group.getDescription())){
                            groups.set(i, group);
                        }
                    }
                } else {
                    if (groups.contains(group)){
                        groups.remove(group);
                    }
                }
                groupAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
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
     * Get search result group list
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

    /**
     * Add user group to the list
     * @param group
     */
    private void addGroup(Group group) {
        groups.add(group);
        if (group.getUserAdmin().equals(currentUserName)) {
            groupAdapter.setVisibility(true);
        } else {
            groupAdapter.setVisibility(false);
        }
    }
}
