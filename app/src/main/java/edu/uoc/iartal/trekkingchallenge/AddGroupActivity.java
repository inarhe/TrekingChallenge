package edu.uoc.iartal.trekkingchallenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGroupActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private FirebaseAuth firebaseAuth;
  //  private ProgressDialog progressDialog;
    private DatabaseReference databaseUser;
    private String name, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        //get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference("group");

   //     progressDialog = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etNameGroup);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionGroup);


        if (firebaseAuth.getCurrentUser() == null) {
            // start login activity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    public void addGroup (View view) {
        name = editTextName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();

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

        String id = databaseUser.push().getKey();
        Group group = new Group(id, name, description);
        // FirebaseUser user = firebaseAuth.getCurrentUser();
        //.child(user.getUid())
        databaseUser.child(id).setValue(group);
        // progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), getString(R.string.groupSaved), Toast.LENGTH_LONG).show();


    }

    public void cancelGroup (View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

        //
}
