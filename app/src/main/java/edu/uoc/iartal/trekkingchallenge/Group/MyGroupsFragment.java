package edu.uoc.iartal.trekkingchallenge.Group;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.GroupAdapter;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 26/11/2017.
 */

public class MyGroupsFragment extends Fragment{

    private List<Group> groups;
    private List<String> keys;
    private GroupAdapter groupAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private String currentUser;
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


        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);
        progressDialog.show();
        keys = new ArrayList<>();
        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> userGroups;
                userGroups = dataSnapshot.getValue(User.class).getGroups();
                currentUser = dataSnapshot.getValue(User.class).getIdUser();
                keys.removeAll(keys);
                for (String key : userGroups.keySet()) {
                    keys.add(key);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TO-DO

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
                // Getting Post failed, log a message
                Log.w("ERROR", "loadUser:onCancelled", databaseError.toException());
                // ...
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
                    if (keys.contains(group.getIdGroup())){
                        groups.add(group);
                    }
                    if (group.getUserAdmin().equals(currentUser)) {
                        Log.i("GRUP", group.getIdGroup());
                        Log.i("ADMIN", group.getUserAdmin() );
                        Log.i("CURRENT",currentUser);
                        groupAdapter.setVisibility(true);
                    } else {
                        Log.i("GRUP", group.getIdGroup());
                        Log.i("ADMIN", group.getUserAdmin() );
                        Log.i("CURRENT",currentUser);
                        groupAdapter.setVisibility(false);
                    }
                    //groupAdapter.notifyDataSetChanged();
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
