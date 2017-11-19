package edu.uoc.iartal.trekkingchallenge.Group;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

public class ListGroupsActivity extends AppCompatActivity {

    //private DatabaseReference databaseGroup;
    //private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    //GroupAdapter groupAdapter;
    private RecyclerView.Adapter groupAdapter;
    private List<Group> groups;
    private DatabaseReference databaseGroup;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.listGroupToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listGroupsActivity));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddGroupActivity.class));
            }
        });

        groups = new ArrayList<>();

        //items.add(new Anime(R.drawable.angel, "Angel Beats", 230));

        recyclerView = (RecyclerView) findViewById(R.id.rvListGroup);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //FirebaseDatabase databaseGroup = FirebaseDatabase.getInstance();
         databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);


        groupAdapter = new GroupAdapter(groups);
        recyclerView.setAdapter(groupAdapter);


        databaseGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.removeAll(groups);
                for (DataSnapshot groupSapshot:
                        dataSnapshot.getChildren()) {
                    Group group = groupSapshot.getValue(Group.class);
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

  /*  @Override
    protected void onStart() {
        super.onStart();

        databaseGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    groups.add(group);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        })
    }*/
}
