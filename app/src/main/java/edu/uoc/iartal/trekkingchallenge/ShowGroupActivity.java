package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ShowGroupActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewDescription;
    private TextView textViewMembers;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);


        textViewDescription = (TextView) findViewById(R.id.textViewGroupDescription);
        textViewMembers = (TextView) findViewById(R.id.textViewNumberMembers);
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle groupData = getIntent().getExtras();
        String name = groupData.getString("groupName");
        String description = groupData.getString("groupDescription");
        int members = groupData.getInt("members");

        Toolbar toolbar = (Toolbar) findViewById(R.id.showGroupToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(name);

        textViewDescription.setText(description);
        textViewMembers.setText(String.valueOf(members));

        if (firebaseAuth.getCurrentUser() == null) {
            // start login activity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
