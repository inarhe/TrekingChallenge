package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;

public class UserAreaActivity extends AppCompatActivity {

    private TextView textViewUserName, textViewUserMail, textViewIdUser;
    private DatabaseReference databaseUser;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        Toolbar toolbar = (Toolbar) findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.userAreaActivity));

        //get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference("user");

        //     progressDialog = new ProgressDialog(this);

        textViewIdUser = (TextView) findViewById(R.id.textViewIdUser);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserMail = (TextView) findViewById(R.id.textViewUserMail);


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
                    textViewIdUser.setText(dataSnapshot.getValue(User.class).getIdUser());
                    textViewUserName.setText(dataSnapshot.getValue(User.class).getNameUser());
                    textViewUserMail.setText(dataSnapshot.getValue(User.class).getMailUser());
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

    public void deleteUserAccount(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.userAccountDeleted), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
    }

    public void signOut(View view) {
        firebaseAuth.signOut();
    }
}
