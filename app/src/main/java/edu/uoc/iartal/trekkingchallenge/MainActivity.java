package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void userActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    public void groupActivity (View view){
        if (firebaseAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),ListGroupsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }
}
