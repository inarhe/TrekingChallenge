package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.Group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.User.AccessActivity;
import edu.uoc.iartal.trekkingchallenge.User.UserAreaActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();

        dataBase = FirebaseDatabase.getInstance();
       // DatabaseReference myRef = dataBase.getReference();

    }


    public void mapActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void searchActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void tripActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void challengeActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void userAreaActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),UserAreaActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void groupActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            Log.i("CURRENTUSER", "groupActivity: " + firebaseAuth.getCurrentUser().getEmail());
            startActivity(new Intent(getApplicationContext(),ListGroupsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }
}
