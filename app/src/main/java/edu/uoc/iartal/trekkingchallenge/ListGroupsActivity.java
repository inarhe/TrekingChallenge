package edu.uoc.iartal.trekkingchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.GroupAdapter;

public class ListGroupsActivity extends AppCompatActivity {

    //private DatabaseReference databaseGroup;
    //private FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;

    List<Group> groups;

    GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);

        recyclerView = (RecyclerView) findViewById(R.id.rvListGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groups = new ArrayList<>();



        FirebaseDatabase database = FirebaseDatabase.getInstance();

        groupAdapter = new GroupAdapter(groups);
        recyclerView.setAdapter(groupAdapter);

        database.getReference().getRoot().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.removeAll(groups);
                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    groups.add(group);
                }
                groupAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        // databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
      //  databaseGroup.addValueEventListener(new ValueEventListener() {
      //      @Override
       //     public void onDataChange(DataSnapshot dataSnapshot) {
        //        Group group = dataSnapshot.getValue(Group.class);

       //     }

     //      @Override
       //     public void onCancelled(DatabaseError databaseError) {

       //     }
       // })

    }
}
