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
import android.widget.ImageButton;
import android.support.v7.widget.SearchView;

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
import java.util.Map;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.objectsDB.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Ingrid Artal on 26/11/2017.
 */

public class MyGroupsFragment extends Fragment implements SearchView.OnQueryTextListener{

    private List<Group> groups;
    private List<String> keys;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUser, currentMail;
    private ImageButton imageButton;

    public MyGroupsFragment(){

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
        View rootView = inflater.inflate(R.layout.activity_list_my_groups,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListMyGroups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        keys = new ArrayList<>();


        groupAdapter = new GroupAdapter(groups);

        recyclerView.setAdapter(groupAdapter);

        // Show database groups in recycler view
        progressDialog.show();
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keys.removeAll(keys);
                for (DataSnapshot groupSapshot :
                        dataSnapshot.getChildren()) {
                    User user = groupSapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUser = user.getIdUser();
                        for (String key : user.getGroups().keySet()) {
                            keys.add(key);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });



        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

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
                for (DataSnapshot groupSapshot :
                        dataSnapshot.getChildren()) {
                    Group group = groupSapshot.getValue(Group.class);
                    if (keys.contains(group.getIdGroup())) {
                        groups.add(group);

                        if (group.getUserAdmin().equals(currentUser)) {
                            groupAdapter.setVisibility(true);
                        } else {
                            groupAdapter.setVisibility(false);
                        }
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
