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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetChildListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.adapter.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.R;

import android.view.MenuInflater;
import android.view.MenuItem;

public class MyGroupsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private ArrayList<Group> groups;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUserId;
    private DatabaseReference databaseUser, databaseGroup;
    private FirebaseController controller;

    public MyGroupsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(getActivity());
        groups = new ArrayList<>();
        groupAdapter = new GroupAdapter(groups);

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
        View rootView = inflater.inflate(R.layout.activity_list_my_groups,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groups.clear();

        return rootView;
    }

    /**
     * Get database public and private groups which current user is a member
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        databaseGroup = controller.getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);
        recyclerView.setAdapter(groupAdapter);

        showGroupsInView();
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
        if (group.getUserAdmin().equals(currentUserId)) {
            groupAdapter.setVisibility(true);
        } else {
            groupAdapter.setVisibility(false);
        }
    }

    /**
     * Get current user to get his groups list later
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
                        break;
                    }
                }
                getUserGroups();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("MyGrpCurrentUser error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get current user groups list
     */
    private void getUserGroups(){
        // Execute controller method to get database groups objects. Use OnGetDataListener interface to know
        // when database data is retrieved and notify adapter to show them in recycler view
        controller.readChild(databaseGroup, new OnGetChildListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loadingData));
                progressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Group group = data.getValue(Group.class);
                if (group.getMembers().containsKey(currentUserId)) {
                    addGroup(group);
                }
                groupAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onChanged(DataSnapshot data) {
                // Get user groups and check if some group has been updated
                Group group = data.getValue(Group.class);
                if (group.getMembers().containsKey(currentUserId)){
                    if (!groups.contains(group)){
                        addGroup(group);
                    } else {
                        int i = groups.indexOf(group);
                        Group groupArray = groups.get(i);
                        if (!groupArray.getName().equals(group.getName()) || !groupArray.getDescription().equals(group.getDescription())
                                || (groupArray.getNumberOfMembers() != group.getNumberOfMembers())){
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
            public void onRemoved(DataSnapshot data) {
                //Nothing to do
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ListMyGroups error", databaseError.getMessage());
            }
        });
    }

    /**
     * Return list of groups
     * @return groups
     */
    private ArrayList<Group> showGroupsInView(){
        return groups;
    }
}
