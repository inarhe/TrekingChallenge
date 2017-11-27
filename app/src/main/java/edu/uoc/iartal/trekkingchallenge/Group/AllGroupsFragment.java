package edu.uoc.iartal.trekkingchallenge.Group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 26/11/2017.
 */

public class AllGroupsFragment extends Fragment{

    private List<Group> groups;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private ImageButton imageButton;

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
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListAllGroups);
        recyclerView.setHasFixedSize(true);

        imageButton = (ImageButton) rootView.findViewById(R.id.icDelGroupAdmin);

        // Floating button for adding new group
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabAddGroup);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        return rootView;
    }
}
