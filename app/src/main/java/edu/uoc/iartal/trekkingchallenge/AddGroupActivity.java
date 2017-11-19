package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.Group;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;

public class AddGroupActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
  //  private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference databaseGroup, databaseUser, databaseGroupMembers;
    private CheckBox checkBox;
    private String userAdmin;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addGroupToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addGroupActivity));

        //get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseGroup = FirebaseDatabase.getInstance().getReference("group");

   //     progressDialog = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etNameGroup);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionGroup);
        checkBox = (CheckBox) findViewById(R.id.cBPublicGgroup);


        if (firebaseAuth.getCurrentUser() == null) {
            // start login activity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            String mail = firebaseAuth.getCurrentUser().getEmail();
            databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild("mailUser").equalTo(mail);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    userAdmin = dataSnapshot.getValue(User.class).getIdUser();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addGroup (View view) {
        Boolean isPublic = false;
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descriptionField), Toast.LENGTH_LONG).show();
            //Stopping the function execution further
            return;
        }


        //   progressDialog.setMessage(getString(R.string.savingGroup));
        // progressDialog.show();

        if (checkBox.isChecked()) {
            isPublic = true;
        }



        String id = databaseGroup.push().getKey();
        Group group = new Group(id, name, description, isPublic, userAdmin, userAdmin);

        // FirebaseUser user = firebaseAuth.getCurrentUser();
        //.child(user.getUid())
        databaseGroup.child(id).setValue(group);
        databaseUser.child(userAdmin).child("groups").setValue(name);
        // progressDialog.dismiss();

        Toast.makeText(getApplicationContext(), getString(R.string.groupSaved), Toast.LENGTH_LONG).show();
        finish();


    }

    public void cancelGroup (View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

        //
}
